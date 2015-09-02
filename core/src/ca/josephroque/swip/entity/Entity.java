package ca.josephroque.swip.entity;

/**
 * Objects which can appear in the game.
 */
public abstract class Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Entity";

    /** Horizontal position of the ball. */
    private float mX;
    /** Vertical position of the ball. */
    private float mY;
    /** Horizontal velocity of the ball. */
    private float mDeltaX;
    /** Vertical velocity of the ball. */
    private float mDeltaY;

    /**
     * Updates the logic of the entity.
     *
     * @param delta number of seconds the previous time lasted
     */
    public abstract void tick(float delta);

    /**
     * Gets the entity's horizontal position.
     *
     * @return {@code mX}
     */
    public float getX() {
        return mX;
    }

    /**
     * Gets the entity's vertical position.
     *
     * @return {@code mY}
     */
    public float getY() {
        return mY;
    }

    /**
     * Gets the entity's horizontal velocity.
     *
     * @return {@code mDeltaX}
     */
    public float getDeltaX() {
        return mDeltaX;
    }

    /**
     * Gets the entity's vertical velocity.
     *
     * @return {@code mDeltaY}
     */
    public float getDeltaY() {
        return mDeltaY;
    }

    /**
     * Updates the position of the ball by the velocities based on the {@code delta}.
     *
     * @param delta number of seconds the previous time lasted
     */
    void updatePosition(float delta) {
        mX += mDeltaX * delta;
        mY += mDeltaY * delta;
    }

    /**
     * Sets a new value for the entity's horizontal position.
     *
     * @param x new horizontal position
     */
    void setX(float x) {
        mX = x;
    }

    /**
     * Sets a new value for the entity's vertical position.
     *
     * @param y new vertical position
     */
    void setY(float y) {
        mY = y;
    }

    /**
     * Set a new value for the entity's horizontal velocity.
     *
     * @param deltaX new horizontal velocity
     */
    void setDeltaX(float deltaX) {
        mDeltaX = deltaX;
    }

    /**
     * Set a new value for the entity's vertical velocity.
     *
     * @param deltaY new vertical velocity
     */
    void setDeltaY(float deltaY) {
        mDeltaY = deltaY;
    }
}
