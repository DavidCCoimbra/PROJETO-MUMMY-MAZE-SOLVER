package agent;

public abstract class State{

    /**
     * Action that generated this state.
     */
    protected Action action;
    protected boolean exitFound;

    public State(){}

    public abstract void executeAction(Action action);
    
    public Action getAction(){
        return action;
    }

    public void setAction(Action action){
        this.action = action;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public boolean isExitFound() {
        return this.exitFound;
    }

}