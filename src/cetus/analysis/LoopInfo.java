package cetus.analysis;

import cetus.hir.Expression;
import cetus.hir.Loop;
import cetus.hir.Symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Represents loop-related information
 */
public class LoopInfo {
	
    private Expression upperBound; // IntLiteral or Variable
    private Expression lowerBound; // IntLiteral or Variable
    private Expression increment;  // IntLiteral or Variable
    private Expression indexVar;
    private LinkedList<Loop> loopNest;
    private int loopSize;
    private int NumberFC;
    private int LevelLoopNest;
    
    //set of all enclosing outermost loops and the loop itself
    
    public LoopInfo () {
        this.upperBound = null;
        this.lowerBound = null;
        this.increment = null;
        this.indexVar = null;
        this.loopNest = null;
        this.loopSize =0;
        this.NumberFC =0;
        this.LevelLoopNest =0;
        
    }
    
    /**
     * Creates a data structure containing loop-related information (use only
     * if canonical loop)
     * @param loop
     */
    public LoopInfo(Loop loop) {
        this.upperBound = LoopTools.getUpperBoundExpression(loop);
        this.lowerBound = LoopTools.getLowerBoundExpression(loop);
        this.increment = LoopTools.getIncrementExpression(loop);
        this.indexVar = LoopTools.getIndexVariable(loop);
        this.loopNest = LoopTools.calculateLoopNest(loop);
        this.loopSize =LoopTools.calculateLoopSize(loop);
        this.NumberFC =LoopTools.numberFunctionCall(loop);
        this.LevelLoopNest = LoopTools.calculateLoopNest(loop).size();
    }
    
    public int calculateLoopNest(Loop loop) {
    	return LoopTools.calculateLoopNest(loop).size();
    }
    
    public int calculateNumberOfFunctionCalls(Loop loop) {
    	return LoopTools.numberFunctionCall(loop);
    }
    
    /* Access functions */
    public Expression getLoopUB() {
        return upperBound;
    }
    
    public void setLoopUB(Expression ub) {
        upperBound = ub;
    }
    
    public Expression getLoopLB() {
        return lowerBound;
    }
    
    public void setLoopLB(Expression lb) {
        lowerBound = lb;
    }
    
    public Expression getLoopIncrement() {
        return increment;
    }
    
    public void setLoopIncrement(Expression inc) {
        increment = inc;
    }
    
    public Expression getLoopIndex() {
        return indexVar;
    }
    
    public LinkedList getNest() {
        return loopNest;
    }
    
    @Override
	public String toString() {
		return "LoopInfo [upperBound=" + upperBound + "\n, lowerBound=" + lowerBound + "\n, increment=" + increment
				+ "\n, indexVar=" + indexVar + "\n, LoopSize=" + loopSize
				+ "\n, NumberFC=" + NumberFC + "\n, LevelLoopNest=" + LevelLoopNest + "\n";
	}
    
    
}
