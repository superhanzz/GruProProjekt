package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;

public class Burrow implements NonBlocking, Actor {

    List<Rabbit> owners;


    public Burrow(Rabbit rabbit){
        owners = new ArrayList<Rabbit>();
        owners.add(rabbit);
    }

    public Burrow(){
        owners = new ArrayList<>();
    }



    @Override
    public void act(World world) {

    }
}
