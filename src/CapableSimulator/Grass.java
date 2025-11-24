package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public class Grass implements NonBlocking, Actor{

    double growthChance;

    public Grass(){
        growthChance = 0.5;
    }

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
        if(rand.nextDouble() < growthChance){
            world.setTile(location, new Grass());
        }


    }
}
