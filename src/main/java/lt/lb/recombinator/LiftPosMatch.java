package lt.lb.recombinator;

/**
 *
 * @author laim0nas100
 */
public interface LiftPosMatch<I, T> extends PosMatch<I, PosMatched<I, T>> {
    
    public interface LiftPosMatchDelegate<I,T> extends LiftPosMatch<I, T>, DelegatedPosMatch<I, PosMatched<I, T>>{
        
    }
}
