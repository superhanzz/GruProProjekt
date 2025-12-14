package CapableSimulator.Actors.Plants;

import CapableSimulator.Actors.WorldActor;
import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
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


    public Grass(CapableWorld world){
        super("grass", world);
        growthChance = 0.15;
        energyValue = 2;
    }

    @Override
    public void act(World world) {

        if ( world.isNight())
            return;

        grow();
    }

    public void grow() {
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
            world.setTile(location, new Grass(world));
        }


    }

    public double getGrowthChance() {
        return growthChance;
    }

    @Override
    public int getEnergyValue() {
        return energyValue;
    }

    @Override
    public DisplayInformation getInformation() {
        return diGrass;
    }
}
