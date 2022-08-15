package lt.lb.recombinator;

/**
 *
 * @author laim0nas100
 */
public interface Id {

    public default Object id() {
        return System.identityHashCode(this);
    }

    public default String stringValues() {
        return "";
    }

    public default String descriptiveString() {
        return getClass().getSimpleName() + "{" + stringValues() + "}";
    }
}
