package lt.lb.recombinator.matchers;

import lt.lb.recombinator.PosMatched;


/**
 *
 * @author laim0nas100
 */
public class NameLiftPosMatch<M, T> extends BaseLiftPosMatch<M, T> {

    protected M name;

    public NameLiftPosMatch(M name) {
        this.name = name;
        this.length = 1;
    }

    @Override
    public boolean matches(int position, PosMatched<M, T> item) {
        return item.containsMatcher(name);
    }


}
