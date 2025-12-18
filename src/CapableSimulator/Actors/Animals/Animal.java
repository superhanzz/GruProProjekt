package CapableSimulator.Actors.Animals;

import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.EnergyConsumer;
import CapableSimulator.Actors.Fungis.Cordycep;
import CapableSimulator.Actors.Fungis.CordycepSpore;
import CapableSimulator.Actors.Fungis.FungiSpore;
import CapableSimulator.Actors.Plants.BerryBush;
import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

public abstract class Animal extends WorldActor implements Cordycep, EnergyConsumer {

    /* ----- ----- ----- Energy variables ----- ----- ----- */

    /** The maximal amount of energy the animal can have.
     *  if the animal eats and the energy exceeds the maximal value, energy is clamped.
     * */
    private final int MAX_ENERGY;

    /** The energy variable is the variable that keeps track of the animal energy.
     *  If energy = 0, the animal dies at the end of the simulation step.
     *  When the animal eats , the energy is increased by the amount of energy stored in the eaten actor.
     *  */
    protected int energy;

    /** The animals age.
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

    private static final double MAX_INTERACT_DISTANCE = 1.0;

    private CapableEnums.AnimalSize animalSize;
    private CapableEnums.AnimalState animalState;
    private CapableEnums.FungiState fungiState;

    private static final int NORMAL_DECAY_RATE = 1;
    private static final int INFECTED_DECAY_RATE = 2;

    private boolean isOnMap;
    private boolean isDead = false;

    protected CordycepSpore fungiSpore;

    protected static final Map<String, List<String>> eatableFoodTypes = new HashMap<>();
    static {
        List<String> bearDiet = new ArrayList<>();
        bearDiet.add("rabbit");
        bearDiet.add("berry");
        bearDiet.add("carcass");

        List<String> wolfDiet = new ArrayList<>();
        wolfDiet.add("rabbit");
        wolfDiet.add("carcass");

        List<String> rabbitDiet = new ArrayList<>();
        rabbitDiet.add("grass");

        List<String> putinDiet = new ArrayList<>();
        putinDiet.add("rabbit");
        putinDiet.add("carcass");
        putinDiet.add("fungus");

        eatableFoodTypes.put("bear", bearDiet);

        eatableFoodTypes.put("wolf", wolfDiet);

        eatableFoodTypes.put("rabbit", rabbitDiet);

        eatableFoodTypes.put("putin", putinDiet);
    }

    /* ----- ----- ----- Constructors ----- ----- ----- */

    /** Default constructor.
     * @param actorType The actor type.
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal
     * @param age The animals starting age.
     * @param MAX_ENERGY The maximum amount of energy the animal can have.
     */
    public Animal(String actorType, World world, int energy, int age, int MAX_ENERGY) {
        super(actorType, world);
        // Non-statics
        animalSize = CapableEnums.AnimalSize.BABY;
        animalState = CapableEnums.AnimalState.AWAKE;
        fungiState = CapableEnums.FungiState.NORMAL;
        this.energy = energy;
        this.age = age;

        // Statics
        this.MAX_ENERGY = MAX_ENERGY;
        this.MATING_AGE = 20;
        this.MATING_COOLDOWN_DURATION = 20;
    }

    /** Constructor for testing.
     * @param actorType The actor type.
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal
     * @param age The animals starting age.
     * @param MAX_ENERGY The maximum amount of energy the animal can have.
     * @param MATING_AGE The required age for mating.
     * @param MATING_COOLDOWN_DURATION The required time (simulation steps) before the animal can reproduce again.
     */
    public Animal(String actorType, World world, int energy, int MAX_ENERGY, int age, int MATING_AGE, int MATING_COOLDOWN_DURATION) {
        super(actorType, world);
        // Non-statics
        animalSize = CapableEnums.AnimalSize.BABY;
        animalState = CapableEnums.AnimalState.AWAKE;
        fungiState = CapableEnums.FungiState.NORMAL;
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
        doEverySimulationStep();
    }

    public void move() {
        Set<Location> neighbours = world.getEmptySurroundingTiles(getLocation());
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


    public boolean lookForFood(int searchRadius){
        Location[] neighbours = world.getSurroundingTiles(getLocation(),searchRadius).toArray(new Location[0]);

        List<WorldActor> foodTiles = findFoodFromSource(neighbours);

        WorldActor eatableActor = getNearestActor(foodTiles);

        if(eatableActor != null){
            prepareToEat(eatableActor);
            return true;
        }
        return false;
    }

    public WorldActor getNearestActor(List<? extends WorldActor> actors) {
        double shortestDistance = Double.MAX_VALUE;
        WorldActor nearestActor = null;

        for (WorldActor actor : actors) {
            double distance = PathFinder.distance(getLocation(), world.getLocation(actor));
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
        else if (eatableActor instanceof BerryBush) {
            System.out.println(getActorType() + " trying to eat a BerryBush");
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
                if (eatableFoodTypes.get(getActorType()).contains(actor.getActorType())) {
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
    public Carcass becomeCarcass(Location location) {
        Carcass carcass = new Carcass(world, Math.max(energy, 10), animalSize);
        world.setTile(location, carcass);
        //System.out.println("Carcass has been created");
        return carcass;
    }



    public void animalJustReproduce() {
        matingCooldown = MATING_COOLDOWN_DURATION;
    }

    /** Handles when an animal dies. If the animal isn't infected by a fungi then it becomes a carcass, otherwise it just disappers.
     * @return Returns the animals carcass, if one is created.
     */
    public Carcass die() {
        Location location = getLocation();
        Carcass carcass = null;

        if (!isInfected()) {
            world.delete(this);
            carcass = becomeCarcass(location);
        }
        else {
            spreadSpores(world);
            world.delete(this);
        }
        isOnMap = false;
        isDead = true;
        return carcass;
    }

    public Location getLocation() {
        if (world == null) throw new NullPointerException("In getLocation(): World is null");
        if (!isOnMap) {
            System.out.println(this + " isn't on map");
            return null;
        }
        return world.getLocation(this);
    }

    /**
     * @param location
     * @param putOnMap
     * @throws NullPointerException if location is null & putOnMap is true*/
    public void updateOnMap(Location location, boolean putOnMap) {
        if(location == null && putOnMap) {
            throw new NullPointerException("In updateOnMap(): Location is null");
        }

        if (putOnMap) {
            if (world.isTileEmpty(location)) {
                world.setTile(location, this);
                isOnMap = true;
            }
        }
        else {
            if (!world.isOnTile(this))
                System.out.println(this + " isn't on map");

            world.remove(this);
            isOnMap = false;
        }
    }

    /** Moves the actor towards the given location.
     * @param location is the location to move towards.
     * @return Returns the new distance to the target location, if the actor couldn't find any free closer tile, returns 0.
     */
    protected double moveTowards(Location location) {
        Location moveTo = PathFinder.getMoveToTile(world, getLocation(), location);
        if (moveTo == null)
            return 0.0;

        world.move(this,  moveTo);
        return PathFinder.distance(getLocation(), moveTo);
    }

    /**
     * @param targetLocation
     * @return Returns true if animal moved close enough to interact with the target, otherwise returns false.
     */
    protected boolean moveNextToTarget(Location targetLocation) {
        double distanceFromTarget = PathFinder.distance(getLocation(), targetLocation);
        if (distanceFromTarget > getMaxInteractDistance()) {
            distanceFromTarget = moveTowards(targetLocation);
        }
        return (distanceFromTarget <= getMaxInteractDistance());
    }

    /* ----- ----- ----- ----- Fungi Related ----- ----- ----- ----- */

    @Override
    public void becomeInfected() {
        System.out.println("Animal becomes infected");
        fungiSpore = new CordycepSpore(world, this);
        fungiState = CapableEnums.FungiState.FUNGI;
    }

    @Override
    public boolean isInfected() {
        return (fungiSpore != null);
    }

    @Override
    public FungiSpore getFungiSpore() {
        return fungiSpore;
    }

    @Override
    public void spreadSpores(World world) {
        //System.out.println("Spreading spore in animal");
        if (!isInfected()) return;

        //fungiSpore.spread(getLocation(), 1.0);
    }

    @Override
    public boolean isCarrierOfType(CapableEnums.FungiType fungiType){
        return FungiSpore.getCanCarryType((Animal.class)).equals(fungiType);
    }
    /* ----- ----- ----- ----- Cordycep ----- ----- ----- ----- */

    @Override
    public void moveTowardsNonInfected(Animal animal) {
        moveTowards(animal.getLocation());
    }

    /* ----- ----- ----- ----- Energy Consumer Implementations ----- ----- ----- ----- */

    @Override
    public void doEverySimulationStep() {
        age++;
        if (!isOnMap()) {
            return;
        }

        energy -= getDecayRate();
        if(energy <= 0) die();

        // Handles child becoming adult
        if (animalSize.equals(CapableEnums.AnimalSize.BABY)) {
            if (age > 10) animalSize = CapableEnums.AnimalSize.ADULT;
        }

        // Updates the matingCooldown
        matingCooldown--;
        matingCooldown = Math.clamp(matingCooldown, 0, MATING_COOLDOWN_DURATION);
    }

    @Override
    public int getDecayRate() {
        if (isInfected()) {
            return INFECTED_DECAY_RATE;
        }
        return NORMAL_DECAY_RATE;
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


    /* ----- ----- ----- ----- Getters and setters ----- ----- ----- ----- */

    /** Method for getting the key to find the correct display information */
    protected String getDisplayInformationsKey() {
        String key = animalSize.label + "-" + fungiState.label + "-" + animalState.label;
        return key;
    }

    /** Set's the isDead variable to true.
     */
    protected void setDead() {
        isDead = true;
    }

    @Override
    public int getEnergyValue() {
        return energy;
    }

    /**
     * @return Returns the animals age.
     */
    public int getAge() {
        return age;
    }

    /** Gets the maximum distance from an actor before this actor can can interact with it.
     * @return returns MAX_INTERACT_DISTANCE: {@value MAX_INTERACT_DISTANCE}.
     */
    public double getMaxInteractDistance() {
        return MAX_INTERACT_DISTANCE;
    }

    /**
     * @return Returns the actor fungi state*/
    public CapableEnums.FungiState getFungiState() {
        return fungiState;
    }

    /** Retrieves the actors animal size.
     * @return Returns the actors animal size.
     */
    public CapableEnums.AnimalSize getAnimalSize() {
        return animalSize;
    }

    /** Updates the actors animal size.
     * @param animalSize The actors new animal size.
     */
    protected void setAnimalSize(CapableEnums.AnimalSize animalSize) {
        this.animalSize = animalSize;
    }

    /** Retrieves the actors animal state.
     * @return Returns the actors animal size.
     */
    public CapableEnums.AnimalState getAnimalState() {
        return animalState;
    }

    /** Updates the actors animal state.
     * @param animalState The actors new animal state.
     */
    protected void setAnimalState(CapableEnums.AnimalState animalState) {
        this.animalState = animalState;
    }

    /* ----- ----- ----- Boolean methods ----- ----- ----- */

    /** Checks all the criteria for mating.
     *  returns true if all the criteria are met, otherwise returns false
     */
    public boolean canMate() {
        boolean canReproduce = (
                matingCooldown <= 0 &&
                        age >= MATING_AGE &&
                        animalSize.equals(CapableEnums.AnimalSize.ADULT) &&
                        world.isDay());

        return canReproduce;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isOnMap() {
        return isOnMap;
    }

}
