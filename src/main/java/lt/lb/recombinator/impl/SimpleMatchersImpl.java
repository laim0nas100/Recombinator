package lt.lb.recombinator.impl;

import java.util.Objects;
import lt.lb.recombinator.LiftPosMatch;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.matchers.MatchersLegacy;

/**
 *
 * @author laim0nas100
 */
public abstract class SimpleMatchersImpl {

    public static class BaseMatchers<I, T> extends MatchersLegacy<I, T, PosMatch<I, T>, LiftPosMatch<I, T>, BaseMatchers<I, T>> {

        @Override
        protected BaseMatchers<I, T> create() {
            return new BaseMatchers<>();
        }

        @Override
        protected BaseMatchers<I, T> me() {
            return this;
        }

        @Override
        protected PosMatch<I, T> simpleType(PosMatch<I, ? super T> posMatched) {
            return (PosMatch) Objects.requireNonNull(posMatched);
        }

        @Override
        protected LiftPosMatch<I, T> liftedType(PosMatch<I, ? super PosMatched<I, T>> posMatchedLift) {
            Objects.requireNonNull(posMatchedLift);
            if (posMatchedLift instanceof LiftPosMatch) {
                return (LiftPosMatch) posMatchedLift;
            }
            return (LiftPosMatch.LiftPosMatchDelegate<I, T>) () -> {
                return (PosMatch) posMatchedLift;
            };
        }

    }

    public static class SimpleMatchers<T> extends MatchersLegacy<String, T, SimplePosMatch<T>, SimpleLiftPosMatch<T>, SimpleMatchers<T>> {

        @Override
        protected SimpleMatchers<T> create() {
            return new SimpleMatchers<>();
        }

        @Override
        protected SimpleMatchers<T> me() {
            return this;
        }

        @Override
        protected SimplePosMatch<T> simpleType(PosMatch<String, ? super T> posMatched) {
            Objects.requireNonNull(posMatched);
            if (posMatched instanceof SimplePosMatch) {
                return (SimplePosMatch) posMatched;
            }
            return () -> (PosMatch) posMatched;
        }

        @Override
        protected SimpleLiftPosMatch<T> liftedType(PosMatch<String, ? super PosMatched<String, T>> posMatchedLift) {
            Objects.requireNonNull(posMatchedLift);
            if (posMatchedLift instanceof SimpleLiftPosMatch) {
                return (SimpleLiftPosMatch) posMatchedLift;
            }
            return () -> (PosMatch) posMatchedLift;

        }
    }
}
