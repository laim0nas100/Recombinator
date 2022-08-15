package lt.lb.recombinator.impl;

import java.util.Collections;
import java.util.List;
import lt.lb.recombinator.Id;
import lt.lb.recombinator.FlatMatched;

/**
 *
 * @author laim0nas100
 */
public class FlatMatchedSimple<I, T> implements FlatMatched<I, T>, Id {

    private static final FlatMatchedSimple empty = new FlatMatchedSimple();

    public static <I, T> FlatMatchedSimple<I, T> empty() {
        return empty;
    }

    protected final List<I> matchedBy;
    protected final T item;

    private FlatMatchedSimple() {
        this.matchedBy = Collections.emptyList();
        this.item = null;
    }

    public FlatMatchedSimple(T item) {
        this.matchedBy = Collections.emptyList();
        this.item = item;
    }

    public FlatMatchedSimple(List<I> matched, T item) {

        if (matched == null || matched.isEmpty()) {
            throw new IllegalArgumentException("Empty matched");
        }
        this.matchedBy = matched;
        this.item = item;
    }

    public boolean contains(I matcher) {
        return matchedBy.contains(matcher);
    }

    @Override
    public String toString() {
        return descriptiveString();
    }

    public String names() {
        return "" + matchedBy;
    }

    public String values() {
        return "" + item;
    }

    @Override
    public String stringValues() {
        return "matchedBy=" + names() + ", token=" + values();
    }

    @Override
    public List<I> matchedBy() {
        return matchedBy;
    }

    @Override
    public T getItem() {
        return item;
    }

}
