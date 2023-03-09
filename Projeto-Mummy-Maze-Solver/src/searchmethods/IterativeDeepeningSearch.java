package searchmethods;

import agent.Action;
import agent.Problem;
import agent.Solution;
import agent.State;

import java.util.List;

public class IterativeDeepeningSearch extends DepthLimitedSearch {
    /*
     * We do not use the code from DepthLimitedSearch because we can optimize
     * so that the algorithm only verifies if a state is a goal if its depth is
     * equal to the limit. Note that given a limit X we are sure not to
     * encounter a solution below this limit because a (failed) limited depth
     * search has already been done. That's why we do not extend this class from
     * DepthLimitedSearch. We extend from DepthFirstSearch so that we don't need
     * to rewrite method insertSuccessorsInFrontier again.
     * After the class, please see a version of the search algorithm without
     * this optimization.
     */

    @Override
    public Solution search(Problem problem) {
        statistics.reset();
        statistics.numGeneratedSates = 0; //specific to this algorithm
        stopped = false;
        limit = 0;
        Solution solution;
        int previousNumGeneratedStates;
        do {
            previousNumGeneratedStates = statistics.numGeneratedSates;
            solution = graphSearch(problem);
            statistics.numGeneratedSates++; //specific to this algorithm
            limit++;
        } while (solution == null && statistics.numGeneratedSates != previousNumGeneratedStates);

        return solution;
    }

    @Override
    protected Solution graphSearch(Problem problem) {
        frontier.clear();
        frontier.add(new Node(problem.getInitialState()));
        while (!frontier.isEmpty() && !stopped) {
            Node n = (Node) frontier.poll();
            State state = n.getState();
            if (n.getDepth() == limit && problem.isGoal(state)) {
                return new Solution(problem, n);
            }
            int SuccessorsSize = 0;
            if (n.getDepth() < limit) {
                List<Action> actions = problem.getActions(state);
                SuccessorsSize = actions.size();

                for (Action action : actions) {
                    State successor = problem.getSuccessor(state, action);
                    if (successor != null)
                        addSuccessorToFrontier(successor, n);
                }
            }
            computeStatistics(SuccessorsSize);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Iterative deepening search";
    }
}