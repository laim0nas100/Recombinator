package lt.lb.recombinator;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public interface FlatMatched<I,T> {

    public default boolean isUnmatched() {
        return matchedBy().isEmpty();
    }

    public List<I> matchedBy();

    public T getItem();

    public default int countMatchers() {
        return matchedBy().size();
    }


    public default I firstMatch() {
        return countMatchers() > 0 ? matchedBy().get(0) : null;
    }


    public default boolean containsMatcher(I name) {
        return matchedBy().contains(name);
    }

    public default boolean containsMatcher(PosMatch<I, ?> matcher) {
        Objects.requireNonNull(matcher);
        return containsMatcher(matcher.getName());
    }
}
