package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rabbit implements Actor {

    private int energy;
    private int maxEnergy;
    private int age;

    public Rabbit() {
        this.energy = 10;
        this.maxEnergy = 10;
        this.age = 0;
    }

    public Rabbit(int energy) {
        this.energy = energy;
    }

    @Override
    public void act(World world) {
        lookForFood(world);
        energy--;
        if(energy <= 0) {
            die(world);
        }

        //maxEnergy = age % 5 + 10;

    }

    public void testEatGrass(World world) {
        Grass foundGrass = searchTile(world);
        if (foundGrass != null) {
            eatGrass(world, foundGrass);
        }
    }

    private void lookForFood(World world) {
        Location[] emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this)).toArray(new Location[0]);
        if (emptyTiles.length <= 0) {
            return;
        }
        List<Location> grassTiles = new ArrayList<>();
        for (Location location : emptyTiles) {
            if (world.getNonBlocking(location) instanceof Grass) grassTiles.add(location);
        }
        Random rand = new Random();
        Location searchLocation;
        if (!grassTiles.isEmpty())
            searchLocation = grassTiles.get(rand.nextInt(grassTiles.size()));
        else
            searchLocation = emptyTiles[rand.nextInt(emptyTiles.length)];


        world.move(this, searchLocation);

        Grass foundGrass = searchTile(world);
        if (foundGrass != null) {
            eatGrass(world, foundGrass);
        }
    }

    private void tryReproduce(World world) {

    }

    private Grass searchTile(World world) {
        Object nonBlockingObject = world.getNonBlocking(world.getLocation(this));
        return nonBlockingObject instanceof Grass ? (Grass) nonBlockingObject : null;
    }

    private void eatGrass(World world,  Grass grass) {
        world.delete(grass);
        energy++;
    }

    void die(World world) {
        world.delete(this);
    }
}
