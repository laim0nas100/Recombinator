package lt.lb.recombinator.matchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.FlatMatched;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;

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
public abstract class MatchersFlat<I, V, T extends FlatMatched<I, V>, P extends PosMatch<I, T>, M extends MatchersFlat<I, V, T, P, M>> {

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

    public P predicate(Predicate<T> pred) {
        return decorate(new PredicatePosMatch<>(pred));
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

    public P or(PosMatch<I, ? super T>... matchers) {
        return decorate(new DisjunctionPosMatch<>(matchers));
    }

    public P orNames(PosMatch<I, ?>... matchers) {
        return orNames(Stream.of(matchers).map(m -> m.getName()).collect(Collectors.toSet()));
    }

    public P orNames(I... names) {
        return orNames(Arrays.asList(names));
    }

    public P orNames(Collection<I> names) {
        Objects.requireNonNull(names);

        return isWhen(p -> {
            if (p.isUnmatched()) {
                return false;
            }
            for (I n : p.matchedBy()) {
                if (names.contains(n)) {
                    return true;
                }
            }
            return false;
        });
    }

    public P and(PosMatch<I, T>... matchers) {
        return decorate(new ConjuctionPosMatch<>(matchers));
    }

    public P andNames(PosMatch<I, ?>... matchers) {
        return andNames(Stream.of(matchers).map(m -> m.getName()).collect(Collectors.toSet()));
    }

    public P andNames(I... names) {
        return andNames(Arrays.asList(names));
    }

    public P andNames(Collection<I> names) {
        Objects.requireNonNull(names);

        return isWhen(p -> {
            if (p.isUnmatched()) {
                return false;
            }
            for (I n : p.matchedBy()) {
                if (!names.contains(n)) {
                    return false;
                }
            }
            return true;
        });
    }

    public P concat(PosMatch<I, ? super T>... matchers) {
        return decorate(new ConcatPosMatch<>(matchers));
    }

    public P concat(List<PosMatch<I, ? super T>> matchers) {
        return decorate(new ConcatPosMatch<>(matchers));
    }

    public P concatNames(PosMatch<I, ?>... matchers) {
        return concatNames(Stream.of(matchers).map(m -> m.getName()).collect(Collectors.toList()));
    }

    public P concatNames(I... names) {
        return concatNames(Arrays.asList(names));
    }

    public P concatNames(List<I> names) {
        Objects.requireNonNull(names);
        BasePosMatch<I, T> concatPosMatch = (BasePosMatch) new ConcatPosMatch<>(
                names.stream().map(m -> new NameFlatPosMatch<>(m)).collect(Collectors.toList())
        );
        return decorate(concatPosMatch);

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
