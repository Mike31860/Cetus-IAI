package cetus.transforms;

import java.util.LinkedList;


import java.util.List;
import cetus.analysis.LoopTools;
import cetus.hir.Program;
import cetus.hir.*;

public class LoopTimerProfiler extends TransformPass {

    public static final int OUTER = 1;
    public static final int INNER = 2;
    public static final String PASS_NAME = "[LoopProfilerTimer]";

    /** Profiling option */
    private int strategy;

    public LoopTimerProfiler(Program program) {
        super(program);
        strategy = Integer.valueOf(
                cetus.exec.Driver.getOptionValue("profileLoop-timer")).intValue();
    }

    @Override
    public String getPassName() {
        return PASS_NAME;
    }

    public void start() {

        if (strategy == 1) {
            int event_num = 0;
            LoopTools.addLoopName(program);
            System.out.println(program);
            List<Statement> stmts = new LinkedList<Statement>();
            collectCandidates(program, stmts);
            System.out.println(stmts.toString());
            for (Statement stmt : stmts) {
                String loop_name = LoopTools.getLoopName(stmt);
                if (loop_name == null) { // stmt is not a loop.
                    loop_name = "event#" + (event_num++);
                }
               
                CompoundStatement parent =
                        IRTools.getAncestorOfType(stmt,CompoundStatement.class);
                Statement start = new AnnotationStatement();
                start.annotate(new PragmaAnnotation.Event(loop_name, "start"));
                Statement stop = new AnnotationStatement();
                stop.annotate(new PragmaAnnotation.Event(loop_name, "stop"));
                parent.addStatementBefore(stmt, start);
                parent.addStatementAfter(stmt, stop);
            }
        }
        System.err.println(program);
        TransformPass.run(new TimerLoop(program));
        System.out.println(program);

    }

    /** Collect candidate statements following the specified option. */
    private void collectCandidates(Traversable t, List<Statement> stmts) {
        List<Traversable> children = t.getChildren();
        if (children == null || children.isEmpty()) {
            return;
        }
        int children_size = children.size();
        for (int i = 0; i < children_size; i++) {
            Traversable child = children.get(i);
            if (child instanceof Statement) {
                Statement stmt = (Statement) child;
            /*     boolean contains_omp_par = stmt.containsAnnotation(OmpAnnotation.class, "parallel");
                boolean contains_omp_for = stmt.containsAnnotation(OmpAnnotation.class, "for"); */
                boolean contains_jump = IRTools.containsClass(stmt, ReturnStatement.class)
                        || IRTools.containsClass(stmt, GotoStatement.class);
                boolean was_profiled = false;
                if (contains_jump) {
                    if (t instanceof Loop) {
                        PrintTools.printlnStatus(0,
                                "[WARNING] Skipping profiling",
                                "of the loop with \"return or goto\"");
                    }
                } else {
                    switch (strategy) {
                    /*     case EVERY: */
                        case OUTER:
                            if (stmt instanceof ForLoop) {
                                stmts.add(stmt);
                                was_profiled = true;
                            }
                            break;
                       /*  case EVERY_OMP_PAR:
                        case OUTER_OMP_PAR:
                            if (contains_omp_par) {
                                stmts.add(stmt);
                                was_profiled = true;
                            }
                            break;
                        case EVERY_OMP_FOR:
                        case OUTER_OMP_FOR:
                            if (contains_omp_for) {
                                stmts.add(stmt);
                                was_profiled = true;
                            }
                            break; */
                        default:
                    }
                }
                if (!was_profiled ||
                        (strategy != OUTER)) {
                    collectCandidates(child, stmts);
                }
            } else if (!(child instanceof VariableDeclaration
                    || child instanceof ExpressionStatement)) {
                collectCandidates(child, stmts);
            }
        }

    }

}
