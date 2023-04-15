package agent;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import searchmethods.*;

public class Agent<E extends State> {

    protected E environment;
    protected ArrayList<SearchMethod> searchMethods;
    protected SearchMethod searchMethod;
    protected ArrayList<Heuristic> heuristics;
    protected Heuristic heuristic;
    protected Solution solution;
    protected long startTime;
    protected long endTime;
    protected long totalTime;

    public Agent(E environment) {
        this.environment = environment;
        searchMethods = new ArrayList<>();
        searchMethods.add(new BreadthFirstSearch());
        searchMethods.add(new UniformCostSearch());
        searchMethods.add(new DepthFirstSearch());
        searchMethods.add(new DepthLimitedSearch());
        searchMethods.add(new IterativeDeepeningSearch());
        searchMethods.add(new GreedyBestFirstSearch());
        searchMethods.add(new AStarSearch());
        searchMethods.add(new BeamSearch());
        searchMethods.add(new IDAStarSearch());
        searchMethod = searchMethods.get(0);
        heuristics = new ArrayList<>();
        startTime = 0;
        endTime = 0;
        totalTime = 0;
    }

    public Solution solveProblem(Problem problem) throws IOException, ClassNotFoundException {
        startTime = System.currentTimeMillis();

        if (heuristic != null) {
            problem.setHeuristic(heuristic);
            heuristic.setProblem(problem);
        }
        solution = searchMethod.search(problem);

        totalTime = System.currentTimeMillis() - startTime;

        return solution;
    }

    public void executeSolution() {
        for (Action action : solution.getActions()) {
            environment.executeAction(action);
        }
    }

    public boolean hasSolution() {
        return solution != null;
    }

    public void stop() {
        getSearchMethod().stop();
    }

    public boolean hasBeenStopped() {
        return getSearchMethod().hasBeenStopped();
    }

    public E getEnvironment() {
        return environment;
    }

    public void setEnvironment(E environment) {
        this.environment = environment;
    }

    public SearchMethod[] getSearchMethodsArray() {
        SearchMethod[] sm = new SearchMethod[searchMethods.size()];
        return searchMethods.toArray(sm);
    }

    public SearchMethod getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(SearchMethod searchMethod) {
        this.searchMethod = searchMethod;
    }

    public Heuristic[] getHeuristicsArray() {
        Heuristic[] sm = new Heuristic[heuristics.size()];
        return heuristics.toArray(sm);
    }

    public Heuristic getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public String getSearchReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(searchMethod + "\n");
        if (solution == null) {
            sb.append("No solution found\n");
        } else {
            sb.append("Solution cost: " + Double.toString(solution.getCost()) + "\n");
        }
        long hour = TimeUnit.MILLISECONDS.toHours(totalTime);
        long min = TimeUnit.MILLISECONDS.toMinutes(totalTime);
        long sec = (TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60);
        long mili = totalTime - TimeUnit.SECONDS.toMillis(sec) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.HOURS.toMillis(hour);
        sb.append("Num of expanded nodes: " + searchMethod.getStatistics().numExpandedNodes + "\n");
        sb.append("Max frontier size: " + searchMethod.getStatistics().maxFrontierSize + "\n");
        sb.append("Num of generated nodes: " + searchMethod.getStatistics().numGeneratedSates + "\n");
        sb.append("Time: " + hour + ":" + min + ":" + sec + "," + mili + "\n");


        //file with statics
        StringBuilder method = new StringBuilder();
        method.append(searchMethod);

        StringBuilder solutionCost = new StringBuilder();
        if (solution != null) {
            solutionCost.append(Double.toString(solution.getCost()));}
        else {
            solutionCost.append(Double.toString(0));
        }

        StringBuilder expandedNodes = new StringBuilder();
        expandedNodes.append(searchMethod.getStatistics().numExpandedNodes);

        StringBuilder maxFrontier = new StringBuilder();
        maxFrontier.append(searchMethod.getStatistics().maxFrontierSize);

        StringBuilder generatedStates = new StringBuilder();
        generatedStates.append(searchMethod.getStatistics().numGeneratedSates);


        //String level = searchMethods.get(0).toString();

        File file = new File("nivel_statistic.xls");
        if (!file.exists()) {
            utils.FileOperations.appendToTextFile("nivel_statistic.xls",
                    "Level" + "\n" +
                            "Method" + "\t" +
                            "Solution cost" + "\t" +
                            "Num of expanded nodes" + "\t" +
                            "Max frontier size:" + "\t" +
                            "Num of generated states:" + "\t" +
                            "Time:" + "\r\n");
        }
        if (solution == null) {
            utils.FileOperations.appendToTextFile("nivel_statistic.xls",
                    method.toString() + "\t" +
                            "No solution found" + "\t" +
                            "null" + "\t" +
                            "null" + "\t" +
                            "null" + "\t" +
                            "null" + "\r\n");
        } else {
            utils.FileOperations.appendToTextFile("nivel_statistic.xls",
                     searchMethod + "\t" +
                            solutionCost.toString() + "\t" +
                            expandedNodes.toString() + "\t" +
                            maxFrontier.toString() + "\t" +
                            generatedStates.toString() + "\t" +
                            hour + ":" + min + ":" + sec + "," + mili + "\r\n");
        }


        return sb.toString();
    }


    public double getSolutionCost() {
        return solution != null ? solution.getCost() : 0;
    }
}
