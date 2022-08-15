package lt.lb.recombinator.matchers;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lt.lb.recombinator.LiftPosMatch;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.impl.SimpleMatchersImpl.BaseMatchers;

/**
 *
 * @author laim0nas100
 * @param <T> type of items to be matched
 * @param <I> matcher id type
 * @param <P> base PosMatch type
 * @param <PP> base PosMatch lifted type
 * @param <M> Matchers implementation
 *
 *
 */
@Deprecated
public abstract class MatchersLegacy<I, T, P extends PosMatch<I, T>, PP extends LiftPosMatch<I, T>, M extends MatchersLegacy<I, T, P, PP, M>> {

    protected I name;
    protected boolean repeating;
    protected int importance;

    public static <I, T> BaseMatchers<I, T> simple() {
        return new BaseMatchers<>();
    }

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

    public P ofType(Class<? extends T> cls, final boolean exact) {
        Objects.requireNonNull(cls);
        return decorate(new PredicatePosMatch<>(t -> {
            if (exact) {
                if (t == null) {
                    return false;
                }
                return cls.getClass().equals(t.getClass());
            }
            return cls.isInstance(t);
        }));
    }

    public P ofType(Class<? extends T> cls) {
        return ofType(cls, false);
    }

    public P any(int len) {
        return decorate(new AnyPosMatch<>(len));
    }

    public PP anyLifted(int len) {
        return decorateLifted(new AnyPosMatch<>(len));
    }

    public P or(PosMatch<I, ? super T>... matchers) {
        return decorate(new DisjunctionPosMatch<>(matchers));
    }

    public PP orLifted(PP... matchers) {
        return decorateLifted(new DisjunctionPosMatch<>(matchers));
    }
    
    public PP orLifted(I... names) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(names).map(n -> new NameLiftPosMatch<I, T>(n)).collect(Collectors.toList());
        return decorateLifted(new DisjunctionPosMatch<>(collect));
    }

    public PP orLiftedNames(PosMatch<I, ? super T>... matchers) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(matchers).map(n -> new NameLiftPosMatch<I, T>(n.getName())).collect(Collectors.toList());
        return decorateLifted(new DisjunctionPosMatch<>(collect));
    }

    public P and(PosMatch<I, T>... matchers) {
        return decorate(new ConjuctionPosMatch<>(matchers));
    }

    public PP andLifted(PP... matchers) {
        return decorateLifted(new ConjuctionPosMatch<>(matchers));
    }
    
    public PP andLifted(I... names) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(names).map(n -> new NameLiftPosMatch<I, T>(n)).collect(Collectors.toList());
        return decorateLifted(new ConjuctionPosMatch<>(collect));
    }

    public PP andLiftedNames(PosMatch<I, ? super T>... matchers) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(matchers).map(n -> new NameLiftPosMatch<I, T>(n.getName())).collect(Collectors.toList());
        return decorateLifted(new ConjuctionPosMatch<>(collect));
    }

    public P concat(PosMatch<I, ? super T>... matchers) {
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

    public PP concatLifted(PP... matchers) {
        return decorateLifted(new ConcatPosMatch<>(matchers));
    }

    public PP concatLifted(I... names) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(names).map(n -> new NameLiftPosMatch<I, T>(n)).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public PP concatLiftedNames(PosMatch<I, ? super T>... matchers) {
        List<NameLiftPosMatch<I, T>> collect = Stream.of(matchers).map(n -> new NameLiftPosMatch<I, T>(n.getName())).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public PP lifted(I name) {
        return decorateLifted(new NameLiftPosMatch<>(name));
    }

    public PP lifted(PosMatch<I, ? super T> posMatch) {
        return decorateLifted(new NameLiftPosMatch<>(posMatch.getName()));
    }

    protected <K extends BasePosMatch<I, T>> P decorate(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return simpleType(k);
    }

    protected <K extends BasePosMatch<I, ? super PosMatched<I, T>>> PP decorateLifted(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return liftedType(k);
    }

    protected abstract P simpleType(PosMatch<I, ? super T> posMatched);

    protected abstract PP liftedType(PosMatch<I, ? super PosMatched<I, T>> posMatchedLift);

    protected abstract M create();

    protected abstract M me();

}
