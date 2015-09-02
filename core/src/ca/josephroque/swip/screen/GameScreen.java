package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import ca.josephroque.swip.entity.Ball;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.gesture.GameGestureListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
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
    private static final int STARTING_TURN_LENGTH = 1000;
    /** Number of milliseconds to subtract from the length of a turn at a time. */
    private static final int TURN_LENGTH_DECREMENT = 50;
    /** Number of turns that must pass before the turn length is decremented. */
    private static final int NUMBER_OF_TURNS_BEFORE_DECREMENT = 10;
    /** Shortest number of milliseconds that a turn can last. */
    private static final int MINIMUM_TURN_LENGTH = 100;
    /** Default number of milliseconds until a game starts. */
    private static final int TIME_UNTIL_GAME_STARTS = 3000;
    /** Number of milliseconds that a fling lasts. */
    private static final float FLING_TIME = 100;

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;
    /** Handles gesture input events. */
    private GameGestureListener mGestureListener;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;
    /** The current state the game is in. */
    private GameState mCurrentGameState;

    /** Random number generator. */
    private Random mRandomGen = new Random();

    /** The ball being used by the game. */
    private Ball mCurrentBall;

    /** Current color of each of the walls. */
    private Color[] mWallColors = new Color[Wall.NUMBER_OF_WALLS];

    /** Indicates if the user has flung the ball. */
    private boolean mFlinging;
    /** Starting x and y location of a fling. */
    private float[] mFlingStartLocation = new float[2];
    /** Target x and y location for the end of a fling. */
    private float[] mFlingTargetLocation = new float[2];
    /** Current travelling velocity of a flung ball. */
    private float[] mFlingVelocity = new float[2];
    /** Time in milliseconds of the start of the fling. */
    private long mFlingStartTime;

    /** Number of milliseconds until a game begins. */
    private long mGameStartTime;

    /** Time that the current turn started at. */
    private long mStartOfTurn;
    /** Number of milliseconds that have passed since this turn began. */
    private int mCurrentTurnDuration;
    /** Length of a single turn. */
    private int mTurnLength;
    /** Total number of turns that have passed since the game began (i.e. the player's score). */
    private int mTotalTurns;

    @Override
    public void tick() {
        switch (mCurrentGameState) {
            case Starting:
                tickStartingGame();
                break;
            case Active:
            case Paused:
                tickActiveGame();
                break;
            case GameOver:
                tickGameOver();
                break;
            default:
                // does nothing
        }
    }

    @Override
    public void draw(float delta) {
        mShapeRenderer.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        switch (mCurrentGameState) {
            case Starting:
                drawStartingGame(delta);
                break;
            case Active:
            case Paused:
                drawActiveGame(delta);
                break;
            case GameOver:
                drawGameOver(delta);
                break;
            default:
                // does nothing
        }
    }

    @Override
    public void show() {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        // Preparing UI objects
        mShapeRenderer = new ShapeRenderer();

        // Creating gesture handler
        mGestureListener = new GameGestureListener();
        GestureDetector gestureDetector = new GestureDetector(mGestureListener);
        Gdx.input.setInputProcessor(gestureDetector);

        // Starting a new game
        startNewGame();
    }

    @Override
    public void render(float delta) {
        tick();

        if (!wasDisposed())
            draw(delta);
    }

    @Override
    public void hide() {
        disposeEventually();

        // Disposing objects
        mShapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        mGestureListener.resize(width, height);
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

        Wall.initializeActiveColors();
        Wall.getRandomWallColors(mRandomGen, mWallColors, false);

        // Creating the ball
        final int randomWall = mRandomGen.nextInt(Wall.NUMBER_OF_WALLS);
        final boolean[] passableWalls = new boolean[Wall.NUMBER_OF_WALLS];
        passableWalls[randomWall] = true;
        Ball.initialize(mScreenWidth, mScreenHeight);
        mCurrentBall = new Ball(mWallColors[randomWall], passableWalls);
    }

    /**
     * Updates the logic for a game that is starting.
     */
    private void tickStartingGame() {
        // Counts down timer to start of game
        if (TimeUtils.timeSinceMillis(mGameStartTime) >= TIME_UNTIL_GAME_STARTS)
            mCurrentGameState = GameState.Active;
    }

    /**
     * Draws a game which is preparing to start.
     *
     * @param delta delta time
     */
    private void drawStartingGame(float delta) {

    }

    /**
     * Updates the logic for a game that is active or paused.
     */
    private void tickActiveGame() {
        if (mCurrentGameState != GameState.Paused) {
            if (mStartOfTurn == 0)
                mStartOfTurn = TimeUtils.millis();
            mCurrentTurnDuration = (int) TimeUtils.timeSinceMillis(mStartOfTurn);

            if (mCurrentTurnDuration >= mTurnLength) {
                mCurrentGameState = GameState.GameOver;
            } else {
                GameGestureListener.FlingDirection flingDirection = mGestureListener.consumeFling();
                attemptToFling(flingDirection);

                if (mFlinging) {
                    long timeSinceFlingStart = TimeUtils.timeSinceMillis(mFlingStartTime);
                    if (timeSinceFlingStart < FLING_TIME) {
                        /*mBallPosition[0] = mFlingStartLocation[0]
                                + (TimeUtils.timeSinceMillis(mFlingStartTime) * mFlingVelocity[0]);
                        mBallPosition[1] = mFlingStartLocation[1]
                                + (TimeUtils.timeSinceMillis(mFlingStartTime) * mFlingVelocity[1]);*/
                    } else {
                        completeFling();
                    }
                }
            }
        }
    }

    /**
     * Draws a game which is active or paused.
     *
     * @param delta delta time
     */
    private void drawActiveGame(float delta) {
        final float wallSize = Wall.getWallSize(mScreenWidth, mScreenHeight);

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        mCurrentBall.draw(mShapeRenderer, mTurnLength, mCurrentTurnDuration);
        Wall.drawWalls(mShapeRenderer, mWallColors, mScreenWidth, mScreenHeight, wallSize);

        mShapeRenderer.end();
    }

    /**
     * Updates the logic for a game that has ended.
     */
    private void tickGameOver() {

    }

    /**
     * Draws a game that has ended.
     *
     * @param delta delta time
     */
    private void drawGameOver(float delta) {

    }

    /**
     * Checks if the user flung the ball and, if yes, starts a fling event.
     *
     * @param flingDirection direction of the fling
     */
    private void attemptToFling(GameGestureListener.FlingDirection flingDirection) {
        // User has already flung ball
        if (mFlinging)
            return;

        final float wallSize = Wall.getWallSize(mScreenWidth, mScreenHeight);
        final float ballSize = Ball.getDefaultBallSize();
        switch (flingDirection) {
            case Left:
                mFlinging = true;
                mFlingTargetLocation[0] = wallSize - ballSize;
                mFlingTargetLocation[1] = mScreenHeight / 2;
                break;
            case Up:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth / 2;
                mFlingTargetLocation[1] = mScreenHeight - wallSize + ballSize;
                break;
            case Right:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth - wallSize + ballSize;
                mFlingTargetLocation[1] = mScreenHeight / 2;
                break;
            case Down:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth / 2;
                mFlingTargetLocation[1] = wallSize - ballSize;
                break;
            default:
                // does nothing
        }

        if (mFlinging) {
            Gdx.app.debug(TAG, "Fling started");
            mFlingStartTime = TimeUtils.millis();
            mFlingStartLocation[0] = mCurrentBall.getX();
            mFlingStartLocation[1] = mCurrentBall.getY();

            mFlingVelocity[0] = (mFlingTargetLocation[0] - mFlingStartLocation[0]) / FLING_TIME;
            mFlingVelocity[1] = (mFlingTargetLocation[1] - mFlingStartLocation[1]) / FLING_TIME;
        }
    }

    /**
     * Resolves a fling and increases a user's score.
     */
    private void completeFling() {
        Gdx.app.debug(TAG, "Fling completed");
        mTotalTurns++;
        mFlinging = false;
        mStartOfTurn = TimeUtils.millis();

        if (mTotalTurns % Wall.NUMBER_OF_TURNS_BEFORE_NEW_COLOR == 0)
            Wall.addWallColorToActive();

        Wall.getRandomWallColors(mRandomGen, mWallColors, mTotalTurns > Wall.NUMBER_OF_TURNS_BEFORE_SAME_WALL_COLORS);

        // Generating new ball at center of screen
        final int randomWall = mRandomGen.nextInt(Wall.NUMBER_OF_WALLS);
        final boolean[] passableWalls = new boolean[Wall.NUMBER_OF_WALLS];
        for (int i = 0; i < passableWalls.length; i++)
            passableWalls[i] = (mWallColors[i].equals(mWallColors[randomWall]));
        mCurrentBall = new Ball(mWallColors[randomWall], passableWalls);

        if (mTotalTurns % NUMBER_OF_TURNS_BEFORE_DECREMENT == 0)
            mTurnLength = Math.max(MINIMUM_TURN_LENGTH, mTurnLength - TURN_LENGTH_DECREMENT);
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
