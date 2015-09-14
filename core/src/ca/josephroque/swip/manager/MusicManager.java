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

    /** Number of seconds fading two songs will take. */
    private static final float FADE_SPEED = 1f;

    /** Primary background music for the application. */
    private static Music sBackgroundMusic;
    /** Next song to be played in the application, pre loaded to transition smoothly. */
    private static Music sNextBackgroundMusic;

    /** The current background track being played by {@code sBackgroundMusic}. */
    private static BackgroundTrack sCurrentBackgroundTrack;
    /** The background track being faded in. */
    private static BackgroundTrack sFadingBackgroundTrack;
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
                if (sNextBackgroundMusic != null)
                    sNextBackgroundMusic.dispose();
            }

            // Updating preferences
            Preferences preferences = Gdx.app.getPreferences(PreferenceUtils.PREFERENCES);
            preferences.putBoolean(MUSIC_ENABLED, enabled);
            preferences.flush();
        }
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
     * If the background music is not currently playing, starts it.
     */
    public static void playBackgroundMusic() {
        if (!sMusicEnabled)
            return;

        if (!sBackgroundMusic.isPlaying())
            sBackgroundMusic.play();
        if (sFadeTime < FADE_SPEED && sNextBackgroundMusic != null)
            sNextBackgroundMusic.play();
    }

    /**
     * If the background music is currently playing, pauses it.
     */
    public static void pauseBackgroundMusic() {
        if (sBackgroundMusic.isPlaying())
            sBackgroundMusic.pause();
        if (sNextBackgroundMusic != null && sNextBackgroundMusic.isPlaying())
            sNextBackgroundMusic.pause();
    }

    /**
     * If the background music is currently playing, stops it.
     */
    public static void stopBackgroundMusic() {
        if (sBackgroundMusic.isPlaying())
            sBackgroundMusic.stop();
        if (sNextBackgroundMusic != null && sNextBackgroundMusic.isPlaying())
            sNextBackgroundMusic.stop();
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
            sNextBackgroundMusic = loadBackgroundMusic(track);
            sFadingBackgroundTrack = track;
            sFadeTime = 0f;
            sNextBackgroundMusic.setVolume(0f);
            sNextBackgroundMusic.setLooping(true);
            sNextBackgroundMusic.play();
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

        if (sNextBackgroundMusic != null && sNextBackgroundMusic.isPlaying()
                && sBackgroundMusic.isPlaying()) {
            sFadeTime += delta;
            if (sFadeTime < FADE_SPEED) {
                sBackgroundMusic.setVolume((-sFadeTime + FADE_SPEED) / FADE_SPEED);
                sNextBackgroundMusic.setVolume(sFadeTime / FADE_SPEED);
            } else {
                sBackgroundMusic.stop();
                sNextBackgroundMusic.setVolume(1f);
                sCurrentBackgroundTrack = sFadingBackgroundTrack;

                final Music oldBackgroundMusic = sBackgroundMusic;
                sBackgroundMusic = sNextBackgroundMusic;
                sNextBackgroundMusic = null;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        oldBackgroundMusic.dispose();
                    }
                }).start();
            }
        }
    }

    /**
     * Loads a single background music track from the game assets.
     *
     * @param track track to load
     * @return the background music
     */
    private static Music loadBackgroundMusic(BackgroundTrack track) {
        return Gdx.audio.newMusic(Gdx.files.internal("audio/bm/" + track + ".wav"));
    }

    /**
     * Loads a single sound effect from the game assets.
     *
     * @param sound sound effect to load
     * @return the sound effect
     */
    private static Sound loadSound(SoundEffect sound) {
        return Gdx.audio.newSound(Gdx.files.internal("audio/sound/" + sound + ".wav"));
    }

    /**
     * Frees resources from objects.
     */
    public static void dispose() {
        if (sBackgroundMusic != null)
            sBackgroundMusic.dispose();
        if (sNextBackgroundMusic != null)
            sNextBackgroundMusic.dispose();

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
