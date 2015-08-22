package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;

/**
 * Handles the logic and rendering of gameplay.
 */
public final class GameScreen
        extends SwipScreen
{

    /** Singleton instance of the game. */
    private static GameScreen sInstance;

    @Override
    public void show()
    {

    }

    @Override
    public void render(float delta)
    {

    }

    @Override
    public void hide()
    {

    }

    /**
     * Gets a singleton instance of the game. Creates a new instance if one is not currently available.
     *
     * @param game the current game
     * @return instance of {@code GameScreen}
     */
    public static GameScreen getInstance(SwipGame game)
    {
        if (sInstance == null)
            sInstance = new GameScreen(game);

        return sInstance;
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    private GameScreen(SwipGame game)
    {
        super(game);
    }
}
