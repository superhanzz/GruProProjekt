package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public abstract class Animals extends WorldActor {

    /* ----- ----- ----- Energy variables ----- ----- ----- */

    /** The maximal amount of energy the animal can have.
     *  if the animal eats and the energy exceeds the maximal value, energy is clamped.
     * */
    private final int MAX_ENERGY;

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
    private final int MATING_AGE;

    /** The cooldown period after an animal has mated.
     *  It goes down by one after each simulation step
     * */
    private int matingCooldown;

    /** What the matingCooldown is set to after a successful mating has occurred.
     * Set's the cooldown in both parrents.
     * Default is set to 20 in Animal class
     * */
    private final int MATING_COOLDOWN_DURATION;

    public boolean dead = false;

    protected CapableEnums.AnimalState animalState;
    protected CapableEnums.AnimalSize animalSize;

    private  boolean hasSpecialMovementBehaviour;
    boolean isOnMap;

    protected static final Map<String, List<String>> eatableFoodTypes = new HashMap<>();
    static {
        List<String> bearDiet = new ArrayList<>();
        bearDiet.add("rabbit");
        bearDiet.add("wolf");
        bearDiet.add("berry");
        bearDiet.add("carcass");

        List<String> wolfDiet = new ArrayList<>();
        wolfDiet.add("rabbit");
        wolfDiet.add("carcass");

        List<String> rabbitDiet = new ArrayList<>();
        rabbitDiet.add("grass");

        eatableFoodTypes.put("bear", bearDiet);

        eatableFoodTypes.put("wolf", wolfDiet);

        eatableFoodTypes.put("rabbit", rabbitDiet);
    }

    /* ----- ----- ----- Constructors ----- ----- ----- */

    /** Default constructor
     * */
    public Animals(String actorType, CapableWorld world, int energy, int age, int MAX_ENERGY) {
        super(actorType, world);

        // Non-statics
        this.energy = energy;
        this.age = age;

        // Statics
        this.MAX_ENERGY = MAX_ENERGY;
        this.MATING_AGE = 20;
        this.MATING_COOLDOWN_DURATION = 20;
    }

    /** A constructor where matingAge and MATING_COOLDOWN_DURATION can be specified
     * */
    public Animals(String actorType, CapableWorld world, int energy, int MAX_ENERGY, int age, int MATING_AGE, int MATING_COOLDOWN_DURATION) {
        super(actorType, world);

        // Non-statics
        this.energy = energy;
        this.age = age;

        // Statics
        this.MAX_ENERGY = MAX_ENERGY;
        this.MATING_AGE = MATING_AGE;
        this.MATING_COOLDOWN_DURATION = MATING_COOLDOWN_DURATION;
    }

    /* ----- ----- ----- ----- Behavior ----- ----- ----- ----- */

    @Override
    public void act(World world) {
        doEverySimStep();
    }

    public void move(World world) {
        Location[] neighbours = getPossibleFoodTiles(1);
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

    protected void doEverySimStep() {
        if (world.isNight()) {
            return;
        }

        energy--;
        if(energy <= 0) die(world);
        age++;

        // Handles child becoming adult
        if (animalSize.equals(CapableEnums.AnimalSize.BABY)) {
            if (age > 10) animalSize = CapableEnums.AnimalSize.ADULT;
        }

        // Updates the matingCooldown
        matingCooldown--;
        matingCooldown = Math.clamp(matingCooldown, 0, MATING_COOLDOWN_DURATION);
    }






    public void lookForFood(int searchRadius){
        Location[] neighbours = world.getSurroundingTiles(getLocation(),searchRadius).toArray(new Location[0]);

        List<WorldActor> foodTiles = findFoodFromSource(neighbours);

        WorldActor eatableActor = getNearestActor(foodTiles);

        if(eatableActor != null){
            prepareToEat(eatableActor);
        }else {
            if (!getHasSpecialMovementBehaviour()) move(world);
        }
    }

    protected WorldActor getNearestActor(List<WorldActor> actors) {
        double shortestDistance = Double.MAX_VALUE;
        WorldActor nearestActor = null;

        for (WorldActor actor : actors) {
            double distance = distance(getLocation(), world.getLocation(actor));
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestActor = actor;
            }
        }
        return nearestActor;
    }

    protected void prepareToEat(WorldActor eatableActor) {
        if (eatableActor instanceof NonBlocking) {
            Location goTo = world.getLocation(eatableActor);
            if (world.isTileEmpty(goTo)) {
                world.move(this, goTo);
                eat(eatableActor);
            }
        }
        else if  (eatableActor instanceof BerryBush) {
            System.out.println(actorType + " trying to eat a BerryBush");
        }
        else {
            eat(eatableActor);
        }
    }

    protected void eat(WorldActor actor){
        if (actor instanceof Carcass carcass) {
            int gainedEnergy = carcass.getConsumed(world, (MAX_ENERGY - energy));
            energy += gainedEnergy;
        }
        else if (actor instanceof BerryBush berryBush) {
            energy = berryBush.getEaten();
        }
        else {
            energy += actor.getEnergyValue();
            world.delete(actor);
        }
    }

    // findFoodFromSources
    protected List<WorldActor> findFoodFromSource(Location[] neighbours) {
        List<WorldActor> foodSources = new ArrayList<>();

        for (Location location : neighbours) {
            Object o =  world.getTile(location);
            if (o instanceof WorldActor actor){
                if (eatableFoodTypes.get(actorType).contains(actor.actorType)) {
                    if (actor instanceof BerryBush berryBush) {
                        if (berryBush.getBerryStatus())
                            foodSources.add(actor);
                    }
                    else {
                        foodSources.add(actor);
                    }
                }
            }
        }
        return foodSources;
    }

    /* ----- ----- ----- World Related ----- ----- ----- */

    /** Converts the dead animal to a carcass
     *  @return A reference to the newly created carcass
     */
    public Carcass becomeCarcass() {
        Location location = getLocation();
        die(world);
        Carcass carcass = new Carcass(world, Math.max(energy, 10), animalSize);
        world.setTile(location, carcass);
        System.out.println("Carcass has been created");

        return carcass;
    }

    /** Checks all the criteria for mating.
     *  returns true if all the criteria are met, otherwise returns false
     */
    public boolean canMate() {
        boolean canReproduce = (
                        matingCooldown <= 0 &&
                        age >= MATING_AGE &&
                        world.isDay());

        return canReproduce;
    }

    public void animalJustReproduce() {
        matingCooldown = MATING_COOLDOWN_DURATION;
    }

    private void becomeFungi() {

    }

    public void die(World world) {
        world.delete(this);
        dead = true;
    }

    public Location getLocation() {
        if (world == null) throw new NullPointerException("In getLocation(): World is null");
        if (!isOnMap) {
            System.out.println(this + " isn't on map");
            return null;
        }
        return world.getLocation(this);
    }

    public void updateOnMap(Location location, boolean putOnMap) {
        if(location == null) {
            throw new NullPointerException("In updateOnMap(): Location is null");
        }


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

    /* ----- ----- ----- ----- Events ----- ----- ----- ----- */

    /** onDay() is an event that is executed when the world's current time is 0.
     * */
    public abstract void onDawn();

    /** almostNight() is an event that is executed  when the world's current time is 9.
     * */
    public abstract void onDusk();

    /** onNight() is an event that is executed when the world's current time is 10.
     * */
    public abstract void onNightFall();


    /* ----- ----- ----- ----- PATHFINDING ----- ----- ----- ----- */

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

    protected void getPossibleMovesForAxis(int axis, List<Integer> possibleMovesList) {
        if (axis == 0) {       // no movement on the given axis
            possibleMovesList.add(0);
            possibleMovesList.add(1);
            possibleMovesList.add(-1);
        }
        else if (axis > 1) {   // movement in the given axis is positive
            possibleMovesList.add(1);
            possibleMovesList.add(0);
        }
        else {  // movement in the given axis is negative
            possibleMovesList.add(-1);
            possibleMovesList.add(0);
        }
    }

    protected Location getMoveToTile(World world, Location fromLocation, Location goalLocation) {
        // DEBUG COLORS FOR printf
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";

        Location movementVector = getMovementVector(fromLocation, goalLocation);

        // all possible movements that where the wolf still moves towards the alpha or a target
        List<Integer> possibleMoves_X = new ArrayList<>();  // this list is ordered based on most ideal movement direction
        List<Integer> possibleMoves_Y = new ArrayList<>();  // this list is ordered based on most ideal movement direction

        // x-axis
        getPossibleMovesForAxis(movementVector.getX(), possibleMoves_X);
        getPossibleMovesForAxis(movementVector.getY(), possibleMoves_Y);

        //
        List<Location> moveToLocations = new ArrayList<>();
        // Check which of the possibilities are free
        Location testMoveTo;
        Location moveToLocation = null;
        for (Integer dx : possibleMoves_X) {
            for (Integer dy : possibleMoves_Y) {
                int x = Math.clamp(fromLocation.getX() + dx, 0, world.getSize() - 1);
                int y = Math.clamp(fromLocation.getY() + dy, 0, world.getSize() - 1);

                testMoveTo = new Location(x, y);

                /*if (false){
                    System.out.println("(" + dx + "," + dy + ") -> (" + x + "," + y + ")\t\t Original distance was: " + distance);
                    double newDistance = distance(testMoveTo, alphaLocation);
                    System.out.println("\t\t\t\t\t New distance is: " + distance(testMoveTo, alphaLocation));

                    boolean isShorter = distance >= newDistance;
                    String truth = isShorter ? GREEN + "Shorter" + RESET : RED + "Longer" + RESET;
                    System.out.printf("\t\t\t\t\t " + truth + "%n");
                }*/
                //if (x > 9 || y > 9) throw new RuntimeException(x + "," + y + "\t not a valid tile");


                Object o = world.getTile(testMoveTo);
                if (o == null || o instanceof NonBlocking) {
                    moveToLocations.add(testMoveTo);
                }
                /*
                if (o instanceof NonBlocking || !(o instanceof Animals)) {
                    moveToLocations.add(testMoveTo);
                }
                */
            }
        }
        if (!moveToLocations.isEmpty()) {
            double shortestDistance = Double.MAX_VALUE;
            for (Location location : moveToLocations) {
                double distance = distance(location, goalLocation);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    moveToLocation = location;
                }
            }
        }
        return moveToLocation;
    }

    public Location getMovementVector(Location start, Location end) {
        int x = end.getX() - start.getX();
        int y = end.getY() - start.getY();
        return new Location(x, y);
    }

    protected Location[] getPossibleFoodTiles(int searchRadius) {
        return world.getSurroundingTiles(world.getLocation(this),searchRadius).toArray(new Location[0]);
    }

    public Location locationAddition(Location A, Location B) {
        int x = A.getX() + B.getX();
        int y = A.getY() + B.getY();
        return new Location(x, y);
    }

    /* ----- ----- ----- ----- Getters and setters ----- ----- ----- ----- */

    /** Method for getting the key to find the correct display information */
    protected String getDisplayInformationsKey() {
        String key = animalSize.label + "-" + fungiState.label + "-" + animalState.label;
        return key;
    }

    public boolean isAnimalAdult() {return (animalSize.equals(CapableEnums.AnimalSize.ADULT));}

    @Override
    public int getEnergyValue() {
        return energy;
    }

    protected boolean getHasSpecialMovementBehaviour() {return false; }
    //protected void setHasSpecialMovementBehaviour(boolean hasSpecialMovementBehaviour) { this.hasSpecialMovementBehaviour = hasSpecialMovementBehaviour; }
}
