package ca.josephroque.swip.manager;

import ca.josephroque.swip.screen.GameScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages drawing of backgrounds.
 */
public class BackgroundManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BackgroundManager";

    /** Size of a single background panel relative to the size of the screen. */
    private static final float BACKGROUND_SIZE_MULTIPLIER = 0.15f;

    /** The current background, chosen by the user. */
    private TextureManager.Background mCurrentBackground;

    /** Handles loading and unloading of textures. */
    private TextureManager mTextureManager;

    /** Size of a single panel. */
    private float mBackgroundSize = 0f;
    /** Number of background panel columns. */
    private int mBackgroundColumns = 0;
    /** Number of background panel rows. */
    private int mBackgroundRows = 0;

    /**
     * Sets up properties of background textures.
     *
     * @param textureManager instance of texture manager
     */
    public BackgroundManager(TextureManager textureManager) {
        mTextureManager = textureManager;
        resize(GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
        setBackground(TextureManager.Background.Default);
    }

    /**
     * Draws the background panels to fill the background of the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    public void draw(SpriteBatch spriteBatch) {
        for (int x = 0; x < mBackgroundColumns; x++) {
            for (int y = 0; y < mBackgroundRows; y++) {
                spriteBatch.draw(mTextureManager.getBackgroundTexture(mCurrentBackground),
                        x * mBackgroundSize,
                        y * mBackgroundSize,
                        mBackgroundSize,
                        mBackgroundSize);
            }
        }
    }

    /**
     * Sets a new background for the game.
     *
     * @param bg new background
     */
    public void setBackground(TextureManager.Background bg) {
        mCurrentBackground = bg;
    }

    /**
     * Calculates size of the background based on the size of the screen.
     *
     * @param width width of the screen
     * @param height height of the screen
     */
    public void resize(int width, int height) {
        mBackgroundSize = Math.min(width, height) * BACKGROUND_SIZE_MULTIPLIER;
        mBackgroundColumns = (int) (width / mBackgroundSize);
        mBackgroundRows = (int) (height / mBackgroundSize);
    }

    /**
     * Frees resources being used by the background manager.
     */
    public void dispose() {
        // does nothing right now
    }
}
