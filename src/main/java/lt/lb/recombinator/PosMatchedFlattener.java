package lt.lb.recombinator;

import java.util.function.Function;

/**
 *
 * @author laim0nas100
 */
public interface PosMatchedFlattener<I, A, B> extends Function<PosMatched<I, A>, FlatMatched<I,B>> {

}
