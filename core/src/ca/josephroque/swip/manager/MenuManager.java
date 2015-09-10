package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.BasicBall;
import ca.josephroque.swip.entity.ButtonBall;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages menu objects and rendering them to the screen.
 */
public class MenuManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MenuManager";

    /** Instance of callback interface. */
    private MenuCallback mCallback;

    /** Buttons for menu options. */
    private ButtonBall[] mMenuOptionBalls;

    /**
     * Sets up a new main menu.
     *
     * @param callback instance of callback interface
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public MenuManager(MenuCallback callback, int screenWidth, int screenHeight) {
        mCallback = callback;

        mMenuOptionBalls = new ButtonBall[MenuBallOptions.getSize()];
        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()]
                = new ButtonBall(MenuBallOptions.MusicOn,
                TextureManager.GameColor.Green,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.MusicOn),
                screenWidth / 2 - BasicBall.getDefaultBallRadius() * 2,
                screenHeight / 2);
        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()]
                = new ButtonBall(MenuBallOptions.MusicOff,
                TextureManager.GameColor.Red,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.MusicOff),
                screenWidth / 2 - BasicBall.getDefaultBallRadius() * 2,
                screenHeight / 2);
        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()]
                = new ButtonBall(MenuBallOptions.SoundEffectsOn,
                TextureManager.GameColor.Green,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.SoundEffectsOn),
                screenWidth / 2 + BasicBall.getDefaultBallRadius() * 2,
                screenHeight / 2);
        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()]
                = new ButtonBall(MenuBallOptions.SoundEffectsOff,
                TextureManager.GameColor.Red,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.SoundEffectsOff),
                screenWidth / 2 + BasicBall.getDefaultBallRadius() * 2,
                screenHeight / 2);
    }

    /**
     * Updates the logic of the main menu based on the current state.
     *
     * @param gameState state of the game
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    public void tick(GameScreen.GameState gameState, GameInputProcessor gameInput, float delta) {
        if (gameState != GameScreen.GameState.MainMenu && gameState != GameScreen.GameState.GamePaused)
            throw new IllegalStateException("Invalid state for updating menu.");

        boolean optionSelected = false;
        for (ButtonBall option : mMenuOptionBalls) {
            option.tick(delta);

            if (option.wasClicked(gameInput)) {
                optionSelected = true;
                switch (option.getMenuOption()) {
                    case MusicOn:
                        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()].shrink();
                        mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()].grow();
                        if (mCallback != null)
                            mCallback.setMusicEnabled(false);
                        break;
                    case MusicOff:
                        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()].grow();
                        mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()].shrink();
                        if (mCallback != null)
                            mCallback.setMusicEnabled(true);
                        break;
                    case SoundEffectsOn:
                        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()].shrink();
                        mMenuOptionBalls[MenuBallOptions.SoundEffectsOff.ordinal()].grow();
                        if (mCallback != null)
                            mCallback.setSoundEffectsEnabled(false);
                        break;
                    case SoundEffectsOff:
                        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()].grow();
                        mMenuOptionBalls[MenuBallOptions.SoundEffectsOff.ordinal()].shrink();
                        if (mCallback != null)
                            mCallback.setSoundEffectsEnabled(true);
                        break;
                    default:
                        throw new IllegalArgumentException("menu option not valid.");
                }

                // Only one option can be selected, so the loop exits
                break;
            }
        }

        if (!optionSelected) {
            // Starts the game if no other option was selected
            if (Gdx.input.justTouched() && mCallback != null) {
                if (gameState == GameScreen.GameState.GamePaused)
                    mCallback.resumeGame();
                else
                    mCallback.startGame();
            }
        }
    }

    /**
     * Draws the menu to the screen.
     *
     * @param gameState the current state of the application
     * @param spriteBatch graphics context to draw to
     */
    public void draw(GameScreen.GameState gameState, SpriteBatch spriteBatch) {
        for (ButtonBall option : mMenuOptionBalls) {
            option.draw(spriteBatch);
        }
    }

    /**
     * Frees references to objects.
     */
    public void dispose() {
        mCallback = null;
    }

    /**
     * Available options for the ball menu items.
     */
    public enum MenuBallOptions {
        /** Represents the menu option for when music is enabled. */
        MusicOn,
        /** Represents the menu option for when music is disabled. */
        MusicOff,
        /** Represents the menu option for when sound effects are enabled. */
        SoundEffectsOn,
        /** Represents the menu option for when sound effects are disabled. */
        SoundEffectsOff;

        /** Size of the enum. */
        private static final int SIZE = MenuBallOptions.values().length;

        /**
         * Gets the size of the enum.
         *
         * @return number of {@code MenuBallOptions}
         */
        public static int getSize() {
            return SIZE;
        }
    }

    /**
     * Provides callback methods for menu interactions.
     */
    public interface MenuCallback {

        /**
         * Should enable or disable the music of the game.
         *
         * @param enabled {@code true} if the music has been enabled through the menu, {@code false} if disabled
         */
        void setMusicEnabled(boolean enabled);

        /**
         * Should enable or disable the sound effects of the game.
         *
         * @param enabled {@code true} if the sound effects have  been enabled through the menu, {@code false} if
         * disabled
         */
        void setSoundEffectsEnabled(boolean enabled);

        /**
         * Should start a new game.
         */
        void startGame();

        /**
         * Returns to playing the current game.
         */
        void resumeGame();
    }

}
