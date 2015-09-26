package ca.josephroque.swip.entity;

import ca.josephroque.swip.manager.TextureManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

/**
 * Properties of ball objects on the screen.
 */
public abstract class BasicBall
        extends Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "BasicBall";

    /** Number of milliseconds the ball will take to scale up. */
    public static final float BALL_SCALE_TIME = 0.175f;
    /** Used to determine size of the ball as a percentage of the screen size. */
    private static final float BALL_SIZE_MULTIPLIER = 0.075f;

    /** Default radius of the ball. */
    private static float sDefaultBallRadius;
    /** Indicates if the static ball properties have been initialized. */
    private static boolean sBallsInitialized = false;

    /** Scale for the ball radius, where {@code 1 = sMaximumBallRadius}. */
    private float mScale;
    /** Number of milliseconds the ball has been scaling for. */
    private float mScaleTime = BALL_SCALE_TIME;
    /** {@code True} if the ball should be growing if it is scaling, {@code false} if it should be shrinking. */
    private boolean mGrowingOrShrinking;
    /** Indicates if the ball has been hidden and should not scale. */
    private boolean mHidden;
    /** Indicates if the ball's scaling animation has not completed before and the listener should be invoked. */
    private boolean mScalingCompleted;

    /** Color of the ball. */
    private final TextureManager.GameColor mBallColor;
    /** Callback interface for completion or interruption of scaling. */
    private ScalingCompleteListener mScalingListener;

    /** Circle which defines the ball's positioning. */
    private Circle mBoundingCircle;

    /**
     * Prepares a new {@code BasicBall} instance. New balls will begin to grow when they are created.
     *
     * @param color color of the ball
     * @param x horizontal position of the ball
     * @param y vertical position of the ball
     */
    public BasicBall(TextureManager.GameColor color, float x, float y) {
        if (!sBallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mBallColor = color;
        mBoundingCircle = new Circle(x, y, 0);
    }

    /**
     * Adjust the size of the object relative to the screen dimensions.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void resize(int screenWidth, int screenHeight) {
        sDefaultBallRadius = Math.min(screenWidth, screenHeight) * BALL_SIZE_MULTIPLIER;
        mBoundingCircle.setRadius(sDefaultBallRadius * mScale);
    }

    /**
     * Evaluates logic of the {@code BasicBall}.
     *
     * @param delta number of seconds the last rendering took
     */
    public void tick(float delta) {
        scale(delta);
    }

    /**
     * Draws the ball to the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    public void draw(SpriteBatch spriteBatch) {
        if (isHidden())
            return;

        spriteBatch.draw(TextureManager.getBallTexture(mBallColor),
                getX() - getRadius(),
                getY() - getRadius(),
                getWidth(),
                getHeight());
    }

    /**
     * Sets the size of the ball depending on how long it has been on screen.
     *
     * @param delta number of seconds the last rendering took
     */
    private void scale(float delta) {
        if (isHidden())
            return;

        mScaleTime += delta;
        if (isScaling()) {
            mScale = (mGrowingOrShrinking)
                    ? Math.min(1f, Math.max(0f, mScaleTime / BALL_SCALE_TIME))
                    : Math.max(0f, Math.min(1f, (-mScaleTime + BALL_SCALE_TIME) / BALL_SCALE_TIME));
        } else {
            mScale = (mGrowingOrShrinking)
                    ? 1f
                    : 0f;
            if (!mScalingCompleted) {
                mScalingCompleted = true;
                if (mScalingListener != null)
                    mScalingListener.onScalingCompleted(this, mGrowingOrShrinking);
            }
        }

        mBoundingCircle.setRadius(sDefaultBallRadius * mScale);
    }

    /**
     * Causes the ball to shrink to a scale of 0.0. If the ball is currently growing, it begins to shrink. If the ball
     * is already shrinking, this method does nothing.
     */
    public void shrink() {
        mScalingCompleted = false;

        if (mGrowingOrShrinking) {
            if (isScaling()) {
                mScaleTime = -mScaleTime + BALL_SCALE_TIME;
                if (mScalingListener != null)
                    mScalingListener.interrupted(this);
            } else {
                mScaleTime = 0f;
            }
            mGrowingOrShrinking = false;
        }
    }

    /**
     * Causes the ball to grow to its default scale (1.0). If the ball is currently shrinking, it begins to grow. If the
     * ball is already growing, this method does nothing.
     */
    public void grow() {
        mHidden = false;
        mScalingCompleted = false;

        if (!mGrowingOrShrinking) {
            if (isScaling()) {
                mScaleTime = -mScaleTime + BALL_SCALE_TIME;
                if (mScalingListener != null)
                    mScalingListener.interrupted(this);
            } else {
                mScaleTime = 0f;
            }
            mGrowingOrShrinking = true;
        }
    }

    /**
     * Hides the ball so it will not be drawn.
     */
    public void hide() {
        mHidden = true;
        mGrowingOrShrinking = false;
        mScale = 0;
        mScaleTime = BALL_SCALE_TIME;
    }

    /**
     * Sets the callback interface.
     *
     * @param listener instance of callback interface, or {@code null}
     */
    public void setScalingCompleteListener(ScalingCompleteListener listener) {
        mScalingListener = listener;
    }

    /**
     * Checks if the ball is currently scaling.
     *
     * @return {@code true} if {@code BALL_SCALE_TIME} has not passed since the ball was created, or since {@code
     * grow()} or {@code shrink()} was invoked.
     */
    public boolean isScaling() {
        return mScaleTime < BALL_SCALE_TIME;
    }

    /**
     * Checks if the ball has been hidden. Can only be unhidden by calling grow().
     *
     * @return {@code true} if the ball has been hidden and will not scale or be drawn.
     */
    public boolean isHidden() {
        return mHidden;
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
        return getRadius() * 2;
    }

    @Override
    public float getHeight() {
        return getRadius() * 2;
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
     * Gets the default radius for balls. Throws an exception if {@code initialize()} has not been called.
     *
     * @return default ball radius.
     */
    public static float getDefaultBallRadius() {
        if (!sBallsInitialized)
            throw new IllegalStateException("Must call initialize before default size can be determined");
        return sDefaultBallRadius;
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

    /**
     * Callback interface for when the ball has finished scaling.
     */
    public interface ScalingCompleteListener {
        /**
         * Called when the ball finishes scaling.
         *
         * @param ball the ball which finished scaling
         * @param growingOrShrinking {@code true} if the ball has finished growing, {@code false} if it finished
         * shrinking
         */
        void onScalingCompleted(BasicBall ball, boolean growingOrShrinking);

        /**
         * Called when the scaling is interrupted by a call to an opposite scaling method.
         *
         * @param ball the ball which was interrupted
         */
        void interrupted(BasicBall ball);
    }
}
