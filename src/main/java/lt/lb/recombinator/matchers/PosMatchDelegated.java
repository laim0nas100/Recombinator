package lt.lb.recombinator.matchers;

import lt.lb.recombinator.PosMatch;

/**
 *
 * @author laim0nas100
 */
public interface PosMatchDelegated<M, T> extends PosMatch<M, T> {

    public PosMatch<M, T> delegate();

    @Override
    public default M getName() {
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
