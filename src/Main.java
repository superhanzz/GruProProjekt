import java.util.*;

import CapableSimulator.CapableSimulator;
import CapableSimulator.Utils.Parser;
import itumulator.executable.Program;

public class Main {

    public static void main(String[] args) {

        String inputDataFilePath = "src/Data/week-2/t2-3a.txt";
        int worldSize = Parser.parseWorldSizeFromFile(inputDataFilePath);

        Program program = new Program(worldSize, 1000, 200);
        CapableSimulator sim = (CapableSimulator) program.getSimulator();
        sim.setInputFilePath(inputDataFilePath);
        sim.runSimulation();
        program.show();

    }
}