package CapableSimulator;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

public abstract class WorldActor implements Actor, DynamicDisplayInformationProvider {

    @Override
    public void act(World world) {

    }

    protected abstract int getEnergyValue();
}
