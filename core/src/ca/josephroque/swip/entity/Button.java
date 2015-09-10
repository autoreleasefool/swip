package ca.josephroque.swip.entity;

import ca.josephroque.swip.input.GameInputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Objects on the screen which provide methods of user interaction.
 */
public class Button
        extends Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Button";

    /** Icon of the button which represents the action it performs. */
    private TextureRegion mIconTexture;

    /** Rectangle which defines the interaction bounds of the object. */
    private final Rectangle mBoundingBox;

    /**
     * Creates a new {@code Button} with the provided parameters.
     *
     * @param textureRegion icon of the button
     * @param x horizontal location
     * @param y vertical location
     * @param width button width
     * @param height button height
     */
    public Button(TextureRegion textureRegion, float x, float y, float width, float height) {
        mBoundingBox = new Rectangle(x, y, width, height);
    }

    /**
     * Checks to see if the user clicked the button.
     *
     * @param gameInput player's input events
     * @return {@code true} if the user clicked within the bounds of the button.
     */
    public boolean wasClicked(GameInputProcessor gameInput) {
        return gameInput.clickOccurred() && getBounds().contains(gameInput.getLastFingerX(),
                gameInput.getLastFingerY());
    }

    /**
     * Draws the button's icon to the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(mIconTexture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void tick(float delta) {
        // does nothing
    }

    @Override
    public float getX() {
        return getBounds().getX();
    }

    @Override
    public float getY() {
        return getBounds().getY();
    }

    @Override
    public float getWidth() {
        return getBounds().getWidth();
    }

    @Override
    public float getHeight() {
        return getBounds().getHeight();
    }

    @Override
    public Rectangle getBounds() {
        return mBoundingBox;
    }

    @Override
    public void updatePosition(float delta) {
        // does nothing
    }
}
