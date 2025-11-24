package CapableSimulator;

import FunctionLibrary.CapableFunc;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Animals {

    Burrow burrow;
    Location burrowLocation;

    private int energy;
    private int maxEnergy;
    private int age;

    private boolean isPregnant;
    //private int

    private int pregnancyCooldown;

    public Rabbit() {
        this.energy = 10;
        this.maxEnergy = 15;
        this.age = 0;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
        pregnancyCooldown = 0;
    }

    public Rabbit(int energy) {
        this.energy = energy;
        burrow = null;
        burrowLocation = null;
        isOnMap = true;
        pregnancyCooldown = 0;
    }

    @Override
    public void act(World world) {
        if(world.isNight() || !isOnMap)
            return;

        /*if (energy >= (maxEnergy - 4)) {
            if (!tryReproduce(world)){
                lookForFood(world);
            }
        }
        else {
            lookForFood(world);
        }*/
        lookForFood(world);
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
        boolean mated = possibleMates.get(rand.nextInt(possibleMates.size())).mate(world);
        if (mated) pregnancyCooldown = 3;

        return mated;
    }

    protected boolean mate(World world) { // This function is called from another Rabbit instance
        //System.out.println("Mate");
        if (!(energy >= (maxEnergy - 4)) && pregnancyCooldown <= 0)
            return false;
        Rabbit baby = new Rabbit();
        Location[] locations = world.getEmptySurroundingTiles(world.getLocation(this)).toArray(new Location[0]);
        if (locations.length <= 0) return false;

        Random rand = new Random();
        world.setTile(locations[rand.nextInt(locations.length)], baby);
        pregnancyCooldown = 3;
        //baby.updateOnMap(world, CapableFunc.getEmptyTile(world, world.getSize()), true);

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

    void digBurrow(World world) {
        Object standingOn = world.getNonBlocking(world.getLocation(this));
        if (standingOn != null) {
            world.delete(standingOn);
        }

        burrow = new Burrow(this);
        world.setTile(world.getLocation(this), burrow);
        burrowLocation = world.getLocation(this);

    }

    @Override
    public void onDay(World world) {
        if (burrowLocation != null && !isOnMap) updateOnMap(world, burrowLocation, true);
    }

    @Override
    public void onNight(World world) {
        if (burrow == null) {
            for (Location l : world.getSurroundingTiles(world.getLocation(this))) {
                if (world.getNonBlocking(l) instanceof Burrow) {
                    burrow = (Burrow) world.getNonBlocking(l);
                    break;
                }
            }
            if (burrow == null) digBurrow(world);
        }
        else {
            //world.move(this, burrowLocation);
        }
        updateOnMap(world, world.getLocation(this),false);
    }
    @Override
    public void almostNight(World world) {
        if(burrow == null) return;
        Location closestTile = getClosestTile(world, burrowLocation);
        if (closestTile == null) return;
        world.move(this, closestTile);
    }


}
