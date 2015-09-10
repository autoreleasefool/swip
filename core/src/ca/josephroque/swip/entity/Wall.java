package ca.josephroque.swip.entity;

import ca.josephroque.swip.manager.TextureManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    public static final int TURNS_BEFORE_NEW_COLOR = 8;
    /** Number of turns that must pass before two walls can be the same color. */
    public static final int TURNS_BEFORE_SAME_WALL_COLORS = 20;
    /** Used to determine size of walls as a percentage of the screen size. */
    private static final float WALL_SIZE_MULTIPLIER = 0.15f;

    /** Array of the possible values for {@code Side}. */
    private static final Side[] POSSIBLE_SIDES = Side.values();

    /** Default width of a wall. */
    private static float sDefaultWallSize;
    /** Indicates if the static wall properties have been initialized. */
    private static boolean sWallsInitialized = false;
    /** Indicates the last wall that was drawn, to ensure walls are drawn in the correct order. */
    private static int sLastWallDrawn;

    /** The chance that two walls will be given the same color in a turn. */
    public static final float CHANCE_OF_SAME_WALL_COLOR = 0.2f;
    /** List of the current active colors. */
    private static List<TextureManager.GameColor> sListActiveColors = new ArrayList<>(TextureManager.GameColor.getSize());

    /** The side of the screen which this wall represents. */
    private final Side mWallSide;
    /** Color of the wall. */
    private TextureManager.GameColor mWallColor;

    /** Rectangle which defines the bounds of the wall. */
    private Rectangle mBoundingBox;

    /**
     * Initializes a new wall by converting the provided int to a {@code Side}.
     *
     * @param wallSide side of the screen
     * @param wallColor color of the wall
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public Wall(int wallSide,
                TextureManager.GameColor wallColor,
                int screenWidth,
                int screenHeight) {
        this(POSSIBLE_SIDES[wallSide], wallColor, screenWidth, screenHeight);
    }

    /**
     * Initializes a new wall with the given side, then adjusts size of the wall to fit the screen.
     *
     * @param wallSide side of the screen
     * @param wallColor color of the wall
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public Wall(Side wallSide,
                TextureManager.GameColor wallColor,
                int screenWidth,
                int screenHeight) {
        if (!sWallsInitialized)
            throw new IllegalStateException("Must call initialize before creating any instances");

        mWallSide = wallSide;
        mWallColor = wallColor;
        resize(screenWidth, screenHeight);
    }

    /**
     * Adjust the size of the object relative to the screen dimensions.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void resize(int screenWidth, int screenHeight) {
        sDefaultWallSize = Math.min(screenWidth, screenHeight) * WALL_SIZE_MULTIPLIER;
        if (mBoundingBox == null)
            mBoundingBox = new Rectangle(0, 0, 0, 0);

        switch (mWallSide) {
            case Top:
                mBoundingBox.setPosition(0, screenHeight - sDefaultWallSize);
                mBoundingBox.setSize(screenWidth, sDefaultWallSize);
                break;
            case Bottom:
                mBoundingBox.setPosition(0, 0);
                mBoundingBox.setSize(screenWidth, sDefaultWallSize);
                break;
            case Left:
                mBoundingBox.setPosition(0, 0);
                mBoundingBox.setSize(sDefaultWallSize, screenHeight);
                break;
            case Right:
                mBoundingBox.setPosition(screenWidth - sDefaultWallSize, 0);
                mBoundingBox.setSize(sDefaultWallSize, screenHeight);
                break;
            default:
                throw new IllegalArgumentException("invalid wall side.");
        }
    }

    /**
     * Draws the wall to the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    public void draw(SpriteBatch spriteBatch) {
        if (mWallSide.ordinal() != sLastWallDrawn + 1)
            throw new IllegalStateException("must draw walls in the natural order determined by Wall.Side");

        if (mWallSide == Side.Right)
            sLastWallDrawn = -1;
        else
            sLastWallDrawn = mWallSide.ordinal();

        if (mWallSide == Side.Top || mWallSide == Side.Bottom)
            drawHorizontalWall(spriteBatch);
        else
            drawVerticalWall(spriteBatch);
    }

    /**
     * Draws a horizontal wall to the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    private void drawHorizontalWall(SpriteBatch spriteBatch) {
        final float rotation = -90;
        spriteBatch.draw(TextureManager.getWallTexture(mWallSide, mWallColor),
                getX() + sDefaultWallSize,
                getY() + sDefaultWallSize,
                0,
                0,
                getHeight(),
                getWidth() - sDefaultWallSize * 2,
                1,
                1,
                rotation);
        spriteBatch.draw(TextureManager.getWallEdge(mWallSide, mWallColor, true),
                getX() + getWidth() - sDefaultWallSize,
                getY() + sDefaultWallSize,
                0,
                0,
                sDefaultWallSize,
                sDefaultWallSize,
                1,
                1,
                rotation);
        spriteBatch.draw(TextureManager.getWallEdge(mWallSide, mWallColor, false),
                getX(),
                getY() + sDefaultWallSize,
                0,
                0,
                sDefaultWallSize,
                sDefaultWallSize,
                1,
                1,
                rotation);
    }

    /**
     * Draws a vertical wall to the screen.
     *
     * @param spriteBatch graphics context to draw to
     */
    private void drawVerticalWall(SpriteBatch spriteBatch) {
        spriteBatch.draw(TextureManager.getWallTexture(mWallSide, mWallColor),
                getX(),
                getY() + sDefaultWallSize,
                getWidth(),
                getHeight() - sDefaultWallSize * 2);
        spriteBatch.draw(TextureManager.getWallEdge(mWallSide, mWallColor, true),
                getX(),
                getY() + getHeight() - sDefaultWallSize,
                sDefaultWallSize,
                sDefaultWallSize);
        spriteBatch.draw(TextureManager.getWallEdge(mWallSide, mWallColor, false),
                getX(),
                getY(),
                sDefaultWallSize,
                sDefaultWallSize);
    }

    @Override
    public void tick(float delta) {
        // does nothing
    }

    /**
     * Updates the color of the wall.
     *
     * @param wallColor new color
     */
    public void updateWallColor(TextureManager.GameColor wallColor) {
        mWallColor = wallColor;
    }

    /**
     * Returns the side of the screen this wall represents.
     *
     * @return {@code mWallSide}
     */
    public Side getSide() {
        return mWallSide;
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
        sListActiveColors.addAll(Arrays.asList(TextureManager.GAME_COLORS).subList(0, NUMBER_OF_WALLS));

        sLastWallDrawn = -1;
        sDefaultWallSize = Math.min(screenWidth, screenHeight) * WALL_SIZE_MULTIPLIER;
        sWallsInitialized = true;
    }

    /**
     * Adds a new color from {@code ALL_POSSIBLE_WALL_COLORS} to the current active wall colors.
     */
    public static void addWallColorToActive() {
        if (!sWallsInitialized)
            throw new IllegalStateException("Must initialize walls.");

        if (sListActiveColors.size() < TextureManager.GameColor.getSize())
            sListActiveColors.add(TextureManager.GAME_COLORS[sListActiveColors.size()]);
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
    public static int getRandomWallColors(Random random, TextureManager.GameColor[] wallColors, boolean allowSame) {
        if (!sWallsInitialized)
            throw new IllegalStateException("Must initialize walls.");
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

    @Override
    public float getX() {
        return mBoundingBox.getX();
    }

    @Override
    public float getY() {
        return mBoundingBox.getY();
    }

    @Override
    public float getWidth() {
        return mBoundingBox.getWidth();
    }

    @Override
    public float getHeight() {
        return mBoundingBox.getHeight();
    }

    @Override
    public Rectangle getBounds() {
        return mBoundingBox;
    }

    @Override
    public void updatePosition(float delta) {
        // does nothing
    }

    /**
     * Represents the four edges of the screen.
     */
    public enum Side {
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
