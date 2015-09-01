package ca.josephroque.swip.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Methods and constants for manipulating walls in the game.
 */
public final class Wall {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Wall";

    /** Maximum number of walls. */
    public static final int NUMBER_OF_WALLS = 4;

    /** Used to determine size of walls as a percentage of the screen size. */
    private static final float WALL_SIZE_MULTIPLIER = 0.1f;

    /** All possible colors which a wall can be. */
    private static final Color[] ALL_POSSIBLE_WALL_COLORS = {
            Color.RED,
            Color.BLUE,
            Color.YELLOW,
            Color.GREEN,
            Color.ORANGE,
            Color.GRAY,
            Color.MAGENTA,
            Color.PURPLE,
            Color.TAN,
            Color.CYAN,
            Color.BROWN,
    };
    /** The chance that two walls will be given the same color in a turn. */
    public static final float CHANCE_OF_SAME_WALL_COLOR = 0.2f;
    /** List of the current active colors, a subset of {@code ALL_POSSIBLE_WALL_COLORS}. */
    private static List<Color> sListActiveColors = new ArrayList<>(ALL_POSSIBLE_WALL_COLORS.length);

    /**
     * Draws the left wall on the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param wallColor color of wall
     * @param screenHeight height of the screen, to allow wall to fill screen
     * @param wallSize width of the wall
     */
    private static void drawLeftWall(ShapeRenderer shapeRenderer,
                                     Color wallColor,
                                     float screenHeight,
                                     float wallSize) {
        shapeRenderer.setColor(wallColor);
        shapeRenderer.rect(0, wallSize, wallSize, screenHeight - 2 * wallSize);
        shapeRenderer.triangle(0, 0, 0, wallSize, wallSize, wallSize);
        shapeRenderer.triangle(0, screenHeight, 0, screenHeight - wallSize, wallSize, screenHeight - wallSize);
    }

    /**
     * Draws the top wall on the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param wallColor color of wall
     * @param screenWidth width of the screen, to allow wall to fill screen
     * @param screenHeight height of the screen, to allow wall to be drawn at maximum height
     * @param wallSize width of the wall
     */
    private static void drawTopWall(ShapeRenderer shapeRenderer,
                                    Color wallColor,
                                    float screenWidth,
                                    float screenHeight,
                                    float wallSize) {
        shapeRenderer.setColor(wallColor);
        shapeRenderer.rect(0, screenHeight - wallSize, screenWidth, wallSize);
    }

    /**
     * Draws the right wall on the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param wallColor color of wall
     * @param screenWidth width of the screen, to allow wall wall to be drawn at maximum width
     * @param screenHeight height of the screen, to allow wall to fill screen
     * @param wallSize width of the wall
     */
    private static void drawRightWall(ShapeRenderer shapeRenderer,
                                      Color wallColor,
                                      float screenWidth,
                                      float screenHeight,
                                      float wallSize) {
        shapeRenderer.setColor(wallColor);
        shapeRenderer.rect(screenWidth - wallSize, wallSize, wallSize, screenHeight - 2 * wallSize);
        shapeRenderer.triangle(screenWidth, 0, screenWidth, wallSize, screenWidth - wallSize, wallSize);
        shapeRenderer.triangle(screenWidth,
                screenHeight,
                screenWidth,
                screenHeight - wallSize,
                screenWidth - wallSize,
                screenHeight - wallSize);
    }

    /**
     * Draws the bottom wall on the screen.
     *
     * @param shapeRenderer graphics context to draw to
     * @param wallColor color of wall
     * @param screenWidth width of the screen, to allow wall to fill screen
     * @param wallSize width of the wall
     */
    private static void drawBottomWall(ShapeRenderer shapeRenderer,
                                       Color wallColor,
                                       float screenWidth,
                                       float wallSize) {
        shapeRenderer.setColor(wallColor);
        shapeRenderer.rect(0, 0, screenWidth, wallSize);
    }

    /**
     * Draws all for walls to the given graphics context using the specified properties. ShapeRenderer should be drawing
     * type {@code ShapeType.Filled}.
     *
     * @param shapeRenderer graphics context to draw to
     * @param wallColors color of the four walls, specified using the values {@code LEFT_WALL}, {@code TOP_WALL}, {@code
     * RIGHT_WALL}, and {@code BOTTOM_WALL}
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     * @param wallSize width / height of the walls to draw (for vertical and horizontal walls, respectively)
     */
    public static void drawWalls(ShapeRenderer shapeRenderer,
                                 Color[] wallColors,
                                 float screenWidth,
                                 float screenHeight,
                                 float wallSize) {
        if (!shapeRenderer.isDrawing())
            throw new IllegalStateException("shape renderer must be drawing");
        else
            if (shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Filled)
                throw new IllegalStateException("shape renderer must be using ShapeType.Filled");

        drawBottomWall(shapeRenderer, wallColors[Sides.BOTTOM.ordinal()], screenWidth, wallSize);
        drawTopWall(shapeRenderer, wallColors[Sides.TOP.ordinal()], screenWidth, screenHeight, wallSize);
        drawLeftWall(shapeRenderer, wallColors[Sides.LEFT.ordinal()], screenHeight, wallSize);
        drawRightWall(shapeRenderer, wallColors[Sides.RIGHT.ordinal()], screenWidth, screenHeight, wallSize);
    }

    /**
     * Calculates the size of walls based on the smallest screen dimension.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     * @return size to use for walls
     */
    public static float getWallSize(float screenWidth, float screenHeight) {
        return Math.min(screenWidth, screenHeight) * WALL_SIZE_MULTIPLIER;
    }

    /**
     * Assigns 4 colors to {@code wallColors} to use for drawing the walls. Selects the colors from {@code
     * sListActiveColors}.
     *
     * @param random to generate random numbers
     * @param wallColors array to return colors. Must be of length 4.
     * @param allowSame if true, up to 2 walls may be the same color. If false, all walls will be different colors.
     * Chance of two walls being the same is determined by {@code CHANCE_OF_SAME_WALL_COLOR}.
     */
    public static void getRandomWallColors(Random random, Color[] wallColors, boolean allowSame) {
        if (wallColors.length != NUMBER_OF_WALLS)
            throw new IllegalArgumentException("color array must have length 4");

        Collections.shuffle(sListActiveColors);
        for (int i = 0; i < wallColors.length; i++) {
            wallColors[i] = sListActiveColors.get(i);
        }

        // Random chance of making 2 walls the same color
        if (allowSame && random.nextFloat() < CHANCE_OF_SAME_WALL_COLOR) {
            int wallToChange = random.nextInt(NUMBER_OF_WALLS);
            int wallToChangeTo = wallToChange;
            int offset = random.nextInt(NUMBER_OF_WALLS - 1) + 1;
            while (offset > 0) {
                wallToChangeTo++;
                offset--;
                if (wallToChangeTo >= NUMBER_OF_WALLS)
                    wallToChangeTo = 0;
            }

            wallColors[wallToChange] = wallColors[wallToChangeTo];
        }
    }

    /**
     * Adds a new color from {@code ALL_POSSIBLE_WALL_COLORS} to the current active wall colors.
     */
    public static void addWallColorToActive() {
        if (sListActiveColors.size() < ALL_POSSIBLE_WALL_COLORS.length)
            sListActiveColors.add(ALL_POSSIBLE_WALL_COLORS[sListActiveColors.size()]);
    }

    /**
     * Represents the four walls.
     */
    private enum Sides {
        /** The left wall. */
        LEFT,
        /** The top wall. */
        TOP,
        /** The right wall. */
        RIGHT,
        /** The bottom wall. */
        BOTTOM,
    }

    /**
     * Default private constructor.
     */
    private Wall() {
        // does nothing
    }
}
