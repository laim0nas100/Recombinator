package lt.lb.recombinator.peekable;

/**
 *
 * @author laim0nas100
 */
public interface DelegatedPeekableIterator<T> extends PeekableIterator<T> {

    public PeekableIterator<T> delegate();
    
    @Override
    public default boolean canPeek(int ahead) {
        return delegate().canPeek(ahead);
    }

    @Override
    public default T peek(int ahead) {
        return delegate().peek(ahead);
    }

    @Override
    public default boolean hasNext() {
        return delegate().hasNext();
    }

    @Override
    public default T next() {
        return delegate().next();
    }
    
}
