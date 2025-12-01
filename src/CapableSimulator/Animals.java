package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public abstract class Animals extends WorldActor {

    /* ----- ----- ----- Energy variables ----- ----- ----- */

    /** The maximal amount of energy the animal can have.
     *  if the animal eats and the energy exceeds the maximal value, energy is clamped.
     * */
    protected int maxEnergy;

    /** The energy variable is the variable that keeps track of the animal energy.
     *  If energy = 0, the animal dies at the end of the simulation step.
     *  When the animal eats , the energy is increased by the amount of energy stored in the eaten actor. //TODO
     *  */
    protected int energy;

    /** The animals age.
     *  The age controls the maximal energy the animal can have    //TODO
     * */
    protected int age;

    /* ----- ----- ----- Mating variables ----- ----- ----- */

    /** Defines the age of the animal before it can mate.
     * Default is set to 10 in Animal class
     * */
    protected final int matingAge;

    /** The animal should not be able to mate during nights, this keeps that from happening.
     * */
    protected boolean canMate;

    /** The cooldown period after an animal has mated.
     *  It goes down by one after each simulation step
     * */
    protected int matingCooldown;

    /** What the matingCooldown is set to after a successful mating has occurred.
     * Set's the cooldown in both parrents.
     * Default is set to 20 in Animal class
     * */
    protected final int MATING_COOLDOWN_DURATION;


    protected AnimalState animalState;
    protected AnimalSize animalSize;


    protected enum AnimalState {
        AWAKE,
        SLEEPING;
    }

    protected enum AnimalSize {
        BABY,
        ADULT;
    }


    protected static final Map<AnimalState, Map<AnimalSize, DisplayInformation>> displayInformations = new HashMap<>();
    static {
        displayInformations.put(AnimalState.AWAKE, new HashMap<>());
        displayInformations.put(AnimalState.SLEEPING, new HashMap<>());
    }


    public String actorType;
    protected  boolean hasSpecialMovementBehaviour;
    boolean isOnMap;

    static final Map<String, List<String>> eatableFoodTypes = new HashMap<>();
    static {
        List<String> bearDiet = new ArrayList<>();
        bearDiet.add("wolf");
        bearDiet.add("rabbit");
        bearDiet.add("berry");

        List<String> wolfDiet = new ArrayList<>();
        wolfDiet.add("rabbit");

        List<String> rabbitDiet = new ArrayList<>();
        rabbitDiet.add("grass");

        eatableFoodTypes.put("bear", bearDiet);

        eatableFoodTypes.put("wolf", wolfDiet);

        eatableFoodTypes.put("rabbit", rabbitDiet);
    }


    private static final Map<CapableSim.ActorTypes, Class<? extends Actor>> actorClassTypes = new HashMap<>();
    static {
        actorClassTypes.put(CapableSim.ActorTypes.GRASS, Grass.class); // Her bliver actor typen Grass placeret inde i mappet, hvor Grass peger på Grass.class
        actorClassTypes.put(CapableSim.ActorTypes.RABBIT, Rabbit.class);
        actorClassTypes.put(CapableSim.ActorTypes.BURROW, Burrow.class);
        actorClassTypes.put(CapableSim.ActorTypes.WOLF, Wolf.class);

    }




    /** Default constructor
     * */
    public Animals() {
        this.matingAge = 20;
        this.MATING_COOLDOWN_DURATION = 20;
    }

    /** A constructor where matingAge and MATING_COOLDOWN_DURATION can be specified
     * */
    public Animals(int matingAge, int MATING_COOLDOWN_DURATION) {
        this.matingAge = matingAge;
        this.MATING_COOLDOWN_DURATION = MATING_COOLDOWN_DURATION;
    }

    public record Vector(int x, int y) {}




    protected Location[] getPossibleFoodTiles(World world, int searchRadius) {
        return world.getSurroundingTiles(world.getLocation(this),searchRadius).toArray(new Location[0]);
    }

    public void move(World world) {
        Location[] neighbours = getPossibleFoodTiles(world, 1);
        List<Location> emptyNeighbours = new ArrayList<>();
        for (Location neighbour : neighbours) {
            if (world.getTile(neighbour) == null || world.getTile(neighbour) instanceof NonBlocking) emptyNeighbours.add(neighbour);
        }
        if (emptyNeighbours.isEmpty()) return;
        Random rand = new Random();
        Location searchLocation = emptyNeighbours.get(rand.nextInt(emptyNeighbours.size()));
        while(!world.isTileEmpty(searchLocation)){
            searchLocation = emptyNeighbours.get(rand.nextInt(emptyNeighbours.size()));
        }
        world.move(this, searchLocation);
    }


    public void lookForFood(World world, int searchRadius){
        Location[] neighbours = world.getSurroundingTiles(world.getLocation(this),searchRadius).toArray(new Location[0]);
        List<WorldActor> foodTiles = findFoodFromSource(world, neighbours);

        if(!foodTiles.isEmpty()){
            WorldActor eatableActor = foodTiles.get(new Random().nextInt(foodTiles.size()));
            eat(world, eatableActor);
        }else
            if (!hasSpecialMovementBehaviour) move(world);
    }

    // findFoodFromSource
    protected abstract List<WorldActor> findFoodFromSource(World world, Location[] neighbours);


    protected void eat(World world, WorldActor actor){
        this.energy += actor.getEnergyValue();
        Location goTo = world.getLocation(actor);
        world.delete(actor);
        world.move(this, goTo);
    }




    @Override
    public void act(World world) {

    }

    public void onDay(World world) {

    }

    public void onNight(World world) {

    }

    public void almostNight(World world) {

    }

    public Location getLocation(World world) {
        return (isOnMap ? world.getLocation(this) : null);
    }

    public Location getMovementVector(Location start, Location end) {
        int x = end.getX() - start.getX();
        int y = end.getY() - start.getY();
        return new Location(x, y);
    }

    /*public Location getMoveToLocation(Location start, Location end) {
        Location moveToLocation = null;
        Location movementVector = null;
        int moveX = 0;
        int moveY = 0;

        if (Math.abs(start.getX()) == Math.abs(end.getX())) {

            switch (new Random().nextInt(1)) {
                case 0: //

            }
        }
        if (Math.abs(start.getX()) < Math.abs(end.getX())) {

        }

        int minElement = Math.min(end.getX(), end.getY());
        // to move take end + start
        minElement == 0

        return moveToLocation;
    }*/

    public Location locationAddition(Location A, Location B) {
        int x = A.getX() + B.getX();
        int y = A.getY() + B.getY();
        return new Location(x, y);
    }


    public Location getClosestTile(World world, Location tileLocation) {
        Set<Location> tiles = world.getEmptySurroundingTiles(tileLocation);
        if (tiles.isEmpty()) return null;

        Location source = world.getLocation(this);
        Location shortestTile = new Location(1000000, 1000000);

        for (Location l : tiles) {
            if (distance(source, l) < distance(source, shortestTile))
                shortestTile = l;
        }

        return shortestTile;
    }

    public double distance(Location A, Location B) {
        Location distanceVector = new Location((A.getX() - B.getX()),(A.getY() - B.getY()));
        double distance = Math.sqrt(Math.pow(distanceVector.getX(), 2) + Math.pow(distanceVector.getY(), 2));
        return distance;
    }

    public void die(World world) {
        world.delete(this);
    }

    public void updateOnMap(World world, Location location, boolean putOnMap) {
        if (putOnMap) {
            if (world.isTileEmpty(location)) {
                world.setTile(location, this);
                isOnMap = true;
            }
        }
        else {
            world.remove(this);
            isOnMap = false;
        }
    }


    @Override
    protected int getEnergyValue() {
        return energy;
    }
}
