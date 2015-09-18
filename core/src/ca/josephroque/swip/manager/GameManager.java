package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.BasicBall;
import ca.josephroque.swip.entity.Button;
import ca.josephroque.swip.entity.GameBall;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.screen.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

/**
 * Manages game objects and rendering them to the screen.
 */
public class GameManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameManager";

    /** Number of seconds that turns initially last. */
    private static final float INITIAL_TURN_LENGTH = 10f; // TODO: change to actual start value, 1.2f
    /** Number of seconds to subtract from the length of a turn at a time. */
    private static final float TURN_LENGTH_DECREMENT = 0.05f;
    /** Number of turns that must pass before the turn length is decremented. */
    private static final int TURNS_BEFORE_DECREMENT = 10;
    /** Shortest number of seconds that a turn can last. */
    private static final float MINIMUM_TURN_LENGTH = 0.3f;
    /** Number of seconds until a game starts. */
    private static final float TIME_UNTIL_GAME_STARTS = 1f;

    /** Size of the pause button relative to the screen. */
    private static final float PAUSE_BUTTON_SCALE = 0.15f;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Generates random numbers for the game. */
    private final Random mRandomNumberGenerator = new Random();

    /** Instance of callback interface. */
    private GameCallback mGameCallback;

    /** Time at which the game began, in milliseconds. */
    private float mGameCountdown;

    /** The ball being used by the game. */
    private GameBall mCurrentGameBall;
    /** Button to pause the game. */
    private Button mPauseButton;
    /** The four main walls in the gam. */
    private Wall[] mPrimaryWalls;
    /** Four walls which switch places with the primary walls. */
    private Wall[] mSecondaryWalls;
    /** Indicates if the secondary walls should be drawn. */
    private boolean mDrawSecondaryWalls;
    /** Colors of the four walls. */
    private final TextureManager.GameColor[] mWallColors;

    /** Length of a single turn. */
    private float mTurnLength;
    /** Number of milliseconds that have passed since this turn began. */
    private float mTurnDuration;
    /** Total number of turns that have passed since the game began (i.e. the player's score). */
    private int mTotalTurns;

    /** Number of secondary walls which have finished animating. */
    private int mWallsFinishedAnimating;
    /** Replaces primary walls with secondary walls when the secondary walls finish animating. */
    @SuppressWarnings("FieldCanBeLocal")
    private Wall.TranslationCompleteListener mWallTranslationListener = new Wall.TranslationCompleteListener() {
        @Override
        public void onTranslationCompleted(Wall wall) {
            for (int i = 0; i < Wall.NUMBER_OF_WALLS; i++) {
                if (mSecondaryWalls[i] == wall)
                    mWallsFinishedAnimating++;
            }

            // If all four walls have finished their animation
            if (mWallsFinishedAnimating == Wall.NUMBER_OF_WALLS) {
                mWallsFinishedAnimating = 0;
                mDrawSecondaryWalls = false;
                Wall[] temp = mPrimaryWalls;
                mPrimaryWalls = mSecondaryWalls;
                mSecondaryWalls = temp;
            }
        }
    };

    /**
     * Sets up a new game manager.
     *
     * @param callback instance of callback interface
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public GameManager(GameCallback callback, int screenWidth, int screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mGameCallback = callback;

        Wall.initialize(screenWidth, screenHeight);
        mWallColors = new TextureManager.GameColor[Wall.NUMBER_OF_WALLS];

        // Randomizing colors for initial walls
        Wall.getRandomWallColors(mRandomNumberGenerator, mWallColors, false);
        mPrimaryWalls = new Wall[Wall.NUMBER_OF_WALLS];
        mSecondaryWalls = new Wall[Wall.NUMBER_OF_WALLS];
        for (int i = 0; i < Wall.NUMBER_OF_WALLS; i++) {
            mPrimaryWalls[i] = new Wall(i, mWallColors[i], mScreenWidth, mScreenHeight);
            mPrimaryWalls[i].setTranslationCompleteListener(mWallTranslationListener);
            mSecondaryWalls[i] = new Wall(i, mWallColors[i], mScreenWidth, mScreenHeight);
            mSecondaryWalls[i].setTranslationCompleteListener(mWallTranslationListener);
        }

        final float pauseButtonSize = Math.min(mScreenWidth, mScreenHeight) * PAUSE_BUTTON_SCALE;
        mPauseButton = new Button(TextureManager.getSystemIconTexture(TextureManager.SystemIcon.Pause),
                0,
                mScreenHeight - pauseButtonSize,
                pauseButtonSize,
                pauseButtonSize);
    }

    /**
     * Updates the logic of the game based on the current state.
     *
     * @param gameState state of the game
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    public void tick(GameScreen.GameState gameState, GameInputProcessor gameInput, float delta) {
        switch (gameState) {
            case GameStarting:
                tickGameStarting(gameInput, delta);
                break;
            case GamePlaying:
                tickGamePlaying(gameInput, delta);
                break;
            case GamePaused:
                tickGamePaused(gameInput, delta);
                break;
            default:
                throw new IllegalArgumentException("invalid game state.");
        }
    }

    /**
     * Updates a game which is starting.
     *
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    private void tickGameStarting(GameInputProcessor gameInput, float delta) {
        // Counts down timer to start of game
        mGameCountdown += delta;
        if (mGameCountdown >= TIME_UNTIL_GAME_STARTS) {
            startGame();
            if (mGameCallback != null)
                mGameCallback.startGame();
        } else if (mPauseButton.wasClicked(gameInput)) {
            if (mGameCallback != null)
                mGameCallback.pauseGame();
        }

        for (Wall wall: mPrimaryWalls)
            wall.tick(delta);
        if (mDrawSecondaryWalls) {
            for (Wall wall : mSecondaryWalls)
                wall.tick(delta);
        }

        if (mPauseButton.wasClicked(gameInput) && mGameCallback != null)
            mGameCallback.pauseGame();
    }

    /**
     * Updates a game which is being played.
     *
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    private void tickGamePlaying(GameInputProcessor gameInput, float delta) {
        mTurnDuration += delta;

        if (mTurnDuration >= mTurnLength) {
            endGame();
        } else {
            mCurrentGameBall.drag(gameInput);
            mCurrentGameBall.tryToReleaseBall(gameInput);
            mCurrentGameBall.tick(delta, mPrimaryWalls);

            if (mCurrentGameBall.hasPassedThroughWall())
                turnSucceeded();
            else if (mCurrentGameBall.hasHitInvalidWall())
                endGame();
        }

        if (mPauseButton.wasClicked(gameInput) && mGameCallback != null)
            mGameCallback.pauseGame();

        for (Wall wall: mPrimaryWalls)
            wall.tick(delta);
        if (mDrawSecondaryWalls) {
            for (Wall wall : mSecondaryWalls)
                wall.tick(delta);
        }
    }

    /**
     * Updates a game which is paused.
     *
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    private void tickGamePaused(GameInputProcessor gameInput, float delta) {

    }

    /**
     * Draws the game to the screen.
     *
     * @param gameState the current state of the application
     * @param spriteBatch graphics context to draw to
     */
    public void draw(GameScreen.GameState gameState, SpriteBatch spriteBatch) {
        if (mCurrentGameBall != null)
            mCurrentGameBall.draw(spriteBatch, mTurnLength, mTurnDuration);
        for (Wall wall : mPrimaryWalls)
            wall.draw(spriteBatch);
        for (Wall wall : mSecondaryWalls)
            wall.draw(spriteBatch);

        if (gameState == GameScreen.GameState.GamePlaying || gameState == GameScreen.GameState.GameStarting)
            mPauseButton.draw(spriteBatch);
    }

    /**
     * Sets up a new game.
     */
    public void prepareNewGame() {
        mTurnDuration = 0;
        mTurnLength = INITIAL_TURN_LENGTH;
        mTotalTurns = 0;

        mCurrentGameBall = null;
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        // Setting initial properties of entities
        BasicBall.initialize(mScreenWidth, mScreenHeight);
        Wall.initialize(mScreenWidth, mScreenHeight);
        replaceWallsAndBall();
    }

    /**
     * Creates new colors for the walls and updates the color of the ball based on those colors.
     */
    private void replaceWallsAndBall() {
        // Creating the four walls
        int wallPairFirstIndex = Wall.getRandomWallColors(mRandomNumberGenerator,
                mWallColors,
                mTotalTurns > Wall.TURNS_BEFORE_SAME_WALL_COLORS);
        for (int i = 0; i < Wall.NUMBER_OF_WALLS; i++) {
            mSecondaryWalls[i].updateWallColor(mWallColors[i]);
            mSecondaryWalls[i].startTranslation();
        }

        // Generating new ball at center of screen
        mDrawSecondaryWalls = true;
        final int randomWall;
        final boolean[] passableWalls = new boolean[Wall.NUMBER_OF_WALLS];
        if (wallPairFirstIndex == -1) {
            randomWall = mRandomNumberGenerator.nextInt(Wall.NUMBER_OF_WALLS);
            passableWalls[randomWall] = true;
        } else {
            randomWall = wallPairFirstIndex;
            passableWalls[randomWall] = true;
            for (int i = randomWall + 1; i < Wall.NUMBER_OF_WALLS; i++)
                passableWalls[i] = mWallColors[randomWall].equals(mWallColors[i]);
        }

        mCurrentGameBall = new GameBall(mWallColors[randomWall], passableWalls, mScreenWidth / 2, mScreenHeight / 2);
        mCurrentGameBall.grow();
    }

    /**
     * Ends the current game - it has been lost.
     */
    public void endGame() {
        if (mGameCallback != null)
            mGameCallback.endGame(mTotalTurns);
    }

    /**
     * Increases the player's score and starts a new turn.
     */
    private void turnSucceeded() {
        MusicManager.playSoundEffect(MusicManager.SoundEffect.PointEarned);
        mTotalTurns++;
        mTurnDuration = 0;

        if (mTotalTurns % Wall.TURNS_BEFORE_NEW_COLOR == 0)
            Wall.addWallColorToActive();

        replaceWallsAndBall();

        if (mTotalTurns % TURNS_BEFORE_DECREMENT == 0)
            mTurnLength = Math.max(MINIMUM_TURN_LENGTH, mTurnLength - TURN_LENGTH_DECREMENT);
    }

    /**
     * Adjusts the size of the game objects to fit the new screen dimensions.
     *
     * @param width new screen width
     * @param height new screen height
     */
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;

        // Resizing entities on screen
        for (Wall wall : mPrimaryWalls)
            wall.resize(width, height);
        for (Wall wall : mSecondaryWalls)
            wall.resize(width, height);
        if (mCurrentGameBall != null)
            mCurrentGameBall.resize(width, height);
    }

    /**
     * Frees references to objects.
     */
    public void dispose() {
        mGameCallback = null;
    }

    /**
     * Provides callback methods for game interactions.
     */
    public interface GameCallback {
        /**
         * Should start a new game.
         */
        void startGame();

        /**
         * Should pause the current game and display a "pause" menu.
         */
        void pauseGame();

        /**
         * Should end the current game - the player has lost.
         *
         * @param finalScore score the user obtained
         */
        void endGame(int finalScore);
    }
}
