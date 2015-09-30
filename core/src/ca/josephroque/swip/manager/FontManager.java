package ca.josephroque.swip.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Manages font loading and unloading.
 */
public final class FontManager {

    private static BitmapFont sFontKenney;

    public static void initialize() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/KenVectorFutureThin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.BLACK;
        sFontKenney = generator.generateFont(parameter);
        generator.dispose();
    }

    public static BitmapFont getDefaultFont() {
        return sFontKenney;
    }

    public static void dispose() {
        sFontKenney.dispose();
    }

    /**
     * Default private constructor.
     */
    private FontManager() {
        // does nothing
    }
}
