package ca.josephroque.swip.screen;

import ca.josephroque.swip.game.GameManager;
import ca.josephroque.swip.game.GameTexture;
import ca.josephroque.swip.input.GameInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Provides default methods for creating a {@link com.badlogic.gdx.Screen}.
 */
public class GameScreen
        implements Screen {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameScreen";

    /** Allows rendering of graphics on the screen. */
    private SpriteBatch mSpriteBatch;
    /** Primary camera of the game. */
    private OrthographicCamera mPrimaryCamera;
    /** Default viewport of the game. */
    private Viewport mPrimaryViewport;

    /** Handles gesture input events. */
    private GameInputProcessor mGameInput;
    /** Manages textures for game objects. */
    private GameTexture mGameTextures;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Current state of the application. */
    private GameState mGameState;

    /** Handles game logic and rendering. */
    private GameManager mGameManager;

    @Override
    public void render(float delta) {
        mPrimaryCamera.update();
        tick(delta);

        // Clear the screen to white
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        draw();
    }

    @Override
    public void show() {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        // Setting up the game rendering
        mPrimaryCamera = new OrthographicCamera();
        mPrimaryCamera.translate(mScreenWidth / 2, mScreenHeight / 2);
        mPrimaryCamera.setToOrtho(false, mScreenWidth, mScreenHeight);
        mPrimaryViewport = new ScreenViewport(mPrimaryCamera);
        mPrimaryViewport.apply();

        // Preparing UI objects
        mSpriteBatch = new SpriteBatch();
        mGameTextures = new GameTexture();

        // Creating gesture handler
        mGameInput = new GameInputProcessor();
        Gdx.input.setInputProcessor(mGameInput);

        // Setting up the game
        mGameManager = new GameManager(mGameTextures, mScreenWidth, mScreenHeight);

        // Displaying the main menu
        setState(GameState.MainMenu);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
        // does nothing
    }

    @Override
    public void resume() {
        // does nothing
    }

    @Override
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        mGameInput.resize(width, height);
        mPrimaryViewport.update(width, height);
        mGameManager.resize(width, height);
    }

    @Override
    public void dispose() {
        mSpriteBatch.dispose();
        mGameTextures.dispose();
    }

    /**
     * Updates the game's objects.
     *
     * @param delta number of seconds the last rendering took
     */
    private void tick(float delta) {
        switch (mGameState) {
            case MainMenu:
                tickMainMenu(delta);
                break;
            case GameStarting:
            case GamePlaying:
            case GamePaused:
                mGameManager.tick(mGameState, mGameInput, delta);
                break;
            case Ended:
                tickGameEnded(delta);
                break;
            default:
                throw new IllegalStateException("invalid game state.");
        }

        if (mGameManager.shouldStartGame()) {
            mGameManager.startGame();
            setState(GameState.GamePlaying);
        } else if (mGameManager.isGameOver()) {
            setState(GameState.Ended);
        }

        // Clear up input
        mGameInput.tick();
    }

    /**
     * Draws the game to the screen.
     */
    private void draw() {
        mSpriteBatch.setProjectionMatrix(mPrimaryCamera.combined);

        mGameManager.draw(mGameState, mSpriteBatch, mGameTextures);

        switch (mGameState) {
            case MainMenu:
                drawMainMenu();
                break;
            case GameStarting:
            case GamePlaying:
            case GamePaused:
                break;
            case Ended:
                drawEndScreen();
                break;
            default:
                throw new IllegalStateException("invalid game state.");
        }
    }

    /**
     * Update the main menu.
     *
     * @param delta number of seconds the last rendering took
     */
    private void tickMainMenu(float delta) {
        if (Gdx.input.justTouched()) {
            mGameManager.prepareNewGame();
            setState(GameState.GameStarting);
        }
    }

    /**
     * Update the end screen.
     *
     * @param delta number of seconds the last rendering took
     */
    private void tickGameEnded(float delta) {
        if (Gdx.input.justTouched()) {
            mGameManager.prepareNewGame();
            setState(GameState.GameStarting);
        }
    }

    /**
     * Draws the main menu.
     */
    private void drawMainMenu() {

    }

    /**
     * Draws the end screen.
     */
    private void drawEndScreen() {

    }

    /**
     * Changes the state of the application.
     *
     * @param newState new state
     */
    public void setState(GameState newState) {
        this.mGameState = newState;
    }

    /**
     * Possible states of the application.
     */
    public enum GameState {
        /** Represents the application being in a main menu. */
        MainMenu,
        /** Represents the game being a startup state. */
        GameStarting,
        /** Represents the game being in an active state - i.e. being played. */
        GamePlaying,
        /** Represents the game being in a paused state. */
        GamePaused,
        /** Represents the game being over. */
        Ended,
    }
}
