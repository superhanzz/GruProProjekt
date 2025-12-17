package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Animals.Predators.Predator;
import CapableSimulator.CapableWorld;
import itumulator.world.World;

public class CordycepSpore extends FungiSpore{

    private Animal host;

    public CordycepSpore(CapableWorld world, Animal host) {
        super("cordycep", world);
        this.host = host;
    }

    @Override
    public void act(World world) {

    }

    @Override
    protected void doEverySimulationStep() {}

    public void searchForNonInfected() {}
}
