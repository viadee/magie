package de.viadee.xai.framework.utility;

/**
 * Class representing a simple tuple.
 * @param <X> The first element.
 * @param <Y> The second element.
 */
public class Tuple<X, Y> {
    private X x;
    private Y y;

    /**
     * Constructor for Tuple.
     * @param x The first element.
     * @param y The second element.
     */
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the first element.
     * @return The first element.
     */
    public X getFirstElement() {
        return x;
    }

    /**
     * Returns the second element.
     * @return The second element.
     */
    public Y getSecondElement() {
        return y;
    }
}
