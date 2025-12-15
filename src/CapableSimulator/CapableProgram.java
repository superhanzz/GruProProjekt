package CapableSimulator;

import CapableSimulator.Utils.Parser;
import itumulator.executable.Program;

public class CapableProgram extends Program {

    CapableSimulator simulator;

    public CapableProgram(int size, int display_size, int delay, Parser parser) {
        super(size, display_size, delay);
        simulator = getCapableSimulator();
        simulator.setParser(parser);

        //setWorld(new CapableWorld(size));
        //setSimulator(new CapableSimulator(getCapableWorld(), getCanvas(), delay,  parser, this));

        //getCapableSimulator().setFrame(getFrame());

        //getCanvas().setWorld(getWorld());
    }

    @Override
    public void run() {
        simulator.runSimulation();
        show();
    }


    public CapableWorld getCapableWorld() { return (CapableWorld) getWorld(); }

    public CapableSimulator getCapableSimulator() { return (CapableSimulator) getSimulator(); }
}
