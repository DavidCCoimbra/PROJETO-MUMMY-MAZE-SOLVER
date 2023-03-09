package mummymaze;

import agent.Action;
import agent.Problem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class MummyMazeProblem extends Problem<MummyMazeState> {

    protected List<Action> actions;

    public MummyMazeProblem(MummyMazeState initialState) {
        super(initialState);
        actions = new LinkedList<Action>() {{
            add(new ActionDown());
            add(new ActionUp());
            add(new ActionRight());
            add(new ActionLeft());
            add(new ActionIdle());
        }};
    }

    @Override
    public List<Action<MummyMazeState>> getActions(MummyMazeState state) {
        List<Action<MummyMazeState>> possibleActions = new LinkedList<>();
        for (Action action : actions) {
            if (!state.isHeroKilled() && action.isValid(state)) {
                possibleActions.add(action);
            }
        }
        return possibleActions;
    }

    @Override
    public MummyMazeState getSuccessor(MummyMazeState state, Action action) {
        MummyMazeState successor = state.clone();
        action.execute(successor);
        //if hero is dead, that successor won't be added to the frontier
        if (!successor.isHeroKilled())
            return successor;
        return null;
    }


    @Override
    public double computePathCost(List<Action> path) {
        return path.size();
    }


    @Override
    public boolean isGoal(MummyMazeState state) {
        return state.isExitFound();
    }

}