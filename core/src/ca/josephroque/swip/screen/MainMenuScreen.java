package ca.josephroque.swip.screen;

import ca.josephroque.swip.SwipGame;
import com.badlogic.gdx.Gdx;

/**
 * Displays a main menu when the user begins the app. Provides options such as starting a new game, viewing high
 * scores...
 */
public final class MainMenuScreen
        extends SwipScreen {

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    @Override
    public void tick(float delta) {
        if (Gdx.input.justTouched()) {
            getSwipGame().setState(SwipGame.SwipState.Game);
            dispose();
        }
    }

    @Override
    public void draw() {
    }

    @Override
    public void show() {
        mScreenWidth = Gdx.graphics.getWidth();
        mScreenHeight = Gdx.graphics.getHeight();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    @Override
    public void render(float delta) {
        tick(delta);

        if (!wasDisposed())
            draw();
    }

    /**
     * Passes parameters to super constructor.
     *
     * @param game instance of game
     */
    public MainMenuScreen(SwipGame game) {
        super(game);
    }
}
