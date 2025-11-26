package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.World;

public abstract class WorldActor implements Actor {

    @Override
    public void act(World world) {

    }

    protected abstract int getEnergyValue();
}
