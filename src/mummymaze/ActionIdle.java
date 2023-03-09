package mummymaze;

import agent.Action;

public class ActionIdle extends Action<MummyMazeState> {

    public ActionIdle() {
        super(1);
    }

    @Override
    public void execute(MummyMazeState state){
        state.idle();
        state.setAction(this);
    }

    @Override
    public boolean isValid(MummyMazeState state){
        return true;
    }
}
