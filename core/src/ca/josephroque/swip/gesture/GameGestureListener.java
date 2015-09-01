package ca.josephroque.swip.gesture;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles gesture input for the application.
 */
public class GameGestureListener
        implements GestureDetector.GestureListener {

    /** Width of the screen. */
    private int mScreenWidth;
    /** Height of the screen. */
    private int mScreenHeight;

    /** Indicates if the user provided a left fling. */
    private boolean mFlungLeft;
    /** Indicates if the user provided a right fling. */
    private boolean mFlungRight;
    /** Indicates if the user provided a down fling. */
    private boolean mFlungDown;
    /** Indicates if the user provided an up fling. */
    private boolean mFlungUp;

    /** Last recorded x location on screen of a finger. */
    private float mLastFingerX;
    /** Last recorded y location on screen of a finger. */
    private float mLastFingerY;

    /**
     * Returns last known x location of finger on screen. Origin is the left of the screen.
     *
     * @return {@code mLastFingerX}
     */
    public float getLastFingerX() {
        return mLastFingerX;
    }

    /**
     * Returns last known y location of finger on screen. Origin is the bottom of the screen.
     *
     * @return {@code mLastFingerY}
     */
    public float getLastFingerY() {
        return mScreenHeight - mLastFingerY;
    }

    /**
     * Updates size of the screen.
     *
     * @param width new width of the screen
     * @param height new height of the screen
     */
    public void resize(int width, int height) {
        this.mScreenWidth = width;
        this.mScreenHeight = height;
    }

    /**
     * Gets the most recent fling event and consumes it.
     *
     * @return the type of the most recent fling, or {@code FlingDirection.NONE} if there were no recent flings.
     */
    public FlingDirection consumeFling() {
        FlingDirection direction = FlingDirection.NONE;
        if (mFlungLeft)
            direction = FlingDirection.LEFT;
        else
            if (mFlungUp)
                direction = FlingDirection.UP;
            else
                if (mFlungRight)
                    direction = FlingDirection.RIGHT;
                else
                    if (mFlungDown)
                        direction = FlingDirection.DOWN;

        mFlungLeft = false;
        mFlungUp = false;
        mFlungRight = false;
        mFlungDown = false;

        return direction;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0)      // left swipe
                mFlungRight = true;
            else                    // right swipe
                mFlungLeft = true;
        } else {
            if (velocityY > 0)      // up swipe
                mFlungDown = true;
            else                    // down swipe
                mFlungUp = true;
        }

        return true;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        mLastFingerX = x;
        mLastFingerY = y;
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        mLastFingerX = x;
        mLastFingerY = y;
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    /**
     * The possible directions a fling action can be in.
     */
    public enum FlingDirection {
        /** Represents a fling left. */
        LEFT,
        /** Represents a fling up. */
        UP,
        /** Represents a fling right. */
        RIGHT,
        /** Represents a fling down. */
        DOWN,
        /** Represents a state of no fling from the user. */
        NONE
    }
}
