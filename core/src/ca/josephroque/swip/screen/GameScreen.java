package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import ca.josephroque.swip.entity.Ball;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.gesture.GameInputProcessor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

/**
 * Handles the logic and rendering of gameplay.
 */
@SuppressWarnings("UnusedParameters")
public final class GameScreen
        extends SwipScreen {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameScreen";

    /** Number of milliseconds that a turn at the start of a game lasts. */
    private static final int STARTING_TURN_LENGTH = 10000; // TODO: change to actual start value, 1200
    /** Number of milliseconds to subtract from the length of a turn at a time. */
    private static final int TURN_LENGTH_DECREMENT = 50;
    /** Number of turns that must pass before the turn length is decremented. */
    private static final int NUMBER_OF_TURNS_BEFORE_DECREMENT = 10;
    /** Shortest number of milliseconds that a turn can last. */
    private static final int MINIMUM_TURN_LENGTH = 300;
    /** Default number of milliseconds until a game starts. */
    private static final int TIME_UNTIL_GAME_STARTS = 1000;

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;
    /** Handles gesture input events. */
    private GameInputProcessor mGameInput;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Random number generator. */
    private Random mRandomGen = new Random();

    /** The current state the game is in. */
    private GameState mCurrentGameState;
    /** Number of milliseconds until a game begins. */
    private long mGameStartTime;

    /** The ball being used by the game. */
    private Ball mCurrentBall;
    /** The four walls in the game. */
    private Wall[] mWalls;
    /** Colors of the four walls. */
    private Color[] mWallColors;

    /** Time that the current turn started at. */
    private long mStartOfTurn;
    /** Length of a single turn. */
    private int mTurnLength;
    /** Number of milliseconds that have passed since this turn began. */
    private int mCurrentTurnDuration;
    /** Total number of turns that have passed since the game began (i.e. the player's score). */
    private int mTotalTurns;

    @Override
    public void show() {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        // Preparing UI objects
        mShapeRenderer = new ShapeRenderer();

        // Creating gesture handler
        mGameInput = new GameInputProcessor();
        Gdx.input.setInputProcessor(mGameInput);

        // Starting a new game
        startNewGame();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();

        // Freeing graphics objects
        mShapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        mGameInput.resize(width, height);

        // Resizing entities on screen
        if (mWalls != null) {
            for (Wall wall : mWalls)
                wall.resize(width, height);
        }
        if (mCurrentBall != null)
            mCurrentBall.resize(width, height);
    }

    @Override
    public void render(float delta) {
        tick(delta);

        if (!wasDisposed())
            draw();
    }

    @Override
    public void tick(float delta) {
        switch (mCurrentGameState) {
            case Starting:
                tickStartingGame(delta);
                break;
            case Active:
            case Paused:
                tickActiveGame(delta);
                break;
            case GameOver:
                tickGameOver(delta);
                break;
            default:
                throw new IllegalArgumentException("invalid game state.");
        }
    }

    @Override
    public void draw() {
        mShapeRenderer.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        switch (mCurrentGameState) {
            case Starting:
                drawStartingGame();
                break;
            case Active:
            case Paused:
                drawActiveGame();
                break;
            case GameOver:
                drawGameOver();
                break;
            default:
                throw new IllegalArgumentException("invalid game state.");
        }
    }

    /**
     * Sets up a new game.
     */
    private void startNewGame() {
        mCurrentGameState = GameState.Starting;
        mStartOfTurn = 0;
        mTurnLength = STARTING_TURN_LENGTH;
        mCurrentTurnDuration = 0;
        mTotalTurns = 0;
        mGameStartTime = TimeUtils.millis();

        // Setting initial properties of entities
        Ball.initialize(mScreenWidth, mScreenHeight);
        Wall.initialize(mScreenWidth, mScreenHeight);

        // Creating the four walls
        mWallColors = new Color[Wall.NUMBER_OF_WALLS];
        Wall.getRandomWallColors(mRandomGen, mWallColors, false);
        mWalls = new Wall[Wall.NUMBER_OF_WALLS];
        for (int i = 0; i < mWalls.length; i++) {
            mWalls[i] = new Wall(i, mScreenWidth, mScreenHeight);
            mWalls[i].updateWallColor(mWallColors[i]);
        }

        // Creating the ball
        final int randomWall = mRandomGen.nextInt(Wall.NUMBER_OF_WALLS);
        final boolean[] passableWalls = new boolean[Wall.NUMBER_OF_WALLS];
        passableWalls[randomWall] = true;
        mCurrentBall = new Ball(mWallColors[randomWall], passableWalls, mScreenWidth / 2, mScreenHeight / 2);
    }

    /**
     * Updates the logic for a game that is starting.
     *
     * @param delta delta time
     */
    private void tickStartingGame(float delta) {
        // Counts down timer to start of game
        if (TimeUtils.timeSinceMillis(mGameStartTime) >= TIME_UNTIL_GAME_STARTS) {
            mCurrentGameState = GameState.Active;
        }
    }

    /**
     * Draws a game which is preparing to start.
     */
    private void drawStartingGame() {

    }

    /**
     * Updates the logic for a game that is active or paused.
     *
     * @param delta delta time
     */
    private void tickActiveGame(float delta) {
        if (mCurrentGameState != GameState.Paused) {
            if (mStartOfTurn == 0)
                mStartOfTurn = TimeUtils.millis();
            mCurrentTurnDuration = (int) TimeUtils.timeSinceMillis(mStartOfTurn);

            if (mCurrentTurnDuration >= mTurnLength) {
                mCurrentGameState = GameState.GameOver;
            } else {
                mCurrentBall.drag(mGameInput);
                mCurrentBall.tryToReleaseBall(mGameInput);
                mCurrentBall.tick(delta, mWalls);

                if (mCurrentBall.hasPassedThroughWall())
                    turnSucceeded();
                else if (mCurrentBall.hasHitInvalidWall())
                    mCurrentGameState = GameState.GameOver;
            }
        }
    }

    /**
     * Increases the player's score and starts a new turn.
     */
    private void turnSucceeded() {
        mTotalTurns++;
        mStartOfTurn = TimeUtils.millis();

        if (mTotalTurns % Wall.NUMBER_OF_TURNS_BEFORE_NEW_COLOR == 0)
            Wall.addWallColorToActive();

        // Generates new colors for the wall
        int wallPairFirstIndex = Wall.getRandomWallColors(mRandomGen,
                mWallColors,
                mTotalTurns > Wall.NUMBER_OF_TURNS_BEFORE_SAME_WALL_COLORS);
        for (int i = 0; i < mWalls.length; i++)
            mWalls[i].updateWallColor(mWallColors[i]);

        // Generating new ball at center of screen
        final int randomWall;
        final boolean[] passableWalls = new boolean[Wall.NUMBER_OF_WALLS];
        if (wallPairFirstIndex == -1) {
            randomWall = mRandomGen.nextInt(Wall.NUMBER_OF_WALLS);
            passableWalls[randomWall] = true;
        } else {
            randomWall = wallPairFirstIndex;
            passableWalls[randomWall] = true;
            for (int i = randomWall + 1; i < Wall.NUMBER_OF_WALLS; i++)
                passableWalls[randomWall] = mWallColors[randomWall].equals(mWallColors[i]);
        }

        mCurrentBall = new Ball(mWallColors[randomWall], passableWalls, mScreenWidth / 2, mScreenHeight / 2);

        if (mTotalTurns % NUMBER_OF_TURNS_BEFORE_DECREMENT == 0)
            mTurnLength = Math.max(MINIMUM_TURN_LENGTH, mTurnLength - TURN_LENGTH_DECREMENT);
    }

    /**
     * Draws a game which is active or paused.
     */
    private void drawActiveGame() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        mCurrentBall.draw(mShapeRenderer, mTurnLength, mCurrentTurnDuration);
        for (Wall wall : mWalls)
            wall.draw(mShapeRenderer);

        mShapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Updates the logic for a game that has ended.
     *
     * @param delta delta time
     */
    private void tickGameOver(float delta) {
        if (Gdx.input.justTouched())
            startNewGame();
    }

    /**
     * Draws a game that has ended.
     */
    private void drawGameOver() {

    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    public GameScreen(SwipGame game) {
        super(game);
    }

    /**
     * States of the game.
     */
    private enum GameState {
        /** Indicates the game is paused. */
        Paused,
        /** Indicates the game is starting. */
        Starting,
        /** Indicates the game is active. */
        Active,
        /** Indicates the game is over. */
        GameOver
    }
}
