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
    private SwipGame mSwipGame;

    /** Indicates if the screen has been disposed. */
    private boolean mWasDisposed;

    /**
     * Stores a reference to the parameter provided.
     *
     * @param game instance of game
     */
    public SwipScreen(SwipGame game)
    {
        this.mSwipGame = game;
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
     * Updates the game logic.
     */
    abstract void tick();

    /**
     * Draws the game to the screen.
     *
     * @param delta delta time
     */
    abstract void draw(float delta);

    /**
     * Gets whether this screen is going to be disposed.
     * @return the value of {@code mWasDisposed}
     */
    boolean wasDisposed()
    {
        return mWasDisposed;
    }

    /**
     * Marks the screen to be disposed.
     */
    void disposeEventually()
    {
        mWasDisposed = true;
    }

    /**
     * Gets the current {@code SwipGame}.
     *
     * @return {@code mSwipGame}
     */
    SwipGame getSwipGame()
    {
        return mSwipGame;
    }
}
