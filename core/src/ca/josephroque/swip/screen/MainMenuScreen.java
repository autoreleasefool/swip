package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;

/**
 * Displays a main menu when the user begins the app. Provides options such as starting a new game, viewing high
 * scores...
 */
public final class MainMenuScreen
        extends SwipScreen
{

    /** Singleton instance of the main menu. */
    private static MainMenuScreen sInstance;

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
     * Gets a singleton instance of the main menu. Creates a new instance if one is not currently available.
     *
     * @param game the current game
     * @return instance of {@code MainMenuScreen}
     */
    public static MainMenuScreen getInstance(SwipGame game)
    {
        if (sInstance == null)
            sInstance = new MainMenuScreen(game);

        return sInstance;
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    private MainMenuScreen(SwipGame game)
    {
        super(game);
    }
}
