package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Wolf extends Animals {

    private int energy;

    private int maxEnergy;

    private int age;

    Set<Actor> wolfGang;

    public Wolf() {
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
    }

    public Wolf(Set<Actor> wolfgang) {
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
        this.wolfGang = wolfgang;
    }

    @Override
    public void act(World world){

        lookForFood(world);

        energy--;
        if(energy <= 0){
            this.die(world);
        }
        age++;
    }

    public void lookForFood(World world){
        Location[] neighbours = world.getSurroundingTiles(world.getLocation(this),2).toArray(new Location[0]);
        List<Location> rabbitTiles = new ArrayList<>();
        for (Location location : neighbours) {
            if(world.getTile(location)  instanceof Rabbit){
                rabbitTiles.add(location);
            }
        }
        Random rand = new Random();
        Location searchLocation;
        if(!rabbitTiles.isEmpty()){
            searchLocation = rabbitTiles.get(rand.nextInt(rabbitTiles.size()));
            world.delete(world.getTile(searchLocation));
            this.energy = this.energy + 5;
        }else {
            searchLocation = neighbours[rand.nextInt(neighbours.length)];
            while(!world.isTileEmpty(searchLocation)){
                searchLocation = neighbours[rand.nextInt(neighbours.length)];
            }
        }

        world.move(this, searchLocation);




    }
}
