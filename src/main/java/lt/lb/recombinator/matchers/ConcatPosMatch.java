package lt.lb.recombinator.matchers;

import java.util.Arrays;
import java.util.List;
import lt.lb.recombinator.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class ConcatPosMatch<M,T> extends CompositePosMatch<M,T> {

    public ConcatPosMatch(PosMatch<M,? super T>... matchers) {
        this(Arrays.asList(matchers));
    }

    public ConcatPosMatch(List<? extends PosMatch<M,? super T>> matchers) {
        super(sumLength(matchers), matchers);
    }

    @Override
    public boolean matches(int position, T token) {
        int i = 0;
        for (PosMatch<M,? super T> m : matchers) {
            int len = m.getLength();
            if (position >= len) {
                position -= len;
                i++;
            } else {
                return matchers.get(i).matches(position, token);
            }

        }
        return false;
    }

}
