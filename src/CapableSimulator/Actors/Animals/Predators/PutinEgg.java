package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.SpawningAgent;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class PutinEgg extends WorldActor {

    private static final int INIT_HATCH_TIME = 10;
    private int timeToHatch;

    private static final DisplayInformation di = new DisplayInformation(Color.cyan, "putin-egg");

    public PutinEgg(World world) {
        super("putinEgg", world);
        timeToHatch = INIT_HATCH_TIME;
    }

    @Override
    public void act(World world) {
        timeToHatch--;
        if (timeToHatch <= 0) {
            hatchEgg();
        }
    }

    private void hatchEgg() {
        Location hatchLocation = world.getLocation(this);
        Putin putin = new Putin(world);
        world.delete(this);
        SpawningAgent.spawnActorAtLocation(world, putin, hatchLocation);
    }

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return di;
    }

}
