package lt.lb.recombinator.matchers;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.FlatMatched;

/**
 *
 * @author laim0nas100
 * @param <T> type of items to be matched
 * @param <I> matcher id type
 * @param <P> base PosMatch type
 * @param <M> Matchers implementation
 *
 *
 */
public abstract class MatchersFlatId<I,T extends FlatMatched<I,?>, P extends PosMatch<I, ?>, M extends MatchersFlatId<I,T, P, M>> {

    protected I name;
    protected boolean repeating;
    protected int importance;

    public M makeNew(I name) {
        Objects.requireNonNull(name);
        M matchers = this.create();
        matchers.name = name;
        matchers.importance = importance;
        matchers.repeating = repeating;
        return matchers;
    }

    public M setDefaultName(I name) {
        M me = me();
        me.name = name;
        return me;
    }

    public M repeating(boolean repeating) {
        M me = me();
        me.repeating = repeating;
        return me;
    }

    public M importance(int importance) {
        M me = me();
        me.importance = importance;
        return me;
    }

    public P isWhen(Predicate<T> pred) {
        return decorate(new PredicatePosMatch<>(pred));
    }
    
    public P isNotWhen(Predicate<T> pred) {
        Objects.requireNonNull(pred);
        return decorate(new PredicatePosMatch<>(pred.negate()));
    }

    public P isEqual(T other) {
        return decorate(new EqPosMatch<>(other));
    }

    public P isNotEqual(T other) {
        return decorate(new NotEqPosMatch<>(other));
    }

    public P any(int len) {
        return decorate(new AnyPosMatch<>(len));
    }

    public P or(PosMatch<I,Object>... matchers) {
        return decorate(new DisjunctionPosMatch<>(matchers));
    }

    public P and(PosMatch<I, Object>... matchers) {
        return decorate(new ConjuctionPosMatch<>(matchers));
    }

    public P concat(PosMatch<I,Object>... matchers) {
        return decorate(new ConcatPosMatch<>(matchers));
    }

    public P array(T[] array) {
        return decorate(new ArrayPosMatch<>(array));
    }

    public P in(Collection<T> set) {
        return decorate(new InPosMatch<>(true, set));
    }

    public P notIn(Collection<T> set) {
        return decorate(new InPosMatch<>(false, set));
    }

    protected <K extends BasePosMatch<I, T>> P decorate(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return simpleType(k);
    }

    protected abstract P simpleType(PosMatch<I, ? super T> posMatched);

    protected abstract M create();

    protected abstract M me();

}
