package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Displays a main menu when the user begins the app. Provides options such as starting a new game, viewing high
 * scores...
 */
public final class MainMenuScreen
        extends SwipScreen
{

    /** Allows rendering of basic shapes on the screen. */
    private ShapeRenderer mShapeRenderer;
    private BitmapFont mBitmapFont;
    private SpriteBatch mSpriteBatch;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    @Override
    public void tick()
    {
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
        mSpriteBatch.setProjectionMatrix(getSwipGame().getCameraCombinedMatrix());

        mSpriteBatch.begin();
        mBitmapFont.draw(mSpriteBatch, "Start", 100, 100);
        mSpriteBatch.end();
    }

    @Override
    public void show()
    {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();

        mShapeRenderer = new ShapeRenderer();
        mSpriteBatch = new SpriteBatch();
        mBitmapFont = new BitmapFont();
    }

    @Override
    public void render(float delta)
    {
        tick();

        if (!wasDisposed())
            draw(delta);
    }

    @Override
    public void dispose()
    {
        disposeEventually();

        // Disposing objects
        mShapeRenderer.dispose();
        mSpriteBatch.dispose();
        mBitmapFont.dispose();
    }

    @Override
    public void resize(int width, int height)
    {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    public MainMenuScreen(SwipGame game)
    {
        super(game);
    }
}
