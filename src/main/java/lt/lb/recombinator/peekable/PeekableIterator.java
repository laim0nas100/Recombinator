package lt.lb.recombinator.peekable;

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 * @author laim0nas100
 */
public interface PeekableIterator<T> extends Iterator<T> {

    public boolean canPeek(int ahead);

    public default boolean canPeek() {
        return canPeek(1);
    }

    public T peek(int ahead);

    public default T peek() {
        return peek(1);
    }

    public default Stream<T> toStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0), false);
    }

}
