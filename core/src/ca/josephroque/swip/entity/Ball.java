package ca.josephroque.swip.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Methods and constants for manipulating balls in the game.
 */
public final class Ball {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Ball";

    /** Maximum number of degrees an overlay can cover - 360 degrees (i.e. a circle). */
    public static final float MAX_OVERLAY_DEGREES = 360f;
    /** Maximum percentage of ball size to move ball away from origin. */
    private static final float MAXIMUM_BALL_OFFSET = 0.1f;
    /** Used to determine size of the ball as a percentage of the screen size. */
    private static final float BALL_SIZE_MULTIPLIER = 0.075f;

    /** Color of the overlay to represent time remaining in a turn. */
    private static final Color BALL_TIMER_COLOR = new Color(0, 0, 0, 0.25f);

    /**
     * Draws the ball on the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param x horizontal center of the ball
     * @param y vertical center of the ball
     * @param radius radius of the ball
     * @param ballColor color of the ball
     */
    private static void drawBall(ShapeRenderer shapeRenderer, float x, float y, float radius, Color ballColor) {
        shapeRenderer.setColor(ballColor);
        shapeRenderer.circle(x, y, radius);
    }

    /**
     * Draws an overlay on the ball to represent the time remaining in a turn.
     *
     * @param shapeRenderer graphics context to draw to
     * @param x horizontal center of the overlay
     * @param y vertical center of the overlay
     * @param radius radius of the overlay
     * @param degrees total degrees to draw, from 0 to 360, inclusive
     */
    private static void drawBallTimer(ShapeRenderer shapeRenderer, float x, float y, float radius, float degrees) {
        shapeRenderer.setColor(BALL_TIMER_COLOR);
        shapeRenderer.arc(x, y, radius, 0, degrees);
    }

    /**
     * Draws the ball and an overlay. ShapeRenderer must be drawing shapes of type {@code ShapeType.Filled} before
     * invoking this method.
     *
     * @param shapeRenderer graphics context to draw to
     * @param ballColor color of the ball
     * @param x horizontal center of the ball
     * @param y vertical center of the ball
     * @param radius radius of the ball
     * @param degrees total degrees overlay should cover, from 0 to 360, inclusive
     */
    public static void drawBallAndTimer(ShapeRenderer shapeRenderer,
                                        Color ballColor,
                                        float x,
                                        float y,
                                        float radius,
                                        float degrees) {
        if (!shapeRenderer.isDrawing())
            throw new IllegalStateException("shape renderer must be drawing");
        else
            if (shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Filled)
                throw new IllegalStateException("shape renderer must be using ShapeType.Filled");

        drawBall(shapeRenderer, x, y, radius, ballColor);
        drawBallTimer(shapeRenderer, x, y, radius, degrees);
    }

    /**
     * Calculates the location the ball should be drawn at, given its origin x and y. The calculated position will be
     * offset towards {@code targetX} and {@code targetY} by a maximum of {@code MAXIMUM_BALL_OFFSET} * {@code radius}.
     * Results will be given as x and y in {@code locationForBall}.
     *
     * @param centerX origin of the ball's horizontal position
     * @param centerY origin of the ball's vertical position
     * @param targetX x to move ball towards
     * @param targetY y to move ball towards
     * @param radius size of the ball
     * @param locationForBall calculated location for ball
     */
    public static void getLocationForBall(float centerX,
                                          float centerY,
                                          float targetX,
                                          float targetY,
                                          float radius,
                                          float[] locationForBall) {
        if (locationForBall == null || locationForBall.length != 2)
            throw new IllegalArgumentException("location int array must be non null and have length 2");

        float maximumOffset = radius * MAXIMUM_BALL_OFFSET;

        // calculating x position of ball
        boolean negativeXOffset = centerX - targetX < 0;
        float xOffset = Math.min(Math.abs(centerX - targetX), maximumOffset);
        locationForBall[0] = centerX + xOffset * ((negativeXOffset)
                ? -1
                : 1);

        // calculating y position of ball
        boolean negativeYOffset = centerY - targetY < 0;
        float yOffset = Math.min(Math.abs(centerY - targetY), maximumOffset);
        locationForBall[1] = centerY + yOffset * ((negativeYOffset)
                ? -1
                : 1);
    }

    /**
     * Calculates the size of the ball based on the smallest screen dimension.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     * @return size to use for ball
     */
    public static float getBallSize(float screenWidth, float screenHeight) {
        return Math.min(screenWidth, screenHeight) * BALL_SIZE_MULTIPLIER;
    }

    /**
     * Default private constructor.
     */
    private Ball() {
        // does nothing
    }
}
