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
    private int matingAge;
    private boolean canMate;

    private int pregnancyCooldown;

    public Rabbit() {
        this.energy = 10;
        this.maxEnergy = 15;
        this.age = 0;
        this.matingAge = 10;
        this.pregnancyCooldown = 0;
        this.canMate = true;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }

    public Rabbit(int energy) {
        this.energy = energy;
        this.age = 0;
        this.matingAge = 10;
        this.pregnancyCooldown = 0;
        this.canMate = true;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }

    public Rabbit(int matingAge, int pregnancyCooldown) {
        this.matingAge = matingAge;
        this.pregnancyCooldown = pregnancyCooldown;

        this.age = 0;
        this.energy = 10;
        this.pregnancyCooldown = 0;
        this.canMate = true;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }


    @Override
    public void act(World world) {
        if(world.isNight() || !isOnMap)
            return;

        if(canMate && age >= matingAge)
            findMate(world);

        pregnancyCooldown--;
        pregnancyCooldown = Math.clamp(pregnancyCooldown, 0, 100);

        lookForFood(world);


        energy--;
        if(energy <= 0) {
            die(world);
        }
        age++;
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


    public void findMate(World world) {
        if (pregnancyCooldown > 0)
            return;

        List<Rabbit> possibleMates = new ArrayList<>();
        for (Location l : world.getSurroundingTiles(world.getLocation(this))) {
            if (world.getTile(l) instanceof Rabbit)
                possibleMates.add((Rabbit) world.getTile(l));
        }
        if (possibleMates.isEmpty()) return;

        Rabbit mate;
        if (possibleMates.size() == 1) {
            mate = possibleMates.get(0);
        }
        else {
            mate =  possibleMates.get(new Random().nextInt(possibleMates.size()));
        }

        if (mate.age < matingAge) return;
        if (mate.pregnancyCooldown > 0) return;

        // makes new rabbit
        Rabbit offSpring = new Rabbit();
        Location[] possibleSpawns = world.getEmptySurroundingTiles(world.getLocation(this)).toArray(new Location[0]);
        Location offSpringLocation = possibleSpawns[new Random().nextInt(possibleSpawns.length)];
        offSpring.updateOnMap(world, offSpringLocation, true);
        mate.pregnancyCooldown = 20;
        pregnancyCooldown = 20;
    }

    /*
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
    */

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

    public void digBurrow(World world) {
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
        if (burrowLocation != null && !isOnMap)
            updateOnMap(world, burrowLocation, true);
        canMate = true;
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
        updateOnMap(world, world.getLocation(this),false);
    }
    @Override
    public void almostNight(World world) {
        canMate = false;
        if(burrow == null) return;
        Location closestTile = getClosestTile(world, burrowLocation);
        if (closestTile == null) return;
        world.move(this, closestTile);
    }


}
