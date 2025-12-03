package CapableSimulator;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;

public abstract class WorldActor implements Actor, DynamicDisplayInformationProvider {

    public final String actorType;

    protected WorldActor(String actorType) {
        this.actorType = actorType;
    }

    @Override
    public void act(World world) {

    }


    //public abstract void setActorType(String actorType);
    public String getActorType() {return actorType;}

    protected abstract int getEnergyValue();
}
