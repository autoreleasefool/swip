package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import com.badlogic.gdx.Screen;

/**
 * Provides default methods for creating a {@link com.badlogic.gdx.Screen}.
 */
public abstract class SwipScreen
        implements Screen
{

    /** The current instance of the game. */
    private SwipGame swipGame;

    /**
     * Stores a reference to the parameter provided.
     *
     * @param game instance of game
     */
    public SwipScreen(SwipGame game)
    {
        this.swipGame = game;
    }

    @Override
    public void resize(int width, int height)
    {
        // does nothing
    }

    @Override
    public void show()
    {
        // does nothing
    }

    @Override
    public void hide()
    {
        // does nothing
    }

    @Override
    public void pause()
    {
        // does nothing
    }

    @Override
    public void resume()
    {
        // does nothing
    }

    @Override
    public void dispose()
    {
        // does nothing
    }

    /**
     * Gets the current {@code SwipGame}.
     *
     * @return {@code swipGame}
     */
    SwipGame getSwipGame()
    {
        return swipGame;
    }
}
