package ca.josephroque.swip.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Edges of the screen which provide targets for the balls to pass through.
 */
public class Wall
        extends Entity {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Wall";

    /** Maximum number of walls. */
    public static final int NUMBER_OF_WALLS = 4;

    /** Number of turns that must pass before a new color is added to the game. */
    public static final int NUMBER_OF_TURNS_BEFORE_NEW_COLOR = 8;
    /** Number of turns that must pass before two walls can be the same color. */
    public static final int NUMBER_OF_TURNS_BEFORE_SAME_WALL_COLORS = 20;
    /** Used to determine size of walls as a percentage of the screen size. */
    private static final float WALL_SIZE_MULTIPLIER = 0.1f;

    /** Array of the possible values for {@code Side}. */
    private static final Side[] POSSIBLE_SIDES = Side.values();

    /** Default width of a wall. */
    private static float sDefaultWallSize;
    /** Indicates if the static wall properties have been initialized. */
    private static boolean sWallsInitialized = false;
    /** Indicates the last wall that was drawn, to ensure walls are drawn in the correct order. */
    private static int sLastWallDrawn;

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

    /** The side of the screen which this wall represents. */
    private final Side mWallSide;
    /** Color of the wall. */
    private Color mWallColor;

    /**
     * Initializes a new wall by converting the provided int to a {@code Side}.
     *
     * @param wallSide side of the screen
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public Wall(int wallSide, int screenWidth, int screenHeight) {
        this(POSSIBLE_SIDES[wallSide], screenWidth, screenHeight);
    }

    /**
     * Initializes a new wall with the given side, then adjusts size of the wall to fit the screen.
     *
     * @param wallSide side of the screen
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public Wall(Side wallSide, int screenWidth, int screenHeight) {
        super(0, 0, 0, 0);
        if (!sWallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mWallSide = wallSide;
        resize(screenWidth, screenHeight);
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        sDefaultWallSize = Math.min(screenWidth, screenHeight) * WALL_SIZE_MULTIPLIER;

        // Resize wall based on screen dimensions
        Rectangle boundingBox = getBoundingBox();
        switch (mWallSide) {
            case Top:
                boundingBox.x = 0;
                boundingBox.y = screenHeight - sDefaultWallSize;
                boundingBox.width = screenWidth;
                boundingBox.height = sDefaultWallSize;
                break;
            case Bottom:
                boundingBox.x = 0;
                boundingBox.y = 0;
                boundingBox.width = screenWidth;
                boundingBox.height = sDefaultWallSize;
                break;
            case Left:
                boundingBox.x = 0;
                boundingBox.y = 0;
                boundingBox.width = sDefaultWallSize;
                boundingBox.height = screenHeight;
                break;
            case Right:
                boundingBox.x = screenWidth - sDefaultWallSize;
                boundingBox.y = 0;
                boundingBox.width = sDefaultWallSize;
                boundingBox.height = screenHeight;
                break;
            default:
                // does nothing
        }
    }

    /**
     * Draws this wall to the screen in its position as determined by {@code mWallSide}.
     *
     * @param shapeRenderer graphics context to draw to
     */
    public void draw(ShapeRenderer shapeRenderer) {
        if (!shapeRenderer.isDrawing())
            throw new IllegalStateException("shape renderer must be drawing");
        else if (shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Filled)
            throw new IllegalStateException("shape renderer must be using ShapeType.Filled");

        if (mWallSide.ordinal() != sLastWallDrawn + 1)
            throw new IllegalStateException("must draw walls in the natural order determined by Wall.Side");
        sLastWallDrawn = mWallSide.ordinal();

        switch (mWallSide) {
            case Top:
            case Bottom:
                drawHorizontalWall(shapeRenderer);
                break;
            case Right:
                // Reset last wall drawn, since all walls should have been drawn now
                sLastWallDrawn = -1;
            case Left:
                drawVerticalWall(shapeRenderer);
                drawEdgeSlants(shapeRenderer, mWallSide == Side.Left);
                break;
            default:
                // does nothing
        }
    }

    /**
     * Draws a wall along the screen horizontally.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawHorizontalWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Draws a wall along the screen vertically, leaving room at the top and bottom.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawVerticalWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(getX(), getY() + sDefaultWallSize, getWidth(), getHeight() - sDefaultWallSize * 2);
    }

    /**
     * Draws triangles at the top and bottom of the wall.
     *
     * @param shapeRenderer graphics context to draw to
     * @param facingRight if true, the slanted edges of the triangles will face the right of the screen. If false, they
     * will face the left.
     */
    private void drawEdgeSlants(ShapeRenderer shapeRenderer, boolean facingRight) {
        if (mWallSide == Side.Top || mWallSide == Side.Bottom)
            throw new IllegalStateException("cannot draw slants on horizontal walls.");

        float thirdVertexX = (facingRight)
                ? getX()
                : getX() + getWidth();
        shapeRenderer.triangle(getX(),
                getY() + sDefaultWallSize,
                getX() + getWidth(),
                getY() + sDefaultWallSize,
                thirdVertexX,
                getY());
        shapeRenderer.triangle(getX(),
                getY() + getHeight() - sDefaultWallSize,
                getX() + getWidth(),
                getY() + getHeight() - sDefaultWallSize,
                thirdVertexX,
                getY() + getHeight());
    }

    /**
     * Updates the color of the wall.
     *
     * @param wallColor new value for {@code mWallColor}
     */
    public void updateWallColor(Color wallColor) {
        mWallColor = wallColor;
    }

    /**
     * Initializes static values common for all walls. Must be called before creating any instances of this object, and
     * should be called any time the screen is resized.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public static void initialize(int screenWidth, int screenHeight) {
        sListActiveColors.clear();
        sListActiveColors.addAll(Arrays.asList(ALL_POSSIBLE_WALL_COLORS).subList(0, NUMBER_OF_WALLS));

        sLastWallDrawn = -1;
        sDefaultWallSize = Math.min(screenWidth, screenHeight) * WALL_SIZE_MULTIPLIER;
        sWallsInitialized = true;
    }

    /**
     * Adds a new color from {@code ALL_POSSIBLE_WALL_COLORS} to the current active wall colors.
     */
    public static void addWallColorToActive() {
        if (sListActiveColors.size() < ALL_POSSIBLE_WALL_COLORS.length)
            sListActiveColors.add(ALL_POSSIBLE_WALL_COLORS[sListActiveColors.size()]);
    }

    /**
     * Assigns 4 colors to {@code wallColors} to use for drawing the walls. Selects the colors from {@code
     * sListActiveColors}.
     *
     * @param random to generate random numbers
     * @param wallColors array to return colors. Must be of length 4.
     * @param allowSame if true, up to 2 walls may be the same color. If false, all walls will be different colors.
     * Chance of two walls being the same is determined by {@code CHANCE_OF_SAME_WALL_COLOR}.
     * @return if there are two walls the same color, then the value returned is the index of the first of the pair. If
     * there are no two walls the same, this method returns -1
     */
    public static int getRandomWallColors(Random random, Color[] wallColors, boolean allowSame) {
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
            return Math.min(wallToChange, wallToChangeTo);
        }

        return -1;
    }

    /**
     * Represents the four edges of the screen.
     */
    private enum Side {
        /** The top wall. */
        Top,
        /** The bottom wall. */
        Bottom,
        /** The left wall. */
        Left,
        /** The right wall. */
        Right,
    }
}
