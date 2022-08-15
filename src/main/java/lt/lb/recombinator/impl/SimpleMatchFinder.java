package lt.lb.recombinator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lt.lb.recombinator.MatchFinder;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.peekable.PeekableIterator;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public class SimpleMatchFinder<I, T> implements MatchFinder<I, T> {

    /**
     * Compare longest first and then by importance.
     */
    public static final Comparator<PosMatch> cmpMatchers = (PosMatch o1, PosMatch o2) -> {
        int c = Integer.compare(o2.getLength(), o1.getLength());
        return c == 0 ? Integer.compare(o2.getImportance(), o1.getImportance()) : c;
    };

    protected final Collection<PosMatch<I, T>> matchers;

    protected final SimpleMatchMode mode;

    public static enum SimpleMatchMode {
        ALL, BEST_LIST, BEST
    }

    public SimpleMatchFinder(Collection<PosMatch<I, T>> matchers) {
        this(matchers, SimpleMatchMode.BEST_LIST);
    }

    public SimpleMatchFinder(Collection<? extends PosMatch<I, T>> matchersCol, SimpleMatchMode mode) {
        matchers = Collections.unmodifiableList(
                Objects.requireNonNull(matchersCol).stream()
                        .filter(p -> p.getLength() > 0)
                        .sorted(cmpMatchers)
                        .collect(Collectors.toList())
        );

        this.mode = Objects.requireNonNull(mode);
    }

    @Override
    public Collection<PosMatch<I, T>> getOrderedMatchers() {
        return matchers;
    }

    @Override
    public MatchResult<I> advanceAndCompute(PeekableIterator<T> peekable, List<T> liveList) {
        switch (mode) {
            case ALL:
                return advanceAndComputeSaveAll(this, peekable, liveList);
            case BEST:
                return advanceAndComputeSaveBest(this, peekable, liveList);
            case BEST_LIST:
                return advanceAndComputeSaveBestList(this, peekable, liveList);
            default:
                throw new IllegalArgumentException("Unrecognized mode:" + mode);
        }

    }

    public static <I, T> MatchResult<I> advanceAndComputeSaveAll(MatchFinder<I, T> matchFinder, PeekableIterator<T> peekable, List<T> liveList) {
        if (!peekable.hasNext()) {
            return MatchResult.empty();
        }
        LinkedList<PosMatch<I, T>> toCheck = new LinkedList<>(matchFinder.getOrderedMatchers());
        SortedMap<Integer, List<I>> finalized = new TreeMap<>();
        boolean doMore = true;
        while (doMore && peekable.canPeek()) {

            final int localPos = liveList.size();
            final int size = localPos + 1;
            T item = peekable.peek();
            Iterator<PosMatch<I, T>> doCheck = toCheck.iterator();
            while (doCheck.hasNext()) {
                PosMatch<I, T> matcher = doCheck.next();
                MatchResultEnum computeMatch = matchFinder.computeMatch(matcher, item, localPos, size);
                switch (computeMatch) {
                    case MATCH:
                        break;// just don't remove
                    case MATCH_AND_FINAL:
                        finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(matcher.getName());
                        break;
                    case MATCH_FAILED:
                        doCheck.remove();
                        break;
                }

            }
            doMore = !toCheck.isEmpty();
            if (doMore) {
                liveList.add(peekable.next());// advance
            }

        }
        if (finalized.isEmpty()) {
            return MatchResult.empty();
        }
        return MatchResult.fromMap(finalized);
    }

    public static <I, T> MatchResult<I> advanceAndComputeSaveBestList(MatchFinder<I, T> matchFinder, PeekableIterator<T> peekable, List<T> liveList) {
        if (!peekable.hasNext()) {
            return MatchResult.empty();
        }
        LinkedList<PosMatch<I, T>> toCheck = new LinkedList<>(matchFinder.getOrderedMatchers());
        List<I> best = new ArrayList<>();
        int bestSize = 1; // at least size 1
        boolean doMore = true;
        while (doMore && peekable.canPeek()) {

            final int localPos = liveList.size();
            final int size = localPos + 1;
            T item = peekable.peek();
            Iterator<PosMatch<I, T>> doCheck = toCheck.iterator();
            while (doCheck.hasNext()) {
                PosMatch<I, T> matcher = doCheck.next();
                MatchResultEnum computeMatch = matchFinder.computeMatch(matcher, item, localPos, size);
                switch (computeMatch) {
                    case MATCH:
                        break;// just don't remove
                    case MATCH_AND_FINAL:
                        if (bestSize < size) {
                            bestSize = size;
                            best.clear(); // replace low size
                        }
                        best.add(matcher.getName());
                        break;
                    case MATCH_FAILED:
                        doCheck.remove();
                        break;
                }

            }
            doMore = !toCheck.isEmpty();
            if (doMore) {
                liveList.add(peekable.next());// advance
            }

        }
        if (best.isEmpty()) {
            return MatchResult.empty();
        }
        return MatchResult.bestList(best);
    }

    public static <I, T> MatchResult<I> advanceAndComputeSaveBest(MatchFinder<I, T> matchFinder, PeekableIterator<T> peekable, List<T> liveList) {
        if (!peekable.hasNext()) {
            return MatchResult.empty();
        }
        LinkedList<PosMatch<I, T>> toCheck = new LinkedList<>(matchFinder.getOrderedMatchers());
        I best = null;
        int bestSize = -1;
        boolean doMore = true;
        while (doMore && peekable.canPeek()) {

            final int localPos = liveList.size();
            final int size = localPos + 1;
            T item = peekable.peek();
            Iterator<PosMatch<I, T>> doCheck = toCheck.iterator();
            while (doCheck.hasNext()) {
                PosMatch<I, T> matcher = doCheck.next();
                MatchResultEnum computeMatch = matchFinder.computeMatch(matcher, item, localPos, size);
                switch (computeMatch) {
                    case MATCH:
                        break;// just don't remove
                    case MATCH_AND_FINAL:
                        if (bestSize < size) {
                            bestSize = size;
                            best = matcher.getName();
                        }
                        break;
                    case MATCH_FAILED:
                        doCheck.remove();
                        break;
                }

            }
            doMore = !toCheck.isEmpty();
            if (doMore) {
                 liveList.add(peekable.next());// advance
            }

        }

        if (best == null) {
            return MatchResult.empty();
        }
        return MatchResult.best(best);
    }

}
