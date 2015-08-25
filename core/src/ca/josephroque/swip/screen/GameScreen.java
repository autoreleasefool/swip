package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles the logic and rendering of gameplay.
 */
public final class GameScreen
        extends SwipScreen
{

    private static final int NUMBER_OF_WALLS = 4;

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;

    /** Width of the screen. */
    private float mScreenWidth;
    /** Height of the screen. */
    private float mScreenHeight;

    private Color[] mWallColors;
    private List<Color> mPotentialColors;
    private long mLastColorChangeTime = -1;
    private int mNextColor = 0;

    @Override
    public void tick()
    {
        if (TimeUtils.timeSinceMillis(mLastColorChangeTime) >= 1000)
        {
            mLastColorChangeTime = TimeUtils.millis();
            for (int i = 0; i < mWallColors.length; i++)
            {
                mWallColors[i] = mPotentialColors.get(mNextColor++);
                if (mNextColor == mPotentialColors.size())
                    mNextColor = 0;
            }
        }

        if (Gdx.input.justTouched())
        {
            getSwipGame().setState(SwipGame.SwipState.Game);
            dispose();
        }
    }

    @Override
    public void draw(float delta)
    {
        mShapeRenderer.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        final float wallSize = Math.min(mScreenWidth, mScreenHeight) * 0.1f;

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Top wall
        mShapeRenderer.setColor(mWallColors[0]);
        mShapeRenderer.rect(0, 0, mScreenWidth, wallSize);

        // Bottom wall
        mShapeRenderer.setColor(mWallColors[1]);
        mShapeRenderer.rect(0, mScreenHeight - wallSize, mScreenWidth, wallSize);

        // Left wall
        mShapeRenderer.setColor(mWallColors[2]);
        mShapeRenderer.rect(0, wallSize, wallSize, mScreenHeight - 2 * wallSize);
        mShapeRenderer.triangle(0, 0, 0, wallSize, wallSize, wallSize);
        mShapeRenderer.triangle(0, mScreenHeight, 0, mScreenHeight - wallSize, wallSize, mScreenHeight - wallSize);

        // Right wall
        mShapeRenderer.setColor(mWallColors[3]);
        mShapeRenderer.rect(mScreenWidth - wallSize, wallSize, wallSize, mScreenHeight - 2 * wallSize);
        mShapeRenderer.triangle(mScreenWidth, 0, mScreenWidth, wallSize, mScreenWidth - wallSize, wallSize);
        mShapeRenderer.triangle(mScreenWidth, mScreenHeight, mScreenWidth, mScreenHeight - wallSize, mScreenWidth - wallSize, mScreenHeight - wallSize);

        mShapeRenderer.end();
    }

    @Override
    public void show()
    {
        mWasDisposed = false;
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        mShapeRenderer = new ShapeRenderer();

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

        if (!mWasDisposed)
            draw(delta);
    }

    @Override
    public void hide()
    {
        mWasDisposed = true;

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
