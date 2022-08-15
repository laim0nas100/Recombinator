package lt.lb.recombinator.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lt.lb.recombinator.PosMatch;

/**
 *
 * @author laim0nas100
 * @param <M>
 * @param <T>
 */
public abstract class CompositePosMatch<M, T> extends BasePosMatch<M, T> {

    protected List<? extends PosMatch<M, ? super T>> matchers;

    public CompositePosMatch(int length, PosMatch<M, ? super T>... matchers) {
        this(length, Arrays.asList(matchers));
    }

    public CompositePosMatch(int length, List<? extends PosMatch<M, ? super T>> matchers) {
        this.matchers = assertList(matchers);
        this.length = length;
    }

    public static int sumLength(List<? extends PosMatch> matchers) {
        return matchers.stream().mapToInt(m -> m.getLength()).sum();
    }

    public static  int assertSameLength(List<? extends PosMatch> matchers) {
        if (matchers.isEmpty()) {
            return -1;
        }
        final int expectedLength = matchers.get(0).getLength();

        for (int i = 1; i < matchers.size(); i++) {
            int length = matchers.get(i).getLength();
            if (expectedLength != length) {
                throw new IllegalArgumentException("Length mismatch. Expected " + expectedLength + " but found:" + length + " at " + matchers.get(i));
            }
        }
        return expectedLength;
    }

    @Override
    public String stringValues() {
        return super.stringValues() + " matchers=" + matchers;
    }

    public static <T> List<T> assertList(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Empty lists not allowed");
        }
        if (list.stream().anyMatch(f -> f == null)) {
            throw new IllegalArgumentException(list + " contains a null");
        }
        return new ArrayList<>(list);
    }

}
