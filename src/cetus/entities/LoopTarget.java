package cetus.entities;

import java.util.ArrayList;
import java.util.Set;

import antlr.collections.List;
import cetus.hir.*;

public class LoopTarget {

    private String loopId;
    private String numberIterations;
    private int totalIterations;
    private int totalArrayAccess;
    private boolean containIfStatements;
    private boolean containsControlFlowModifierOtherThanBreakStmt;
    private boolean containsWhileLoop;

    private int scalarDependencies;
    private int numberIOOperations;

    private Set<Expression> scalarDependeciesArray;

    private int numberLoads;
    private int numberStores;
    private int numberInstructions;
    private int numberStatements;
    private int loopLevel;
    private int NumberBitsIteration;
    private int NumberChangedDataType;
    private int NumberFloatOperations;
    private int NumberDoubleOperations;
    private int NumberShortOperations;
    private int NumberIntegerOperations;
    private String bigONotation;
    private int numberMultiplicationOperant;
    private int numberAdditionOperations;
    private int numberSubstractionOperations;
    private int numberFunctionCallSF;
    private int numberFunctionCall;
    private boolean dataDependenceFree;
    private int numberReductionStatements;

    private double ratioRT;
    private int numberFDRemaining;

    /**
     * 
     */
    public LoopTarget() {

    }

    public int getNumberReductionStatements() {
        return numberReductionStatements;
    }

    public void setNumberReductionStatements(int numberReductionStatements) {
        this.numberReductionStatements = numberReductionStatements;
    }

    public boolean ContainsWhileLoop() {
        return containsWhileLoop;
    }

    public void setContainsWhileLoop(boolean containsWhileLoop) {
        this.containsWhileLoop = containsWhileLoop;
    }

    public int getNumberIOOperations() {
        return numberIOOperations;
    }

    public void setNumberIOOperations(int numberIOOperations) {
        this.numberIOOperations = numberIOOperations;
    }

    public int getScalarDependencies() {
        return scalarDependencies;
    }

    public void setScalarDependencies(int scalarDependencies) {
        this.scalarDependencies = scalarDependencies;
    }

    public Set<Expression> getScalarDependeciesArray() {
        return scalarDependeciesArray;
    }

    public void setScalarDependeciesArray(Set<Expression> scalarDependeciesArray) {
        this.scalarDependeciesArray = scalarDependeciesArray;
    }

    public void setLoopId(String loopId) {
        this.loopId = loopId;
    }

    public int getTotalArrayAccess() {
        return totalArrayAccess;
    }

    public boolean ContainsControlFlowModifierOtherThanBreakStmt() {
        return containsControlFlowModifierOtherThanBreakStmt;
    }

    public void setContainsControlFlowModifierOtherThanBreakStmt(
            boolean containsControlFlowModifierOtherThanBreakStmt) {
        this.containsControlFlowModifierOtherThanBreakStmt = containsControlFlowModifierOtherThanBreakStmt;
    }

    public boolean ContainIfStatements() {
        return containIfStatements;
    }

    public void setContainIfStatements(boolean containIfStatements) {
        this.containIfStatements = containIfStatements;
    }

    public void setTotalArrayAccess(int totalArrayAccess) {
        this.totalArrayAccess = totalArrayAccess;
    }

    public void setNumberIterations(String numberIterations) {
        this.numberIterations = numberIterations;
    }

    public void setTotalIterations(int totalIterations) {
        this.totalIterations = totalIterations;
    }

    public void setNumberLoads(int numberLoads) {
        this.numberLoads = numberLoads;
    }

    public void setNumberStores(int numberStores) {
        this.numberStores = numberStores;
    }

    public void setNumberInstructions(int numberInstructions) {
        this.numberInstructions = numberInstructions;
    }

    public void setNumberStatements(int numberStatements) {
        this.numberStatements = numberStatements;
    }

    public void setLoopLevel(int loopLevel) {
        this.loopLevel = loopLevel;
    }

    public void setNumberBitsIteration(int numberBitsIteration) {
        NumberBitsIteration = numberBitsIteration;
    }

    public void setNumberChangedDataType(int numberChangedDataType) {
        NumberChangedDataType = numberChangedDataType;
    }

    public void setNumberFloatOperations(int numberFloatOperations) {
        NumberFloatOperations = numberFloatOperations;
    }

    public int getNumberDoubleOperations() {
        return NumberDoubleOperations;
    }

    public void setNumberDoubleOperations(int numberDoubleOperations) {
        NumberDoubleOperations = numberDoubleOperations;
    }

    public int getNumberShortOperations() {
        return NumberShortOperations;
    }

    public void setNumberShortOperations(int numberShortOperations) {
        NumberShortOperations = numberShortOperations;
    }

    public int getNumberIntegerOperations() {
        return NumberIntegerOperations;
    }

    public void setNumberIntegerOperations(int numberIntegerOperations) {
        NumberIntegerOperations = numberIntegerOperations;
    }

    public void setBigONotation(String bigONotation) {
        this.bigONotation = bigONotation;
    }

    public String getLoopId() {
        return loopId;
    }

    public String getNumberIterations() {
        return numberIterations;
    }

    public int getTotalIterations() {
        return totalIterations;
    }

    public int getNumberLoads() {
        return numberLoads;
    }

    public int getNumberStores() {
        return numberStores;
    }

    public int getNumberInstructions() {
        return numberInstructions;
    }

    public int getNumberStatements() {
        return numberStatements;
    }

    public int getLoopLevel() {
        return loopLevel;
    }

    public int getNumberBitsIteration() {
        return NumberBitsIteration;
    }

    public int getNumberChangedDataType() {
        return NumberChangedDataType;
    }

    public int getNumberFloatOperations() {
        return NumberFloatOperations;
    }

    public String getBigONotation() {
        return bigONotation;
    }

    public int getNumberFunctionCall() {
        return numberFunctionCall;
    }

    public void setNumberFunctionCall(int numberFunctionCall) {
        this.numberFunctionCall = numberFunctionCall;
    }

    public int getNumberMultiplicationOperant() {
        return numberMultiplicationOperant;
    }

    public void setNumberMultiplicationOperant(int numberMultiplicationOperant) {
        this.numberMultiplicationOperant = numberMultiplicationOperant;
    }

    public int getNumberAdditionOperations() {
        return numberAdditionOperations;
    }

    public void setNumberAdditionOperations(int numberAdditionOperations) {
        this.numberAdditionOperations = numberAdditionOperations;
    }

    public int getNumberSubstractionOperations() {
        return numberSubstractionOperations;
    }

    public void setNumberSubstractionOperations(int numberSubstractionOperations) {
        this.numberSubstractionOperations = numberSubstractionOperations;
    }

    public int getNumberFunctionCallSF() {
        return numberFunctionCallSF;
    }

    public void setNumberFunctionCallSF(int numberFunctionCallSF) {
        this.numberFunctionCallSF = numberFunctionCallSF;
    }

    public boolean isDataDependenceFree() {
        return dataDependenceFree;
    }

    public void setDataDependenceFree(boolean dataDependenceFree) {
        this.dataDependenceFree = dataDependenceFree;
    }

    public double getRatioRT() {
        return ratioRT;
    }

    public void setRatioRT(double ratioRT) {
        this.ratioRT = ratioRT;
    }

    public int getNumberFDRemaining() {
        return numberFDRemaining;
    }

    public void setNumberFDRemaining(int numberFDRemaining) {
        this.numberFDRemaining = numberFDRemaining;
    }

    @Override
    public String toString() {
        return loopId + "," + numberIterations + "," + totalIterations + "," + numberLoads + "," + numberStores
                + "," + numberInstructions + "," + numberStatements
                + "," + loopLevel + "," + NumberBitsIteration
                + "," + NumberChangedDataType
                + "," + NumberFloatOperations + "," + NumberDoubleOperations
                + "," + NumberShortOperations + "," + bigONotation
                + "," + numberMultiplicationOperant + "," + numberAdditionOperations
                + "," + numberSubstractionOperations
                + "," + numberFunctionCallSF + "," + numberFunctionCall
                + "," + dataDependenceFree + "," + ratioRT + "," + numberFDRemaining;
    }

}
