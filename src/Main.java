import java.util.*;

import CapableSimulator.CapableSimulator;
import CapableSimulator.Utils.Parser;
import itumulator.executable.Program;

public class Main {

    public static void main(String[] args) {

        String inputDataFilePath = "src/Data/week-3/tf3-1a.txt";
        Parser parser = new Parser(inputDataFilePath);
        parser.parseInputsFromFile();

        Program program = new Program(parser.getWorldSize(), 800, 50);
        CapableSimulator sim = (CapableSimulator) program.getSimulator();
        sim.setParser(parser);
        program.show();
        program.run();


    }
}