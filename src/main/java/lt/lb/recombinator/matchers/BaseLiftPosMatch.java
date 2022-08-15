package lt.lb.recombinator.matchers;

import lt.lb.recombinator.LiftPosMatch;
import lt.lb.recombinator.PosMatched;

/**
 *
 * @author laim0nas100
 */
public abstract class BaseLiftPosMatch<M, T> extends BasePosMatch<M, PosMatched<M, T>> implements LiftPosMatch<M, T> {

}
