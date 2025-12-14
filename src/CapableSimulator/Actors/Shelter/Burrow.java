package CapableSimulator.Actors.Shelter;

import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.WorldActor;
import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Burrow extends WorldActor implements NonBlocking {

    List<Rabbit> owners;
    DisplayInformation diBurrow = new DisplayInformation(Color.blue, "hole-small");

    public Burrow(CapableWorld world, Rabbit rabbit){
        super("burrow", world);
        owners = new ArrayList<Rabbit>();
        owners.add(rabbit);
    }

    public Burrow(CapableWorld world){
        super("burrow", world);
        owners = new ArrayList<>();
    }

    @Override
    public void act(World world) {

    }

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return diBurrow;
    }
}
