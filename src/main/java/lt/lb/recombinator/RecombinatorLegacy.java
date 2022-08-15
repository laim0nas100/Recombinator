package lt.lb.recombinator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lt.lb.recombinator.peekable.PeekableIterator;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
@Deprecated
public interface RecombinatorLegacy<I, T> extends PeekableIterator<PosMatched<I, T>> {

    public Collection<PosMatch<I, T>> getOrderedMatchers();

    public PosMatched<I, T> constructUnmatched(List<T> tokens);

    public PosMatched<I, T> construct(List<I> identifiers, List<T> tokens);

    public PosMatched<I, T> constructEmpty();

    public default PosMatched<I, T> findBestMatch(PeekableIterator<T> peekable) {
        while (peekable.hasNext()) {
            ArrayList<T> liveList = new ArrayList<>();
            LinkedList<PosMatch<I, T>> toCheck = new LinkedList<>(getOrderedMatchers());
            HashMap<Integer, List<I>> finalized = new HashMap<>();
            boolean doMore = true;
            int max = -1;
            while (doMore && peekable.canPeek()) {

                final int size = liveList.size() + 1;
                final int localPos = size - 1;
                T token = peekable.peek();
                Iterator<PosMatch<I, T>> doCheck = toCheck.iterator();
                while (doCheck.hasNext()) {
                    PosMatch<I, T> m = doCheck.next();
                    boolean rep = m.isRepeating();
                    int len = m.getLength();
                    boolean sizeOk = rep ? true : len >= size;
                    int pos = rep ? localPos % len : localPos;
                    boolean matches = false;
                    if (sizeOk) {
                        matches = m.matches(pos, token);
                    }

                    if (matches) {
                        if ((!rep && len == size) || (rep && size % len == 0)) {
                            finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(m.getName());
                            max = Math.max(max, size);
                        }

                    } else {
                        doCheck.remove();
                    }

                }
                doMore = !toCheck.isEmpty();
                if (doMore) {
                    liveList.add(token);
                    peekable.next();// advance
                }

            }

            if (finalized.isEmpty()) {
                if (liveList.isEmpty()) {
                    if (peekable.hasNext()) {
                        return constructUnmatched(Arrays.asList(peekable.next()));
                    }else{
                        return constructEmpty();
                    }
                } else {
                    return constructUnmatched(liveList);
                }
            }

            return construct(finalized.get(max), liveList);
        }
        return constructEmpty();
    }

    public default List<PosMatched<I, T>> tryMatchAll() {
        return toStream().collect(Collectors.toList());
    }

    public default List<PosMatched<I, T>> tryMatchAll(Iterator<T> items) {

        PeekableIterator<T> refillable = Utils.peekable(items);
        ArrayList<PosMatched<I, T>> matched = new ArrayList<>();
        PosMatched<I, T> findBestMatch = findBestMatch(refillable);

        while (!findBestMatch.isEmpty()) {
            matched.add(findBestMatch);
            findBestMatch = findBestMatch(refillable);
        }
        return matched;

    }

    /**
     * Lift {@link PosMatched} items to further combine it based on provided
     * matchers, then collect the items into the one {@link PosMatched} instead
     * of multiple ones.
     *
     * @param sort
     * @param iterator
     * @param matchers
     * @return
     */
//    public default SimpleMatch<I, T, P> flatLift(boolean sort, Iterator<PosMatched<I, T>> iterator, Collection<? extends PosMatch<PosMatched<I, T>, T>> matchers) {
//
//        return null;
//        Iterator<PosMatched<PosMatched<I, T>, T>> iterator1 = lift(sort, iterator, matchers);
//
//        return new Iterator<PosMatched<I, T>>() {
//            @Override
//            public boolean hasNext() {
//                return iterator1.hasNext();
//            }
//
//            @Override
//            public PosMatched<I, T> next() {
//                PosMatched<PosMatched<I, T>, T> next = iterator1.next();
//                if (next.countMatchers() == 0) {
//                    return next.getItem(0);
//                }
//                ArrayList<I> items = new ArrayList<>();
//                next.items().forEach(it -> {
//                    items.addAll(it.items());
//                });
//                return new PosMatchedSimple<>(next.matchedBy(), items);
//            }
//        };
//
//    }
    /**
     * Lift {@link PosMatched} items to further combine it based on provided
     * matchers.
     *
     * @param sort
     * @param iterator
     * @param matchers
     * @return
     */
    public RecombinatorLegacy<I, PosMatched<I, T>> lift(Collection<PosMatch<I, PosMatched<I, T>>> matchers);

    /**
     * Lift {@link PosMatched} items to further combine it based on provided
     * matchers, then collect the items into the one {@link PosMatched} instead
     * of multiple ones.
     *
     * @param sort
     * @param iterator
     * @param matchers
     * @return
     */
    public PeekableIterator<PosMatched<I, T>> flatLift(Collection<PosMatch<I, PosMatched<I, T>>> matchers);

}
