package ca.josephroque.swip.manager;

import ca.josephroque.swip.entity.ButtonBall;
import ca.josephroque.swip.entity.Wall;
import ca.josephroque.swip.game.GameTexture;
import ca.josephroque.swip.input.GameInputProcessor;
import ca.josephroque.swip.screen.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

/**
 * Manages menu objects and rendering them to the screen.
 */
public class MenuManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MenuManager";

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Generates random numbers for the game. */
    private final Random mRandomNumberGenerator = new Random();

    /** Manages textures for game objects. */
    private GameTexture mGameTextures;

    /** Buttons for menu options. */
    private ButtonBall[] mMenuOptionBalls;

    /**
     * Sets up a new main menu.
     *
     * @param gameTexture textures for game objects
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public MenuManager(GameTexture gameTexture, int screenWidth, int screenHeight) {
        mGameTextures = gameTexture;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mMenuOptionBalls = new ButtonBall[2];
        mMenuOptionBalls[MenuBallOptions.Music.ordinal()]
                = new ButtonBall(MenuBallOptions.Music,
                GameTexture.GameColor.Red,
                mGameTextures.getButtonTexture(MenuBallOptions.Music, ))
        mMenuOptionBalls[MenuBallOptions.SoundEffects.ordinal()]
                = new ButtonBall(MenuBallOptions.SoundEffects,
                GameTexture.GameColor.Red,
                mGameTextures.getButtonTexture(MenuBallOptions.SoundEffects, ))
    }

    /**
     * Updates the logic of the main menu based on the current state.
     *
     * @param gameState state of the game
     * @param gameInput player's input events
     * @param delta number of seconds the last rendering took
     */
    public void tick(GameScreen.GameState gameState, GameInputProcessor gameInput, float delta) {
        for (ButtonBall option : mMenuOptionBalls) {
            if (option.wasClicked(gameInput)) {
                switch (option.getMenuOption()) {
                    case Music:
                        break;
                    case SoundEffects:
                        break;
                    default:
                        throw new IllegalArgumentException("menu option not valid.");
                }
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
            option.draw(spriteBatch, mGameTextures);
        }
    }

    /**
     * Available options for the ball menu items.
     */
    public enum MenuBallOptions {
        /** Represents the menu option for enabling or disabling music. */
        Music,
        /** Represents the menu option for enabling or disabling sound effects. */
        SoundEffects,
    }
}
