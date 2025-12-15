package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public abstract class WorldActor implements Actor, DynamicDisplayInformationProvider {

    public final String actorType;
    protected CapableWorld world;
    protected CapableEnums.FungiState fungiState;


    protected WorldActor(String actorType, CapableWorld world) {
        this.actorType = actorType;
        this.world = world;
        fungiState = CapableEnums.FungiState.NORMAL;
    }

    /** Handles what should be done at each simulation step */
    protected abstract void doEverySimulationStep();

    //public abstract void setActorType(String actorType);
    public String getActorType() {return actorType;}


    public CapableEnums.FungiState getFungiState() {
        return fungiState;
    }

    public void makeFungi() {
        fungiState = CapableEnums.FungiState.FUNGI;
    }

    public abstract int getEnergyValue();

    public Location getLocation() {
        if (world == null) throw new NullPointerException("In getLocation(): World is null");

        if (world.isOnTile(this)) return world.getLocation(this);
        else return null;
    }
}
