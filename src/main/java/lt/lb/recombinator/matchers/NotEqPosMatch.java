package lt.lb.recombinator.matchers;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class NotEqPosMatch<I, T> extends BasePosMatch<I, T> {

    protected T item;

    public NotEqPosMatch(T item) {
        this.item = item;
        this.length = 1;
    }

    @Override
    public boolean matches(int position, T item) {
        return !Objects.equals(this.item, item);
    }

}
