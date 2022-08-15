package lt.lb.recombinator;

import lt.lb.recombinator.peekable.PeekableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;

/**
 *
 * @author laim0nas100
 * @param <I>
 * @param <T>
 */
public interface MatchFinder<I, T> {

    public static enum MatchResultEnum {
        MATCH_FAILED, MATCH, MATCH_AND_FINAL
    }

    public static interface MatchResult<I> {

        public I getFirstMax();

        public int getMaxCount();
        
        public List<I> getAllMax();

        public boolean isEmpty();

        public static <I> MatchResult<I> empty() {
            return EmptyMatchResult.empty;
        }

        public static <I> MatchResult<I> bestList(List<I> list) {
            return new MaxListMatchResult(list);
        }

        public static <I> MatchResult<I> best(I item) {
            return new MaxBestMatchResult<>(item);
        }

        public static <I> MatchResult<I> fromMap(SortedMap<Integer, List<I>> map) {
            return new MapMatchResult<>(map);
        }

    }

    public static class EmptyMatchResult<I> implements MatchResult<I> {

        public static final EmptyMatchResult empty = new EmptyMatchResult();

        public EmptyMatchResult() {
        }

        @Override
        public I getFirstMax() {
            return null;
        }

        @Override
        public List<I> getAllMax() {
            return Collections.emptyList();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int getMaxCount() {
            return 0;
        }

    }

    public static class MaxListMatchResult<I> implements MatchResult<I> {

        protected final List<I> maxSized;

        public MaxListMatchResult(List<I> list) {
            this.maxSized = Objects.requireNonNull(list);
        }

        @Override
        public I getFirstMax() {
            if (!maxSized.isEmpty()) {
                return maxSized.get(0);
            }
            return null;
        }

        @Override
        public List<I> getAllMax() {
            return maxSized;
        }

        @Override
        public boolean isEmpty() {
            return maxSized.isEmpty();
        }

        @Override
        public int getMaxCount() {
            return maxSized.size();
        }

    }

    public static class MaxBestMatchResult<I> implements MatchResult<I> {

        protected final I best;

        public MaxBestMatchResult(I best) {
            this.best = Objects.requireNonNull(best);
        }

        @Override
        public I getFirstMax() {
            return best;
        }

        @Override
        public List<I> getAllMax() {
            return Collections.singletonList(best);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getMaxCount() {
            return 1;
        }

    }

    public static class MapMatchResult<I> implements MatchResult<I> {

        protected final SortedMap<Integer, List<I>> map;
        protected final List<I> maxSized;
        protected final int maxSize;

        public MapMatchResult(SortedMap<Integer, List<I>> map) {
            this.map = Objects.requireNonNull(map);
            this.maxSize = map.lastKey();
            this.maxSized = map.get(maxSize);
        }

        @Override
        public I getFirstMax() {
            if (!maxSized.isEmpty()) {
                return maxSized.get(0);
            }
            return null;
        }

        @Override
        public List<I> getAllMax() {
            return maxSized;
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public int getMaxCount() {
            return maxSized.size();
        }

    }

    public MatchResult<I> advanceAndCompute(PeekableIterator<T> peekable, List<T> liveList);

    public default MatchResultEnum computeMatch(PosMatch<?, T> m, T item, int absolutePos, int size) {
        Objects.requireNonNull(m, "PosMatch is null");
        boolean R = m.isRepeating();
        int L = m.getLength();
        if (!R && L < size) {
            return MatchResultEnum.MATCH_FAILED;
        }
        int pos = R ? absolutePos % L : absolutePos;
        if (!m.matches(pos, item)) {
            return MatchResultEnum.MATCH_FAILED;
        }

        // matched, now check size
        if ((!R && L == size) || (R && (L == 1 || size % L == 0))) {
            return MatchResultEnum.MATCH_AND_FINAL;
        } else {
            return MatchResultEnum.MATCH;
        }

    }

    public Collection<PosMatch<I, T>> getOrderedMatchers();
    

}
