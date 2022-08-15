package lt.lb.recombinator;

import java.util.function.Function;

/**
 *
 * @author laim0nas100
 */
public interface PosMatchedDelifter<I, A, B> extends Function<PosMatched<I, A>, PosMatched<I,B>> {

}
