package ca.josephroque.swip.util;

/**
 * Manages 3 objects in a tuple.
 *
 * @param <A> first object in triplet
 * @param <B> second object in triplet
 * @param <C> third object in triplet
 */
public class Triplet<A, B, C> {

    /** Identifies output from this class in the logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "Triplet";

    /** First object in the triplet. */
    private final A mFirst;
    /** Second object in the triplet. */
    private final B mSecond;
    /** Third object in the triplet. */
    private final C mThird;

    /**
     * Gets references to parameters for triplet objects.
     *
     * @param a first object
     * @param b second object
     * @param c third object
     */
    public Triplet(A a, B b, C c) {
        mFirst = a;
        mSecond = b;
        mThird = c;
    }

    /**
     * Creates a new {@code Triplet} with the given objects.
     *
     * @param <A> type of first object
     * @param <B> type of second object
     * @param <C> type of third object
     * @param a first object
     * @param b second object
     * @param c third object
     * @return a new {@code Triplet} instance
     */
    public static <A, B, C> Triplet<A, B, C> create(A a, B b, C c) {
        return new Triplet<>(a, b, c);
    }

    /**
     * Gets the first item.
     * @return {@code first}
     */
    public A getFirst() {
        return mFirst;
    }

    /**
     * Gets the second item.
     * @return {@code second}
     */
    public B getSecond() {
        return mSecond;
    }

    /**
     * Gets the third item.
     * @return {@code third}
     */
    public C getThird() {
        return mThird;
    }
}
