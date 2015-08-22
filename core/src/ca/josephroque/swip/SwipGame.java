package ca.josephroque.swip;

import ca.josephroque.swip.screen.MainMenuScreen;
import com.badlogic.gdx.Game;

/**
 * Initial starting point of the game. Delegates functionality to relevant {@link ca.josephroque.swip.screen.SwipScreen}
 * instances.
 */
public class SwipGame
        extends Game
{

    @Override
    public void create()
    {
        // Opens the main menu when the application begins
        setScreen(new MainMenuScreen(this));
    }
}
