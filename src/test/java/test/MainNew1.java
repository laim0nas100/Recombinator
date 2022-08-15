package test;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.Utils;
import lt.lb.recombinator.impl.SimpleMatchFinder;
import lt.lb.recombinator.impl.SimpleMatchImpl;
import lt.lb.recombinator.impl.codepoint.CodepointMatchersLegacy;
import lt.lb.recombinator.Recombinator;
import lt.lb.recombinator.peekable.SimplePeekableIterator;

/**
 *
 * @author laim0nas100
 */
public class MainNew1 {

    static boolean bench = true;

    public static void main(String[] args) throws Exception {
        URL resource;
        if (bench) {
            resource = MainNew1.class.getResource("/bible.txt");
        } else {
            resource = MainNew1.class.getResource("/parse_test.txt");
            resource = MainNew1.class.getResource("/bible_small.txt");
        }

        System.setOut(new PrintStream(System.out, true, "UTF8")); // Essential!

        SimplePeekableIterator<Integer> ofReaderCodepoints = Utils.peekableReaderCodepoints(Files.newBufferedReader(Paths.get(resource.toURI()), StandardCharsets.UTF_8));

//        ofReaderCodepoints = new SimplePeekableIterator<>(ofReaderCodepoints.toStream().collect(Collectors.toList()).iterator());
//        SimplePeekableIterator<Integer> ofReaderCodepoints = PeekableIterator.ofReaderChars2(new ReusableStringReader(term));
        CodepointMatchersLegacy ch = new CodepointMatchersLegacy();
        List<PosMatch<String, Integer>> list = Arrays.asList(
                ch.whitespace(),
                ch.letters(),
                ch.digits(),
                ch.makeNew("Any").repeating(false).importance(-1).any(1)
        );

        SimpleMatchFinder<String, Integer> simpleMatchFinder = new SimpleMatchFinder<>(list, SimpleMatchFinder.SimpleMatchMode.BEST);
        Recombinator<String, Integer> simpleMatch = new SimpleMatchImpl<>(ofReaderCodepoints, simpleMatchFinder);

        if (bench) {

            long time = System.currentTimeMillis();
            long count = simpleMatch.toStream().count();

            time = System.currentTimeMillis() - time;
            System.out.println(time);
            System.out.println(count);

            return;
        }
        List<PosMatched<String, Integer>> all = simpleMatch.tryMatchAll();
        for (PosMatched<String, Integer> pm : all) {
            System.out.println(pm.matchedBy() + " tokens=" + pm.items().stream().map(Utils::fromCodepoint).collect(Collectors.joining()));
        }

        try ( PrintWriter writer = new PrintWriter(new File("out.txt"), "UTF-8")) {
            for (PosMatched<String, Integer> pm : all) {
                writer.println(pm.matchedBy() + " tokens=" + pm.items().stream().map(Utils::fromCodepoint).collect(Collectors.joining()));
            }
        }

    }
}
