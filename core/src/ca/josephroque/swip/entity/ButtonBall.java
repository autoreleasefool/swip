package ca.josephroque.swip.entity;

import ca.josephroque.swip.game.GameTexture;
import ca.josephroque.swip.input.GameInputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Balls for offering button interactions.
 */
public class ButtonBall
        extends BasicBall {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "ButtonBall";

    /** Icon of the button. */
    private TextureRegion mButtonIcon;

    /**
     * Prepares a new ball object.
     *
     * @param ballColor color of the ball
     * @param buttonIcon icon for the button
     * @param x starting horizontal position of the ball
     * @param y starting vertical position of the ball
     */
    public ButtonBall(GameTexture.GameColor ballColor, TextureRegion buttonIcon, float x, float y) {
        super(ballColor, x, y);
    }

    /**
     * Checks to see if the user clicked the ball.
     *
     * @param gameInput player's input events
     * @return {@code true} if the user clicked within the bounds of the button.
     */
    public boolean wasClicked(GameInputProcessor gameInput) {
        return gameInput.wasFingerJustReleased() && getBounds().contains(gameInput.getLastFingerX(),
                gameInput.getLastFingerY());
    }

    /**
     * Draws the ball and its icon to the screen.
     *
     * @param spriteBatch graphics context to draw to
     * @param gameTexture textures for game objects
     */
    public void draw(SpriteBatch spriteBatch, GameTexture gameTexture) {
        super.draw(spriteBatch, gameTexture);

        if (mButtonIcon != null)
            drawIcon(spriteBatch);
    }

    /**
     * Draws the button's icon over top of the ball.
     *
     * @param spriteBatch graphics context to draw to
     */
    private void drawIcon(SpriteBatch spriteBatch) {
        spriteBatch.draw(mButtonIcon, getX() - getRadius(), getY() - getRadius(), getWidth(), getHeight());
    }
}
