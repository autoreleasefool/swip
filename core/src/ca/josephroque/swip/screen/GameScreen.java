package ca.josephroque.swip.screen;

import ca.josephroque.swip.manager.GameManager;
import ca.josephroque.swip.manager.TextureManager;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.manager.MenuManager;
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

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Current state of the application. */
    private GameState mGameState;
    /** State of the application prior to it being paused. */
    private GameState mPausedState;

    /** Handles game logic and rendering. */
    private GameManager mGameManager;
    /** Handles main menu events and rendering. */
    private MenuManager mMenuManager;

    /** Callback interface for main menu events. */
    private MenuManager.MenuCallback mMenuCallback = new MenuManager.MenuCallback() {
        @Override
        public void setMusicEnabled(boolean enabled) {
            // TODO: enable or disable music
        }

        @Override
        public void setSoundEffectsEnabled(boolean enabled) {
            // TODO: enable or disable sound effects
        }

        @Override
        public void startGame() {
            if (mGameManager != null)
                mGameManager.startGame();
        }

        @Override
        public void resumeGame() {
            setState(mPausedState);
        }
    };

    /** Callback interface for game events. */
    private GameManager.GameCallback mGameCallback = new GameManager.GameCallback() {
        @Override
        public void startGame() {
            setState(GameState.GamePlaying);
        }

        @Override
        public void pauseGame() {
            setState(GameState.GamePaused);
        }

        @Override
        public void endGame() {
            setState(GameState.Ended);
        }
    };

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
        TextureManager.initialize();

        // Creating gesture handler
        mGameInput = new GameInputProcessor();
        Gdx.input.setInputProcessor(mGameInput);

        // Setting up the game and menu
        mGameManager = new GameManager(mGameCallback, mScreenWidth, mScreenHeight);
        mMenuManager = new MenuManager(mMenuCallback, mScreenWidth, mScreenHeight);

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
        // Disposes resources being used by instances
        mSpriteBatch.dispose();
        TextureManager.dispose();
        mGameManager.dispose();
        mMenuManager.dispose();

        // Removes references
        mSpriteBatch = null;
        mGameManager = null;
        mMenuManager = null;
    }

    /**
     * Updates the game's objects.
     *
     * @param delta number of seconds the last rendering took
     */
    private void tick(float delta) {
        switch (mGameState) {
            case MainMenu:
                mMenuManager.tick(mGameState, mGameInput, delta);
                break;
            case GameStarting:
            case GamePlaying:
            case GamePaused:
                mGameManager.tick(mGameState, mGameInput, delta);
                break;
            case Ended:
                if (Gdx.input.justTouched()) {
                    mGameManager.prepareNewGame();
                    setState(GameState.GameStarting);
                }
                break;
            default:
                throw new IllegalStateException("invalid game state.");
        }

        // Clear up input
        mGameInput.tick();
    }

    /**
     * Draws the game to the screen.
     */
    private void draw() {
        mSpriteBatch.setProjectionMatrix(mPrimaryCamera.combined);
        mSpriteBatch.begin();

        mGameManager.draw(mGameState, mSpriteBatch);

        switch (mGameState) {
            case MainMenu:
                mMenuManager.draw(mGameState, mSpriteBatch);
                break;
            case GameStarting:
            case GamePlaying:
            case GamePaused:
                break;
            case Ended:
                break;
            default:
                throw new IllegalStateException("invalid game state.");
        }

        mSpriteBatch.begin();
    }

    /**
     * Changes the state of the application.
     *
     * @param newState new state
     */
    public void setState(GameState newState) {
        if (newState == GameState.GamePaused)
            mPausedState = mGameState;
        mGameState = newState;
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
