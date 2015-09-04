package ca.josephroque.swip.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Properties of ball objects on the screen.
 */
public abstract class BasicBall
        extends Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BasicBall";

    /** Number of milliseconds the ball will take to scale up. */
    public static final float BALL_SCALE_TIME = 175f;
    /** Used to determine size of the ball as a percentage of the screen size. */
    private static final float BALL_SIZE_MULTIPLIER = 0.075f;

    /** Default radius of the ball. */
    private static float sDefaultBallRadius;
    /** Indicates if the static ball properties have been initialized. */
    private static boolean sBallsInitialized = false;

    /** Scale for the ball radius, where {@code 1 = sMaximumBallRadius}. */
    private float mScale;
    /** Time in milliseconds that this object was created at. */
    private long mTimeCreated;
    /** Color of the ball. */
    private final Color mBallColor;

    /** Circle which defines the ball's positioning. */
    private Circle mBoundingCircle;

    /**
     * Prepares a new {@code BasicBall} instance.
     *
     * @param color color of the ball
     * @param x horizontal position of the ball
     * @param y vertical position of the ball
     */
    public BasicBall(Color color, float x, float y) {
        if (!sBallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mTimeCreated = TimeUtils.millis();
        mBallColor = color;
        mBoundingCircle = new Circle(x, y, 0);
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        sDefaultBallRadius = Math.min(screenWidth, screenHeight) * BALL_SIZE_MULTIPLIER;
        mBoundingCircle.setRadius(sDefaultBallRadius * mScale * 2);
    }

    /**
     * Evaluates logic of the {@code BasicBall}.
     *
     * @param delta number of seconds the last rendering took
     */
    public void tick(float delta) {
        scale();
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mBallColor);
        shapeRenderer.circle(getX(), getY(), getRadius());
    }

    /**
     * Sets the size of the ball depending on how long it has been on screen.
     */
    private void scale() {
        final long timeSinceCreated = TimeUtils.timeSinceMillis(mTimeCreated);
        if (timeSinceCreated < BALL_SCALE_TIME)
            mScale = Math.min(1f, Math.max(0f, timeSinceCreated / BALL_SCALE_TIME));
        else
            mScale = 1f;
    }

    /**
     * Causes the ball to shrink.
     */
    public void shrink() {

    }

    /**
     * Returns the radius of this ball.
     *
     * @return radius of the bounds
     */
    public float getRadius() {
        return mBoundingCircle.radius;
    }

    @Override
    public float getX() {
        return mBoundingCircle.x;
    }

    @Override
    public float getY() {
        return mBoundingCircle.y;
    }

    @Override
    public float getWidth() {
        return mBoundingCircle.radius;
    }

    @Override
    public float getHeight() {
        return mBoundingCircle.radius;
    }

    @Override
    public Circle getBounds() {
        return mBoundingCircle;
    }

    @Override
    public void updatePosition(float delta) {
        mBoundingCircle.x += getXVelocity() * delta;
        mBoundingCircle.y += getYVelocity() * delta;
    }

    /**
     * Initializes static values common for all balls. Must be called before creating any instances of this object, and
     * should be called any time the screen is resized.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public static void initialize(int screenWidth, int screenHeight) {
        sDefaultBallRadius = Math.min(screenWidth, screenHeight) * BALL_SIZE_MULTIPLIER;
        sBallsInitialized = true;
    }
}
