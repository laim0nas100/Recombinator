package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lt.lb.commons.DLog;
import lt.lb.commons.benchmarking.Benchmark;
import lt.lb.luceneindexandsearch.indexing.content.Premade;
import lt.lb.luceneindexandsearch.indexing.content.SimpleAnalyzer;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.Recombinator;
import lt.lb.recombinator.Utils;
import lt.lb.recombinator.impl.FlatMatchedSimple;
import lt.lb.recombinator.impl.SimpleMatchFinder;
import lt.lb.recombinator.impl.SimpleMatchImpl;
import lt.lb.recombinator.impl.codepoint.CodepointMatchers;
import lt.lb.recombinator.impl.codepoint.CodepointPosMatch;
import lt.lb.recombinator.impl.codepoint.StringMatchers;
import lt.lb.recombinator.peekable.PeekableIterator;
import org.apache.commons.lang3.RegExUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DocValuesFieldExistsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import lt.lb.recombinator.FlatMatched;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.impl.PosMatchedSimple;
import lt.lb.recombinator.impl.codepoint.StringMatchers.StringPosMatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

/**
 *
 * @author laim0nas100
 */
public class RevQuery1 {

    public static final int TERMS_HARD_LIMIT = 1024;

    public static final CodepointMatchers C = new CodepointMatchers();
    public static final StringMatchers S = new StringMatchers();

    public static final String OPERATOR_AND = "and";
    public static final String OPERATOR_OR = "or";
    public static final String OPERATOR_NOT = "not";

    public static final String OPERATOR_WILD_QUESTION = "?";
    public static final String OPERATOR_WILD_STAR = "*";
    public static final String OPERATOR_WILD_QUESTION_ESC = "\\?";
    public static final String OPERATOR_WILD_STAR_ESC = "\\*";

    public static final CodepointPosMatch and = exact(OPERATOR_AND);
    public static final CodepointPosMatch or = exact(OPERATOR_OR);
    public static final CodepointPosMatch not = exact(OPERATOR_NOT);

    public static final String ALLOWED_SPEC_CHARS = "-/\\";
    static final Pattern REPLACE_REPEATING_WILDCARD = Pattern.compile("(\\*+\\?+)|(\\?+\\*+)|(\\*)+");

    // 1-st pass
    public static final CodepointPosMatch wildStar = exact(OPERATOR_WILD_STAR);
    public static final CodepointPosMatch wildQuestion = exact(OPERATOR_WILD_QUESTION);
    public static final CodepointPosMatch wildStarEsc = exact(OPERATOR_WILD_STAR_ESC);
    public static final CodepointPosMatch wildQuestionEsc = exact(OPERATOR_WILD_QUESTION_ESC);
    public static final CodepointPosMatch literal = C.lettersOrDigits();
    public static final CodepointPosMatch allowedChars = C.makeNew("ALLOWED_SPEC_CHARS").repeating(true).in(ALLOWED_SPEC_CHARS.codePoints().boxed().collect(Collectors.toList()));
    public static final CodepointPosMatch text = C.makeNew("text").or(literal, allowedChars);

    // 2-nd pass
    public static final StringPosMatch concatable = S.makeNew("concatable").orNames(text, wildStarEsc, wildQuestionEsc);
    public static final StringPosMatch wildCard = S.makeNew("wild_card").orNames(wildStar, wildQuestion);
    public static final StringPosMatch gate = S.makeNew("gate").orNames(and, or, not);
    public static final StringPosMatch or_L = S.makeNew("lifted_or").orNames(OPERATOR_OR);
    public static final StringPosMatch and_L = S.makeNew("lifted_and").orNames(OPERATOR_AND);
    public static final StringPosMatch not_L = S.makeNew("lifted_not").orNames(OPERATOR_NOT);

    // 3-rd pass
    public static final StringPosMatch wildCard_word = S.makeNew("wildCard_word").concat(wildCard, concatable);
    public static final StringPosMatch word_wildCard = S.makeNew("word_wildCard").concat(concatable, wildCard);
    public static final StringPosMatch wildCard_word_wildcard = S.makeNew("wildCard_word_wildcard").concat(wildCard, concatable, wildCard);

    public static final SimpleMatchFinder<String, Integer> simpleMatchFinder = new SimpleMatchFinder<>(
            Arrays.asList(
                    wildQuestion, wildStar,
                    and, or, not,
                    text, literal
            )
    );

    public static final SimpleMatchFinder<String, FlatMatched<String, String>> simpleFlatMatchFinder1 = new SimpleMatchFinder<>(
            Arrays.asList(
                    wildCard_word_wildcard, word_wildCard, wildCard_word, wildCard,
                    gate, and_L, or_L, not_L, concatable)
    );

    public static CodepointPosMatch exact(String str) {
        return C.makeNew(str).repeating(false).string(str);
    }

    public static List<String> tokenizeTerms(String tokenize, Analyzer analyzer) throws IOException {

        ArrayList<String> terms = new ArrayList<>();
        try ( TokenStream tokenStream = analyzer.tokenStream("anyField", tokenize)) {
            tokenStream.reset();

            int count = 0;
            while (count < TERMS_HARD_LIMIT) {

                boolean hasToken = tokenStream.incrementToken();
                if (!hasToken) {
                    break;
                }
                boolean hasChar = tokenStream.hasAttribute(CharTermAttribute.class);
                if (!hasChar) {
                    continue;
                }
                CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
                String term = new String(attribute.buffer(), 0, attribute.length());
                terms.add(term);
                count++;

            }
        }
        return terms;

    }

    public static String tokenizeTermsToString(String tokenize, Analyzer analyzer) throws IOException {
        return tokenizeTerms(tokenize, analyzer).stream().collect(Collectors.joining(" "));
    }

    public static Recombinator<String, Integer> buildTokenizer(String str) {
        return new SimpleMatchImpl<>(Utils.peekableCodepoints(str), simpleMatchFinder);
    }

    public static Query buildQuery(final String search, Analyzer analyzer, String fieldName, String revFieldName, boolean allowAll, BooleanClause.Occur defaultOccur) throws Exception {

        String replaced = RegExUtils.replaceAll(search, REPLACE_REPEATING_WILDCARD, "*");
        List<String> terms = tokenizeTerms(replaced, analyzer);
        LinkedList<List<PosMatched<String, String>>> splitMatch = new LinkedList<>();

//        PosMatch<String, FlatMatched<String, String>> orNames = S.makeNew("concatable").orNames(text, wildStarEsc, wildQuestionEsc);
        for (String t : terms) {
//            t += " ==";
            Recombinator<String, Integer> tokenizer = new SimpleMatchImpl<>(Utils.peekableCodepoints(t), simpleMatchFinder);
            PeekableIterator<FlatMatched<String, String>> flatten = tokenizer
                    .flatten(p -> {
                        String string = p.items().stream().map(Utils::fromCodepoint).collect(Collectors.joining());
                        if (p.isUnmatched()) {
                            return new FlatMatchedSimple<String, String>(string);
                        }

                        return new FlatMatchedSimple<>(p.matchedBy(), string);
                    });

            SimpleMatchImpl<String, FlatMatched<String, String>> simpleMatchImpl2 = new SimpleMatchImpl<>(flatten, simpleFlatMatchFinder1);
            PeekableIterator<PosMatched<String, String>> delift = simpleMatchImpl2.delift(p -> {
                Set<String> id = new LinkedHashSet<>();
                List<String> sb = new ArrayList<>();
                final boolean unmatched = p.isUnmatched();
                id.addAll(p.matchedBy());
                p.items().forEach(fm -> {
                    if (unmatched) {
                        id.addAll(fm.matchedBy());
                    }

                    sb.add(fm.getItem());
                });
                if (id.isEmpty()) {
                    return new PosMatchedSimple<String, String>(sb);
                }
                return new PosMatchedSimple<String, String>(new ArrayList<>(id), sb);
            });
            List<PosMatched<String, String>> collect = delift.toStream().collect(Collectors.toList());

            DLog.printLines(collect);
            splitMatch.add(collect);

        }
//        DLog.printLines(splitMatch);

//        return new MatchNoDocsQuery();
        if (splitMatch.isEmpty()) {
            return new MatchNoDocsQuery();
        }

        if (splitMatch.size()
                == 1 && allowAll) {
            List<PosMatched<String, String>> ma = splitMatch.getFirst();
            if (ma.size() == 1) {
                PosMatched<String, String> get = ma.get(0);
                if (get.containsMatcher(wildCard)) {
                    return new BooleanQuery.Builder()
                            .add(new DocValuesFieldExistsQuery(fieldName), BooleanClause.Occur.SHOULD)
                            .add(new DocValuesFieldExistsQuery(revFieldName), BooleanClause.Occur.SHOULD)
                            .build();
                }
            }
        }

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        BooleanClause.Occur nextOccur = defaultOccur;
        boolean occurChanged = false;
        for (List<PosMatched<String, String>> split : splitMatch) {
            boolean needreverse = false;
            boolean needregular = false;
            boolean simpleTerm = false;
            if (occurChanged) {
                occurChanged = false;
            } else {
                nextOccur = defaultOccur;
            }

            LinkedList<String> q = new LinkedList<>();
            LinkedList<String> rev = new LinkedList<>();
            for (PosMatched<String, String> ma : split) {

                if (ma.containsMatcher(wildCard_word_wildcard)) { // main case

                    q.addAll(ma.getItems(0, 1, 2));
                    rev.addAll(0, ma.getItems(2, 1, 0));
                    needreverse = true;
                    needregular = true;
                } else if (ma.containsMatcher(word_wildCard)) {
                    q.addAll(ma.getItems(0, 1));
                    rev.addAll(0, ma.getItems(1, 0));
                    needregular = true;
                } else if (ma.containsMatcher(wildCard_word)) {
                    q.addAll(ma.getItems(0, 1));
                    rev.addAll(0, ma.getItems(1, 0));
                    needreverse = true;
                } else if (ma.containsMatcher(gate)) {

                    occurChanged = true;
                    if (ma.containsMatcher(and_L)) {
                        nextOccur = BooleanClause.Occur.MUST;
                    } else if (ma.containsMatcher(or_L)) {
                        nextOccur = BooleanClause.Occur.SHOULD;
                    } else if (ma.containsMatcher(not_L)) {
                        nextOccur = BooleanClause.Occur.MUST_NOT;

                    }
                } else if (ma.containsMatcher(concatable)) {
                    q.addAll(ma.getItems(0));
                    rev.addFirst(ma.getItem(0));
                    simpleTerm = true;
                } else if (ma.containsMatcher(wildCard)) {
                    q.addAll(ma.getItems(0));
                    rev.addFirst(ma.getItem(0));
                } else { // assume grabage input
                }
            }

            List<Query> querys = new ArrayList<>();
            if (needregular) {
                while (!q.isEmpty() && (OPERATOR_WILD_STAR + OPERATOR_WILD_QUESTION).contains(q.getFirst())) {
                    // need to remove first wild card
                    q.removeFirst();
                }
                querys.add(new WildcardQuery(new Term(fieldName, q.stream().collect(Collectors.joining()))));

            }
            if (needreverse) {
                while (!rev.isEmpty() && (OPERATOR_WILD_STAR + OPERATOR_WILD_QUESTION).contains(rev.getFirst())) {
                    // need to remove first wild card
                    rev.removeFirst();
                }
                String revQeury = rev.stream().map(StringUtils::reverse).collect(Collectors.joining());
                querys.add(new WildcardQuery(new Term(revFieldName, revQeury)));

            }
            if (simpleTerm && !(needregular || needreverse)) {
                querys.add(new TermQuery(new Term(fieldName, q.stream().collect(Collectors.joining()))));
            }
            if (!querys.isEmpty()) {
                if (querys.size() > 1) {
                    BooleanQuery.Builder inner = new BooleanQuery.Builder();
                    for (Query query : querys) {
                        inner.add(query, BooleanClause.Occur.SHOULD); // duplicating querys
                    }
                    builder.add(inner.build(), nextOccur);
                } else {
                    builder.add(querys.get(0), nextOccur);
                }
            }

        }

        return builder.build();
    }

    public static Query buildQuery(final String query, Analyzer analyzer, String fieldName, String revFieldName) throws Exception {
        return buildQuery(query, analyzer, fieldName, revFieldName, false, BooleanClause.Occur.SHOULD); // default AND
    }

    public static void main(String[] args) throws IOException, Exception {

        DLog.main().async = true;
        SimpleAnalyzer defaultAnalyzer = Premade.defaultSearchAnalyzer();

        Benchmark bm = new Benchmark();
        bm.threads = 1;
        bm.warmupTimes = 100;

        String term = "*hell?o?? *help?me?jesus? " + " NOT " + " **something else?* regular";
//        String term = "*13225456 ";

//        String term = " ";
//        for (int i = 0; i < 10; i++) {
//            term = term + " " + term;
//        }
        TokenStream tokenStream = defaultAnalyzer.tokenStream(OPERATOR_AND, term);
        tokenStream.reset();
        while (true) {

            boolean inc = tokenStream.incrementToken();

            CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class
            );
            DLog.print(attribute.toString());
            if (!inc) {
                break;
            }
        }
        tokenStream.close();
        buildQuery(term, defaultAnalyzer, "field", "revField");

//        bm.executeBench(10000, "ok", () -> {
//            buildQuery(term, defaultAnalyzer, "field", "revField");
//        }).print(DLog::print);
        DLog.print("Build query");
        Query query = buildQuery(term, defaultAnalyzer, "field", "revField");
        DLog.print("After build");

        DLog.print(query);

        DLog.close();
    }
}
