import java.util.*;

import CapableSimulator.CapableSimulator;
import CapableSimulator.Utils.Parser;
import itumulator.executable.Program;

public class Main {

    public static void main(String[] args) {

        String inputDataFilePath = "src/Data/week-3/tf3-1a.txt";
        int worldSize = Parser.parseWorldSizeFromFile(inputDataFilePath);

        Program program = new Program(worldSize, 800, 50);
        CapableSimulator sim = (CapableSimulator) program.getSimulator();
        sim.setInputFilePath(inputDataFilePath);
        sim.runSimulation();
        program.show();

    }
}