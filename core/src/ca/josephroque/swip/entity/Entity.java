package ca.josephroque.swip.entity;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

/**
 * Objects which can appear in the game.
 */
public abstract class Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Entity";

    /** Velocity of the entity. */
    private Vector2 mVelocity;

    /**
     * Initializes a new entity.
     */
    public Entity() {
        mVelocity = new Vector2();
    }

    /**
     * Evaluates the entity's logic.
     *
     * @param delta number of seconds the last rendering took
     */
    public abstract void tick(float delta);


    /**
     * Gets the entity's horizontal position.
     *
     * @return horizontal position
     */
    public abstract float getX();

    /**
     * Gets the entity's vertical position.
     *
     * @return vertical position
     */
    public abstract float getY();

    /**
     * Gets the entity's width.
     *
     * @return width of the entity
     */
    public abstract float getWidth();

    /**
     * Gets the entity's height.
     *
     * @return height of the entity
     */
    public abstract float getHeight();

    /**
     * Gets a bounding box for the entity.
     *
     * @return bounding box
     */
    public abstract Shape2D getBounds();

    /**
     * Updates the position of the entity based on its velocity.
     *
     * @param delta number of seconds the last rendering took
     */
    public abstract void updatePosition(float delta);

    /**
     * Gets the entity's horizontal velocity.
     *
     * @return x velocity
     */
    public float getXVelocity() {
        return mVelocity.x;
    }

    /**
     * Gets the entity's vertical velocity.
     *
     * @return y velocity
     */
    public float getYVelocity() {
        return mVelocity.y;
    }

    /**
     * Updates the entity's moving velocity.
     *
     * @param velocity new velocity
     */
    void setVelocity(Vector2 velocity) {
        mVelocity.set(velocity);
    }
}
