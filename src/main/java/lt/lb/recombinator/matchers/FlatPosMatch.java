package lt.lb.recombinator.matchers;

import java.util.Objects;
import java.util.function.Predicate;
import lt.lb.recombinator.FlatMatched;

/**
 *
 * @author laim0nas100
 */
public class FlatPosMatch<I,T> extends BasePosMatch<I, FlatMatched<I,T>> {

    protected Predicate<FlatMatched<I,T>> predicate;
    public FlatPosMatch(Predicate<FlatMatched<I,T>> predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }


    @Override
    public boolean matches(int position, FlatMatched<I, T> item) {
        return predicate.test(item);
    }
    
    
    
}
