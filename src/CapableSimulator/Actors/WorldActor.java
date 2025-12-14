package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
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

    @Override
    public void act(World world) {

    }

    //public abstract void setActorType(String actorType);
    public String getActorType() {return actorType;}


    public CapableEnums.FungiState getFungiState() {
        return fungiState;
    }

    public void makeFungi() {
        fungiState = CapableEnums.FungiState.FUNGI;
    }

    public abstract int getEnergyValue();
}
