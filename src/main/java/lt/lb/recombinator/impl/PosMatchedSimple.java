package lt.lb.recombinator.impl;

import java.util.Collections;
import java.util.List;
import lt.lb.recombinator.Id;
import lt.lb.recombinator.PosMatched;

/**
 *
 * @author laim0nas100
 */
public class PosMatchedSimple<I, T> implements PosMatched<I, T>, Id {

    private static final PosMatchedSimple empty = new PosMatchedSimple();

    public static <I, T> PosMatchedSimple<I, T> empty() {
        return empty;
    }

    protected final List<I> matchedBy;
    protected final List<T> items;

    private PosMatchedSimple() {
        this.matchedBy = Collections.emptyList();
        this.items = Collections.emptyList();
    }

    public PosMatchedSimple(List<T> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = Collections.emptyList();
        this.items = items;
    }

    public PosMatchedSimple(List<I> matched, List<T> items) {

        if (matched == null || matched.isEmpty()) {
            throw new IllegalArgumentException("Empty matched");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = matched;
        this.items = items;
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
        return "" + items;
    }

    @Override
    public String stringValues() {
        return "matchedBy=" + names() + ", tokens=" + values();
    }

    @Override
    public List<I> matchedBy() {
        return matchedBy;
    }

    @Override
    public List<T> items() {
        return items;
    }

}
