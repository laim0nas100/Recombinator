package lt.lb.recombinator;

/**
 *
 * @author laim0nas100
 * @param <M> implementation
 * @param <T> item type
 *
 */
public interface PosMatch<M, T> extends Id {

    public abstract M getName();

    /**
     * How many items are required. 0 or below means it is never used.
     *
     * @return
     */
    public abstract int getLength();

    /**
     * If the sequence can be repeating.
     *
     * @return
     */
    public abstract boolean isRepeating();

    /**
     * Higher importance means it is tried applied sooner
     *
     * @return
     */
    public abstract int getImportance();

    /**
     * If given item can be matched at given position
     *
     * @param position should be within length (non-negative)
     * @param item
     * @return
     */
    public abstract boolean matches(int position, T item);
    
    
    
}
