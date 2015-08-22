package ca.josephroque.swip;

import ca.josephroque.swip.screen.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private OrthographicCamera camera;
    /** Default viewport of the game. */
    private Viewport viewport;

    /** Instance of the main menu. */
    private MainMenuScreen mainMenuScreen;

    /** Current state of the game. */
    private SwipState currentState;

    @Override
    public void create()
    {
        // Setting up the game rendering
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply(true);

        // Getting singleton instances of the screens
        mainMenuScreen = MainMenuScreen.getInstance(this);

        // Opens the main menu when the application begins
        setState(SwipState.MainMenu);
    }

    @Override
    public final void setScreen(Screen newScreen)
    {
        throw new IllegalStateException("You should instead use SwipGame.setState(SwipState)");
    }

    /**
     * Starts a new state of the game.
     *
     * @param nextState new state
     */
    public void setState(SwipState nextState)
    {
        if (currentState == nextState)
            return;

        currentState = nextState;
        switch (nextState)
        {
            case MainMenu:
                super.setScreen(mainMenuScreen);
                break;
            case Game:
                // super.setScreen(gameScreen);
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
