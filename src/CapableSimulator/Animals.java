package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
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
        Random rand = new Random();
        Location searchLocation = neighbours[rand.nextInt(neighbours.length)];
        while(!world.isTileEmpty(searchLocation)){
            searchLocation = neighbours[rand.nextInt(neighbours.length)];
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
            move(world);
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

    double distance(Location A, Location B) {
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
