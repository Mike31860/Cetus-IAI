package cetus.analysis;

import cetus.hir.Program;
import cetus.transforms.IVSubstitution;

public class ProgramAnalysis extends AnalysisPass {
	
	 // Pass name
    private static final String tag = "[ProgramAnalysis]";

	private ArrayPrivatization arrayPrivatization;
	private IVSubstitution iVSubstitution;
	
	
	
	protected ProgramAnalysis(Program program) {
		super(program);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getPassName() {
		// TODO Auto-generated method stub
		return tag;
	}
	
 
}
