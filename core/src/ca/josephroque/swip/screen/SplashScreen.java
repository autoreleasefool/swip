package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Displays an image to the user while the bulk of the application is loaded.
 */
public class SplashScreen implements Screen {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SplashScreen";

    /** Minimum number of seconds the splash screen should be shown for. */
    private static final float MINIMUM_SPLASH_TIME = 3f;

    /** Width of the screen. */
    private static int sScreenWidth;
    /** Height of the screen. */
    private static int sScreenHeight;

    /** Instance of the game. */
    private SwipGame mSwipGame;

    /** Allows rendering of graphics on the screen. */
    private SpriteBatch mSpriteBatch;
    /** Primary camera of the game. */
    private OrthographicCamera mPrimaryCamera;
    /** Default viewport of the game. */
    private Viewport mPrimaryViewport;

    /** Indicates if the process to preload assets has begun. */
    private boolean mPreloadInitiated;
    /** Indicates if the process to preload assets has concluded. */
    private boolean mPreloadCompleted;

    /** Number of seconds the splash screen has been shown for. */
    private float mSplashTime;

    /** Instance of callback interface when preload process has completed. */
    private GameScreen.PreloadCallback mPreloadCallback = new GameScreen.PreloadCallback() {
        @Override
        public void preloadCompleted() {
            mPreloadCompleted = true;
        }
    };

    /**
     * Stores references to parameters.
     *
     * @param game instance of the game
     */
    public SplashScreen(SwipGame game) {
        mSwipGame = game;
    }

    @Override
    public void show() {
        sScreenWidth = Gdx.graphics.getWidth();
        sScreenHeight = Gdx.graphics.getHeight();

        // Setting up the game rendering
        mPrimaryCamera = new OrthographicCamera();
        mPrimaryCamera.translate(sScreenWidth / 2, sScreenHeight / 2);
        mPrimaryCamera.setToOrtho(false, sScreenWidth, sScreenHeight);
        mPrimaryViewport = new ScreenViewport(mPrimaryCamera);
        mPrimaryViewport.apply();

        // Preparing UI objects
        mSpriteBatch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        mPrimaryCamera.update();
        tick(delta);

        // Clear the screen to white
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        draw();
    }

    /**
     * Checks if the game objects have finished loading yet.
     *
     * @param delta number of seconds the last rendering took
     */
    private void tick(float delta) {
        if (!mPreloadInitiated) {
            mPreloadInitiated = true;
            GameScreen.preloadAssets(mPreloadCallback);
        }

        mSplashTime += delta;
        if (mSplashTime >= MINIMUM_SPLASH_TIME && mPreloadCompleted) {
            mPreloadCompleted = false;
            mSwipGame.showGameScreen();
        }
    }

    /**
     * Draws the splash image to the screen.
     */
    private void draw() {
        mSpriteBatch.setProjectionMatrix(mPrimaryCamera.combined);
        mSpriteBatch.begin();

        // TODO: draw splash screen

        mSpriteBatch.end();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        sScreenWidth = width;
        sScreenHeight = height;
        mPrimaryViewport.update(width, height);
    }

    @Override
    public void dispose() {
        mSpriteBatch.dispose();
    }
}
