package lt.lb.recombinator.impl.codepoint;

import java.util.List;
import lt.lb.recombinator.impl.PosMatchedSimple;

/**
 *
 * @author laim0nas100
 */
public class CodepointMatched extends PosMatchedSimple<String, Integer> {

    public CodepointMatched(List<Integer> items) {
        super(items);
    }

    public CodepointMatched(List<String> matched, List<Integer> items) {
        super(matched, items);
    }
    
    
    
}
