package lt.lb.recombinator.matchers;

import java.util.Objects;
import lt.lb.recombinator.FlatMatched;


/**
 *
 * @author laim0nas100
 */
public class NameFlatPosMatch<I, T> extends BasePosMatch<I, FlatMatched<I,T>> {

    protected I name;

    public NameFlatPosMatch(I name) {
        this.name = Objects.requireNonNull(name);
        this.length = 1;
    }

    @Override
    public boolean matches(int position, FlatMatched<I, T> item) {
        return item.containsMatcher(name);
    }


}
