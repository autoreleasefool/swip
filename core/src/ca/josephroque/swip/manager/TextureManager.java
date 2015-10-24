package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Retrieves textures for displaying games objects.
 */
public final class TextureManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TextureManager";

    /** Primary texture for game objects. */
    private Texture mGameTexture;
    /** Primary texture for menu objects. */
    private Texture mMenuTexture;
    /** Primary texture for background panels. */
    private Texture mBackgroundTexture;

    /** A map from {@code Wall.Side} and {@code TextureManager.Color} values to texture regions. */
    private HashMap<String, TextureRegion> mWallTextures;
    /** A map from {@code TextureManager.Color} values to texture regions. */
    private HashMap<String, TextureRegion> mBallTextures;
    /** An array of texture regions of shadows to overlay balls as the timer progresses. */
    private TextureRegion[] mBallOverlayTextures;
    /** A map from {@code GameManager.GameCountdown} values to texture regions. */
    private HashMap<String, TextureRegion> mGameCountdownTextures;
    /** A map from {@code MenuManager.MenuBallOption} values to texture regions. */
    private HashMap<String, TextureRegion> mMenuIcons;
    /** A map from {@code TextureManager.SystemIcon} values to texture regions. */
    private HashMap<String, TextureRegion> mSystemIcons;
    /** A map from {@code TextureManager.Background} values to texture regions. */
    private HashMap<String, TextureRegion> mBackgroundTextures;

    /** Potential colors of walls in the game. */
    public static final GameColor[] GAME_COLORS = GameColor.values();

    /**
     * Loads textures for the application.
     */
    public TextureManager() {
        Gdx.app.debug(TAG, "Initializing");
        mGameTexture = new Texture(Gdx.files.internal("game_spritesheet.png"));
        mMenuTexture = new Texture(Gdx.files.internal("menu_spritesheet.png"));
        mBackgroundTexture = new Texture(Gdx.files.internal("bg_spritesheet.png"));

        prepareGameTextureRegions();
        prepareMenuTextureRegions();
    }

    /**
     * Loads the textures for game objects.
     */
    private void prepareGameTextureRegions() {
        mWallTextures = parseTextureProperties(mGameTexture, loadTextureProperties("walls.txt"));
        mBallTextures = parseTextureProperties(mGameTexture, loadTextureProperties("balls.txt"));
        mBallOverlayTextures = parseTexturePropertiesArray(mGameTexture, "ball_overlays.txt");
        mGameCountdownTextures = parseTextureProperties(mMenuTexture, loadTextureProperties("countdown.txt"));
        mBackgroundTextures = parseTextureProperties(mBackgroundTexture, loadTextureProperties("backgrounds.txt"));
    }

    /**
     * Loads the textures for menu objects.
     */
    private void prepareMenuTextureRegions() {
        mMenuIcons = parseTextureProperties(mMenuTexture, loadTextureProperties("menu.txt"));
        mSystemIcons = parseTextureProperties(mMenuTexture, loadTextureProperties("system.txt"));
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
     * Creates an array of {@code TextureRegion} objects by using {@code fileHandle} and {@code texture} to
     * create the instances, then adding them in the order defined to an array.
     *
     * @param texture texture source for {@code TextureRegion}
     * @param fileHandle file to parse
     * @return an array of {@code TextureRegion} objects
     */
    private static TextureRegion[] parseTexturePropertiesArray(Texture texture, String fileHandle) {
        final byte propertyX = 0;
        final byte propertyY = 1;
        final byte propertyWidth = 2;
        final byte propertyHeight = 3;

        final String rawFile = Gdx.files.internal("texture_properties/" + fileHandle).readString();
        final String[] lines = rawFile.split("\n");
        List<TextureRegion> regionList = new ArrayList<>(lines.length);

        for (String line : lines) {
            if (line != null && line.length() > 0 && line.charAt(0) != '#') {
                String[] rawProperties = line.split("\\s+");
                TextureRegion textureRegion = new TextureRegion(texture,
                        Integer.parseInt(rawProperties[propertyX]),
                        Integer.parseInt(rawProperties[propertyY]),
                        Integer.parseInt(rawProperties[propertyWidth]),
                        Integer.parseInt(rawProperties[propertyHeight]));
                regionList.add(textureRegion);
            }
        }

        TextureRegion[] regionArray = new TextureRegion[regionList.size()];
        regionList.toArray(regionArray);
        return regionArray;
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
    public TextureRegion getWallTexture(Wall.Side side, GameColor color) {
        return mWallTextures.get(color.name() + side.name());
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
        return mWallTextures.get(color.name() + side.name() + ((topEdge)
                ? "Top"
                : "Bottom") + "Edge");
    }

    /**
     * Gets the corresponding icon for the system icon.
     *
     * @param icon system icon
     * @return icon texture
     */
    public TextureRegion getSystemIconTexture(SystemIcon icon) {
        return mSystemIcons.get(icon.name());
    }

    /**
     * Gets the corresponding icon for the ball option in the main menu.
     *
     * @param option main menu option
     * @return icon texture
     */
    public TextureRegion getMenuButtonIconTexture(MenuManager.MenuBallOption option) {
        return mMenuIcons.get(option.name());
    }

    /**
     * Gets the texture of a particular color for a ball.
     *
     * @param color color of the ball
     * @return the texture to draw
     */
    public TextureRegion getBallTexture(GameColor color) {
        return mBallTextures.get(color.name());
    }

    /**
     * Gets the texture of a particular section of the ball overlay, where 0 is the beginning of the shadow shape,
     * and {@code getTotalBallShadowParts()} is the end of the shadow shape.
     *
     * @param index index of shadow overlay piece
     * @return the texture to draw
     */
    public TextureRegion getBallOverlayTexture(int index) {
        if (index < 0 || index > getTotalBallShadowParts())
            throw new IndexOutOfBoundsException("Must be between 0 and getTotalBallShadowParts()");
        return mBallOverlayTextures[index];
    }

    /**
     * Gets the texture of a particular icon for the initial game countdown.
     *
     * @param item position in the countdown
     * @return the texture to draw
     */
    public TextureRegion getCountdownTexture(GameManager.GameCountdown item) {
        return mGameCountdownTextures.get(item.name());
    }

    /**
     * Gets the texture of a particular background panel.
     *
     * @param bg background panel
     * @return the texture to draw
     */
    public TextureRegion getBackgroundTexture(Background bg) {
        return mBackgroundTextures.get(bg.name());
    }

    /**
     * Frees resources used by textures in this class.
     */
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing");
        mWallTextures = null;
        mBallTextures = null;
        mGameCountdownTextures = null;
        mMenuIcons = null;
        mSystemIcons = null;
        mBackgroundTextures = null;

        mGameTexture.dispose();
        mMenuTexture.dispose();
        mBackgroundTexture.dispose();
    }

    /**
     * The total number of parts required to fill the ball overlay shadow.
     *
     * @return size of {@code mBallOverlayTextures}
     */
    public int getTotalBallShadowParts() {
        return mBallOverlayTextures.length;
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
     * Available background textures.
     */
    public enum Background {
        /** The default game background texture. */
        Default,
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
