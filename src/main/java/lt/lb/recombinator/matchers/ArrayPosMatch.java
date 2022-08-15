package lt.lb.recombinator.matchers;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class ArrayPosMatch<M,T > extends BasePosMatch<M, T> {

    protected T[] array;

    public ArrayPosMatch(T[] array) {
        this.length = array.length;
        this.array = array;
    }

    @Override
    public boolean matches(int position, T item) {
        return Objects.equals(item, array[position]);
    }
}
