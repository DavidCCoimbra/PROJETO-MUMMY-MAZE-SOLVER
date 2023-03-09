package searchmethods;

import agent.Action;
import agent.Problem;
import agent.Solution;
import agent.State;

import java.util.List;

public class IDAStarSearch extends InformedSearch {
    /*
     * Note that, on each iteration, the search is done in a depth first search way.
     */

    private double limit;
    private double newLimit;

    @Override
    public Solution search(Problem problem) {
        statistics.reset();
        stopped = false;
        this.heuristic = problem.getHeuristic();

        limit = heuristic.compute(problem.getInitialState());

        Solution solution;
        do {
            solution = graphSearch(problem);
        } while (solution == null);

        return solution;
    }

    @Override
    protected Solution graphSearch(Problem problem) {
        newLimit = Double.POSITIVE_INFINITY;
        frontier.clear();
        frontier.add(new Node(problem.getInitialState()));

        while (!frontier.isEmpty() && !stopped) {
            Node n = (Node) frontier.poll();
            State state = n.getState();
            if (problem.isGoal(state)) {
                return new Solution(problem, n);
            }
            int numSuccessorsSize = 0;
            List<Action> actions = problem.getActions(state);
            numSuccessorsSize = actions.size();
            for (Action action : actions) {
                State successor = problem.getSuccessor(state, action);
                if (successor != null)
                    addSuccessorToFrontier(successor, n);
            }
            computeStatistics(numSuccessorsSize);
        }
        limit = newLimit;
        return null;
    }

    @Override
    public void addSuccessorToFrontier(State successor, Node parent) {

        if (successor == null)
            return;

        double g = parent.getG() + successor.getAction().getCost();
        double f = g + heuristic.compute(successor);
        if (!frontier.containsState(successor)) {
            if (f <= limit) {
                if (!parent.isCycle(successor)) {
                    frontier.add(new Node(successor, parent, g, f));
                }
            } else {
                newLimit = Math.min(newLimit, f);
            }
        } else if (frontier.getNode(successor).getG() > g) {
            frontier.removeNode(successor);
            frontier.add(new Node(successor, parent, g, f));
        }
    }

    @Override
    public String toString() {
        return "IDA* search";
    }
}
