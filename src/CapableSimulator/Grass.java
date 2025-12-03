package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Grass extends WorldActor implements NonBlocking{

    private final double growthChance;
    private final int energyValue;

    DisplayInformation diGrass = new DisplayInformation(Color.green, "grass");


    public Grass(){
        super("grass");
        growthChance = 0.15;
        energyValue = 2;
    }

    @Override
    public void act(World world) {

        if ( world.isNight())
            return;

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

    public double getGrowthChance() {
        return growthChance;
    }

    @Override
    protected int getEnergyValue() {
        return energyValue;
    }

    @Override
    public DisplayInformation getInformation() {
        return diGrass;
    }
}
