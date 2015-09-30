package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.BasicBall;
import ca.josephroque.swip.entity.ButtonBall;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.screen.GameScreen;
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

    /** When a button ball finishes shrinking, this causes the opposing option to grow in its place. */
    @SuppressWarnings("FieldCanBeLocal")
    private BasicBall.ScalingCompleteListener mMenuOptionBallsListener = new BasicBall.ScalingCompleteListener() {
        @Override
        public void onScalingCompleted(BasicBall ball, boolean growingOrShrinking) {
            if (growingOrShrinking)
                return;

            for (ButtonBall buttonBall : mMenuOptionBalls) {
                if (buttonBall == ball) {
                    switch (buttonBall.getMenuOption()) {
                        case MusicOn:
                            mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()].grow();
                            break;
                        case MusicOff:
                            mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()].grow();
                            break;
                        case SoundEffectsOn:
                            mMenuOptionBalls[MenuBallOptions.SoundEffectsOff.ordinal()].grow();
                            break;
                        case SoundEffectsOff:
                            mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()].grow();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid menu option");
                    }
                }
            }
        }

        @Override
        public void interrupted(BasicBall ball) {
            // does nothing
        }
    };

    /**
     * Sets up a new main menu.
     *
     * @param callback instance of callback interface
     */
    public MenuManager(MenuCallback callback) {
        mCallback = callback;

        BasicBall.initialize(GameScreen.getScreenWidth(), GameScreen.getScreenHeight());
        mMenuOptionBalls = new ButtonBall[MenuBallOptions.getSize()];
        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()]
                = new ButtonBall(MenuBallOptions.MusicOn,
                TextureManager.GameColor.Green,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.MusicOn),
                GameScreen.getScreenWidth() / 2 - BasicBall.getDefaultBallRadius() * 2,
                GameScreen.getScreenHeight() / 2);
        mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()]
                = new ButtonBall(MenuBallOptions.MusicOff,
                TextureManager.GameColor.Red,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.MusicOff),
                GameScreen.getScreenWidth() / 2 - BasicBall.getDefaultBallRadius() * 2,
                GameScreen.getScreenHeight() / 2);
        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()]
                = new ButtonBall(MenuBallOptions.SoundEffectsOn,
                TextureManager.GameColor.Green,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.SoundEffectsOn),
                GameScreen.getScreenWidth() / 2 + BasicBall.getDefaultBallRadius() * 2,
                GameScreen.getScreenHeight() / 2);
        mMenuOptionBalls[MenuBallOptions.SoundEffectsOff.ordinal()]
                = new ButtonBall(MenuBallOptions.SoundEffectsOff,
                TextureManager.GameColor.Red,
                TextureManager.getMenuButtonIconTexture(MenuBallOptions.SoundEffectsOff),
                GameScreen.getScreenWidth() / 2 + BasicBall.getDefaultBallRadius() * 2,
                GameScreen.getScreenHeight() / 2);
        for (ButtonBall ball : mMenuOptionBalls)
            ball.setScalingCompleteListener(mMenuOptionBallsListener);
    }

    /**
     * Updates the logic of the main menu based on the current state.
     *
     * @param gameState state of the game
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    public void tick(GameScreen.GameState gameState, GameInputProcessor gameInput, float delta) {
        if (gameState != GameScreen.GameState.Ended && gameState != GameScreen.GameState.GamePaused
                && gameState != GameScreen.GameState.MainMenu)
            throw new IllegalStateException("Invalid state for updating menu.");

        boolean optionSelected = false;
        for (ButtonBall option : mMenuOptionBalls) {
            option.tick(delta);

            if (option.wasClicked(gameInput)) {
                optionSelected = true;
                switch (option.getMenuOption()) {
                    case MusicOn:
                        mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()].shrink();
                        if (mCallback != null)
                            mCallback.setMusicEnabled(false);
                        break;
                    case MusicOff:
                        mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()].shrink();
                        if (mCallback != null)
                            mCallback.setMusicEnabled(true);
                        break;
                    case SoundEffectsOn:
                        mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()].shrink();
                        if (mCallback != null)
                            mCallback.setSoundEffectsEnabled(false);
                        break;
                    case SoundEffectsOff:
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
            if (gameInput.clickOccurred() && mCallback != null) {
                if (gameState == GameScreen.GameState.GamePaused)
                    mCallback.resumeGame();
                else
                    mCallback.prepareNewGame();
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
        for (ButtonBall option : mMenuOptionBalls)
            option.draw(spriteBatch);

        FontManager.getDefaultFont()
                .draw(spriteBatch, "Tap to begin", GameScreen.getScreenWidth() / 2, GameScreen.getScreenHeight() / 2);
    }

    /**
     * Resets menu items to an initial state to be animated again.
     */
    public void resetMenuItems() {
        for (ButtonBall option : mMenuOptionBalls)
            option.hide();

        if (MusicManager.isMusicPlaybackEnabled())
            mMenuOptionBalls[MenuBallOptions.MusicOn.ordinal()].grow();
        else
            mMenuOptionBalls[MenuBallOptions.MusicOff.ordinal()].grow();

        if (MusicManager.isSoundEffectPlaybackEnabled())
            mMenuOptionBalls[MenuBallOptions.SoundEffectsOn.ordinal()].grow();
        else
            mMenuOptionBalls[MenuBallOptions.SoundEffectsOff.ordinal()].grow();
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
        void prepareNewGame();

        /**
         * Returns to playing the current game.
         */
        void resumeGame();

        /**
         * Should get the high score of the user.
         *
         * @return high score
         */
        int getHighScore();

        /**
         * Should get the most recent score obtained by the user in a game.
         *
         * @return most recent score
         */
        int getMostRecentScore();
    }

}
