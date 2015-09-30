package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.BasicBall;
import ca.josephroque.swip.entity.Button;
import ca.josephroque.swip.entity.GameBall;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.screen.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private static final float TIME_UNTIL_GAME_STARTS = 2f;

    /** Size of the pause button relative to the screen. */
    private static final float PAUSE_BUTTON_SCALE = 0.15f;

    /** Generates random numbers for the game. */
    private final Random mRandomNumberGenerator = new Random();

    /** Instance of callback interface. */
    private GameCallback mGameCallback;

    /** Time that has passed since the game began≈, in seconds. */
    private float mGameCountdown;
    /** The countdown item which was active in the last frame. */
    private GameCountdown mLastCountdownItem;

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
     */
    public GameManager(GameCallback callback) {
        mGameCallback = callback;

        Wall.initialize(GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
        mWallColors = new TextureManager.GameColor[Wall.NUMBER_OF_WALLS];

        // Getting specific colors for initial walls
        Wall.getDefaultWallColors(mWallColors, 0);
        mPrimaryWalls = new Wall[Wall.NUMBER_OF_WALLS];
        mSecondaryWalls = new Wall[Wall.NUMBER_OF_WALLS];
        for (int i = 0; i < Wall.NUMBER_OF_WALLS; i++) {
            mPrimaryWalls[i] = new Wall(i, mWallColors[i], GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
            mPrimaryWalls[i].setTranslationCompleteListener(mWallTranslationListener);
            mSecondaryWalls[i] = new Wall(i, mWallColors[i], GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
            mSecondaryWalls[i].setTranslationCompleteListener(mWallTranslationListener);
        }

        final float pauseButtonSize = Math.min(GameScreen.getScreenWidth(), GameScreen.getScreenHeight())
                * PAUSE_BUTTON_SCALE;
        mPauseButton = new Button(TextureManager.getSystemIconTexture(TextureManager.SystemIcon.Pause),
                0,
                GameScreen.getScreenHeight() - pauseButtonSize,
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
        } else {
            GameCountdown countdownItem = GameCountdown.getCountdownItem(mGameCountdown / TIME_UNTIL_GAME_STARTS);
            if (mLastCountdownItem != null
                    && mLastCountdownItem != countdownItem) {
                replaceWallsAndBall(countdownItem);
            }
            mLastCountdownItem = countdownItem;

            if (mPauseButton.wasClicked(gameInput)) {
                if (mGameCallback != null)
                    mGameCallback.pauseGame();
            }
        }

        for (Wall wall : mPrimaryWalls)
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

        for (Wall wall : mPrimaryWalls)
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
        if (mDrawSecondaryWalls) {
            for (Wall wall : mSecondaryWalls)
                wall.draw(spriteBatch);
        }

        switch (gameState) {
            case GamePlaying:
                mPauseButton.draw(spriteBatch);
                break;
            case GameStarting:
                mPauseButton.draw(spriteBatch);
                float countdownPosition = mGameCountdown / TIME_UNTIL_GAME_STARTS;
                TextureRegion countdownIcon
                        = TextureManager.getCountdownTexture(GameCountdown.getCountdownItem(countdownPosition));
                float sizeRatio = countdownIcon.getRegionWidth() / (float) countdownIcon.getRegionHeight();
                spriteBatch.draw(countdownIcon,
                        GameScreen.getScreenWidth() / 2 - BasicBall.getDefaultBallRadius() * sizeRatio,
                        GameScreen.getScreenHeight() / 2 - BasicBall.getDefaultBallRadius(),
                        BasicBall.getDefaultBallRadius() * 2 * sizeRatio,
                        BasicBall.getDefaultBallRadius() * 2);
                break;
            default:
                // does nothing - no more to draw
        }
    }

    /**
     * Sets up a new game.
     */
    public void prepareNewGame() {
        mTurnDuration = 0;
        mTurnLength = INITIAL_TURN_LENGTH;
        mTotalTurns = 0;
        mGameCountdown = 0;

        mCurrentGameBall = null;
    }

    /**
     * Starts the game.
     */
    public void startGame() {
        // Setting initial properties of entities
        BasicBall.initialize(GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
        Wall.initialize(GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
        replaceWallsAndBall();
    }

    /**
     * Creates new colors for the walls and updates the color of the ball based on those colors.
     */
    private void replaceWallsAndBall() {
        replaceWallsAndBall(null);
    }

    /**
     * Creates new colors for the walls and updates the color of the ball based on those colors.
     *
     * @param countdown if not null, then the colors of the walls will be the four default colors in an orientation
     * based on the {@code countdown.ordinal()}
     */
    private void replaceWallsAndBall(GameCountdown countdown) {
        // Creating the four walls
        int wallPairFirstIndex;
        if (countdown == null) {
            wallPairFirstIndex = Wall.getRandomWallColors(mRandomNumberGenerator,
                    mWallColors,
                    mTotalTurns > Wall.TURNS_BEFORE_SAME_WALL_COLORS);
        } else {
            wallPairFirstIndex = Wall.getDefaultWallColors(mWallColors, countdown.ordinal());
        }

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

        mCurrentGameBall = new GameBall(mWallColors[randomWall],
                passableWalls,
                GameScreen.getScreenWidth() / 2,
                GameScreen.getScreenHeight() / 2);
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
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void resize(int screenWidth, int screenHeight) {
        // Resizing entities on screen
        for (Wall wall : mPrimaryWalls)
            wall.resize(screenWidth, screenHeight);
        for (Wall wall : mSecondaryWalls)
            wall.resize(screenWidth, screenHeight);
        if (mCurrentGameBall != null)
            mCurrentGameBall.resize(screenWidth, screenHeight);
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

    /**
     * Icons which represent the countdown before a game begins.
     */
    public enum GameCountdown {
        /** Item which represents a 3 in the countdown. */
        Three,
        /** Item which represents a 2 in the countdown. */
        Two,
        /** Item which represents a 1 in the countdown. */
        One,
        /** Item which represents GO! in the countdown. */
        Go;

        /** SIze of the enum. */
        private static final int SIZE = GameCountdown.values().length;

        /**
         * Gets the size of the enum.
         *
         * @return number of {@code GameCountdown}s
         */
        public static int getSize() {
            return SIZE;
        }

        /**
         * Gets a countdown item based on the total percentage of the countdown which has passed.
         *
         * @param percentage a percentage from 0 to 1
         * @return the countdown item
         */
        @SuppressWarnings("CheckStyle")
        public static GameCountdown getCountdownItem(float percentage) {
            if (percentage < 0.25f)
                return Three;
            else if (percentage < 0.5f)
                return Two;
            else if (percentage < 0.75f)
                return One;
            else
                return Go;
        }
    }
}
