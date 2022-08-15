package lt.lb.recombinator.matchers;

import java.util.Arrays;
import java.util.List;
import lt.lb.recombinator.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class DisjunctionPosMatch<M, T> extends CompositePosMatch<M, T> {

    public DisjunctionPosMatch(PosMatch<M, ? super T>... matchers) {
        this(Arrays.asList(matchers));
    }

    public DisjunctionPosMatch(List<? extends PosMatch<M, ? super T>> matchers) {
        super(assertSameLength(matchers), matchers);
    }

    @Override
    public boolean matches(int position, T token) {
        for (PosMatch<M, ? super T> matcher : matchers) {
            if (matcher.matches(position, token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRepeating() {
        for (PosMatch<M, ? super T> matcher : matchers) {
            if (matcher.isRepeating()) {
                return true;
            }
        }
        return false;
    }

}
