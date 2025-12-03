package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public class Putin extends Animals {

    DisplayInformation diPutin = new DisplayInformation(Color.blue, "putin");

    public Putin(){
        super("putin");
        this.energy = 100;
        this.maxEnergy = 1000;
        this.age = 73;
    }
    @Override
    protected List<WorldActor> findFoodFromSource(World world, Location[] neighbours){
        List<WorldActor> worldActorList = new ArrayList<>();
        for(Location location : neighbours){
            Object o = world.getTile(location);
            if(o instanceof Wolf || o instanceof Rabbit || o instanceof Grass || o instanceof BerryBush){
                worldActorList.add((WorldActor) o);
            }
        }
        return worldActorList;
    }
    @Override
    public DisplayInformation getInformation() { return diPutin; }

    @Override
    public void act(World world){
        lookForFood(world, 5);

    }
}

