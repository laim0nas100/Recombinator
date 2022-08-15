package lt.lb.recombinator.impl.codepoint;

import java.util.Locale;
import java.util.Objects;
import lt.lb.recombinator.PosMatch;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.matchers.MatchersLegacy;

/**
 *
 * @author laim0nas100
 */
public class CodepointMatchersLegacy extends MatchersLegacy<String, Integer, CodepointPosMatch, CodepointLiftPosMatch, CodepointMatchersLegacy> {
    
    private static final CodepointMatchersLegacy INSTANCE = new CodepointMatchersLegacy();
    
    protected static final CodepointPosMatch WHITESPACE = INSTANCE.makeNew("Whitespace").repeating(true).isWhen(Character::isWhitespace);
    protected static final CodepointPosMatch DIGIT = INSTANCE.makeNew("Digit").repeating(false).isWhen(Character::isDigit);
    protected static final CodepointPosMatch LETTER = INSTANCE.makeNew("Letter").repeating(false).isWhen(Character::isAlphabetic);
    protected static final CodepointPosMatch LETTER_OR_DIGIT = INSTANCE.makeNew("Letter_or_digit").repeating(false).isWhen(Character::isLetterOrDigit);
    
    protected static final CodepointPosMatch DIGITS = INSTANCE.makeNew("Digits").repeating(true).isWhen(Character::isDigit);
    protected static final CodepointPosMatch LETTERS = INSTANCE.makeNew("Letters").repeating(true).isWhen(Character::isAlphabetic);
    protected static final CodepointPosMatch LETTERS_OR_DIGITS = INSTANCE.makeNew("Letters_or_digits").repeating(true).isWhen(Character::isLetterOrDigit);
    
    @Override
    protected CodepointPosMatch simpleType(PosMatch<String, ? super Integer> posMatched) {
        Objects.requireNonNull(posMatched);
        if (posMatched instanceof CodepointPosMatch) {
            return (CodepointPosMatch) posMatched;
        }
        return () -> (PosMatch) posMatched;
    }
    
      @Override
    protected CodepointLiftPosMatch liftedType(PosMatch<String, ? super PosMatched<String, Integer>> posMatched) {
        Objects.requireNonNull(posMatched);
        if (posMatched instanceof CodepointLiftPosMatch) {
            return (CodepointLiftPosMatch) posMatched;
        }
        return () -> (PosMatch) posMatched;
    }
    
    
    @Override
    protected CodepointMatchersLegacy create() {
        return new CodepointMatchersLegacy();
    }
    
    @Override
    protected CodepointMatchersLegacy me() {
        return this;
    }
    
    public CodepointPosMatch whitespace() {
        return WHITESPACE;
    }
    
    public CodepointPosMatch digit() {
        return DIGIT;
    }
    
    public CodepointPosMatch letter() {
        return LETTER;
    }
    
    public CodepointPosMatch letterOrDigit() {
        return LETTER_OR_DIGIT;
    }
    
    public CodepointPosMatch digits() {
        return DIGITS;
    }
    
    public CodepointPosMatch letters() {
        return LETTERS;
    }
    
    public CodepointPosMatch lettersOrDigits() {
        return LETTERS_OR_DIGITS;
    }
    
    
    public CodepointPosMatch isEqual(char c) {
        return isEqual((int) c);
    }
    
    public CodepointPosMatch string(String str) {
        Objects.requireNonNull(str);
        return array(str.codePoints().boxed().toArray(s -> new Integer[s]));
    }
    public CodepointPosMatch stringIgnoreCase(String str, Locale locale) {
        Objects.requireNonNull(str);
        CodepointPosMatch lower = string(str.toLowerCase(locale));
        CodepointPosMatch upper = string(str.toUpperCase(locale));
        return or(lower, upper);
    }
    
    public CodepointPosMatch stringIgnoreCase(String str) {
        return stringIgnoreCase(str, Locale.getDefault());
    }
    
    public void ok() {
        makeNew("number.separator").isEqual('.');
        
    }

  
    
}
