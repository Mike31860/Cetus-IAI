package cetus.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.hamcrest.core.IsInstanceOf;

import cetus.application.AnalysisLoopTarget;
import cetus.codegen.ProfitableOMP;
import cetus.entities.LoopTarget;
import cetus.entities.ReductionDTO;
import cetus.exec.Driver;
import cetus.hir.AccessExpression;
import cetus.hir.AccessSymbol;
import cetus.hir.ArrayAccess;
import cetus.hir.ArraySpecifier;
import cetus.hir.BinaryExpression;
import cetus.hir.BinaryOperator;
import cetus.hir.BreakStatement;
import cetus.hir.CetusAnnotation;
import cetus.hir.DFIterator;
import cetus.hir.DataFlowTools;
import cetus.hir.Declarator;
import cetus.hir.DoLoop;
import cetus.hir.Expression;
import cetus.hir.ForLoop;
import cetus.hir.FunctionCall;
import cetus.hir.GotoStatement;
import cetus.hir.IRTools;
import cetus.hir.IntegerLiteral;
import cetus.hir.Loop;
import cetus.hir.PointerSpecifier;
import cetus.hir.PrintTools;
import cetus.hir.Procedure;
import cetus.hir.Program;
import cetus.hir.RangeExpression;
import cetus.hir.ReturnStatement;
import cetus.hir.StandardLibrary;
import cetus.hir.Statement;
import cetus.hir.StatementExpression;
import cetus.hir.Symbol;
import cetus.hir.SymbolTable;
import cetus.hir.SymbolTools;
import cetus.hir.Symbolic;
import cetus.hir.Tools;
import cetus.hir.TranslationUnit;
import cetus.hir.Traversable;
import cetus.hir.UnaryExpression;
import cetus.hir.UnaryOperator;
import cetus.hir.UserSpecifier;
import cetus.hir.WhileLoop;

public class ProgramFeatures {
	// Miguel Added
	public static final String PATTERN1 = "#,##0.00;(#,##0.00)";

	// Miguel Added

	private FileWriter myWriter;

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public static Set<Symbol> pri_set;

	private ArrayList<AnalysisLoopTarget> loops_Features;

	private Map<Loop, Map<String, Set<Expression>>> reductionLoops;

	private ArrayList<Loop> all_loops = new ArrayList<Loop>();

	// Pass name
	private static final String tag = "[ProgramAnalysis]";

	public ProgramFeatures(Program program, ArrayList<Loop> all_loops, Map<Loop, AnalysisLoopTarget> loopsAnalysis,
			Map<Loop, Map<String, Set<Expression>>> ReductionLoops,
			Map<Loop, List<ReductionDTO>> LoopReductionStatements,
			Map<Expression, Expression> assignmentExpressionsMaps) {

		setLogger();
		reductionLoops = ReductionLoops;
		loops_Features = new ArrayList<AnalysisLoopTarget>();
/* 		try {
			String currentDir = System.getProperty("user.dir");
            String filePath = currentDir + File.separator + "Results.txt";
			File myObj = new File(filePath);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
				myWriter = new FileWriter(filePath);
			} else {
				myWriter = new FileWriter(filePath);
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */

		logger.info(
				"Application Name, DataSize, LoopId,Number of Iterations, total of Iterations, Number of Loads ,Number of Stores,Number of Instructions,Number of Statements, Loopness Level,"
						+
						"No of Bits per iteration,No of Times a Data Type Changed,No of Operation Type (Integer),No of Float Operation Type,No of Double Operation Type,No of Long Double Operation"
						+
						",No of Long Operation,No of Short Operation,Big O Notation,No of *,No of -, No of +,No of Function Calls Side effect Free,Data dependence Free,Ratio of Reduction Statements,Ratio of Flow Dependences Remaining ");
		// debug = PrintTools.getVerbosity();
		// ProgramAnalysis(all_loops, loopsAnalysis, ReductionLoops,
		// LoopReductionStatements, assignmentExpressionsMaps);
		startAnalysis(program, ((TranslationUnit) program.getChildren().get(0)).getOutputFilename());
	/* 	startAnalysis(all_loops);
		ProgramAnalysis(all_loops, loopsAnalysis, ReductionLoops, LoopReductionStatements, assignmentExpressionsMaps); */

	}

	public void startAnalysis(Program program, String fileName) {
		DFIterator<Loop> iter = new DFIterator<Loop>(program, Loop.class);
		iter.pruneOn(Loop.class);
		iter.pruneOn(StatementExpression.class);
		while (iter.hasNext()) {
			Loop neeLoop = iter.next();
			addAllLoopNest(neeLoop);
		}
		for (int i = 0; i < all_loops.size(); i++) {
			gatherFeatures(all_loops.get(i), fileName);
		}
	}

	public void gatherFeatures(Loop loop, String fileName) {
		if (LoopTools.isCanonical(loop)) {
			LoopTarget targetLoop = new LoopTarget();

			// Gather the number of iterations
			Expression upperBoundExpression = LoopTools.getUpperBoundExpression(loop);
			Expression lowerBound = LoopTools.getLowerBoundExpression(loop);
			Expression stride = LoopTools.getIncrementExpression(loop);
			Expression Substraction = Symbolic.subtract(upperBoundExpression, lowerBound);
			Expression numI = Symbolic.add(Substraction, stride);
			Expression NumberIterations = Symbolic.divide(numI, stride);

			// mirar
			targetLoop.setLoopId(LoopTools.getLoopName((Statement) loop));
			targetLoop.setNumberIterations(NumberIterations.toString());
			// Notes
			// Number of loads everything that is read
			// Type of Operations

			targetLoop.setTotalArrayAccess(LoopTools.getArrayAccess(loop).size());
			// Number of reads, number of declared variables + number of arrayAcess

			// Ratio of Reduction Statements
			Map<String, Set<Expression>> reductionStatementsLoop = reductionLoops.get(loop);
			if (reductionStatementsLoop != null) {
				int numberReductionStatements = iterateReductionVariableLoop(reductionStatementsLoop);
				targetLoop.setNumberReductionStatements(numberReductionStatements);

			}

			targetLoop.setNumberStores(LoopTools.getNumberAssigmentExpressions(loop));
			targetLoop.setNumberStatements(LoopTools.calculateNumberStatements(loop));
			if (targetLoop.getNumberReductionStatements() > 0) {
				targetLoop.setRatioRT(targetLoop.getNumberStatements() / targetLoop.getNumberReductionStatements());
			}
			targetLoop.setLoopLevel(LoopTools.calculateLoopNest(loop).size());
			targetLoop.setContainIfStatements(LoopTools.containIfStatements(loop));
			targetLoop.setContainsControlFlowModifierOtherThanBreakStmt(
					LoopTools.containsControlFlowModifierOtherThanBreakStmt(loop));
			targetLoop.setContainsWhileLoop(LoopTools.constainsWhileLoop(loop));

			targetLoop.setScalarDependencies(LoopTools.collectScalarDependences(loop).size());
			targetLoop.setScalarDependeciesArray(LoopTools.collectScalarDependences(loop));
			targetLoop.setNumberIOOperations(LoopTools.numberIOoperations(loop));
			// #Operations
		/* 	HashMap<String, Integer> identifiers = LoopTools.getNumberOperations(loop);
			targetLoop.setNumberFloatOperations(identifiers.get("FLOAT"));
			targetLoop.setNumberShortOperations(identifiers.get("SHORT"));
			targetLoop.setNumberIntegerOperations(identifiers.get("INTEGER")); */
			targetLoop.setNumberFunctionCallSF(LoopTools.getNumberParallelizableCall(loop));
			targetLoop.setNumberFunctionCall(LoopTools.numberFunctionCall(loop));
			targetLoop.setDataDependenceFree(LoopTools.checkDataDependenceEligibility(loop));
			// FlowDependences remaining

			logger.info(fileName + "," + targetLoop.toString());
		}
	}

	// This methos iterates the Reductions variables of a Loop
	public int iterateReductionVariableLoop(Map<String, Set<Expression>> reductionsV) {
		for (Map.Entry<String, Set<Expression>> entry : reductionsV.entrySet()) {
			String key = entry.getKey();
			Set<Expression> value = entry.getValue();
			return value.size();
		}
		return 0;
	}

	public String operandsExpression(Expression expression, String operator) {

		if (expression.getChildren().size() > 0) {
			for (int i = 0; i < expression.getChildren().size(); i++) {
				// Expression expre =(Expression) expression.getChildren().get(i);
				if (expression instanceof BinaryExpression) {
					BinaryExpression b = (BinaryExpression) expression;
					BinaryOperator p = b.getOperator();
					Expression a = (Expression) b.getChildren().get(0);
					Expression c = (Expression) b.getChildren().get(1);
					if (p.toString() == "+") {
						operator += "+,";

					} else if (p.toString() == "-") {
						operator += "-,";

					} else if (p.toString() == "*") {
						operator += "*,";

					} else if (p.toString() == "/") {
						operator += "/,";

					}
					return operandsExpression(a, operator) + operandsExpression(c, operator);
				}
				// operatesExpression( (Expression) expre);

			}

		}
		if (expression.toString().endsWith("]")) {

		}
		return operator;

	}

	private void setLogger() {

		FileHandler handler;
		try {
			handler = new FileHandler("./out/" + this.getClass().getSimpleName() + ".csv", true);

			Formatter formatter = new Formatter() {
				@Override
				public String format(LogRecord record) {
					StringBuilder sb = new StringBuilder();
					sb.append(record.getMessage()).append('\n');
					return sb.toString();
				}
			};
			handler.setFormatter(formatter);
			handler.setEncoding("utf-8");
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	// Add all the outer and inner most loop of a target loop
	public void addAllLoopNest(Loop loop) {
		DFIterator<Loop> iter = new DFIterator<Loop>(loop.getBody(), Loop.class);
		iter.pruneOn(Loop.class);
		iter.pruneOn(StatementExpression.class);
		while (iter.hasNext()) {
			Loop neeLoop = iter.next();
			addAllLoopNest(neeLoop);
		}
		all_loops.add(loop);
		// System.out.println("-----------\n" + loop.toString() + "-------------\n");

	}

	public void ProgramAnalysis(ArrayList<Loop> all_loops, Map<Loop, AnalysisLoopTarget> loopsAnalysis,
			Map<Loop, Map<String, Set<Expression>>> ReductionLoops,
			Map<Loop, List<ReductionDTO>> LoopReductionStatements,
			Map<Expression, Expression> assignmentExpressionsMaps) {
		// all_loops.size()
		for (int i = 0; i < all_loops.size(); i++) {
			System.out.println("----------------------------------------");
			AnalysisLoopTarget loopTarget = loopsAnalysis.get(all_loops.get(i));
			if (loopTarget != null && LoopTools.isCanonical(loopTarget.getLoop())) {
				Expression upperBoundExpression = LoopTools.getUpperBoundExpression(all_loops.get(i));
				// numberIterations = LoopTools.getUpperExpression.toString();
				Expression lowerBound = LoopTools.getLowerBoundExpression(all_loops.get(i));
				Expression stride = LoopTools.getIncrementExpression(loopTarget.getLoop());
				Expression Substraction = Symbolic.subtract(upperBoundExpression, lowerBound);
				Expression numI = Symbolic.add(Substraction, stride);
				Expression numI2 = Symbolic.divide(numI, stride);

				LoopTools.getnumberOperationsLoop(loopTarget.getLoop());
				ForLoop loo = (ForLoop) loopTarget.getLoop();
				loopTarget.setOperations(loo.getOperations());

				if (!numI2.toString().endsWith("]")) {
					Expression inte = inorder(numI2, assignmentExpressionsMaps);
					if (!inte.toString().contains("[") || !inte.toString().contains("]")) {
						if (inte.getChildren().size() > 0) {
							boolean condition = false;
							if (inte.getChildren().size() > 1) {
								while (!condition && !inte.toString().endsWith("]")) {
									try {
										loopTarget.setNumberIterations(inte.toString());
										int interations = operatesExpression(inte);
										loopTarget.setTotalIterations(interations);
										loopTarget.setInstructionCount(interations * loopTarget.getNumberStatements());
										condition = true;

									} catch (Exception a) {
										inte = inorder(inte, assignmentExpressionsMaps);
										condition = false;
									}
								}
							} else {
								inte = (Expression) inte.getChildren().get(0);
								while (!condition && !inte.toString().endsWith("]")) {
									try {
										loopTarget.setNumberIterations(inte.toString());
										int interations = operatesExpression(inte);
										loopTarget.setTotalIterations(interations);
										loopTarget.setInstructionCount(interations * loopTarget.getNumberStatements());
										condition = true;

									} catch (Exception a) {
										inte = inorder(inte, assignmentExpressionsMaps);
										condition = false;
									}
								}
							}
						} else {
							try {
								int number = Integer.parseInt(inte.toString());
								loopTarget.setTotalIterations(number);
								loopTarget.setInstructionCount(number * loopTarget.getNumberStatements());
							} catch (Exception e) {
								loopTarget.setTotalIterations(0);
								loopTarget.setInstructionCount(0 * loopTarget.getNumberStatements());
							}
						}
					} else {
						loopTarget.setNumberIterations(inte.toString());
						loopTarget.setTotalIterations(0);
						loopTarget.setInstructionCount(0);
					}
				}
				// numberIterations = "";
				Map<String, Set<Expression>> reductionMaps = ReductionLoops.get(loopTarget.getLoop());
				List<ReductionDTO> listReductions = LoopReductionStatements.get(all_loops.get(i));
				String LoopId = LoopTools.getLoopName((Statement) all_loops.get(i));
				loopTarget.setLoopId(LoopId);
				ArrayList<ReductionDTO> reductionStatements = new ArrayList<ReductionDTO>();
				if (reductionMaps != null) {
					for (String key : reductionMaps.keySet()) {
						int iterator = 0;
						for (Iterator<Expression> it = reductionMaps.get(key).iterator(); it.hasNext();) {
							Expression f = it.next();
							iterator++;
							loopTarget.setNumberRV(iterator);
							for (int index = 0; index < listReductions.size(); index++) {
								if (listReductions.get(index).getLHS().toString().equals(f.toString())) {
									ReductionDTO reductionEntity = listReductions.get(index);
									for (int j = 0; j < reductionEntity.getRHS().toString().length(); j++) {
										char letter = reductionEntity.getRHS().toString().charAt(j);
										if (letter == '*' || letter == '+' || letter == '-') {
											String operations = reductionEntity.getOperationTypes();
											operations += letter + ",";
											reductionEntity.setOperationTypes(operations);
										}
									}
									reductionStatements.add(reductionEntity);
								}
							}

						}
					}
					loopTarget.setReductionStatements(reductionStatements);
				} else {
					loopTarget.setNumberRV(0);
				}

			}

			if (loopTarget != null && LoopTools.isCanonical(loopTarget.getLoop())) {

				for (Symbol a : loopTarget.getVariables()) {
					// System.out.println(a.toString());
					char finalLetter = a.toString().charAt(a.toString().length() - 1);
					if (finalLetter == ']') {
						loopTarget.getArrayVariables().add(a);
					}

				}
				loopTarget.setNumberArrayVariables(loopTarget.getArrayVariables().size());

				System.out.println(loopTarget.toString());
				loops_Features.add(loopTarget);
			}

			System.out.println("----------------------------------------/n/n/n");

			loopsAnalysis.put(all_loops.get(i), loopTarget);

		}
		getLoopAnalysis(loops_Features);
	}

	// Miguel
	public void getLoopAnalysis(ArrayList<AnalysisLoopTarget> loops_Features) {
		// int i ;
		Map<String, String> loopAnalysis = new HashMap<String, String>();
		for (int i = 0; i < loops_Features.size(); i++) {
			AnalysisLoopTarget loop = loops_Features.get(i);
			String[] nameId = loop.getLoopId().split("#");
			String analysis = "";
			String nameIDFirs = nameId[0] + "#" + nameId[1].charAt(0);
			if (loopAnalysis.get(nameId[0]) == null) {
				int j = i + 1;
				analysis = loop.toString() + "\n\n";
				if (j < loops_Features.size()) {
					String nameIdLoopTwo = loops_Features.get(j).getLoopId();
					if (nameIdLoopTwo != null && nameIdLoopTwo.startsWith(nameIDFirs)) {
						while (nameIdLoopTwo != null && nameIdLoopTwo.startsWith(nameIDFirs)) {
							analysis += loops_Features.get(j).toString() + "\n\n";
							j = j + 1;
							nameIdLoopTwo = loops_Features.get(j).getLoopId();
						}
						AnalysisLoopTarget looParent = getLoop(loops_Features, nameIDFirs);
						if (looParent != null) {
							analysis += "\n\n" + looParent.getLoop().toString() + "\n\n";
							loopAnalysis.put(looParent.getLoopId(), analysis);
						}
						i = j - 1;
					} else {
						analysis += loop.getLoop().toString() + "\n\n";
						loopAnalysis.put(nameIDFirs, analysis);
					}
				} else {
					analysis += loop.getLoop().toString() + "\n\n";
					loopAnalysis.put(nameIDFirs, analysis);
				}
			}

		}
		exportInfor(loopAnalysis);
	}

	// Miguel Added
	public void exportInfor(Map<String, String> loopAnalysis) {
		for (Map.Entry<String, String> entry : loopAnalysis.entrySet()) {
			try {
				myWriter.write(entry.getKey() + "\n\n" + entry.getValue());
				myWriter.write("------------------------------------");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Miguel
	public AnalysisLoopTarget getLoop(ArrayList<AnalysisLoopTarget> loops_Features, String nameId) {
		int i = 0;
		AnalysisLoopTarget loop = loops_Features.get(i);
		while (!loop.getLoopId().equals(nameId)) {
			i++;
			loop = loops_Features.get(i);
		}
		return loop;
	}

	public Expression findNumberIterations(Expression upperBoundExpression,
			Map<Expression, Expression> assignmentExpressionsMaps) {

		for (int i = 0; i < upperBoundExpression.getChildren().size(); i++) {
			Traversable expre = upperBoundExpression.getChildren().get(i);
			Expression expreSingle = findNumberIterations((Expression) expre,
					assignmentExpressionsMaps);
			upperBoundExpression.getChildren().set(i, expreSingle);

		}
		Expression result = assignmentExpressionsMaps.get(upperBoundExpression);
		if (result != null) {
			upperBoundExpression = result;
			// numberIterations += upperBoundExpression.toString();
			// findNumberIterations(upperBoundExpression, assignmentExpressionsMaps);

		}
		return upperBoundExpression;

	}

	public Expression inorder(Expression upperBoundExpression, Map<Expression, Expression> assignmentExpressionsMaps) {
		if (upperBoundExpression == null)
			return null;

		Stack<Expression> s = new Stack<Expression>();
		Expression curr = upperBoundExpression;

		// traverse the tree
		while (curr != null || s.size() > 0) {

			/*
			 * Reach the left most Node of the
			 * curr Node
			 */
			while (curr != null) {
				/*
				 * place pointer to a tree node on
				 * the stack before traversing
				 * the node's left subtree
				 */
				s.push(curr);
				if (curr.getChildren().size() > 0) {
					curr = (Expression) curr.getChildren().get(0);
				} else {
					curr = null;
				}
			}

			/* Current must be NULL at this point */
			curr = s.pop();

			System.out.print(curr.toString() + "\n");
			/*
			 * we have visited the node and its
			 * left subtree. Now, it's right
			 * subtree's turn
			 */
			if (curr.getChildren().size() > 1 && !curr.toString().endsWith("]")) {

				Traversable left = assignmentExpressionsMaps.get(curr.getChildren().get(0));

				Traversable right = assignmentExpressionsMaps.get(curr.getChildren().get(1));

				Expression newu = curr.clone();
				if (left != null) {
					left.setParent(null);
					curr.setChild(0, left);

				}
				if (right != null) {
					right.setParent(null);
					curr.setChild(1, right);

				}
				curr = (Expression) curr.getChildren().get(1);

			} else {
				curr = null;

			}

		}
		if (upperBoundExpression.getChildren().size() == 0) {
			Expression value = assignmentExpressionsMaps.get(upperBoundExpression);
			if (value != null) {
				return value;
			}
			return upperBoundExpression;
		} else {
			return upperBoundExpression;
		}
	}

	public int operatesExpression(Expression expression) {

		if (expression.getChildren().size() > 0) {
			for (int i = 0; i < expression.getChildren().size(); i++) {
				// Expression expre =(Expression) expression.getChildren().get(i);
				if (expression instanceof BinaryExpression) {
					BinaryExpression b = (BinaryExpression) expression;
					BinaryOperator p = b.getOperator();
					if (p.toString() == "+") {
						Expression a = (Expression) b.getChildren().get(0);
						Expression c = (Expression) b.getChildren().get(1);
						return operatesExpression(a) + operatesExpression(c);
					} else if (p.toString() == "-") {
						Expression a = (Expression) b.getChildren().get(0);
						Expression c = (Expression) b.getChildren().get(1);
						return operatesExpression(a) - operatesExpression(c);
					} else if (p.toString() == "*") {
						Expression a = (Expression) b.getChildren().get(0);
						Expression c = (Expression) b.getChildren().get(1);
						return operatesExpression(a) * operatesExpression(c);
					}
				}
				// operatesExpression( (Expression) expre);

			}

		}
		if (expression.toString().endsWith("]")) {

		}
		return Integer.parseInt(expression.toString());

	}

}
