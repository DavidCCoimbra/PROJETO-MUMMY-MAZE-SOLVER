package mummymaze;

public class Trap {
    private final int line;
    private final int column;
    private final Character character;

    public Trap(int line, int column) {
        this.line = line;
        this.column = column;
        this.character = 'A';
    }

    public Character getState() {
        return character;
    }

    public int getLine() {
        return line;
    }


    public int getColumn() {
        return column;
    }


    @Override
    public String toString() {
        return "Gate{" +
                "state=" + character +
                '}';
    }
}