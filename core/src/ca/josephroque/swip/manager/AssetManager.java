package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Retrieves textures for displaying games objects.
 */
public class AssetManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "AssetManager";

    /** Size of icon assets. */
    private static final int ICON_SIZE = 162;
    /** Number of columns icons are organized into. */
    private static final int ICON_COLUMNS = 6;

    /** Primary texture for game objects. */
    private final Texture mGameTexture;
    /** Primary texture for menu objects. */
    private final Texture mMenuTexture;

    /** Texture regions for left walls, derived from {@code mGameTexture}. */
    private TextureRegion[] mLeftWalls;
    /** Texture regions for top walls, derived from {@code mGameTexture}. */
    private TextureRegion[] mTopWalls;
    /** Texture regions for right walls, derived from {@code mGameTexture}. */
    private TextureRegion[] mRightWalls;
    /** Texture regions for bottom walls, derived from {@code mGameTexture}. */
    private TextureRegion[] mBottomWalls;
    /** Texture regions for left wall edges, derived from {@code mGameTexture}. */
    private TextureRegion[][] mLeftWallEdges;
    /** Texture regions for top wall edges, derived from {@code mGameTexture}. */
    private TextureRegion[][] mTopWallEdges;
    /** Texture regions for right wall edges, derived from {@code mGameTexture}. */
    private TextureRegion[][] mRightWallEdges;
    /** Texture regions for bottom wall edges, derived from {@code mGameTexture}. */
    private TextureRegion[][] mBottomWallEdges;
    /** Texture regions for balls, derived from {@code mGameTexture}. */
    private TextureRegion[] mBalls;

    /** Texture regions for balls in the main menu. */
    private TextureRegion[] mMenuOptionBalls;

    /** Texture regions for system icons. */
    private TextureRegion[] mSystemIcons;

    /** Potential colors of walls in the game. */
    public static final GameColor[] GAME_COLORS = GameColor.values();

    /**
     * Prepares the primary texture.
     */
    @SuppressWarnings("CheckStyle") // Longer than 60 lines, but doesn't really matter
    public AssetManager() {
        mGameTexture = new Texture(Gdx.files.internal("game_spritesheet.png"));
        mMenuTexture = new Texture(Gdx.files.internal("menu_spritesheet.png"));
    }

    /**
     * Loads textures for the application.
     */
    public void initialize() {
        // Creating object arrays for game textures
        mLeftWalls = new TextureRegion[GameColor.getSize()];
        mTopWalls = new TextureRegion[GameColor.getSize()];
        mRightWalls = new TextureRegion[GameColor.getSize()];
        mBottomWalls = new TextureRegion[GameColor.getSize()];
        mBalls = new TextureRegion[GameColor.getSize()];
        mLeftWallEdges = new TextureRegion[GameColor.getSize()][2];
        mTopWallEdges = new TextureRegion[GameColor.getSize()][2];
        mRightWallEdges = new TextureRegion[GameColor.getSize()][2];
        mBottomWallEdges = new TextureRegion[GameColor.getSize()][2];

        // Creating object arrays for menu textures
        mMenuOptionBalls = new TextureRegion[MenuManager.MenuBallOptions.getSize()];

        // Creating object arrays for other textures
        mSystemIcons = new TextureRegion[SystemIcon.getSize()];

        loadGameTextures();
        loadMenuTextures();
        loadOtherTextures();
    }

    /**
     * Loads textures for the game.
     */
    private void loadGameTextures() {
        final int entitySize = 162;
        final int topWallX = 324;
        final int rightWallX = 162;
        final int bottomWallX = 486;
        final int ballX = 3286;
        final int verticalWallHeight = 1920;
        final int horizontalWallHeight = 1080;
        final int bottomEdgeVerticalWallYOffset = 1758;
        final int bottomEdgeHorizontalWallYOffset = 918;

        for (int i = 0; i < GameColor.getSize(); i++) {
            mLeftWalls[i] = new TextureRegion(mGameTexture,
                    entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    verticalWallHeight - entitySize * 2);
            mTopWalls[i] = new TextureRegion(mGameTexture,
                    topWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    horizontalWallHeight - entitySize * 2);
            mRightWalls[i] = new TextureRegion(mGameTexture,
                    rightWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    verticalWallHeight - entitySize * 2);
            mBottomWalls[i] = new TextureRegion(mGameTexture,
                    bottomWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    horizontalWallHeight - entitySize * 2);
            mBalls[i] = new TextureRegion(mGameTexture,
                    ballX + entitySize * (i % (GameColor.getSize() / 2)),
                    entitySize * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    entitySize);
            for (int j = 0; j < 2; j++) {
                mLeftWallEdges[i][j] = new TextureRegion(mGameTexture,
                        entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                mRightWallEdges[i][j] = new TextureRegion(mGameTexture,
                        rightWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                mTopWallEdges[i][j] = new TextureRegion(mGameTexture,
                        topWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeHorizontalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                mBottomWallEdges[i][j] = new TextureRegion(mGameTexture,
                        bottomWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeHorizontalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
            }
        }
    }

    /**
     * Loads textures for the menu.
     */
    private void loadMenuTextures() {
        for (int i = 0; i < mMenuOptionBalls.length; i++)
            mMenuOptionBalls[i] = new TextureRegion(mMenuTexture, ICON_SIZE * i, 0, ICON_SIZE, ICON_SIZE);
    }

    /**
     * Loads other textures for the application.
     */
    private void loadOtherTextures() {
        for (int i = 0; i < mSystemIcons.length; i++) {
            mSystemIcons[i] = new TextureRegion(mMenuTexture,
                    ICON_SIZE * (i % ICON_COLUMNS),
                    ICON_SIZE * (i / ICON_COLUMNS),
                    ICON_SIZE,
                    ICON_SIZE);
        }
    }

    /**
     * Gets the texture of a particular color for a wall.
     *
     * @param side side of the wall
     * @param color color of the wall
     * @return the texture to draw
     */
    public TextureRegion getWallTexture(Wall.Side side, GameColor color) {
        final TextureRegion[] source;
        switch (side) {
            case Left:
                source = mLeftWalls;
                break;
            case Top:
                source = mTopWalls;
                break;
            case Right:
                source = mRightWalls;
                break;
            case Bottom:
                source = mBottomWalls;
                break;
            default:
                throw new IllegalArgumentException("Invalid wall side.");
        }

        return source[color.ordinal()];
    }

    /**
     * Gets the slanted edge to draw for the specified wall. {@code topEdge} refers to whether the edge closest to the
     * top of the original texture should be retrieved, or the bottom edge.
     *
     * @param side side of the wall
     * @param color color of the wall
     * @param topEdge true to get the top edge of the original texture, false to get the right
     * @return the texture to draw
     */
    public TextureRegion getWallEdge(Wall.Side side, GameColor color, boolean topEdge) {
        final TextureRegion[][] source;
        switch (side) {
            case Left:
                source = mLeftWallEdges;
                break;
            case Top:
                source = mTopWallEdges;
                break;
            case Right:
                source = mRightWallEdges;
                break;
            case Bottom:
                source = mBottomWallEdges;
                break;
            default:
                throw new IllegalArgumentException("Invalid wall side.");
        }

        return source[color.ordinal()][(topEdge)
                ? 0
                : 1];
    }

    /**
     * Gets the corresponding icon for the system icon.
     *
     * @param icon system icon
     * @return icon texture
     */
    public TextureRegion getSystemIconTexture(SystemIcon icon) {
        return mSystemIcons[icon.ordinal()];
    }

    /**
     * Gets the corresponding icon for the ball option in the main menu.
     *
     * @param option main menu option
     * @return icon texture
     */
    public TextureRegion getMenuButtonIconTexture(MenuManager.MenuBallOptions option) {
        return mMenuOptionBalls[option.ordinal()];
    }

    /**
     * Gets the texture of a particular color for a ball.
     *
     * @param color color of the ball
     * @return the texture to draw
     */
    public TextureRegion getBallTexture(GameColor color) {
        return mBalls[color.ordinal()];
    }

    /**
     * Frees resources used by textures in this class.
     */
    public void dispose() {
        mBalls = null;
        mLeftWalls = null;
        mTopWalls = null;
        mRightWalls = null;
        mBottomWalls = null;
        mLeftWallEdges = null;
        mTopWallEdges = null;
        mRightWallEdges = null;
        mBottomWallEdges = null;
        mMenuOptionBalls = null;

        mGameTexture.dispose();
        mMenuTexture.dispose();
    }

    /**
     * Available texture colors.
     */
    public enum GameColor {
        /** Red color. */
        Red,
        /** Blue color. */
        Blue,
        /** Green color. */
        Green,
        /** Orange color. */
        Orange,
        /** Pink color. */
        Pink,
        /** Purple color. */
        Purple,
        /** Gray color. */
        Gray,
        /** Cyan color. */
        Cyan,
        /** Salmon color. */
        Salmon,
        /** Field color. */
        Field;

        /** Size of the enum. */
        private static final int SIZE = GameColor.values().length;

        /**
         * Gets the size of the enum.
         *
         * @return number of {@code GameColor} values
         */
        public static int getSize() {
            return SIZE;
        }
    }

    /**
     * Icons which represent system operations.
     */
    public enum SystemIcon {
        /** Icon which represents a pause button. */
        Pause;

        /** Size of the enum. */
        private static final int SIZE = SystemIcon.values().length;

        /**
         * Gets the size of the enum.
         *
         * @return number of {@code SystemIcons}
         */
        public static int getSize() {
            return SIZE;
        }
    }
}
