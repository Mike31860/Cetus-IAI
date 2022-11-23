package cetus.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cetus.entities.ReductionDTO;
import cetus.hir.Expression;
import cetus.hir.Loop;
import cetus.hir.Symbol;

public class AnalysisLoopTarget implements Cloneable{

	private Loop loop;
	private String loopId;
	private String numberIterations;
	private int totalIterations;
	private int numberIOoperations;
	private int funcitonCalls;
	private int numberStatements;
	private int instructionCount;
	private int LoopNestLevel;
	private int numberIV;
	private int numberPV;
	private int numberRV;
	private int numberVariants;
	private int numberArrayVariables;
	private Set<Symbol> arrayVariables;
	private Set<Symbol> variables;
	public Set<Symbol> pri_set;
	private ArrayList<ReductionDTO> reductionStatements;
	private double RatioRV;
	private double RatioPV;
	private Map<Character, Integer> operations;
	private int numberOperations;
	
	public AnalysisLoopTarget() {
		super();
		operations = new HashMap<Character, Integer>();
		arrayVariables = new HashSet<Symbol>();
		reductionStatements = new ArrayList<ReductionDTO> ();
	}


	public Loop getLoop() {
		return loop;
	}

	
	public int getNumberArrayVariables() {
		return numberArrayVariables;
	}

	public void setNumberArrayVariables(int numberArrayVariables) {
		this.numberArrayVariables = numberArrayVariables;
	}

	public void setLoop(Loop loop) {
		this.loop = loop;
	}

	public int getFuncitonCalls() {
		return funcitonCalls;
	}

	public void setFuncitonCalls(int funcitonCalls) {
		this.funcitonCalls = funcitonCalls;
	}



	public int getLoopNestLevel() {
		return LoopNestLevel;
	}

	public void setLoopNestLevel(int loopNestLevel) {
		LoopNestLevel = loopNestLevel;
	}

	public int getNumberIV() {
		return numberIV;
	}

	public void setNumberIV(int numberIV) {
		this.numberIV = numberIV;
	}

	public int getNumberPV() {
		return numberPV;
	}

	public void setNumberPV(int numberPV) {
		this.numberPV = numberPV;
	}

	public int getNumberRV() {
		return numberRV;
	}

	public void setNumberRV(int numberRV) {
		this.numberRV = numberRV;
	}

	public int getNumberVariants() {
		return numberVariants;
	}

	public void setNumberVariants(int numberVariants) {
		this.numberVariants = numberVariants;
	}

	public Set<Symbol> getPri_set() {
		return pri_set;
	}

	public void setPri_set(Set<Symbol> pri_set) {
		this.pri_set = pri_set;
	}
			

	public Set<Symbol> getVariables() {
		return variables;
	}

	public void setVariables(Set<Symbol> variables) {
		this.variables = variables;
	}

	public String getNumberIterations() {
		return numberIterations;
	}

	public void setNumberIterations(String numberIterations) {
		this.numberIterations = numberIterations;
	}
	

	public String getLoopId() {
		return loopId;
	}

	public void setLoopId(String loopId) {
		this.loopId = loopId;
	}

	
	public ArrayList<ReductionDTO> getReductionStatements() {
		return reductionStatements;
	}

	public void setReductionStatements(ArrayList<ReductionDTO> reductionStatements) {
		this.reductionStatements = reductionStatements;
	}
	
	
	public int getNumberIOoperations() {
		return numberIOoperations;
	}

	public void setNumberIOoperations(int numberIOoperations) {
		this.numberIOoperations = numberIOoperations;
	}

	
	public Set<Symbol> getArrayVariables() {
		return arrayVariables;
	}

	public void setArrayVariables(Set<Symbol> arrayVariables) {
		this.arrayVariables = arrayVariables;
	}
 
	
    /**
     * @return int return the instructionCalls
     */
    public int getInstructionCount() {
        return instructionCount;
    }

    /**
     * @param instructionCalls the instructionCalls to set
     */
    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }


    /**
     * @return int return the totalIterations
     */
    public int getTotalIterations() {
        return totalIterations;
    }

    /**
     * @param totalIterations the totalIterations to set
     */
    public void setTotalIterations(int totalIterations) {
        this.totalIterations = totalIterations;
    }


    /**
     * @return int return the numberStatements
     */
    public int getNumberStatements() {
        return numberStatements;
    }

    /**
     * @param numberStatements the numberStatements to set
     */
    public void setNumberStatements(int numberStatements) {
        this.numberStatements = numberStatements;
    }


    /**
     * @return double return the RatioRV
     */
    public double getRatioRV() {
        return RatioRV;
    }

    /**
     * @param RatioRV the RatioRV to set
     */
    public void setRatioRV(double RatioRV) {
        this.RatioRV = RatioRV;
    }


    /**
     * @return double return the RatioPV
     */
    public double getRatioPV() {
        return RatioPV;
    }

    /**
     * @param RatioPV the RatioPV to set
     */
    public void setRatioPV(double RatioPV) {
        this.RatioPV = RatioPV;
    }

    /**
     * @return Map<Character, Integer> return the operations
     */
    public Map<Character, Integer> getOperations() {
        return operations;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(Map<Character, Integer> operations) {
        this.operations = operations;
    }

    /**
     * @return int return the numberOperations
     */
    public int getNumberOperations() {
        return numberOperations;
    }

    /**
     * @param numberOperations the numberOperations to set
     */
    public void setNumberOperations(int numberOperations) {
        this.numberOperations = numberOperations;
    }

	
	@Override
	public String toString() {

		RatioPV =  (pri_set.size()/((double)(variables.size())));
		String result = "AnalysisLoopTarget [/* \n\n\nProgram Analysis \n\n "+"Name of the loop: "+loopId+"\nNumberIterations="+numberIterations+ "\nIterations="+totalIterations+"\nfuncitonCalls=" + funcitonCalls +  "\n, LoopNestLevel=" + LoopNestLevel + "\n InstructionCount="+instructionCount+"\n NumberStatements="+numberStatements+"\n, numberIV=" + numberIV + "\n, numberPV=" + numberPV
				+   "\n, RatioPV="+RatioPV+"\n PV=[";

		for (Symbol a : pri_set) {
			result += "" + a.getSymbolName() + ",";
		}
		result+="],\n variablesNumber="+variables.size() +"\nvariables=[";
		
		for (Symbol a : variables) {
			result += "" + a.getSymbolName() + ",";
		}

		result+="],\n arrayVariables=[";
		for (Symbol a : arrayVariables) {
			result += "" + a.getSymbolName() + ",";
		}
		result+="]\n numberArrayVariable="+arrayVariables.size();
		

		result += "\n numberVariants=" + numberVariants +",\n numberRV=" + numberRV +"\n RV=[";

		for (int i = 0; i < reductionStatements.size(); i++) {
			
			result+="LHS="+reductionStatements.get(i).getLHS()+", RHS="+reductionStatements.get(i).getRHS()+" Operations=["+reductionStatements.get(i).getOperationTypes()+"]\n";
		}
		RatioRV =reductionStatements.size()/((double)(numberStatements));
		result+="] \n RatioRV="+RatioRV+"\n Operations [ Operator '+' ="+operations.get('+')+", Operator '-' ="+operations.get('-')+", Operator '*' ="+operations.get('*')+", Operator '/' ="+operations.get('/')+"]\n";

		int count = Thread.activeCount();
		result+="currently active threads = " + count;


		return result;

	}


}
