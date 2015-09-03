package ca.josephroque.swip.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Objects which can appear in the game.
 */
public abstract class Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Entity";

    /** Rectangular bounds of the object. */
    private Rectangle mBoundingBox;
    /** Velocity of the entity. */
    private Vector2 mVelocity;

    /**
     * Creates a bounding box {@link com.badlogic.gdx.math.Rectangle} using the parameters.
     *
     * @param x horizontal position
     * @param y vertical position
     * @param width width
     * @param height height
     */
    public Entity(float x, float y, float width, float height) {
        mBoundingBox = new Rectangle(x, y, width, height);
        mVelocity = new Vector2();
    }

    /**
     * Updates the logic of the entity.
     *
     * @param delta number of seconds the previous time lasted
     */
    public abstract void tick(float delta);

    /**
     * Resizes the object relative to the screen dimensions.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public abstract void resize(int screenWidth, int screenHeight);

    /**
     * Gets the entity's horizontal position.
     *
     * @return horizontal position of bounding box
     */
    public float getX() {
        return mBoundingBox.getX();
    }

    /**
     * Gets the entity's vertical position.
     *
     * @return vertical position of bounding box
     */
    public float getY() {
        return mBoundingBox.getY();
    }

    /**
     * Gets the entity's width.
     *
     * @return {@code mBoundingBox.getWidth()}
     */
    public float getWidth() {
        return mBoundingBox.getWidth();
    }

    /**
     * Gets the entity's height.
     *
     * @return {@code mBoundingBox.getHeight()}
     */
    public float getHeight() {
        return mBoundingBox.getHeight();
    }

    /**
     * Gets the entity's horizontal velocity.
     *
     * @return x velocity
     */
    public float getHorizVelocity() {
        return mVelocity.x;
    }

    /**
     * Gets the entity's vertical velocity.
     *
     * @return y velocity
     */
    public float getVertVelocity() {
        return mVelocity.y;
    }

    /**
     * Gets the bounding box of the object. Can be used to manipulate the object's position and dimensions.
     *
     * @return {@code mBoundingBox}
     */
    public Rectangle getBoundingBox() {
        return mBoundingBox;
    }

    /**
     * Updates the position of the ball by the velocities based on the {@code delta}.
     *
     * @param delta number of seconds the previous tick lasted
     */
    void updatePosition(float delta) {
        mBoundingBox.x += mVelocity.x;
        mBoundingBox.y += mVelocity.y;
    }
}
