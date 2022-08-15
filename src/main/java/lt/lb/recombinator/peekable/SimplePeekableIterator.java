package lt.lb.recombinator.peekable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class SimplePeekableIterator<T> implements PeekableIterator<T> {

    protected LinkedList<T> buffer = new LinkedList<>();
    protected Iterator<T> real;

    public SimplePeekableIterator(Iterator<T> real) {
        this.real = Objects.requireNonNull(real, "Iterator provided is null");
    }

    @Override
    public boolean hasNext() {
        return !buffer.isEmpty() || real.hasNext();
    }

    @Override
    public T next() {
        if (buffer.isEmpty()) {
            return real.next();
        } else {
            return buffer.pollFirst();
        }

    }

    @Override
    public T peek(int ahead) {
        if (ahead <= 0) {
            throw new IllegalArgumentException("Non-positive peek index");
        }

        while (buffer.size() < ahead && hasNext()) {
            buffer.addLast(real.next());
        }

        if (buffer.size() >= ahead) {
            return buffer.get(ahead - 1);
        } else {
            throw new NoSuchElementException("No more items to peek");
        }
    }

    @Override
    public boolean canPeek(int ahead) {
        if (ahead <= 0) {
            throw new IllegalArgumentException("Non-positive peek index");
        }
        if (buffer.size() >= ahead) {
            return true;
        }
        while (hasNext() && buffer.size() < ahead) {
            buffer.addLast(real.next());
        }
        return buffer.size() >= ahead;
    }
}
