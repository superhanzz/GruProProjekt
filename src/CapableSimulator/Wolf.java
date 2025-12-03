package CapableSimulator;

import FunctionLibrary.CapableFunc;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Wolf extends Predator {

    //the pack that the wolf is part of
    //Set<Actor> wolfGang;
    WolfGang wolfGang;

    protected Wolf alpha;
    protected WolfDen wolfDen;

    protected WolfType wolfType;

    protected enum WolfType {
        ALPHA,
        NPC;
    }

    /* Sets the actors type */
    static {  }

    //Default constructor for wolf, used in the actorConstructorRegistry
    public Wolf() {
        super("wolf");
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;

        this.animalSize = AnimalSize.BABY;
        this.animalState = AnimalState.AWAKE;

        //actorType = "wolf";

        setupDisplayInformations();
    }

    //Wolf constructor used for instantiating new wolves, with a pack as parameter.
    public Wolf(Set<Actor> wolfgang) {
        super("wolf");
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
        //this.wolfGang = wolfgang;

        this.animalSize = AnimalSize.BABY;
        this.animalState = AnimalState.AWAKE;

        setupDisplayInformations();
    }

    public Wolf(WolfGang wolfgang, boolean isAlpha) {
        super("wolf");
        //this.energy = isAlpha ? 20 : 100;
        this.energy = 100;
        this.maxEnergy = 30;
        this.age = 0;
        this.wolfGang = wolfgang;

        this.animalSize = AnimalSize.ADULT;
        this.animalState = AnimalState.AWAKE;
        this.wolfType = isAlpha ? WolfType.ALPHA : WolfType.NPC;

        hasSpecialMovementBehaviour = wolfType.equals(WolfType.NPC);

        setupDisplayInformations();
    }

    //Act method implemented from Actor, every step the wolf is updated and methods are called.
    @Override
    public void act(World world){
        if (world.isNight()) {
            if (isOnMap) goIntoDen(world);
            else {

            }
        }
        else {
            //if (alpha == null) System.out.println(wolfType.toString());
            if (!isOnMap) {
                exitDen(world);
            }
            else {
                if (!alpha.isOnMap) {
                    lookForFood(world, 1);
                }
            }
        }

        if (wolfType == WolfType.ALPHA && world.isDay() && isOnMap) {
            lookForFood(world, 1);
            wolfGang.alphaMoved(world, world.getLocation(this));


        }
        //doEverySimStep(world);

        //lookForFood(world, 2);


    }

    private void doEverySimStep(World world){
        energy--;
        if(energy <= 0) this.die(world);
        age++;
        if (animalSize == AnimalSize.BABY && age > 10) animalSize = AnimalSize.ADULT;
    }

    // alpha can see whole map TODO
    private void alphaSight(World world) {
        // Gets references to all the possible food sources
        Map<String, Set<WorldActor>> allPossibleFoodActors = CapableFunc.getAllWorldActorsAsMap(world, eatableFoodTypes.get(actorType));

        if (false) {    // Debug
            System.out.println(allPossibleFoodActors.size());
            for (String at : allPossibleFoodActors.keySet()) {
                System.out.println("Number of " + at + " in the world: " + allPossibleFoodActors.get(at).size());
            }
            System.out.println();
        }

        // Finds the closest one
        if (false) {
            double shortestDistance = Double.MAX_VALUE;
            WorldActor nearestFoodActor = null;

            // This is dumb because there is no priority in the food source
            for (String actorType : allPossibleFoodActors.keySet()) {
                for (WorldActor actor : allPossibleFoodActors.get(actorType)) {
                    double distance = distance(getLocation(world), world.getLocation(actor));
                    if (distance < shortestDistance) {
                        shortestDistance = distance;
                        nearestFoodActor = actor;
                    }
                }
            }
        }


        // smart because it is categorised
        record PreyDist(WorldActor actor, double distance) {}   //

        Map<String, PreyDist> nearestPreyActors = new HashMap<>();
        Map<String, Double> distances = new HashMap<>();

        for (String actorType : allPossibleFoodActors.keySet()) {
            distances.put(actorType, Double.MAX_VALUE);
            for (WorldActor actor : allPossibleFoodActors.get(actorType)) {
                double distance = distance(getLocation(world), world.getLocation(actor));
                if (distance < distances.get(actorType)) {
                    distances.put(actorType, distance);
                    nearestPreyActors.put(actorType, new PreyDist(actor, distance));
                }
            }
        }

        for (String at : eatableFoodTypes.get(actorType)) {

        }


    }

    public void followAlpha(World world, Location alphaLocation) {
        if (!isOnMap) return;

        Location wolfLocation = world.getLocation(this);
        double distance =distance(wolfLocation, alphaLocation);


        if (distance >= wolfGang.radiusAroundAlpha) { // if the wolf is within the allowed radius of the alpha
            //something
        }

        Location moveToLocation = getMoveToTile(world, wolfLocation, alphaLocation);
        if (moveToLocation != null) world.move(this, moveToLocation);
        else move(world);
        lookForFood(world, 1);
    }


    public void setAlpha(Wolf wolf) {
        this.alpha = wolf;
        hasSpecialMovementBehaviour = false;
    }

    public void promoteToAlpha() {
        wolfType = WolfType.ALPHA;
        wolfGang.setNewAlpha(this);
        setAlpha(alpha);
    }


    private void digWolfDen(World world) {
        wolfDen = new WolfDen(wolfGang);
        Object nonBlocking = world.getNonBlocking(getLocation(world));
        if (nonBlocking != null) world.delete(nonBlocking);
        world.setTile(world.getLocation(this), wolfDen);
        wolfGang.wolfDenCreated(world, wolfDen);
    }

    public void setWolfDen(WolfDen wolfDen) {
        this.wolfDen = this.wolfDen ==  null ? wolfDen : this.wolfDen;
    }

    protected void goIntoDen(World world) {
        if (wolfGang.denLocation == null) return;
        Location wolfLocation = getLocation(world);

        if (distance(wolfLocation, wolfGang.denLocation) <= 2 && isOnMap) {
            updateOnMap(world, wolfLocation, false);
            wolfDen.wolfEnteredDen(this);
        }
        else {
            Location moveToTile = getMoveToTile(world, wolfLocation, wolfGang.denLocation);
            if (moveToTile != null) world.move(this, moveToTile);
        }

        if (isOnMap && world.getCurrentTime() == 19) System.out.println(distance(wolfLocation, wolfGang.denLocation));

    }

    protected void exitDen(World world) {
        if (wolfGang == null && wolfGang.denLocation == null) return;

        List<Location> possibleLocations = new ArrayList<>(world.getEmptySurroundingTiles(wolfGang.denLocation));
        if (possibleLocations.isEmpty()) {
            //System.out.println("No tiles found");
            return;
        }
        Location apperAt = possibleLocations.get(new Random().nextInt(possibleLocations.size()));
        updateOnMap(world, apperAt, true);
        wolfDen.wolfLeftDen(this);
    }

    @Override
    public void die(World world) {
        wolfGang.wolfDied(this);
        world.delete(this);
    }

    @Override
    public void onDay(World world) {
        animalState = AnimalState.AWAKE;
        if (!isOnMap) exitDen(world);
    }

    @Override
    public void onNight(World world) {
        animalState = AnimalState.SLEEPING;
    }

    @Override
    public void almostNight(World world) {
        if (wolfType.equals(WolfType.ALPHA) && wolfDen == null) {
            digWolfDen(world);
        }
        if (wolfDen != null) {
            //System.out.println("go to den");
        }
    }

    @Override
    protected List<WorldActor> findFoodFromSource(World world, Location[] neighbours) {
        List<WorldActor> worldActorList = new ArrayList<>();
        for (Location location : neighbours) {
            Object o =  world.getTile(location);
            if (o instanceof Rabbit)
                worldActorList.add((WorldActor) o);
        }
        return worldActorList;
    }
/*
    //Method to make the wolf move around looking for food, in this case Objects of the type Rabbit.
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
    */



    void setupDisplayInformations(){
        displayInformations.get(AnimalState.AWAKE).put(AnimalSize.BABY, new DisplayInformation(Color.pink, "wolf-small"));
        displayInformations.get(AnimalState.AWAKE).put(AnimalSize.ADULT, new DisplayInformation(Color.magenta, "wolf"));

        displayInformations.get(AnimalState.SLEEPING).put(AnimalSize.BABY, new DisplayInformation(Color.pink, "wolf-small-sleeping"));
        displayInformations.get(AnimalState.SLEEPING).put(AnimalSize.ADULT, new DisplayInformation(Color.magenta, "wolf-sleeping"));
    }

    @Override
    public DisplayInformation getInformation() {
        DisplayInformation di = displayInformations.get(animalState).get(animalSize);

        if (wolfType == WolfType.ALPHA) {
            di = new DisplayInformation(Color.pink, animalState.equals(AnimalState.AWAKE) ? "alpha-wolf" : "alpha-wolf-sleeping");
        }

        return di;
    }
}
