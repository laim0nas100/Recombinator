package lt.lb.recombinator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public interface PosMatched<I, T> {

    public default boolean isEmpty() {
        return items().isEmpty();
    }

    public default boolean isUnmatched() {
        return matchedBy().isEmpty();
    }

    public List<I> matchedBy();

    public List<T> items();

    public default int countMatchers() {
        return matchedBy().size();
    }

    public default int countItems() {
        return items().size();
    }

    public default I firstMatch() {
        return countMatchers() > 0 ? matchedBy().get(0) : null;
    }

    public default T getItem(int index) {
        return items().get(index);
    }

    public default List<T> getItems(int... index) {
        List<T> list = new ArrayList<>(index.length);
        for (int i = 0; i < index.length; i++) {
            list.add(getItem(index[i]));
        }
        return list;
    }

    public default boolean containsMatcher(I name) {
        return matchedBy().contains(name);
    }

    public default boolean containsMatcher(PosMatch<I, ?> matcher) {
        Objects.requireNonNull(matcher);
        return containsMatcher(matcher.getName());
    }

    public default boolean containsItem(T item) {
        return items().contains(item);
    }
}
