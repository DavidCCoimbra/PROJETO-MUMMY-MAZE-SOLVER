package searchmethods;

import agent.Action;
import agent.Problem;
import agent.Solution;
import agent.State;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import utils.NodePriorityQueue;

public class BeamSearch extends AStarSearch {

    private int beamSize; //limite da fronteira

    public BeamSearch() {
        this(100);
    }

    public BeamSearch(int beamSize) {
        this.beamSize = beamSize;
    }

    @Override
    protected Solution graphSearch(Problem problem){
        frontier.clear();
        explored.clear();
        frontier.add(new Node(problem.getInitialState()));

        while (!frontier.isEmpty() && !stopped) {
            Node n = frontier.poll();
            State state = n.getState();
            if (problem.isGoal(state)) {
                return new Solution(problem, n);
            }
            explored.add(state);
            List<Action> actions = problem.getActions(state);
            for (Action action : actions) {
                State successor = problem.getSuccessor(state, action);
                if (successor != null)
                    addSuccessorToFrontier(successor, n);
            }
            manageFrontierSize();
            computeStatistics(actions.size());
        }
        return null;
    }

    private void manageFrontierSize() {
        if (frontier.size() > beamSize) {
            NodePriorityQueue aux = new NodePriorityQueue();
            for (int i = 0; i < beamSize; i++) {
                aux.add(frontier.poll());
            }
            frontier = aux;
        }
    }

    public void setBeamSize(int beamSize) {
        this.beamSize = beamSize;
    }

    public int getBeamSize() {
        return beamSize;
    }

    @Override
    public String toString() {
        return "Beam search";
    }
}
