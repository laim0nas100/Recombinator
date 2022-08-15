package lt.lb.recombinator.matchers;

import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author laim0nas100
 */
public class PredicatePosMatch<M, T> extends BasePosMatch<M, T> {

    protected final Predicate<T> pred;

    public PredicatePosMatch(Predicate<T> pred) {
        this.pred = Objects.requireNonNull(pred);
        this.length = 1;
    }

    @Override
    public boolean matches(int position, T item) {
        return pred.test(item);
    }

}
