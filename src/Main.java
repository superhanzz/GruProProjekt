import java.util.*;

import CapableSimulator.CapableProgram;
import CapableSimulator.CapableSimulator;
import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.Parser;

public class Main {

    public static void main(String[] args) {

        String inputDataFilePath = "src/Data/week-3/t3-2ab.txt";
        Parser parser = new Parser(inputDataFilePath);
        parser.parseInputsFromFile();

        CapableProgram program = new CapableProgram(parser.getWorldSize(), 800, 50, parser);
        program.run();





        /*
        CapableSim sim = new CapableSim(200, 800, 200, "src/Data/week-2/t2-3a.txt");
        sim.runSimulation();
        */
    }
}