package lt.lb.recombinator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lt.lb.recombinator.MatchFinder.MatchResult;
import lt.lb.recombinator.peekable.PeekableIterator;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public interface Recombinator<I, T> extends PeekableIterator<PosMatched<I, T>> {

    public MatchFinder<I, T> getMatchFinder();

    public PosMatched<I, T> constructUnmatched(List<T> tokens);

    public PosMatched<I, T> construct(List<I> identifiers, List<T> tokens);

    public PosMatched<I, T> construct(I identifier, List<T> tokens);

    public PosMatched<I, T> constructEmpty();

    public default PosMatched<I, T> findBestMatch(PeekableIterator<T> peekable) {
        if (!peekable.canPeek()) {
            return constructEmpty();
        }
        ArrayList<T> liveList = new ArrayList<>();
        MatchResult<I> finalized = getMatchFinder().advanceAndCompute(peekable, liveList);
        if (finalized.isEmpty()) {
            if (liveList.isEmpty()) {
                if (peekable.hasNext()) { // explicitly advance the seek, with unmatched
                    return constructUnmatched(Arrays.asList(peekable.next()));
                } else {
                    return constructEmpty();
                }
            } else {
                return constructUnmatched(liveList);
            }
        }

        if (finalized.getMaxCount() > 1) {
            return construct(finalized.getAllMax(), liveList);
        } else {
            return construct(finalized.getFirstMax(), liveList);
        }

    }

    public default List<PosMatched<I, T>> tryMatchAll() {
        return toStream().collect(Collectors.toList());
    }

    public default List<PosMatched<I, T>> tryMatchAll(PeekableIterator<T> items) {

        ArrayList<PosMatched<I, T>> matched = new ArrayList<>();
        PosMatched<I, T> findBestMatch = findBestMatch(items);

        while (!findBestMatch.isEmpty()) {
            matched.add(findBestMatch);
            findBestMatch = findBestMatch(items);
        }
        return matched;

    }

    public <R> PeekableIterator<FlatMatched<I, R>> flatten(PosMatchedFlattener<I, T, R> reducer);
    
    public <R> PeekableIterator<PosMatched<I,R>> delift(PosMatchedDelifter<I, T, R> reducer);

    public static interface DelegatedRecombinator<I, T> extends Recombinator<I, T> {

        public Recombinator<I, T> delegate();

        @Override
        public default MatchFinder<I, T> getMatchFinder() {
            return delegate().getMatchFinder();
        }

        @Override
        public default PosMatched<I, T> constructUnmatched(List<T> tokens) {
            return delegate().constructUnmatched(tokens);
        }

        @Override
        public default PosMatched<I, T> construct(List<I> identifiers, List<T> tokens) {
            return delegate().construct(identifiers, tokens);
        }

        @Override
        public default PosMatched<I, T> construct(I identifier, List<T> tokens) {
            return delegate().construct(identifier, tokens);
        }

        @Override
        public default PosMatched<I, T> constructEmpty() {
            return delegate().constructEmpty();
        }

        @Override
        public default PosMatched<I, T> findBestMatch(PeekableIterator<T> peekable) {
            return delegate().findBestMatch(peekable);
        }

        @Override
        public default List<PosMatched<I, T>> tryMatchAll() {
            return delegate().tryMatchAll();
        }

        @Override
        public default List<PosMatched<I, T>> tryMatchAll(PeekableIterator<T> items) {
            return delegate().tryMatchAll(items);
        }

        @Override
        public default <R> PeekableIterator<FlatMatched<I, R>> flatten(PosMatchedFlattener<I, T, R> reducer) {
            return delegate().flatten(reducer);
        }

        @Override
        public default boolean canPeek(int ahead) {
            return delegate().canPeek(ahead);
        }

        @Override
        public default boolean canPeek() {
            return delegate().canPeek();
        }

        @Override
        public default PosMatched<I, T> peek(int ahead) {
            return delegate().peek(ahead);
        }

        @Override
        public default PosMatched<I, T> peek() {
            return delegate().peek();
        }

        @Override
        public default Stream<PosMatched<I, T>> toStream() {
            return delegate().toStream();
        }

        @Override
        public default boolean hasNext() {
            return delegate().hasNext();
        }

        @Override
        public default PosMatched<I, T> next() {
            return delegate().next();
        }

    }

}
