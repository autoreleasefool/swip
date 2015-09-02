package ca.josephroque.swip.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

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
    /** Width of the screen. */
    private static float sScreenWidth;
    /** Height of the screen. */
    private static float sScreenHeight;
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
     */
    public Wall(int wallSide) {
        this(POSSIBLE_SIDES[wallSide]);
    }

    /**
     * Initializes a new wall with the given side.
     *
     * @param wallSide side of the screen
     */
    public Wall(Side wallSide) {
        mWallSide = wallSide;
    }

    @Override
    public void tick(float delta) {
        // does nothing
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
            case Left:
                drawLeftWall(shapeRenderer);
                break;
            case Top:
                drawTopWall(shapeRenderer);
                break;
            case Right:
                // Reset last wall drawn, since all walls should have been drawn now
                sLastWallDrawn = -1;
                drawRightWall(shapeRenderer);
                break;
            case Bottom:
                drawBottomWall(shapeRenderer);
                break;
            default:
                // does nothing
        }
    }

    /**
     * Draws a wall on the left side of the screen.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawLeftWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(0, sDefaultWallSize, sDefaultWallSize, sScreenHeight - sDefaultWallSize * 2);
        shapeRenderer.triangle(0, 0, 0, sDefaultWallSize, sDefaultWallSize, sDefaultWallSize);
        shapeRenderer.triangle(0,
                sScreenHeight,
                0,
                sScreenHeight - sDefaultWallSize,
                sDefaultWallSize,
                sScreenHeight - sDefaultWallSize);
    }

    /**
     * Draws a wall on the right side of the screen.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawRightWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(sScreenWidth - sDefaultWallSize,
                sDefaultWallSize,
                sDefaultWallSize,
                sScreenHeight - 2 * sDefaultWallSize);
        shapeRenderer.triangle(sScreenWidth,
                0,
                sScreenWidth,
                sDefaultWallSize,
                sScreenWidth - sDefaultWallSize,
                sDefaultWallSize);
        shapeRenderer.triangle(sScreenWidth,
                sScreenHeight,
                sScreenWidth,
                sScreenHeight - sDefaultWallSize,
                sScreenWidth - sDefaultWallSize,
                sScreenHeight - sDefaultWallSize);
    }

    /**
     * Draws a wall on the bottom edge of the screen.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawBottomWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(0, 0, sScreenWidth, sDefaultWallSize);
    }

    /**
     * Draws a wall on the top edge of the screen.
     *
     * @param shapeRenderer graphics context to draw to
     */
    private void drawTopWall(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(mWallColor);
        shapeRenderer.rect(0, sScreenHeight - sDefaultWallSize, sScreenWidth, sDefaultWallSize);
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
        sScreenWidth = screenWidth;
        sScreenHeight = screenHeight;
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
     * Gets the default size of a wall.
     *
     * @return {@code sDefaultWallSize}
     */
    public static float getDefaultWallSize() {
        return sDefaultWallSize;
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
