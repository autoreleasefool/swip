package ca.josephroque.swip.input;

import ca.josephroque.swip.util.Triplet;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.LinkedList;

/**
 * Handles gesture input for the application.
 */
public class GameInputProcessor
        implements InputProcessor {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "GameInput";

    /** The maximum number of locations to store of the user's finger history on the screen. */
    private static final int MAXIMUM_FINGER_HISTORY = 5;
    /** Maximum number of milliseconds a user can hold their finger on the screen for to be "clicking". */
    private static final int MAXIMUM_CLICK_HOLD_THRESHOLD = 300;
    /** Maximum number of pixels a user's finger can move on the screen to be considered a click. */
    private static final int MAXIMUM_CLICK_MOVE_THRESHOLD = 10;

    /**
     * The finger velocity is calculated in milliseconds, but game object velocities use seconds, so the finger velocity
     * must be scaled up.
     */
    private static final int FINGER_VELOCITY_SCALE = 1000;

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Last recorded x location on screen of a finger. */
    private int mLastFingerX;
    /** Last recorded y location on screen of a finger. */
    private int mLastFingerY;
    /** X location at which the user placed their finger on the screen. */
    private int mFingerDownX;
    /** Y location at which the user placed their finger on the screen. */
    private int mFingerDownY;
    /** Indicates if the user's finger is currently on the screen. */
    private boolean mFingerDown;
    /** Time that the user last placed their finger on the screen. */
    private long mFingerDownTime;
    /** Indicates if the user took their finger off the screen in the last tick. */
    private boolean mFingerJustReleased;

    /** Past locations of the user's finger. */
    private final LinkedList<Triplet<Integer, Integer, Long>> mFingerHistory = new LinkedList<>();
    /** Used to store the moving velocity of the user's finger. */
    private final Vector2 mFingerDragVelocity = new Vector2();

    /**
     * Returns last known x location of finger on screen. Origin is the left of the screen.
     *
     * @return x location of the user's first finger
     */
    public int getLastFingerX() {
        return mLastFingerX;
    }

    /**
     * Returns last known y location of finger on screen. Origin is the bottom of the screen.
     *
     * @return y location of the user's first finger
     */
    public int getLastFingerY() {
        return mScreenHeight - mLastFingerY;
    }

    /**
     * Checks if the user's finger is on the screen. Only considers the first finger on the screen.
     *
     * @return {@code true} if the user's first finger is on the screen
     */
    public boolean isFingerDown() {
        return mFingerDown;
    }

    /**
     * Checks if the user has placed their finger down and released it in a very quick "clicking" motion.
     *
     * @return {@code true} if the user has met the conditions for a click
     */
    public boolean clickOccurred() {
        return mFingerJustReleased && TimeUtils.timeSinceMillis(mFingerDownTime) < MAXIMUM_CLICK_HOLD_THRESHOLD
                && Math.abs(mLastFingerX - mFingerDownX) < MAXIMUM_CLICK_MOVE_THRESHOLD
                && Math.abs(mLastFingerY - mFingerDownY) < MAXIMUM_CLICK_MOVE_THRESHOLD;
    }

    /**
     * Calculates the velocity of the user's finger movements using the last {@code MAXIMUM_FINGER_HISTORY} positions.
     *
     * @return the velocity of the user's finger movements
     */
    public Vector2 calculateFingerDragVelocity() {
        if (mFingerHistory.size() == 0) {
            mFingerDragVelocity.set(0, 0);
        } else {
            int totalXDistance = mFingerHistory.getLast().getFirst() - mFingerHistory.getFirst().getFirst();
            int totalYDistance = mFingerHistory.getLast().getSecond() - mFingerHistory.getFirst().getSecond();
            float elapsedTime = mFingerHistory.getLast().getThird() - mFingerHistory.getFirst().getThird();
            mFingerDragVelocity.set(totalXDistance / elapsedTime * FINGER_VELOCITY_SCALE,
                    -totalYDistance / elapsedTime * FINGER_VELOCITY_SCALE);
        }

        return mFingerDragVelocity;
    }

    /**
     * Updates input objects.
     */
    public void tick() {
        mFingerJustReleased = false;
    }

    /**
     * Update the screen size.
     *
     * @param screenWidth width of the screen
     * @param screenHeight height of the screen
     */
    public void resize(int screenWidth, int screenHeight) {
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer > 0)
            return false;

        mFingerHistory.clear();
        mLastFingerX = screenX;
        mLastFingerY = screenY;
        mFingerDownX = screenX;
        mFingerDownY = screenY;
        mFingerDown = true;

        mFingerDownTime = TimeUtils.millis();
        mFingerHistory.add(Triplet.create(mLastFingerX, mLastFingerY, mFingerDownTime));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer > 0)
            return false;

        mLastFingerX = screenX;
        mLastFingerY = screenY;
        mFingerDown = false;
        mFingerJustReleased = true;

        mFingerHistory.add(Triplet.create(screenX, screenY, TimeUtils.millis()));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer > 0)
            return false;

        mLastFingerX = screenX;
        mLastFingerY = screenY;
        while (mFingerHistory.size() >= MAXIMUM_FINGER_HISTORY)
            mFingerHistory.removeFirst();
        mFingerHistory.add(Triplet.create(screenX, screenY, TimeUtils.millis()));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // does nothing
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // does nothing
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        // does nothing
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // does nothing
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // does nothing
        return false;
    }
}
