package CapableSimulator.Actors;

public interface EnergyConsumer {

    /** Handles what should be done at each simulation step */
    void doEverySimulationStep();

    /** The rate at which the actors energy decreases */
    int getDecayRate();
}
