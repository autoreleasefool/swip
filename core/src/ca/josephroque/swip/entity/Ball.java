package ca.josephroque.swip.entity;

import ca.josephroque.swip.gesture.GameInputProcessor;
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

    /** Number of milliseconds the ball will take to scale up. */
    public static final float BALL_SCALE_TIME = 175f;
    /** Used to determine size of the ball as a percentage of the screen size. */
    private static final float BALL_SIZE_MULTIPLIER = 0.075f;

    /** Color of the overlay to represent time remaining in a turn. */
    private static final Color BALL_TIMER_COLOR = new Color(0, 0, 0, 0.4f);

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
    /** Indicates if the ball has touched a wall it cannot pass through. */
    private boolean mHitInvalidWall;
    /** Indicates if the ball has passed at least halfway through a valid wall. */
    private boolean[] mHalfwayThroughWall = new boolean[Wall.NUMBER_OF_WALLS];
    /** Indicates if the ball has completely passed through a valid wall. */
    private boolean[] mPassedThroughWall = new boolean[Wall.NUMBER_OF_WALLS];

    /** Indicates if the ball is currently being dragged around the screen by the user. */
    private boolean mIsDragging;

    /**
     * Prepares a new ball object.
     *
     * @param ballColor color of the ball
     * @param passableWalls walls which the ball can pass through
     * @param x starting horizontal position of the ball
     * @param y starting vertical position of the ball
     */
    public Ball(Color ballColor, boolean[] passableWalls, float x, float y) {
        super(x, y, 0, 0);
        if (!sBallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mTimeCreated = TimeUtils.millis();
        mBallColor = ballColor;
        mPassableWalls = passableWalls;
    }

    /**
     * Updates the ball's position and evaluates relevant logic.
     *
     * @param delta number of seconds the last tick took
     * @param walls walls on the screen
     */
    public void tick(float delta, Wall[] walls) {
        if (!mIsDragging)
            updatePosition(delta);

        checkWalls(walls);
        scale();
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        sDefaultBallRadius = Math.min(screenWidth, screenHeight) * BALL_SIZE_MULTIPLIER;
        getBoundingBox().setSize(sDefaultBallRadius * mScale * 2, sDefaultBallRadius * mScale * 2);
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

        getBoundingBox().setSize(sDefaultBallRadius * mScale * 2, sDefaultBallRadius * mScale * 2);
    }

    /**
     * Checks to see if the ball is passing through a wall or colliding with a solid wall.
     *
     * @param walls walls on the screen
     */
    private void checkWalls(Wall[] walls) {
        for (int i = 0; i < Wall.NUMBER_OF_WALLS; i++) {
            boolean hitWall;
            switch (walls[i].getSide()) {
                case Top:
                    hitWall = getY() + getHeight() > walls[i].getY();
                    break;
                case Bottom:
                    hitWall = getY() < walls[i].getY() + walls[i].getHeight();
                    break;
                case Left:
                    hitWall = getX() < walls[i].getX() + walls[i].getWidth();
                    break;
                case Right:
                    hitWall = getX() + getWidth() > walls[i].getX();
                    break;
                default:
                    throw new IllegalArgumentException("invalid wall side.");
            }

            if (hitWall) {
                if (mPassableWalls[i]) {
                    checkIfPastWall(walls[i], i);
                } else if (!hasPassedHalfwayThroughWall() && !hasPassedThroughWall()) {
                    mHitInvalidWall = true;
                }
            }
        }
    }

    /**
     * Checks to see if the ball has successfully passed completely through a wall.
     *
     * @param wall wall to check
     * @param index index of wall to check against member variables
     */
    private void checkIfPastWall(Wall wall, int index) {
        switch (wall.getSide()) {
            case Top:
                mPassedThroughWall[index] = getY() > wall.getY();
                mHalfwayThroughWall[index] = getY() + getHeight() / 2 > wall.getY();
                break;
            case Bottom:
                mPassedThroughWall[index] = getY() + getHeight() < wall.getY() + wall.getHeight();
                mHalfwayThroughWall[index] = getY() + getHeight() / 2 < wall.getY() + wall.getHeight();
                break;
            case Left:
                mPassedThroughWall[index] = getX() + getWidth() < wall.getX() + wall.getWidth();
                mHalfwayThroughWall[index] = getX() + getWidth() / 2 < wall.getX() + wall.getWidth();
                break;
            case Right:
                mPassedThroughWall[index] = getX() > wall.getX();
                mHalfwayThroughWall[index] = getX() + getWidth() / 2 > wall.getX();
                break;
            default:
                throw new IllegalArgumentException("invalid wall side.");
        }
    }

    /**
     * Attempts to start a drag event if the player has touched the ball.
     *
     * @param gameInput player's input events
     */
    public void drag(GameInputProcessor gameInput) {
        if (!gameInput.isFingerDown())
            return;

        if (!mIsDragging) {
            if (getBoundingBox().contains(gameInput.getLastFingerX(), gameInput.getLastFingerY()))
                mIsDragging = true;
        } else {
            getBoundingBox().setPosition(gameInput.getLastFingerX() - getWidth() / 2,
                    gameInput.getLastFingerY() - getHeight() / 2);
        }
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

        drawBall(shapeRenderer);
        drawBallOverlay(shapeRenderer, maxTurnLength, currentTurnLength);
    }

    /**
     * Draws the ball's circle to the screen.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawBall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mBallColor);
        shapeRenderer.circle(getX() + getWidth() / 2, getY() + getHeight() / 2, getWidth() / 2);
    }

    /**
     * Draws an overlay over the ball to indicate the amount of time remaining in a turn.
     *
     * @param shapeRenderer graphics context to draw to
     * @param maxTurnLength total number of milliseconds the current turn will take
     * @param currentTurnLength duration of the current turn
     */
    private void drawBallOverlay(ShapeRenderer shapeRenderer,
                                 int maxTurnLength,
                                 int currentTurnLength) {
        // TODO: determine number of segments based on degrees
        final int segments = 100;
        final float degrees = -(currentTurnLength / (float) maxTurnLength) * MAX_OVERLAY_DEGREES;
        shapeRenderer.setColor(BALL_TIMER_COLOR);
        shapeRenderer.arc(getX() + getWidth() / 2,
                getY() + getHeight() / 2,
                getWidth() / 2,
                OVERLAY_STARTING_DEGREE,
                degrees,
                segments);
    }

    /**
     * Cancels the drag on the ball and sets its velocity to move at the speed that it was being dragged.
     *
     * @param gameInput player's input events
     */
    public void tryToReleaseBall(GameInputProcessor gameInput) {
        if (!mIsDragging)
            return;

        if (!gameInput.isFingerDown()) {
            mIsDragging = false;
            setVelocity(gameInput.calculateFingerDragVelocity());
        }
    }

    /**
     * Returns true if the ball has successfully passed completely through a wall.
     *
     * @return {@code true} if the ball has currently passed through any wall
     */
    public boolean hasPassedThroughWall() {
        for (boolean throughWall : mPassedThroughWall)
            if (throughWall)
                return true;
        return false;
    }

    /**
     * Checks if the ball has passed at least halfway through any wall.
     *
     * @return {@code true} if the ball is currently at least halfway through a wall
     */
    public boolean hasPassedHalfwayThroughWall() {
        for (boolean halfway : mHalfwayThroughWall)
            if (halfway)
                return true;
        return false;
    }

    /**
     * Returns true if the ball has touched a wall which it cannot pass through.
     *
     * @return {@code true} if an invalud wall has been touched
     */
    public boolean hasHitInvalidWall() {
        return mHitInvalidWall;
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
