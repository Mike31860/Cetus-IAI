package cetus.entities;

public class LoopTarjet {

    private String loopId;
	private String numberIterations;
	private int totalIterations;
    private int numberLoads;
    private int numberStores;
    private int numberInstructions;
    private int numberStatements;
    private int loopLevel;
    private int NumberBitsIteration;
    private int NumberChangedDataType;
    private int NumberFloatOperations;
    private int NumberDoubleOperation;
    private int NumberShortOperation;
    private String bigONotation;
    private int numberMultiplicationOperant;
    private int numberAdditionOperations;
    private int numberSubstractionOperations;
    private int numberFunctionCallSF;
    private boolean dataDependenceFree;
    private int ratioRT;
    private int numberFDRemaining;


    public LoopTarjet() {
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
    public int getNumberDoubleOperation() {
        return NumberDoubleOperation;
    }
    public int getNumberShortOperation() {
        return NumberShortOperation;
    }
    public String getBigONotation() {
        return bigONotation;
    }
    


    
}
