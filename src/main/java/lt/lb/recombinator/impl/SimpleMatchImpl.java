package lt.lb.recombinator.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lt.lb.recombinator.MatchFinder;
import lt.lb.recombinator.PosMatched;
import lt.lb.recombinator.PosMatchedFlattener;
import lt.lb.recombinator.Recombinator;
import lt.lb.recombinator.Utils;
import lt.lb.recombinator.peekable.PeekableIterator;
import lt.lb.recombinator.FlatMatched;
import lt.lb.recombinator.PosMatchedDelifter;

/**
 *
 * @author laim0nas100
 */
public class SimpleMatchImpl<I, T> implements Recombinator<I, T> {
    
    protected PeekableIterator<T> peekable;
    protected PeekableIterator<PosMatched<I, T>> matchedIterator;
    
    protected MatchFinder<I, T> matchFinder;
    
    public SimpleMatchImpl(PeekableIterator<T> items, MatchFinder<I, T> matchFinder) {
        this.peekable = Objects.requireNonNull(items);
        this.matchFinder = Objects.requireNonNull(matchFinder);
        matchedIterator = Utils.peekableGenerator(() -> findBestMatch(peekable), t -> !t.isEmpty());
    }
    
    @Override
    public PosMatched<I, T> constructUnmatched(List<T> tokens) {
        return new PosMatchedSimple<>(tokens);
    }
    
    @Override
    public PosMatched<I, T> construct(List<I> identifiers, List<T> tokens) {
        return new PosMatchedSimple<>(identifiers, tokens);
    }
    
    @Override
    public PosMatched<I, T> constructEmpty() {
        return PosMatchedSimple.empty();
    }
    
    @Override
    public PosMatched<I, T> construct(I identifier, List<T> tokens) {
        return new PosMatchedSimple<>(Collections.singletonList(identifier), tokens);
    }
    
    @Override
    public boolean canPeek(int ahead) {
        return matchedIterator.canPeek(ahead);
    }
    
    @Override
    public PosMatched<I, T> peek(int ahead) {
        return matchedIterator.peek(ahead);
    }
    
    @Override
    public boolean hasNext() {
        return matchedIterator.hasNext();
    }
    
    @Override
    public PosMatched<I, T> next() {
        return matchedIterator.next();
    }
    
    @Override
    public MatchFinder<I, T> getMatchFinder() {
        return matchFinder;
    }
    
    @Override
    public <R> PeekableIterator<FlatMatched<I, R>> flatten(PosMatchedFlattener<I, T, R> reducer) {
        SimpleMatchImpl<I, T> me = this;
        Iterator<FlatMatched<I, R>> iterator = new Iterator<FlatMatched<I, R>>() {
            @Override
            public boolean hasNext() {
                return me.hasNext();
            }
            
            @Override
            public FlatMatched<I, R> next() {
                return reducer.apply(me.next());
            }
        };
        
        return Utils.peekable(iterator);
    }

    @Override
    public <R> PeekableIterator<PosMatched<I, R>> delift(PosMatchedDelifter<I, T, R> reducer) {
         SimpleMatchImpl<I, T> me = this;
        Iterator<PosMatched<I, R>> iterator = new Iterator<PosMatched<I, R>>() {
            @Override
            public boolean hasNext() {
                return me.hasNext();
            }
            
            @Override
            public PosMatched<I, R> next() {
                return reducer.apply(me.next());
            }
        };
        
        return Utils.peekable(iterator);
    }
    
    
    
    
    
}
