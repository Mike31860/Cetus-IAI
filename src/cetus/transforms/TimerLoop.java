package cetus.transforms;

import java.util.*;
import cetus.hir.*;

/**
 * This pass instruments the program that contains
 * {@link cetus.hir.PragmaAnnotation.Event} annotations. The annotations may be
 * inserted either manually or automatically.
 */
public class TimerLoop extends TransformPass {

    /** Line separator */
    private static final String NEWLINE = System.getProperty("line.separator");

    /** The variable name used for global profiling */
    private static final String prof_name = "cetus_prof";

    /** Pass name */
    private static final String pass_name = "[EventTimer]";

    /** Heading string for result printing */
    private static final String header = "CETUS_TIMING";

    /**
     * The contents of code to be prepended to each translation unit that does
     * not contain the program entry (main function). This code contains the
     * declaration of the library calls.
     */
    private static final String[] headercode = {
        "#ifdef " + header,
        "typedef struct cetusprofile cetusprofile;",
        "extern cetusprofile " + prof_name + ";",
        "void cetus_tic(cetusprofile *, int);",
        "void cetus_toc(cetusprofile *, int);",
        "#endif /* " + header + " */",
        ""
    };

    /**
     * The contents of code to be prepended to the translation unit that
     * contains the program entry. This code contains the definition of the
     * library calls.
     */
   

    /** The translation unit to be detected which contains the program entry */
    private TranslationUnit main_tunit;

    /** The main function to be detected */
    private Procedure main_proc;

    /** Number of events to be profiled */
    private int num_events;

    /** The list of event names to be collected */
    private List<String> event_names;

    /** The list of forced program exit points to be collected */
    private List<Statement> exit_stmts;

    /**
     * Constructs an event-timing instrumenter with the specified program.
     * @param prog the program to be instrumented.
     */
    public TimerLoop(Program prog) {
        super(prog);
        main_tunit = null;
        main_proc = null;
        num_events = 0;
        event_names = new LinkedList<String>();
        exit_stmts = new LinkedList<Statement>();
        //disable_protection = true;
    }

    /** Returns the name of this pass */
    public String getPassName() {
        return pass_name;
    }

    /**
     * Performs instrumentation after analyzing the program to collect
     * information required to generate the profiling instrumentation.
     */
    public void start() {
        for (Traversable t : program.getChildren()) {
            transformTUnitTimmer((TranslationUnit)t);
            System.out.println(program);
        }
 
    }

  
    /**
     * Inserts timing library calls at the location specified by event-type
     * annotation.
     * @param tunit the translation unit to be transformed.
     */
    public void transformTUnitTimmer(TranslationUnit tunit) {
        DFIterator<Traversable> iter = new DFIterator<Traversable>(tunit);
        iter.pruneOn(VariableDeclaration.class);
        int idLoop = 0;
        while (iter.hasNext()) {
            Traversable t = iter.next();
            if (t instanceof Procedure
                    && ((Procedure)t).getSymbolName().equals("main")) {
                main_proc = (Procedure) t;
                main_tunit = tunit;
            } else if (t instanceof Statement) {
                Statement stmt = (Statement)t;
                PragmaAnnotation.Event event =
                        stmt.getAnnotation(PragmaAnnotation.Event.class,"name");
                if (event != null) {
                    String fcall = "";
                    String name = event.getName(), command = event.getCommand();
                    name=name.replace("#", "_");
                    if (command.equals("start")) {
                        fcall = "struct timeval start_"+(name)+", end_"+(name)+";\n"
                        + "gettimeofday(&start_"+(name)+", NULL);";
                        event_names.add(name);
                    } else if (command.equals("stop")) {
                        int event_num = event_names.indexOf(name);
                        fcall = "gettimeofday(&end_" + (name)  +", NULL);\n"
                        +"printf(\"Time " + name +" seconds %0.8f \\n\", time_diff(&start_"+(name)+", &end_"+(name)+"));";
                    } else {
                        throw new InternalError(pass_name +
                                " Unknown event pragma");
                    }
                    stmt.annotate(new CodeAnnotation(
                           NEWLINE
                            + fcall + NEWLINE
                            ));
                }
            } else if (t instanceof FunctionCall
                    && ((FunctionCall)t).getName().toString().equals("exit")) {
                exit_stmts.add(((Expression)t).getStatement());
            }
            idLoop++;
        }
        if (tunit != main_tunit) {
            tunit.addDeclarationFirst(new AnnotationDeclaration(
                    new CodeAnnotation(genCode(headercode))));
        }
    }

    /** Returns the first statement after declaration part */
    private static Statement
            getFirstStatementAfterDeclaration(CompoundStatement cstmt) {
        Statement ret = null;
        List<Traversable> children = cstmt.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            Statement stmt = (Statement)children.get(i);
            if (stmt instanceof DeclarationStatement) {
                break;
            }
            ret = stmt;
        }
        return ret;
    }

    /** Returns the procedure that lexically appears first in the user code */
    private Declaration getFirstProcedure(TranslationUnit tu) {
        Declaration ret = null;
        List<Traversable> children = tu.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            Declaration decl = (Declaration)children.get(i);
            if (decl instanceof AnnotationDeclaration &&
                decl.toString().contains("endinclude")) {
                break;
            }
            if (decl instanceof Procedure) {
                ret = decl;
            }
        }
        return ret;
    }

    /** Generates code with the given list of lines */
    private String genCode(List<String> code) {
        String ret = "";
        for (String line : code) {
            ret += line + NEWLINE;
        }
        return ret;
    }

    /** Generates code with the given list of lines */
    private String genCode(String[] code) {
        String ret = "";
        for (String line : code) {
            ret += line + NEWLINE;
        }
        return ret;
    }

}
