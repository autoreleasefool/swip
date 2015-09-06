package ca.josephroque.swip.game;

import ca.josephroque.swip.entity.Wall;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Retrieves textures for displaying games objects.
 */
public class GameTexture {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameTexture";

    /** Total number of available colors for game textures. */
    public static final int NUMBER_OF_COLORS = 10;

    /** Primary texture for game objects. */
    private final Texture mGameTexture;
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

    /** Potential colors of walls in the game. */
    public static final GameColor[] GAME_COLORS = GameColor.values();

    /**
     * Prepares the primary texture.
     */
    @SuppressWarnings("CheckStyle") // Longer than 60 lines, but doesn't really matter
    public GameTexture() {
        final int wallSize = 162;
        final int ballSize = 162;
        final int topWallX = 324;
        final int rightWallX = 162;
        final int bottomWallX = 486;
        final int ballX = 3286;
        final int verticalWallHeight = 1920;
        final int horizontalWallHeight = 1080;
        final int bottomEdgeVerticalWallYOffset = 1758;
        final int bottomEdgeHorizontalWallYOffset = 918;
        mGameTexture = new Texture(Gdx.files.internal("game_spritesheet.png"));

        mLeftWalls = new TextureRegion[NUMBER_OF_COLORS];
        mTopWalls = new TextureRegion[NUMBER_OF_COLORS];
        mRightWalls = new TextureRegion[NUMBER_OF_COLORS];
        mBottomWalls = new TextureRegion[NUMBER_OF_COLORS];
        mBalls = new TextureRegion[NUMBER_OF_COLORS];
        mLeftWallEdges = new TextureRegion[NUMBER_OF_COLORS][2];
        mTopWallEdges = new TextureRegion[NUMBER_OF_COLORS][2];
        mRightWallEdges = new TextureRegion[NUMBER_OF_COLORS][2];
        mBottomWallEdges = new TextureRegion[NUMBER_OF_COLORS][2];

        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            mLeftWalls[i] = new TextureRegion(mGameTexture,
                    wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                    wallSize + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                    wallSize,
                    verticalWallHeight - wallSize * 2);
            mTopWalls[i] = new TextureRegion(mGameTexture,
                    topWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                    wallSize + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                    wallSize,
                    horizontalWallHeight - wallSize * 2);
            mRightWalls[i] = new TextureRegion(mGameTexture,
                    rightWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                    wallSize + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                    wallSize,
                    verticalWallHeight - wallSize * 2);
            mBottomWalls[i] = new TextureRegion(mGameTexture,
                    bottomWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                    wallSize + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                    wallSize,
                    horizontalWallHeight - wallSize * 2);
            mBalls[i] = new TextureRegion(mGameTexture,
                    ballX + ballSize * (i % (NUMBER_OF_COLORS / 2)),
                    ballSize * (i / (NUMBER_OF_COLORS / 2)),
                    ballSize,
                    ballSize);
            for (int j = 0; j < 2; j++) {
                mLeftWallEdges[i][j] = new TextureRegion(mGameTexture,
                        wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                        wallSize,
                        wallSize);
                mRightWallEdges[i][j] = new TextureRegion(mGameTexture,
                        rightWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                        bottomEdgeVerticalWallYOffset * j + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                        wallSize,
                        wallSize);
                mTopWallEdges[i][j] = new TextureRegion(mGameTexture,
                        topWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                        bottomEdgeHorizontalWallYOffset * j + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                        wallSize,
                        wallSize);
                mBottomWallEdges[i][j] = new TextureRegion(mGameTexture,
                        bottomWallX + wallSize * Wall.NUMBER_OF_WALLS * (i % (NUMBER_OF_COLORS / 2)),
                        bottomEdgeHorizontalWallYOffset * j + verticalWallHeight * (i / (NUMBER_OF_COLORS / 2)),
                        wallSize,
                        wallSize);
            }
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
        mGameTexture.dispose();
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
        Field,
    }
}
