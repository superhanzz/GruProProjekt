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

    private boolean isPregnant;
    //private int

    private final int pregnancyTime = 3;
    private final int pregnancyCooldown = 3;

    public Rabbit() {
        this.energy = 10;
        this.maxEnergy = 15;
        this.age = 0;
    }

    public Rabbit(int energy) {
        this.energy = energy;
    }

    @Override
    public void act(World world) {
        if (energy >= (maxEnergy - 4)) {
            if (!tryReproduce(world)){
                lookForFood(world);
            }
        }
        else {
            lookForFood(world);
        }
        if(isPregnant){

        }

        energy--;
        if(energy <= 0) {
            die(world);
        }

        if (age % 5 == 4) {
            maxEnergy++;
        }

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

    private boolean tryReproduce(World world) {
        List<Rabbit> possibleMates = new ArrayList<>();


        world.getSurroundingTiles(world.getLocation(this)).forEach(location -> {
            Object o = world.getTile(location);
            if (o instanceof Rabbit) {
                possibleMates.add((Rabbit) o);
            }
        });

        if (possibleMates.isEmpty()) return false;

        Random rand = new Random();
        boolean mated = possibleMates.get(rand.nextInt(possibleMates.size())).mate();

        return mated;
    }

    protected boolean mate() { // This function is called from another Rabbit instance
        System.out.println("Mate");
        if (!(energy >= (maxEnergy - 4)))
            return false;


        return true;
    }

    private Grass searchTile(World world) {
        Object nonBlockingObject = world.getNonBlocking(world.getLocation(this));
        return nonBlockingObject instanceof Grass ? (Grass) nonBlockingObject : null;
    }

    private void eatGrass(World world,  Grass grass) {
        world.delete(grass);
        energy += 2;
        energy = Math.clamp(energy, 0, maxEnergy);
    }

    void die(World world) {
        world.delete(this);
    }
}
