package searchmethods;

import agent.Problem;
import agent.Solution;

import java.io.IOException;

public interface SearchMethod {

   Solution search(Problem problem) throws IOException, ClassNotFoundException;
   
   Statistics getStatistics();
   
   void stop();
   
   boolean hasBeenStopped();
}