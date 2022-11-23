package cetus.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



import cetus.analysis.DDGraph;
import cetus.analysis.DependenceVector;
import cetus.analysis.LoopTools;
import cetus.analysis.DDGraph.Arc;
import cetus.hir.DFIterator;
import cetus.hir.DepthFirstIterator;
import cetus.hir.Loop;
import cetus.hir.Procedure;
import cetus.hir.Program;
import cetus.hir.Statement;
import cetus.hir.Traversable;

public class DependencesAnalysis extends AnalysisPass {

    private static Map<Loop, ArrayList<Arc>> dependenciesList;

    public void start() {

    }

    public DependencesAnalysis(Program program) {
        super(program);
        dependenciesList = new HashMap<Loop, ArrayList<Arc>>();
        // TODO Auto-generated constructor stub
    }

    public static void analyzeProcedure(Program p, ArrayList<Loop> all_loops) {

        DDGraph ddGraph = p.getDDGraph();
        if (ddGraph == null) {
            System.out.println("Empty dd graph");
            return;
        }
        List<Arc> arcs = ddGraph.getAllArcs();
        int n = arcs.size();
        for (int index = 18; index < all_loops.size(); index++) {
            Loop loop = all_loops.get(index);
            for (int i = 0; i < n; i++) {
                Arc arc = arcs.get(i);
                Map<Loop, ArrayList<Arc>> dependenciesList2 = dependenciesList;
                DDArrayAccessInfo sourceStatement = arc.getSource();
                DDArrayAccessInfo sinkStatement = arc.getSink();
                if(arc.belongsToLoop(loop))
                {
                    if (dependenciesList.containsKey(sinkStatement.getAccessLoop())) {
                        ArrayList<Arc> list = dependenciesList.get(sinkStatement.getAccessLoop());
                        list.add(arc);
                    } else {
                        ArrayList<Arc> list = new ArrayList<Arc>();
                        list.add(arc);
                        dependenciesList.put(sinkStatement.getAccessLoop(), list);
                    }
                }
                
                //
    
                
    
            }
        }
       

        for (Map.Entry<Loop, ArrayList<Arc>> entry : dependenciesList.entrySet()) {

            System.out.println("----------------------\n" + entry.getKey().toString() + "\n");

            for (int index = 0; index < entry.getValue().size(); index++) {
                Arc arc = entry.getValue().get(index);
                byte type = arc.getDependenceType();
                String dependencyType = type == 1 ? "Flow" : type == 2 ? "Anti" : type == 3 ? "Output" : "" + type;

                System.out.printf("%s: %s -> %s\n",
                        dependencyType,
                        arc.getSinkStatement(),
                        arc.getSourceStatement());
            }

        }
    }

    private static void analyzeLoop(Loop loop) {
    }

    private static void printDirectionMatrix(List<DependenceVector> dependenceVectors) {
        int n = dependenceVectors.size();
        for (int i = 0; i < n; i++) {
            DependenceVector vector = dependenceVectors.get(i);
            System.out.printf("vector: %s\n", vector.VectorToString());
        }
    }

    @Override
    public String getPassName() {
        // TODO Auto-generated method stub
        return null;
    }

}
