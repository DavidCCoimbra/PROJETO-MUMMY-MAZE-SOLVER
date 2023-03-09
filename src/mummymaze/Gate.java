package mummymaze;

public class Gate {
    private final int line;
    private final int column;
    private Character state;

    public Gate(int line, int column, Character state) {
        this.line = line;
        this.column = column;
        this.state = state;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Character getState() {
        return state;
    }



    @Override
    public String toString() {
        return "Gate{" +
                "state=" + state +
                '}';
    }

}
