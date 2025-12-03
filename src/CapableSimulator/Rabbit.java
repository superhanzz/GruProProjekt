package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Rabbit extends Animals {

    /* ----- ----- ----- Burrow variables ----- ----- ----- */

    /** The reference to the rabbits burrow */
    private Burrow burrow;

    /** The Location of the rabbits burrow */
    private Location burrowLocation;

    DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");


    /* ----- ----- ----- Constructors ----- ----- ----- */

    /**
     * The default constructor for the rabbit class.
     * This is the constructor to use when making the simulation.
     * */
    public Rabbit() {
        super("rabbit");
        this.energy = 15;
        this.maxEnergy = 20;
        this.age = 0;
        this.matingCooldown = 0;
        this.canMate = true;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }

    /**
     * A constructor where the rabbits starting energy can be defined, mostly for testing purposes.
     * */
    public Rabbit(int energy) {
        super("rabbit");
        this.energy = energy;
        this.age = 0;
        //this.matingAge = 10;
        this.matingCooldown = 0;
        this.canMate = true;
        //this.MATING_COOLDOWN_DURATION = 20;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }

    /**
     * A constructor where the age required before mating can occur and how long before mating can occur again can be defined.
     * */
    public Rabbit(int matingAge, int MATING_COOLDOWN_DURATION) {
        super("rabbit", matingAge, MATING_COOLDOWN_DURATION);

        this.age = 0;
        this.energy = 10;
        this.matingCooldown = 0;
        this.canMate = true;

        burrow = null;
        burrowLocation = null;
        isOnMap = true;
    }

    /* ----- ----- ----- Behavior ----- ----- ----- */

    /** Implementation of act() method from the Actor interface.
     *  Executes once per simulation step.
     *  Most of the behavior occurs within this method.
     * */
    @Override
    public void act(World world) {
        if(world.isNight() || !isOnMap)
            return;

        if(canMate && age >= matingAge)
            findMate(world);

        matingCooldown--;
        matingCooldown = Math.clamp(matingCooldown, 0, 100);

        //lookForGrass(world);
        lookForFood(world, 2);


        energy--;
        if(energy <= 0) {
            die(world);
        }
        age++;
    }

    /** A method for testing if
     * */
    public void testEatGrass(World world) {
        Grass foundGrass = searchTile(world, world.getLocation(this));
        if (foundGrass != null) {
            eatGrass(world, foundGrass);
        }
    }



    @Override
    protected List<WorldActor> findFoodFromSource(World world, Location[] neighbours) {
        List<WorldActor> worldActorList = new ArrayList<>();
        for (Location location : neighbours) {
            Object o =  world.getTile(location);
            if (o instanceof Grass)
                worldActorList.add((WorldActor) o);
        }
        return worldActorList;
    }


    /** Tries to find food.
     *  First, checks if the rabbit is standing on grass. //TODO
     *  If it is not standing on grass then it tries to find grass on surrounding empty tiels.
     *  If grass is found on one or more surrounding tiles, then a random tile is chosen and moved to.
     * */
    private void lookForGrass(World world) {
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

        Grass foundGrass = searchTile(world, world.getLocation(this));
        if (foundGrass != null) {
            eatGrass(world, foundGrass);
        }
    }

    /** Tries to find a mate to reproduce
     *  If more than one possible mate is found, then a random one is chosen.
     *  If the chosen mate is eligible to reproduce, then a offspring is created.
     *  The offspring is inserted into the world at a free surrounding tile, around the instigating rabbit
     *  If no free tile is found the offspring dies, but the mating is still counted as a success. TODO
     *  // TODO when the mate is chosen, set a variable in the other mate to make sure that they dont try and mate with another rabbit in the same turn.
     * */
    public void findMate(World world) {
        if (matingCooldown > 0)
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
        if (mate.matingCooldown > 0) return;

        // makes new rabbit
        Rabbit offSpring = new Rabbit();
        Location[] possibleSpawns = world.getEmptySurroundingTiles(world.getLocation(this)).toArray(new Location[0]);
        Location offSpringLocation = possibleSpawns[new Random().nextInt(possibleSpawns.length)];
        offSpring.updateOnMap(world, offSpringLocation, true);
        mate.matingCooldown = 20;
        matingCooldown = 20;
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

    /** A method that checks if the tile at the given location has grass.
     *  If there is grass on the tile, then a reference to the grass is returned.
     *  If there is no grass then null is returned.
     * */
    private Grass searchTile(World world, Location location) {
        Object nonBlockingObject = world.getNonBlocking(location);
        return nonBlockingObject instanceof Grass ? (Grass) nonBlockingObject : null;
    }

    /** Handles eating grass i.e. increases the rabbits energy and deletes the grass actor from the world
     * TODO make the grass deleting happen in the grass actor insted of here, and make the grass return the amount of energy it provides
     * */
    private void eatGrass(World world,  Grass grass) {
        world.delete(grass);
        energy += 2;
        energy = Math.clamp(energy, 0, maxEnergy);
    }

    /** Handles the rabbit upon death i.e. deletes it from the world */


    /** Handels the digging of a burrow, and connecting the rabbit to the burrow.
     * */
    public void digBurrow(World world) {
        Object standingOn = world.getNonBlocking(world.getLocation(this));
        if (standingOn != null) {
            world.delete(standingOn);
        }

        burrow = new Burrow(this);
        world.setTile(world.getLocation(this), burrow);
        burrowLocation = world.getLocation(this);

    }

    /** TODO make this method handle ribbits entering their burrow */
    void goIntoBurrow(World world) {

    }


    /** The implementation of the animal method onDay()
     *  Handles the events linked to day break i.e.
     *  Enabling mating again, and the rabbit appearing on the map again.
     * */
    @Override
    public void onDay(World world) {
        if(burrow == null) {
            System.out.println("Burrow is null");
            return;
        }

        if (burrowLocation != null && !isOnMap)
            updateOnMap(world, burrowLocation, true);
        canMate = true;
    }

    /** The implementation of the animal method onNight()
     *  Handles the events linked to nightfall i.e.
     *  The rabbit going into it's burrow if it has one, otherwise it tries to find a nearby burrow.
     *  If no nearby burrow is found, then it tries to dig one.
     * */
    @Override
    public void onNight(World world) {
        if (burrow == null) {
            for (Location l : world.getSurroundingTiles(world.getLocation(this))) {
                if (world.getNonBlocking(l) instanceof Burrow) {
                    burrow = (Burrow) world.getNonBlocking(l);
                    burrowLocation = world.getLocation(burrow);
                    break;
                }
            }
            if (burrow == null) digBurrow(world);
        }
        updateOnMap(world, world.getLocation(this),false);
    }

    /** The implementation of the animal method almostNight()
     *  Handles the events linked to it almost being night i.e.
     *  Making the rabbits go towards their burrow if they have one.
     *  Disabling mating until daybreak.
     * */
    @Override
    public void almostNight(World world) {
        canMate = false;

        if(burrow == null) return;

        Location closestTile = getClosestTile(world, burrowLocation);

        if (closestTile == null) {
            System.out.println("Rabbit could not find a free tile around it's burrow.");    // error message if no empty tile was found around its burrow
            //TODO kill rabbit if no tile was found, burrow might be full
            return;
        }

        world.move(this, closestTile);
    }




    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- ----- */

    /** Returns the rabbits burrow reference */
    public Burrow getBurrow() { return burrow; }

    /** Returns the rabbits burrows location */
    public Location getBurrowLocation() { return burrowLocation; }

    /** Returns the location of the rabbit */
    public Location getLocation(World world) { return world.getLocation(this); }

    @Override
    public DisplayInformation getInformation() {
        return diRabbit;
    }

}
