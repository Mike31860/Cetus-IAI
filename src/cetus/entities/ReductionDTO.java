package cetus.entities;

import java.util.ArrayList;

import cetus.hir.Expression;

public class ReductionDTO {
	
	private Expression LHS;
	private Expression RHS;
    private String ReductionOperator;
    private String operationTypes;
    
    
	public Expression getLHS() {
		return LHS;
	}
	public void setLHS(Expression lHS) {
		LHS = lHS;
	}
	public Expression getRHS() {
		return RHS;
	}
	public void setRHS(Expression rHS) {
		RHS = rHS;
	}
	public String getReductionOperator() {
		return ReductionOperator;
	}
	public void setReductionOperator(String reductionOperator) {
		ReductionOperator = reductionOperator;
	}
	
	
	public String getOperationTypes() {
		return operationTypes;
	}
	public void setOperationTypes(String operationTypes) {
		this.operationTypes = operationTypes;
	}
	
	
	
	@Override
	public String toString() {
		return "ReductionDTO [LHS=" + LHS + ", RHS=" + RHS + ", ReductionOperator=" + ReductionOperator + "]";
	}
 

}
