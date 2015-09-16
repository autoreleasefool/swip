package ca.josephroque.swip.manager;

import ca.josephroque.swip.util.PreferenceUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Manages music loading and playback.
 */
public final class MusicManager {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "MusicManager";

    /** Preference identifier to indicate if music has been disabled by the user. */
    private static final String MUSIC_ENABLED = "music_enabled";
    /** Preference identifier to indicate if sound effects have been disabled by the user. */
    private static final String SFX_ENABLED = "sfx_enabled";

    /** Number of seconds a song will take to fade out or in. */
    private static final float FADE_SPEED = 1f;

    /** Primary background music for the application. */
    private static Music sBackgroundMusic;

    /** The current background track being played by {@code sBackgroundMusic}. */
    private static BackgroundTrack sCurrentBackgroundTrack;
    /** The background track to be played next. */
    private static BackgroundTrack sNextBackgroundTrack;
    /** Number of seconds that a song has been fading for. */
    private static float sFadeTime = 1f;

    /** Indicates if music playback has been enabled or disabled. */
    private static boolean sMusicEnabled;
    /** Indicates if sound effect playback has been enabled or disabled. */
    private static boolean sSoundEffectsEnabled;

    /** Sound effects in the application. */
    private static Sound[] sSoundEffects = new Sound[SoundEffect.getSize()];

    /**
     * Loads background music and sound effects for the game.
     *
     * @param initialBackgroundTrack initial background song to load
     */
    public static void initialize(BackgroundTrack initialBackgroundTrack) {
        sBackgroundMusic = loadBackgroundMusic(initialBackgroundTrack);
        sCurrentBackgroundTrack = initialBackgroundTrack;

        Preferences preferences = Gdx.app.getPreferences(PreferenceUtils.PREFERENCES);
        sMusicEnabled = preferences.getBoolean(MUSIC_ENABLED, true);
        sMusicEnabled = preferences.getBoolean(SFX_ENABLED, true);

        // Loading sounds
        SoundEffect[] soundEffects = SoundEffect.values();
        for (int i = 0; i < sSoundEffects.length; i++) {
            sSoundEffects[i] = loadSound(soundEffects[i]);
        }
    }

    /**
     * Enables or disables the playback of music. Persists across instances of the application.
     *
     * @param enabled {@code true} to allow playback of music
     */
    public static void setMusicPlaybackEnabled(boolean enabled) {
        if (sMusicEnabled != enabled) {
            sMusicEnabled = enabled;

            if (!enabled) {
                sFadeTime = FADE_SPEED;
                stopBackgroundMusic();
            }

            // Updating preferences
            Preferences preferences = Gdx.app.getPreferences(PreferenceUtils.PREFERENCES);
            preferences.putBoolean(MUSIC_ENABLED, enabled);
            preferences.flush();
        }
    }

    /**
     * Checks if the user has enabled or disabled the playback of music.
     *
     * @return {@code true} if music playback is enabled, {@code false} otherwise
     */
    public boolean isMusicPlaybackEnabled() {
        return sMusicEnabled;
    }

    /**
     * Enables or disables the playback of sound effects. Persists across instances of the application.
     *
     * @param enabled {@code true} to allow playback of sound effects
     */
    public static void setSoundEffectPlaybackEnabled(boolean enabled) {
        if (sSoundEffectsEnabled != enabled) {
            sSoundEffectsEnabled = enabled;

            // Updating preferences
            Preferences preferences = Gdx.app.getPreferences(PreferenceUtils.PREFERENCES);
            preferences.putBoolean(SFX_ENABLED, enabled);
            preferences.flush();
        }
    }

    /**
     * Checks if the user has enabled or disabled the playback of sound effects.
     *
     * @return {@code true} if sound effect playback is enabled, {@code false} otherwise
     */
    public boolean isSoundEffectPlaybackEnabled() {
        return sSoundEffectsEnabled;
    }

    /**
     * Plays a sound effect once.
     *
     * @param sound sound effect to play
     */
    public static void playSoundEffect(SoundEffect sound) {
        if (!sSoundEffectsEnabled)
            return;

        sSoundEffects[sound.ordinal()].play();
    }

    /**
     * If the background music is not currently playing, starts it.
     */
    public static void playBackgroundMusic() {
        if (!sMusicEnabled)
            return;

        if (!sBackgroundMusic.isPlaying())
            sBackgroundMusic.play();
    }

    /**
     * If the background music is currently playing, pauses it.
     */
    public static void pauseBackgroundMusic() {
        if (sBackgroundMusic.isPlaying())
            sBackgroundMusic.pause();
    }

    /**
     * If the background music is currently playing, stops it.
     */
    public static void stopBackgroundMusic() {
        if (sBackgroundMusic.isPlaying())
            sBackgroundMusic.stop();
    }

    /**
     * Fades a new song in and fades out the currently playing music.
     *
     * @param track track to fade in
     */
    public static void fadeInSong(BackgroundTrack track) {
        if (!sMusicEnabled)
            return;

        // Checks if the music is already fading
        if (sFadeTime < FADE_SPEED)
            return;

        if (track != sCurrentBackgroundTrack) {
            sNextBackgroundTrack = track;
            sFadeTime = 0f;
        }
    }

    /**
     * Updates volume of fading songs.
     *
     * @param delta number of seconds last rendering took
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"}) // Avoid Java 8 for Android
    public static void tick(float delta) {
        if (!sMusicEnabled)
            return;

        if (sBackgroundMusic.isPlaying() && sFadeTime < FADE_SPEED * 2) {
            if (sFadeTime < FADE_SPEED) {
                sBackgroundMusic.setVolume((-sFadeTime + FADE_SPEED) / FADE_SPEED);
            } else {
                if (sNextBackgroundTrack != null) {
                    sCurrentBackgroundTrack = sNextBackgroundTrack;
                    sNextBackgroundTrack = null;

                    sBackgroundMusic.stop();
                    sBackgroundMusic.dispose();
                    sBackgroundMusic = loadBackgroundMusic(sCurrentBackgroundTrack);
                    sBackgroundMusic.setLooping(true);
                    sBackgroundMusic.play();
                }
                sBackgroundMusic.setVolume(sFadeTime / FADE_SPEED);
            }
            sFadeTime += delta;
        }  else if (sBackgroundMusic.getVolume() < 1) {
            sBackgroundMusic.setVolume(1);
        }
    }

    /**
     * Loads a single background music track from the game assets.
     *
     * @param track track to load
     * @return the background music
     */
    private static Music loadBackgroundMusic(BackgroundTrack track) {
        return Gdx.audio.newMusic(Gdx.files.internal("audio/bm/" + track + ".mp3"));
    }

    /**
     * Loads a single sound effect from the game assets.
     *
     * @param sound sound effect to load
     * @return the sound effect
     */
    private static Sound loadSound(SoundEffect sound) {
        return Gdx.audio.newSound(Gdx.files.internal("audio/sfx/" + sound + ".wav"));
    }

    /**
     * Frees resources from objects.
     */
    public static void dispose() {
        if (sBackgroundMusic != null)
            sBackgroundMusic.dispose();

        for (Sound sound : sSoundEffects)
            sound.dispose();
    }

    /**
     * Default private constructor.
     */
    private MusicManager() {
        // does nothing
    }

    /**
     * Background music available to be played by the application.
     */
    public enum BackgroundTrack {
        /** First background track. */
        One,
        /** Second background track. */
        Two,
        /** Third background track. */
        Three
    }

    /**
     * Sound effects available to be played by the application.
     */
    public enum SoundEffect {
        /** Sound corresponding to a point earned. */
        PointEarned;

        /** Number of available sound effects. */
        private static final int SIZE = SoundEffect.values().length;

        /**
         * Gets the number of available sound effects.
         *
         * @return number of available sound effects
         */
        public static int getSize() {
            return SIZE;
        }
    }
}
