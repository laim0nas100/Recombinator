package lt.lb.recombinator.matchers;

/**
 *
 * @author laim0nas100
 */
public class AnyPosMatch<M, T> extends BasePosMatch<M, T> {

    public AnyPosMatch(int length) {
        this.length = length;
    }

    public AnyPosMatch() {
        this(1);
    }

    @Override
    public boolean matches(int position, T item) {
        return true;
    }

}
