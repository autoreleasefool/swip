package ca.josephroque.swip;

import ca.josephroque.swip.screen.GameScreen;
import ca.josephroque.swip.screen.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Initial starting point of the game. Delegates functionality to relevant {@link ca.josephroque.swip.screen.SwipScreen}
 * instances.
 */
public class SwipGame
        extends Game
{

    /** Primary camera of the game. */
    private OrthographicCamera mPrimaryCamera;
    /** Default viewport of the game. */
    private Viewport mPrimaryViewport;

    /** Current state of the game. */
    private SwipState mCurrentState;

    @Override
    public void create()
    {
        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();

        // Setting up the game rendering
        mPrimaryCamera = new OrthographicCamera();
        mPrimaryCamera.translate(screenWidth / 2, screenHeight / 2);
        mPrimaryCamera.setToOrtho(false, screenWidth, screenHeight);
        mPrimaryViewport = new ScreenViewport(mPrimaryCamera);
        mPrimaryViewport.apply();

        // Opens the main menu when the application begins
        setState(SwipState.MainMenu);
    }

    @Override
    public void resize(int width, int height)
    {
        mPrimaryViewport.update(width, height);
    }

    @Override
    public void render()
    {
        mPrimaryCamera.update();

        // Clear the screen to white
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

        super.render();
    }

    @Override
    public final void setScreen(Screen newScreen)
    {
        throw new IllegalStateException("You should instead use SwipGame.setState(SwipState)");
    }

    /**
     * Gets {@link com.badlogic.gdx.graphics.OrthographicCamera#combined} from {@code mPrimaryCamera}.
     *
     * @return {@code mPrimaryCamera.combined}
     */
    public Matrix4 getCameraCombinedMatrix()
    {
        return mPrimaryCamera.combined;
    }

    /**
     * Starts a new state of the game.
     *
     * @param nextState new state
     */
    public void setState(SwipState nextState)
    {
        if (mCurrentState == nextState)
            return;

        mCurrentState = nextState;
        switch (nextState)
        {
            case MainMenu:
                super.setScreen(new MainMenuScreen(this));
                break;
            case Game:
                super.setScreen(new GameScreen(this));
                break;
            case GameOver:
                // super.setScreen(gameOverScreen);
                break;
            default:
                throw new IllegalArgumentException("invalid state: " + nextState);
        }
    }

    /**
     * Possible states of the application.
     */
    public enum SwipState
    {
        /** Represents the game being in a main menu. */
        MainMenu,
        /** Represents the game being in an active state - i.e. being played. */
        Game,
        /** Represents the game being over. */
        GameOver,
    }
}
