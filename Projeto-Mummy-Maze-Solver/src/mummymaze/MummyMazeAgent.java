package mummymaze;

import agent.Agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class MummyMazeAgent extends Agent<MummyMazeState> {

    protected MummyMazeState initialEnvironment;

    public MummyMazeAgent(MummyMazeState environment) {
        super(environment);
        initialEnvironment = environment.clone();
        heuristics.add(new HeuristicTileDistance());
        heuristics.add(new HeuristicTilesOutOfPlace());
        heuristics.add(new HeuristicTileEuclideanDistanc());
        heuristic = heuristics.get(0);
    }

    public MummyMazeState resetEnvironment() {
        environment = initialEnvironment.clone();
        return environment;
    }

    public MummyMazeState readInitialStateFromFile(File file) throws IOException {

        FileReader fr = new FileReader(file);   //Creation of File Reader object
        BufferedReader br = new BufferedReader(fr);  //Creation of BufferedReader object
        int c;
        int i = 0;
        int j = 0;
        LinkedList<Gate> gates = new LinkedList<>();
        LinkedList<Trap> traps = new LinkedList<>();

        int lineExit = 0, columnExit = 0, lineKey = 0, columnKey = 0;
        char[][] matrix = new char[13][13];
        while ((c = br.read()) != -1)         //Read char by Char
        {
            if (c == '\n') {
                i++;
                j = 0;
                continue;
            }
            matrix[i][j] = (char) c;   //converting integer to char //elemetos estaticos do nivel
            if (matrix[i][j] == 'S') {
                lineExit = i;
                columnExit = j;
            }
            if (matrix[i][j] == 'A') {
                traps.add(new Trap(i, j));
            }
            if (matrix[i][j] == 'C') {
                lineKey = i;
                columnKey = j;
            }
            if (matrix[i][j] == '=' || matrix[i][j] == '"' || matrix[i][j] == '_' || matrix[i][j] == ')') {
                gates.add(new Gate(i, j, matrix[i][j]));
            }

            j++;
        }
        initialEnvironment = new MummyMazeState(matrix, lineKey, columnKey, lineExit, columnExit, gates, traps);
        resetEnvironment();
        return environment;
    }
}
