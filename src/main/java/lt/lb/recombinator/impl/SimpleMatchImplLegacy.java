package lt.lb.recombinator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lt.lb.recombinator.peekable.PeekableIterator;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.RecombinatorLegacy;
import lt.lb.recombinator.Utils;

/**
 *
 * @author laim0nas100
 */
@Deprecated
public class SimpleMatchImplLegacy<I, T> implements RecombinatorLegacy<I, T> {

    /**
     * Compare longest first and then by importance.
     */
    public static final Comparator<PosMatch> cmpMatchers = (PosMatch o1, PosMatch o2) -> {
        int c = Integer.compare(o2.getLength(), o1.getLength());
        return c == 0 ? Integer.compare(o2.getImportance(), o1.getImportance()) : c;
    };

    protected PeekableIterator<T> items;
    protected Collection<PosMatch<I, T>> matchers;
    protected PeekableIterator<PosMatched<I, T>> matchedIterator;

    protected LinkedList<PosMatched<I, T>> buffer = new LinkedList<>();

    public SimpleMatchImplLegacy(PeekableIterator<T> items, Collection<? extends PosMatch<I, T>> matchersCol) {
        this.items = Objects.requireNonNull(items);
        matchers = Objects.requireNonNull(matchersCol).stream()
                .filter(p -> p.getLength() > 0)
                .sorted(cmpMatchers)
                .collect(Collectors.toList());

        Iterator<PosMatched<I, T>> matcherIter = new Iterator<PosMatched<I, T>>() {

            private void fillBufferIfEmpty() {
                if (buffer.isEmpty()) {
                    PosMatched<I, T> findBestMatch = findBestMatch(items);
                    if (!findBestMatch.isEmpty()) {
                        buffer.add(findBestMatch);
                    }
                }
            }

            @Override
            public boolean hasNext() {
                fillBufferIfEmpty();
                return !buffer.isEmpty();
            }

            @Override
            public PosMatched<I, T> next() {
                fillBufferIfEmpty();
                return buffer.pollFirst();
            }
        };
        matchedIterator = Utils.peekable(matcherIter);
    }
    
    
    @Override
    public PosMatched<I, T> constructUnmatched(List<T> tokens) {
        return new PosMatchedSimple<>(tokens);
    }

    @Override
    public PosMatched<I, T> construct(List<I> identifiers, List<T> tokens) {
        return new PosMatchedSimple<>(identifiers, tokens);
    }

    @Override
    public PosMatched<I, T> constructEmpty() {
        return PosMatchedSimple.empty();
    }
    

    @Override
    public Collection<PosMatch<I, T>> getOrderedMatchers() {
        return matchers;
    }

    @Override
    public boolean canPeek(int ahead) {
        return matchedIterator.canPeek(ahead);
    }

    @Override
    public PosMatched<I, T> peek(int ahead) {
        return matchedIterator.peek(ahead);
    }

    @Override
    public boolean hasNext() {
        return matchedIterator.hasNext();
    }

    @Override
    public PosMatched<I, T> next() {
        return matchedIterator.next();
    }

    @Override
    public RecombinatorLegacy<I, PosMatched<I, T>> lift(Collection<PosMatch<I, PosMatched<I, T>>> matchers) {
        return new SimpleMatchImplLegacy<>(this, matchers);
    }

    @Override
    public PeekableIterator<PosMatched<I, T>> flatLift(Collection<PosMatch<I, PosMatched<I, T>>> matchers) {

        SimpleMatchImplLegacy<I, T> me = this;
        RecombinatorLegacy<I, PosMatched<I, T>> lift = lift(matchers);

        Iterator<PosMatched<I, T>> iterator = new Iterator<PosMatched<I, T>>() {
            @Override
            public boolean hasNext() {
                return lift.hasNext();
            }

            @Override
            public PosMatched<I, T> next() {
                PosMatched<I, PosMatched<I, T>> next = lift.next();
                if (next.countMatchers() == 0) {
                    return next.getItem(0);
                }
                ArrayList<T> items = new ArrayList<>();
                next.items().forEach(it -> {
                    items.addAll(it.items());
                });
                return me.construct(next.matchedBy(), items);

            }
        };
        
        return Utils.peekable(iterator);
    }

}
