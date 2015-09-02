package ca.josephroque.swip.entity;

import ca.josephroque.swip.gesture.GameGestureListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Balls for swiping into the walls.
 */
public class Ball
        extends Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Ball";

    /** Maximum number of degrees an overlay can cover - 360 degrees (i.e. a circle). */
    private static final float MAX_OVERLAY_DEGREES = 360f;
    /** Degree to start drawing overlay from. */
    private static final float OVERLAY_STARTING_DEGREE = 90f;

    /** Maximum percentage of ball size to move ball away from origin. */
    private static final float MAXIMUM_BALL_OFFSET = 0.1f;
    /** Number of milliseconds the ball will take to scale up. */
    public static final float BALL_SCALE_TIME = 100f;
    /** Used to determine size of the ball as a percentage of the screen size. */
    private static final float BALL_SIZE_MULTIPLIER = 0.075f;
    /** Number of milliseconds that a fling lasts. */
    private static final float FLING_TIME = 100;

    /** Color of the overlay to represent time remaining in a turn. */
    private static final Color BALL_TIMER_COLOR = new Color(0, 0, 0, 0.25f);

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
    /** Indicates the walls which the ball can pass through. */
    private final boolean[] mPassableWalls;

    /**
     * If false, the ball should slow as it nears its target. If true, its speed should not decrease as it approaches
     * its target.
     */
    private boolean mOvershootTarget;
    /** Horizontal location which the ball should head towards. */
    private float mTargetLocationX;
    /** Vertical location which the ball should head towards. */
    private float mTargetLocationY;

    /** Indicates if the user has flung the ball. */
    private boolean mFlinging;
    /** Indicates if a fling has been recently completed. */
    private boolean mFlingCompleted;
    /** Starting horizontal location of a fling. */
    private float mFlingStartX;
    /** Starting vertical location of a fling. */
    private float mFlingStartY;
    /** Time in milliseconds of the start of the fling. */
    private long mFlingStartTime;

    /**
     * Prepares a new ball object.
     *
     * @param ballColor color of the ball
     * @param passableWalls walls which the ball can pass through
     * @param x starting horizontal position of the ball
     * @param y starting vertical position of the ball
     */
    public Ball(Color ballColor, boolean[] passableWalls, float x, float y) {
        if (!sBallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mTimeCreated = TimeUtils.millis();
        mBallColor = ballColor;
        mPassableWalls = passableWalls;
        setX(x);
        setY(y);
    }

    @Override
    public void tick(float delta) {
        updatePosition(delta);

        final long timeSinceCreated = TimeUtils.timeSinceMillis(mTimeCreated);
        if (timeSinceCreated < BALL_SCALE_TIME)
            mScale = Math.min(1f, Math.max(0f, timeSinceCreated / BALL_SCALE_TIME));
        else
            mScale = 1f;

        if (mFlinging) {
            long timeSinceFlingStart = TimeUtils.timeSinceMillis(mFlingStartTime);
            if (timeSinceFlingStart >= FLING_TIME)
                completeFling();
        }
    }

    /**
     * Checks if the user flung the ball and, if yes, starts a fling event.
     *
     * @param flingDirection direction of the fling
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void attemptToFling(GameGestureListener.FlingDirection flingDirection,
                               float screenWidth,
                               float screenHeight) {
        // User has already flung ball
        if (mFlinging)
            return;

        final float wallSize = Wall.getDefaultWallSize();
        final float ballSize = Ball.getDefaultBallSize();
        switch (flingDirection) {
            case Left:
                mFlinging = true;
                mTargetLocationX = wallSize - ballSize;
                mTargetLocationY = screenHeight / 2;
                break;
            case Up:
                mFlinging = true;
                mTargetLocationX = screenWidth / 2;
                mTargetLocationY = screenHeight - wallSize + ballSize;
                break;
            case Right:
                mFlinging = true;
                mTargetLocationX = screenWidth - wallSize + ballSize;
                mTargetLocationY = screenHeight / 2;
                break;
            case Down:
                mFlinging = true;
                mTargetLocationX = screenWidth / 2;
                mTargetLocationY = wallSize - ballSize;
                break;
            default:
                // does nothing
        }

        if (mFlinging) {
            Gdx.app.debug(TAG, "Fling started");
            mFlingStartTime = TimeUtils.millis();
            mFlingStartX = getX();
            mFlingStartY = getY();

            setDeltaX((mTargetLocationX - mFlingStartX) / FLING_TIME);
            setDeltaY((mTargetLocationY - mFlingStartY) / FLING_TIME);
        }
    }

    /**
     * Resolves a fling and increases a user's score.
     */
    private void completeFling() {
        Gdx.app.debug(TAG, "Fling completed");

        mFlinging = false;
        mFlingCompleted = true;
    }

    /**
     * Draws the ball and its overlay to the screen. The overlay is based on the amount of time that is remaining in the
     * turn.
     *
     * @param shapeRenderer graphics context to draw to
     * @param maxTurnLength total number of milliseconds the current turn will take
     * @param currentTurnLength duration of the current turn
     */
    public void draw(ShapeRenderer shapeRenderer, int maxTurnLength, int currentTurnLength) {
        if (!shapeRenderer.isDrawing())
            throw new IllegalStateException("shape renderer must be drawing");
        else if (shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Filled)
            throw new IllegalStateException("shape renderer must be using ShapeType.Filled");

        final float ballSize = getBallSize();
        drawBall(shapeRenderer, ballSize);
        drawBallOverlay(shapeRenderer, ballSize, maxTurnLength, currentTurnLength);
    }

    /**
     * Draws the ball's circle to the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param ballSize size of the ball
     */
    private void drawBall(ShapeRenderer shapeRenderer, float ballSize) {
        shapeRenderer.setColor(mBallColor);
        shapeRenderer.circle(getX(), getY(), ballSize);
    }

    /**
     * Draws an overlay over the ball to indicate the amount of time remaining in a turn.
     *
     * @param shapeRenderer graphics context to draw to
     * @param ballSize size of the ball
     * @param maxTurnLength total number of milliseconds the current turn will take
     * @param currentTurnLength duration of the current turn
     */
    private void drawBallOverlay(ShapeRenderer shapeRenderer,
                                 float ballSize,
                                 int maxTurnLength,
                                 int currentTurnLength) {
        // TODO: determine number of segments based on degrees
        final int segments = 100;
        final float degrees = -(currentTurnLength / (float) maxTurnLength) * MAX_OVERLAY_DEGREES;
        shapeRenderer.setColor(BALL_TIMER_COLOR);
        shapeRenderer.arc(getX(), getY(), ballSize, OVERLAY_STARTING_DEGREE, degrees, segments);
    }

    /**
     * Checks if the ball can pass through the provided wall.
     *
     * @param wall wall to check
     * @return {@code true} if the wall is passable, false otherwise
     */
    public boolean canPassThroughWall(int wall) {
        return mPassableWalls[wall];
    }

    /**
     * Calculates the size of the ball to draw based on {@code mScale}.
     *
     * @return the size of the ball
     */
    public float getBallSize() {
        return sDefaultBallRadius * mScale;
    }

    /**
     * Checks if a fling was completed, then consumes it.
     *
     * @return {@code true} if a fling was completed, {@code false} otherwise
     */
    public boolean isFlingComplete() {
        if (mFlingCompleted) {
            mFlingCompleted = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the default size of a ball.
     *
     * @return {@code sDefaultBallRadius}
     */
    public static float getDefaultBallSize() {
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
}
