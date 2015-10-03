package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * Retrieves textures for displaying games objects.
 */
public final class TextureManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TextureManager";

    /** Primary texture for game objects. */
    private static Texture sGameTexture;
    /** Primary texture for menu objects. */
    private static Texture sMenuTexture;

    /** A map from {@code Wall.Side} and {@code TextureManager.Color} values to texture regions. */
    private static HashMap<String, TextureRegion> sWallTextures;
    /** A map from {@code TextureManager.Color} values to texture regions. */
    private static HashMap<String, TextureRegion> sBallTextures;
    /** A map from {@code GameManager.GameCountdown} values to texture regions. */
    private static HashMap<String, TextureRegion> sGameCountdownTextures;
    /** A map from {@code MenuManager.MenuBallOption} values to texture regions. */
    private static HashMap<String, TextureRegion> sMenuIcons;
    /** A map from {@code TextureManager.SystemIcon} values to texture regions. */
    private static HashMap<String, TextureRegion> sSystemIcons;

    /** Potential colors of walls in the game. */
    public static final GameColor[] GAME_COLORS = GameColor.values();

    /**
     * Loads textures for the application.
     */
    public static void initialize() {
        sGameTexture = new Texture(Gdx.files.internal("game_spritesheet.png"));
        sMenuTexture = new Texture(Gdx.files.internal("menu_spritesheet.png"));

        prepareGameTextureRegions();
        prepareMenuTextureRegions();
    }

    /**
     * Loads the textures for game objects.
     */
    private static void prepareGameTextureRegions() {
        sWallTextures = parseTextureProperties(sGameTexture, loadTextureProperties("walls.txt"));
        sBallTextures = parseTextureProperties(sGameTexture, loadTextureProperties("balls.txt"));
        sGameCountdownTextures = parseTextureProperties(sMenuTexture, loadTextureProperties("countdown.txt"));
    }

    /**
     * Loads the textures for menu objects.
     */
    private static void prepareMenuTextureRegions() {
        sMenuIcons = parseTextureProperties(sMenuTexture, loadTextureProperties("menu.txt"));
        sSystemIcons = parseTextureProperties(sMenuTexture, loadTextureProperties("system.txt"));
    }

    /**
     * Creates a {@code HashMap} of {@code TextureRegion} objects by using {@code properties} and {@code texture} to
     * create the instances, then mapping them to the keys provided in {@code properties}.
     *
     * @param texture texture source for {@code TextureRegion}
     * @param properties properties to create {@code TextureRegion}
     * @return a mapping from the keys in {@code properties} to a new set of {@code TextureRegion} objects
     */
    private static HashMap<String, TextureRegion> parseTextureProperties(
            Texture texture,
            HashMap<String, TextureProperties> properties) {
        HashMap<String, TextureRegion> textureRegions = new HashMap<>();
        for (String key : properties.keySet()) {
            TextureProperties textureProperties = properties.get(key);
            TextureRegion region = new TextureRegion(texture,
                    textureProperties.mX,
                    textureProperties.mY,
                    textureProperties.mWidth,
                    textureProperties.mHeight);
            textureRegions.put(key, region);
        }

        return textureRegions;
    }

    /**
     * Opens the file defined by {@code fileHandle} and parses its contents to create a {@code HashMap} of {@code
     * TextureProperties} objects.
     *
     * @param fileHandle file to parse
     * @return a mapping from the first word on each line to a {@code TextureProperties} object created using the rest
     * of the information on the line in the file
     */
    private static HashMap<String, TextureProperties> loadTextureProperties(String fileHandle) {
        final byte propertyName = 0;
        final byte propertyX = 1;
        final byte propertyY = 2;
        final byte propertyWidth = 3;
        final byte propertyHeight = 4;

        final String rawFile = Gdx.files.internal("texture_properties/" + fileHandle).readString();
        final String[] lines = rawFile.split("\n");
        HashMap<String, TextureProperties> properties = new HashMap<>();

        for (String line : lines) {
            if (line != null && line.length() > 0 && line.charAt(0) != '#') {
                String[] rawProperties = line.split("\\s+");
                TextureProperties textureProperties = new TextureProperties(Integer.parseInt(rawProperties[propertyX]),
                        Integer.parseInt(rawProperties[propertyY]),
                        Integer.parseInt(rawProperties[propertyWidth]),
                        Integer.parseInt(rawProperties[propertyHeight]));
                properties.put(rawProperties[propertyName], textureProperties);
            }
        }

        return properties;
    }

    /**
     * Gets the texture of a particular color for a wall.
     *
     * @param side side of the wall
     * @param color color of the wall
     * @return the texture to draw
     */
    public static TextureRegion getWallTexture(Wall.Side side, GameColor color) {
        return sWallTextures.get(color.name() + side.name());
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
        return sWallTextures.get(color.name() + side.name() + ((topEdge)
                ? "Top"
                : "Bottom") + "Edge");
    }

    /**
     * Gets the corresponding icon for the system icon.
     *
     * @param icon system icon
     * @return icon texture
     */
    public static TextureRegion getSystemIconTexture(SystemIcon icon) {
        return sSystemIcons.get(icon.name());
    }

    /**
     * Gets the corresponding icon for the ball option in the main menu.
     *
     * @param option main menu option
     * @return icon texture
     */
    public static TextureRegion getMenuButtonIconTexture(MenuManager.MenuBallOption option) {
        return sMenuIcons.get(option.name());
    }

    /**
     * Gets the texture of a particular color for a ball.
     *
     * @param color color of the ball
     * @return the texture to draw
     */
    public static TextureRegion getBallTexture(GameColor color) {
        return sBallTextures.get(color.name());
    }

    /**
     * Gets the texture of a particular icon for the initial game countdown.
     *
     * @param item position in the countdown
     * @return the texture to draw
     */
    public static TextureRegion getCountdownTexture(GameManager.GameCountdown item) {
        return sGameCountdownTextures.get(item.name());
    }

    /**
     * Frees resources used by textures in this class.
     */
    public static void dispose() {
        sWallTextures = null;
        sBallTextures = null;
        sGameCountdownTextures = null;

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
     * Declares certain basic properties to construct a {@code TextureRegion}.
     */
    public static final class TextureProperties {

        /** Left edge of the texture. */
        private int mX;
        /** Top edge of the texture. */
        private int mY;
        /** Width of the texture. */
        private int mWidth;
        /** Height of the texture. */
        private int mHeight;

        /**
         * Creates a new {@code TextureProperties} using the provided parameters as member variables.
         *
         * @param x left edge of the texture
         * @param y top edge of the texture
         * @param width width of the texture
         * @param height height of the texture
         */
        public TextureProperties(int x, int y, int width, int height) {
            this.mX = x;
            this.mY = y;
            this.mWidth = width;
            this.mHeight = height;
        }
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
        Pause
    }
}
