package mummymaze;

import agent.Heuristic;

public class HeuristicTileEuclideanDistanc extends Heuristic<MummyMazeProblem, MummyMazeState>{

    @Override
    public double compute(MummyMazeState state){
        return state.computeTileEuclideanDistance();
    }
    
    @Override
    public String toString(){
        return "Tiles distance to final position diagonally";
    }
}