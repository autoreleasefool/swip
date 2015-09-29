package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Retrieves textures for displaying games objects.
 */
public final class TextureManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TextureManager";

    /** Size of icon assets. */
    private static final int ICON_SIZE = 162;
    /** Number of columns icons are organized into. */
    private static final int ICON_COLUMNS = 6;

    /** Primary texture for game objects. */
    private static Texture sGameTexture;
    /** Primary texture for menu objects. */
    private static Texture sMenuTexture;

    /** Texture regions for left walls, derived from {@code sGameTexture}. */
    private static TextureRegion[] sLeftWalls;
    /** Texture regions for top walls, derived from {@code sGameTexture}. */
    private static TextureRegion[] sTopWalls;
    /** Texture regions for right walls, derived from {@code sGameTexture}. */
    private static TextureRegion[] sRightWalls;
    /** Texture regions for bottom walls, derived from {@code sGameTexture}. */
    private static TextureRegion[] sBottomWalls;
    /** Texture regions for left wall edges, derived from {@code sGameTexture}. */
    private static TextureRegion[][] sLeftWallEdges;
    /** Texture regions for top wall edges, derived from {@code sGameTexture}. */
    private static TextureRegion[][] sTopWallEdges;
    /** Texture regions for right wall edges, derived from {@code sGameTexture}. */
    private static TextureRegion[][] sRightWallEdges;
    /** Texture regions for bottom wall edges, derived from {@code sGameTexture}. */
    private static TextureRegion[][] sBottomWallEdges;
    /** Texture regions for balls, derived from {@code sGameTexture}. */
    private static TextureRegion[] sBalls;

    /** Texture regions for balls in the main menu. */
    private static TextureRegion[] sMenuOptionBalls;

    /** Texture regions for system icons. */
    private static TextureRegion[] sSystemIcons;

    /** Texture regions for the countdown when a new game begins. */
    private static TextureRegion[] sGameCountdown;

    /** Potential colors of walls in the game. */
    public static final GameColor[] GAME_COLORS = GameColor.values();

    /**
     * Loads textures for the application.
     */
    public static void initialize() {
        sGameTexture = new Texture(Gdx.files.internal("game_spritesheet.png"));
        sMenuTexture = new Texture(Gdx.files.internal("menu_spritesheet.png"));

        // Creating object arrays for game textures
        sLeftWalls = new TextureRegion[GameColor.getSize()];
        sTopWalls = new TextureRegion[GameColor.getSize()];
        sRightWalls = new TextureRegion[GameColor.getSize()];
        sBottomWalls = new TextureRegion[GameColor.getSize()];
        sBalls = new TextureRegion[GameColor.getSize()];
        sLeftWallEdges = new TextureRegion[GameColor.getSize()][2];
        sTopWallEdges = new TextureRegion[GameColor.getSize()][2];
        sRightWallEdges = new TextureRegion[GameColor.getSize()][2];
        sBottomWallEdges = new TextureRegion[GameColor.getSize()][2];

        // Creating object arrays for menu textures
        sMenuOptionBalls = new TextureRegion[MenuManager.MenuBallOptions.getSize()];

        // Creating object arrays for other textures
        sSystemIcons = new TextureRegion[SystemIcon.getSize()];
        sGameCountdown = new TextureRegion[GameManager.GameCountdown.getSize()];

        loadGameTextures();
        loadMenuTextures();
        loadOtherTextures();
    }

    /**
     * Loads textures for the game.
     */
    private static void loadGameTextures() {
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
            sLeftWalls[i] = new TextureRegion(sGameTexture,
                    entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    verticalWallHeight - entitySize * 2);
            sTopWalls[i] = new TextureRegion(sGameTexture,
                    topWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    horizontalWallHeight - entitySize * 2);
            sRightWalls[i] = new TextureRegion(sGameTexture,
                    rightWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    verticalWallHeight - entitySize * 2);
            sBottomWalls[i] = new TextureRegion(sGameTexture,
                    bottomWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                    entitySize + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    horizontalWallHeight - entitySize * 2);
            sBalls[i] = new TextureRegion(sGameTexture,
                    ballX + entitySize * (i % (GameColor.getSize() / 2)),
                    entitySize * (i / (GameColor.getSize() / 2)),
                    entitySize,
                    entitySize);
            for (int j = 0; j < 2; j++) {
                sLeftWallEdges[i][j] = new TextureRegion(sGameTexture,
                        entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                sRightWallEdges[i][j] = new TextureRegion(sGameTexture,
                        rightWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                sTopWallEdges[i][j] = new TextureRegion(sGameTexture,
                        topWallX + entitySize * Wall.NUMBER_OF_WALLS * (i % (GameColor.getSize() / 2)),
                        bottomEdgeHorizontalWallYOffset * j + verticalWallHeight * (i / (GameColor.getSize() / 2)),
                        entitySize,
                        entitySize);
                sBottomWallEdges[i][j] = new TextureRegion(sGameTexture,
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
    private static void loadMenuTextures() {
        for (int i = 0; i < sMenuOptionBalls.length; i++)
            sMenuOptionBalls[i] = new TextureRegion(sMenuTexture, ICON_SIZE * i, 0, ICON_SIZE, ICON_SIZE);
    }

    /**
     * Loads other textures for the application.
     */
    private static void loadOtherTextures() {
        for (int i = 0; i < sSystemIcons.length; i++) {
            sSystemIcons[i] = new TextureRegion(sMenuTexture,
                    ICON_SIZE * (i % ICON_COLUMNS),
                    ICON_SIZE * (i / ICON_COLUMNS) + ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE);
        }

        final int left = 0;
        final int top = 1;
        final int width = 2;
        final int height = 3;

        String countdownIconDefinitions = Gdx.files.internal("config/countdown_icons.txt").readString();
        String[] countdownIcons = countdownIconDefinitions.split("\n");
        for (int i = 0; i < sGameCountdown.length; i++) {
            String[] rect = countdownIcons[i].split(" ");
            sGameCountdown[i] = new TextureRegion(sMenuTexture,
                    Integer.parseInt(rect[left]),
                    Integer.parseInt(rect[top]),
                    Integer.parseInt(rect[width]),
                    Integer.parseInt(rect[height]));
        }
    }

    /**
     * Gets the texture of a particular color for a wall.
     *
     * @param side side of the wall
     * @param color color of the wall
     * @return the texture to draw
     */
    public static TextureRegion getWallTexture(Wall.Side side, GameColor color) {
        final TextureRegion[] source;
        switch (side) {
            case Left:
                source = sLeftWalls;
                break;
            case Top:
                source = sTopWalls;
                break;
            case Right:
                source = sRightWalls;
                break;
            case Bottom:
                source = sBottomWalls;
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
    public static TextureRegion getWallEdge(Wall.Side side, GameColor color, boolean topEdge) {
        final TextureRegion[][] source;
        switch (side) {
            case Left:
                source = sLeftWallEdges;
                break;
            case Top:
                source = sTopWallEdges;
                break;
            case Right:
                source = sRightWallEdges;
                break;
            case Bottom:
                source = sBottomWallEdges;
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
    public static TextureRegion getSystemIconTexture(SystemIcon icon) {
        return sSystemIcons[icon.ordinal()];
    }

    /**
     * Gets the corresponding icon for the ball option in the main menu.
     *
     * @param option main menu option
     * @return icon texture
     */
    public static TextureRegion getMenuButtonIconTexture(MenuManager.MenuBallOptions option) {
        return sMenuOptionBalls[option.ordinal()];
    }

    /**
     * Gets the texture of a particular color for a ball.
     *
     * @param color color of the ball
     * @return the texture to draw
     */
    public static TextureRegion getBallTexture(GameColor color) {
        return sBalls[color.ordinal()];
    }

    /**
     * Gets the texture of a particular icon for the initial game countdown.
     *
     * @param item position in the countdown
     * @return the texture to draw
     */
    public static TextureRegion getCountdownTexture(GameManager.GameCountdown item) {
        return sGameCountdown[item.ordinal()];
    }

    /**
     * Frees resources used by textures in this class.
     */
    public static void dispose() {
        sBalls = null;
        sLeftWalls = null;
        sTopWalls = null;
        sRightWalls = null;
        sBottomWalls = null;
        sLeftWallEdges = null;
        sTopWallEdges = null;
        sRightWallEdges = null;
        sBottomWallEdges = null;
        sMenuOptionBalls = null;
        sGameCountdown = null;

        sGameTexture.dispose();
        sMenuTexture.dispose();
    }

    /**
     * Default private constructor.
     */
    private TextureManager() {
        // does nothing
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
         * @return number of {@code SystemIcon}s
         */
        public static int getSize() {
            return SIZE;
        }
    }
}
