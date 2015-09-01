package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import ca.josephroque.swip.entity.Ball;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.gesture.GameGestureListener;

import ca.josephroque.swip.util.Timing;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

/**
 * Handles the logic and rendering of gameplay.
 */
public final class GameScreen
        extends SwipScreen {

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;
    /** Handles gesture input events. */
    private GameGestureListener mGestureListener;

    /** Width of the screen. */
    private float mScreenWidth;
    /** Height of the screen. */
    private float mScreenHeight;
    /** The current state the game is in. */
    private GameState mCurrentGameState;

    /** Random number generator. */
    private Random mRandomGen = new Random();

    /** Current color of each of the walls. */
    private Color[] mWallColors = new Color[Wall.NUMBER_OF_WALLS];

    /** Current wall which the ball can pass through. */
    private int mWallForBall;
    /** Coordinates for the ball to be drawn at. */
    private float[] mBallPosition = new float[2];

    /** Indicates if the user has flung the ball. */
    private boolean mFlinging;
    /** Starting x and y location of a fling. */
    private float[] mFlingStartLocation = new float[2];
    /** Target x and y location for the end of a fling. */
    private float[] mFlingTargetLocation = new float[2];
    /** Current travelling velocity of a flung ball. */
    private float[] mFlingVelocity = new float[2];

    private long mLastColorChangeTime = 0;

    @Override
    public void tick() {
        final float ballSize = Ball.getBallSize(mScreenWidth, mScreenHeight);

        if (TimeUtils.timeSinceMillis(mLastColorChangeTime) >= Timing.MILLISECONDS_IN_A_SECOND)
        {
            mLastColorChangeTime = TimeUtils.millis();
            Wall.getRandomWallColors(mRandomGen, mWallColors, false);
            mWallForBall = mRandomGen.nextInt(Wall.NUMBER_OF_WALLS);
        }

        GameGestureListener.FlingDirection flingDirection = mGestureListener.consumeFling();
        attemptToFling(flingDirection);

        if (!mFlinging) {
            Ball.getLocationForBall(mScreenWidth / 2,
                    mScreenHeight / 2,
                    mGestureListener.getLastFingerX(),
                    mGestureListener.getLastFingerY(),
                    ballSize,
                    mBallPosition);
        }
    }

    @Override
    public void draw(float delta) {
        mShapeRenderer.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        final float wallSize = Wall.getWallSize(mScreenWidth, mScreenHeight);
        final float ballSize = Ball.getBallSize(mScreenWidth, mScreenHeight);

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Ball.drawBallAndTimer(mShapeRenderer, mWallColors[mWallForBall], mBallPosition[0], mBallPosition[1], ballSize, 360f);
        Wall.drawWalls(mShapeRenderer, mWallColors, mScreenWidth, mScreenHeight, wallSize);

        mShapeRenderer.end();
    }

    @Override
    public void show() {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();
        mCurrentGameState = GameState.Starting;

        mShapeRenderer = new ShapeRenderer();

        // Creating gesture handler
        mGestureListener = new GameGestureListener();
        GestureDetector gestureDetector = new GestureDetector(mGestureListener);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void render(float delta) {
        tick();

        if (!wasDisposed())
            draw(delta);
    }

    @Override
    public void hide() {
        disposeEventually();

        // Disposing objects
        mShapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
        mGestureListener.resize(width, height);
    }

    /**
     * Checks if the user flung the ball and, if yes, starts a fling event.
     *
     * @param flingDirection direction of the fling
     */
    private void attemptToFling(GameGestureListener.FlingDirection flingDirection) {
        // User has already flung ball
        if (mFlinging)
            return;

        final float wallSize = Wall.getWallSize(mScreenWidth, mScreenHeight);
        switch (flingDirection) {
            case LEFT:
                mFlinging = true;
                mFlingTargetLocation[0] = wallSize;
                mFlingTargetLocation[1] = mScreenHeight / 2;
                break;
            case UP:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth / 2;
                mFlingTargetLocation[1] = mScreenHeight - wallSize;
                break;
            case RIGHT:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth - wallSize;
                mFlingTargetLocation[1] = mScreenHeight / 2;
                break;
            case DOWN:
                mFlinging = true;
                mFlingTargetLocation[0] = mScreenWidth / 2;
                mFlingTargetLocation[1] = wallSize;
                break;
            default:
                // does nothing
        }

        if (mFlinging) {
            mFlingStartLocation[0] = mBallPosition[0];
            mFlingStartLocation[1] = mBallPosition[1];
        }
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    public GameScreen(SwipGame game) {
        super(game);
    }

    /**
     * States of the game.
     */
    private enum GameState {
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
