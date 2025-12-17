import java.util.*;

import CapableSimulator.CapableProgram;
import CapableSimulator.Utils.Parser;

public class Main {

    public static void main(String[] args) {

        String inputDataFilePath = "src/Data/week-3/tf3-1a.txt";
        Parser parser = new Parser(inputDataFilePath);
        parser.parseInputsFromFile();

        CapableProgram program = new CapableProgram(parser.getWorldSize(), 800, 50, parser);
        program.run();


    }
}