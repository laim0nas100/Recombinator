package lt.lb.recombinator;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public interface DelegatedPosMatch<I, T> extends PosMatch<I, T> {

    public PosMatch<I, T> delegate();

    @Override
    public default I getName() {
        return delegate().getName();
    }

    @Override
    public default int getLength() {
        return delegate().getLength();
    }

    @Override
    public default boolean isRepeating() {
        return delegate().isRepeating();
    }

    @Override
    public default int getImportance() {
        return delegate().getImportance();
    }

    @Override
    public default boolean matches(int position, T item) {
        return delegate().matches(position, item);
    }

    @Override
    public default Object id() {
        return delegate().id();
    }

    @Override
    public default String stringValues() {
        return delegate().stringValues();
    }

    @Override
    public default String descriptiveString() {
        return delegate().descriptiveString();
    }

}
