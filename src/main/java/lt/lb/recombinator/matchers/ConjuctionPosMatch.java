package lt.lb.recombinator.matchers;

import java.util.Arrays;
import java.util.List;
import lt.lb.recombinator.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class ConjuctionPosMatch<M, T> extends CompositePosMatch<M, T> {

    public ConjuctionPosMatch(PosMatch<M, ? super T>... matchers) {
        this(Arrays.asList(matchers));
    }

    public ConjuctionPosMatch(List<? extends PosMatch<M, ? super T>> matchers) {
        super(assertSameLength(matchers), matchers);
    }

    @Override
    public boolean matches(int position, T token) {
        for (PosMatch<M, ? super T> matcher : matchers) {
            if (!matcher.matches(position, token)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isRepeating() {
        for (PosMatch<M, ? super T> matcher : matchers) {
            if (!matcher.isRepeating()) {
                return false;
            }
        }
        return true;
    }

}
