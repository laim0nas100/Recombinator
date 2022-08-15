package lt.lb.recombinator.matchers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author laim0nas100
 */
public class InPosMatch<I,T> extends BasePosMatch<I, T> {

    public InPosMatch(boolean inclusive, Collection<T> set) {
        this.inclusive = inclusive;
        this.set = new HashSet<>(set);
    }

    protected Set<T> set;
    protected boolean inclusive = true;
    
    @Override
    public boolean matches(int position, T item) {
        return inclusive ? set.contains(item) : !set.contains(item);
    }
    
    
    
}
