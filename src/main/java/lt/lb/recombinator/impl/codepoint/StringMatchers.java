package lt.lb.recombinator.impl.codepoint;

import java.util.Objects;
import lt.lb.recombinator.DelegatedPosMatch;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.matchers.MatchersFlat;
import lt.lb.recombinator.FlatMatched;
import lt.lb.recombinator.impl.codepoint.StringMatchers.StringPosMatch;

/**
 *
 * @author laim0nas100
 */
public class StringMatchers extends MatchersFlat<String, String, FlatMatched<String, String>, StringPosMatch, StringMatchers> {

    public static interface StringPosMatch extends DelegatedPosMatch<String, FlatMatched<String, String>> {

    }

    private static final StringMatchers INSTANCE = new StringMatchers();

    @Override
    protected StringMatchers create() {
        return new StringMatchers();
    }

    @Override
    protected StringMatchers me() {
        return this;
    }

    @Override
    protected StringPosMatch simpleType(PosMatch<String, ? super FlatMatched<String, String>> posMatched) {
        Objects.requireNonNull(posMatched);
        if (posMatched instanceof StringPosMatch) {
            return (StringPosMatch) posMatched;
        }
        return (StringPosMatch) () -> (PosMatch) posMatched;
    }

}
