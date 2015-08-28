package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import ca.josephroque.swip.gesture.GameGestureListener;
import ca.josephroque.swip.util.Timing;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Handles the logic and rendering of gameplay.
 */
public final class GameScreen
        extends SwipScreen
{

    /** Maximum number of walls. */
    private static final int NUMBER_OF_WALLS = 4;

    /** Represents the left wall. */
    private static final byte LEFT_WALL = 0;
    /** Represents the top wall. */
    private static final byte TOP_WALL = 1;
    /** Represents the right wall. */
    private static final byte RIGHT_WALL = 2;
    /** Represents the bottom wall. */
    private static final byte BOTTOM_WALL = 3;

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;
    /** Handles gesture input events. */
    private GameGestureListener mGestureListener;

    /** Width of the screen. */
    private float mScreenWidth;
    /** Height of the screen. */
    private float mScreenHeight;

    /** Random number generator. */
    private Random mRandomGen = new Random();

    /** Current color of each of the walls. */
    private Color[] mWallColors;
    /** List of possible colors. */
    private List<Color> mPotentialColors;
    /** Number of milliseconds that has passed since the colors last changed. */
    private long mLastColorChangeTime = -1;
    /** Next color in the list to use for a wall. */
    private int mNextColor = 0;
    /** Randomly selected wall which the ball will match. */
    private int randomWall = 0;

    /** Color to indicate fling gesture detected. */
    private Color mGestureIndicatorColor;

    @Override
    public void tick()
    {
        if (TimeUtils.timeSinceMillis(mLastColorChangeTime) >= Timing.MILLISECONDS_IN_A_SECOND)
        {
            mLastColorChangeTime = TimeUtils.millis();
            for (int i = 0; i < mWallColors.length; i++)
            {
                mWallColors[i] = mPotentialColors.get(mNextColor++);
                if (mNextColor == mPotentialColors.size())
                    mNextColor = 0;
            }
            randomWall = mRandomGen.nextInt(NUMBER_OF_WALLS);
        }

        byte fling = mGestureListener.consumeFling();
        switch (fling)
        {
            case GameGestureListener.LEFT_FLING:
                mGestureIndicatorColor = Color.RED;
                break;
            case GameGestureListener.UP_FLING:
                mGestureIndicatorColor = Color.BLUE;
                break;
            case GameGestureListener.RIGHT_FLING:
                mGestureIndicatorColor = Color.GREEN;
                break;
            case GameGestureListener.DOWN_FLING:
                mGestureIndicatorColor = Color.YELLOW;
                break;
            default:
                mGestureIndicatorColor = null;
        }
    }

    @Override
    public void draw(float delta)
    {
        mShapeRenderer.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        final float wallSize = Math.min(mScreenWidth, mScreenHeight) * 0.1f;
        final float ballSize = Math.min(mScreenWidth, mScreenHeight) * 0.075f;

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Ball
        mShapeRenderer.setColor(mWallColors[randomWall]);
        mShapeRenderer.circle(mGestureListener.getLastFingerX(),
                mScreenHeight - mGestureListener.getLastFingerY(),
                ballSize);

        // Bottom wall
        mShapeRenderer.setColor(mWallColors[TOP_WALL]);
        mShapeRenderer.rect(0, 0, mScreenWidth, wallSize);

        // Top wall
        mShapeRenderer.setColor(mWallColors[BOTTOM_WALL]);
        mShapeRenderer.rect(0, mScreenHeight - wallSize, mScreenWidth, wallSize);

        // Left wall
        mShapeRenderer.setColor(mWallColors[LEFT_WALL]);
        mShapeRenderer.rect(0, wallSize, wallSize, mScreenHeight - 2 * wallSize);
        mShapeRenderer.triangle(0, 0, 0, wallSize, wallSize, wallSize);
        mShapeRenderer.triangle(0, mScreenHeight, 0, mScreenHeight - wallSize, wallSize, mScreenHeight - wallSize);

        // Right wall
        mShapeRenderer.setColor(mWallColors[RIGHT_WALL]);
        mShapeRenderer.rect(mScreenWidth - wallSize, wallSize, wallSize, mScreenHeight - 2 * wallSize);
        mShapeRenderer.triangle(mScreenWidth, 0, mScreenWidth, wallSize, mScreenWidth - wallSize, wallSize);
        mShapeRenderer.triangle(mScreenWidth,
                mScreenHeight,
                mScreenWidth,
                mScreenHeight - wallSize,
                mScreenWidth - wallSize,
                mScreenHeight - wallSize);

        // Gesture indicator
        if (mGestureIndicatorColor != null)
        {
            mShapeRenderer.setColor(mGestureIndicatorColor);
            mShapeRenderer.rect(wallSize, wallSize, wallSize, wallSize);
        }

        mShapeRenderer.end();
    }

    @Override
    public void show()
    {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        mShapeRenderer = new ShapeRenderer();

        // Creating gesture handler
        mGestureListener = new GameGestureListener();
        GestureDetector gestureDetector = new GestureDetector(mGestureListener);
        Gdx.input.setInputProcessor(gestureDetector);

        // Setting up colors for walls
        mPotentialColors = new ArrayList<>();
        mPotentialColors.add(Color.RED);
        mPotentialColors.add(Color.GRAY);
        mPotentialColors.add(Color.GOLD);
        mPotentialColors.add(Color.GREEN);
        mPotentialColors.add(Color.ORANGE);
        mPotentialColors.add(Color.PURPLE);
        mPotentialColors.add(Color.BLACK);
        mPotentialColors.add(Color.BLUE);
        mPotentialColors.add(Color.YELLOW);
        mPotentialColors.add(Color.MAGENTA);
        mPotentialColors.add(Color.PINK);
        mPotentialColors.add(Color.CHARTREUSE);
        mPotentialColors.add(Color.FOREST);
        Collections.shuffle(mPotentialColors);

        mWallColors = new Color[NUMBER_OF_WALLS];
    }

    @Override
    public void render(float delta)
    {
        tick();

        if (!wasDisposed())
            draw(delta);
    }

    @Override
    public void hide()
    {
        disposeEventually();

        // Disposing objects
        mShapeRenderer.dispose();
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    public GameScreen(SwipGame game)
    {
        super(game);
    }

    /**
     * States of the game.
     */
    private enum GameState
    {
        /** Indicates the game is paused. */
        Paused,
        /** Indicates the game is starting. */
        Starting,
        /** Indicates the game is active. */
        Active,
        /** Indicates the game is over. */
        GameOver
    }
}
