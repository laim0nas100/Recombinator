package lt.lb.recombinator;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lt.lb.recombinator.peekable.SimplePeekableIterator;

/**
 *
 * @author laim0nas100
 */
public class Utils {

    public static String fromCodepoint(int cp) {
        return String.valueOf(Character.toChars(cp));
    }

    /**
     *
     * @param <T>
     * @param supplier
     * @param acceptable
     * @return
     */
    public static <T> Iterator<T> ofGenerator(Supplier<T> supplier, Predicate<T> acceptable) {
        Objects.requireNonNull(supplier, "Supplier is null");
        Objects.requireNonNull(acceptable, "Predicate is null");
        return new Iterator<T>() {

            boolean cached;
            T next;

            private boolean fillCacheOrEmpty() {
                if (!cached) {
                    T get = supplier.get();
                    cached = acceptable.test(get);
                    next = get;
                }
                return cached;
            }

            @Override
            public boolean hasNext() {
                return fillCacheOrEmpty();
            }

            @Override
            public T next() {

                if (!fillCacheOrEmpty()) {
                    throw new NoSuchElementException("No more items");
                }
                cached = false;
                return next;
            }
        };

    }

    public static <T> Iterator<T> ofGeneratorNonNull(Supplier<T> supplier) {
        return ofGenerator(supplier, Objects::nonNull);
    }

    public static SimplePeekableIterator<Integer> peekableReaderCodepointsLines(Reader reader) {
        BufferedReader br;
        if (reader instanceof BufferedReader) {
            br = (BufferedReader) reader;
        } else {
            br = new BufferedReader(reader);
        }
        return new SimplePeekableIterator<>(br.lines().flatMapToInt(s -> s.codePoints()).iterator());
    }

    public static SimplePeekableIterator<Integer> peekableReaderChars(Reader reader) {
        BufferedReader br;
        if (reader instanceof BufferedReader) {
            br = (BufferedReader) reader;
        } else {
            br = new BufferedReader(reader);
        }
        return new SimplePeekableIterator<>(br.lines().flatMapToInt(s -> s.chars()).iterator());
    }

    public static SimplePeekableIterator<Integer> peekableReaderCodepoints(Reader reader) {
        return new SimplePeekableIterator<>(new CodepointIterator(reader));
    }
    
    public static SimplePeekableIterator<Integer> peekableCodepoints(CharSequence seq) {
        return new SimplePeekableIterator<>(seq.codePoints().iterator());
    }

    public static <T> SimplePeekableIterator<T> peekable(Iterator<T> iter) {
        return new SimplePeekableIterator<>(iter);
    }

    public static <T> SimplePeekableIterator<T> peekableGeneratorNonNull(Supplier<T> supplier) {
        return new SimplePeekableIterator<>(Utils.ofGeneratorNonNull(supplier));
    }

    public static <T> SimplePeekableIterator<T> peekableGenerator(Supplier<T> supplier, Predicate<T> acceptable) {
        return new SimplePeekableIterator<>(Utils.ofGenerator(supplier, acceptable));
    }

}
