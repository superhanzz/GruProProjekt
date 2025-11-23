package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public class Grass implements NonBlocking, Actor{
    @Override
    public void act(World world) {

        grow(world);
    }

    public void grow(World world) {
        Set<Location> tiles = world.getSurroundingTiles(world.getLocation(this));
        List<Location> locations = new ArrayList<>();
        for (Location location : tiles){
            if(!world.containsNonBlocking(location)){
                locations.add(location);
            }
        }
        Random rand = new Random();
        if(locations.isEmpty()){
            return;
        }
        int index = rand.nextInt(locations.size());
        Location location = locations.get(index);
        if(rand.nextDouble() < 0.01){
            world.setTile(location, new Grass());
        }


    }
}
