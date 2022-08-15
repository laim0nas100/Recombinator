package lt.lb.recombinator;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class CodepointIterator implements Iterator<Integer>, AutoCloseable {

    protected char[] chars = null;
    protected Reader reader;

    public CodepointIterator(Reader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    protected boolean fillBufferIfEmpty() {
        if (chars != null) {
            return true;
        }
        try {
            int read0 = reader.read();
            if (read0 < 0) {
                return false;
            } else {
                char r0 = (char) read0;
                if (Character.isHighSurrogate(r0)) {
                    int read1 = reader.read();
                    if (read1 < 0) {
                        chars = new char[]{r0};
                        throw new IOException("High surragate without it's pair:" + String.valueOf(chars));
                    }
                    char r1 = (char) read1;
                    if (Character.isLowSurrogate(r1)) {
                        chars = new char[]{r0, r1};
                    } else {
                        throw new IOException("Expected low-surrogate char and found pair:" + String.valueOf(new char[]{r0, r1}));
                    }
                } else {
                    chars = new char[]{r0};
                }
            }
        } catch (IOException io) {
            throw new UncheckedIOException(io);
        }
        return true;

    }

    @Override
    public boolean hasNext() {
        return fillBufferIfEmpty();

    }

    @Override
    public Integer next() {
        fillBufferIfEmpty();
        if (chars == null) {
            throw new NoSuchElementException("Out of characters");
        }
        if (chars.length == 1) {
            int c = chars[0];
            chars = null;
            return c;
        } else { // must be length 2 then
            int c = Character.toCodePoint(chars[0], chars[1]);
            chars = null;
            return c;
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
