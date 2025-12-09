package CapableSimulator.Actors;

import CapableSimulator.SpawningAgent;
import CapableSimulator.TileFinder;
import CapableSimulator.WorldUtils;
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
    protected WolfGang wolfGang;

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
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
        this.wolfGang = wolfgang;

        this.animalSize = AnimalSize.ADULT;
        this.animalState = AnimalState.AWAKE;
        this.wolfType = isAlpha ? WolfType.ALPHA : WolfType.NPC;

        hasSpecialMovementBehaviour = wolfType.equals(WolfType.NPC);

        setupDisplayInformations();
    }

    public Wolf(WolfGang wolfgang, Wolf alpha, WolfDen wolfDen, AnimalSize animalSize, AnimalState animalState) {
        super("wolf");
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;

        this.wolfGang = wolfgang;
        this.alpha = alpha;
        this.wolfDen = wolfDen;
        this.animalSize = animalSize;
        this.animalState = animalState;

        this.wolfType = WolfType.NPC;

        hasSpecialMovementBehaviour = true;

        setupDisplayInformations();
    }

    //Act method implemented from Actor, every step the wolf is updated and methods are called.
    @Override
    public void act(World world){
        if (dead) return;

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
                    lookForFood(1);
                }
            }
        }

        if (wolfType == WolfType.ALPHA && world.isDay() && isOnMap) {
            lookForFood(1);
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
        Map<String, Set<WorldActor>> allPossibleFoodActors = new WorldUtils(world).getAllWorldActorsAsMap(eatableFoodTypes.get(actorType));

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
                    double distance = distance(getLocation(), world.getLocation(actor));
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
                double distance = distance(getLocation(), world.getLocation(actor));
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
        if (!isOnMap || dead) return;

        Location wolfLocation = getLocation();
        double distance =distance(wolfLocation, alphaLocation);


        if (distance >= wolfGang.radiusAroundAlpha) { // if the wolf is within the allowed radius of the alpha
            //something
        }

        Location moveToLocation = getMoveToTile(world, wolfLocation, alphaLocation);
        if (moveToLocation != null) world.move(this, moveToLocation);
        else move(world);
        List<Predator> enemies = new ArrayList<>();
        if (lookForEnemy(enemies, 1)) {
            Wolf enemy = null;
            for (Predator p : enemies) {
                if (p instanceof Wolf w) {
                    enemy = w;
                }
            }
            if (enemy == null) return;

            System.out.println(this + " wolf is attacking");
            attackEnemy(world, enemy);
        }
        else lookForFood(1);
    }


    protected void attackEnemy(World world, Wolf enemy) {
        double winChance = 0.0;
        if (animalSize.equals(AnimalSize.ADULT)) {

            if (wolfType.equals(WolfType.ALPHA)) {
                if (enemy.isAlpha() && enemy.isAnimalAdult()) winChance = 0.5;
                else if (enemy.isAlpha() && !enemy.isAnimalAdult()) winChance = 0.75;
                else if (!enemy.isAlpha() && enemy.isAnimalAdult()) winChance = 0.75;
                else winChance = 1;
            }
            else {
                if (enemy.isAlpha() && enemy.isAnimalAdult()) winChance = 0.0;
                else if (enemy.isAlpha() && !enemy.isAnimalAdult()) winChance = 0.5;
                else if (!enemy.isAlpha() && enemy.isAnimalAdult()) winChance = 0.5;
                else winChance = 0.75;
            }

        }
        else return;

        if (new Random().nextDouble() < winChance) {
            kill(enemy);
        }
        else {
            makeCarcass(world);
        }
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
        Object nonBlocking = world.getNonBlocking(getLocation());
        if (nonBlocking != null) {
            if (nonBlocking instanceof Burrow || nonBlocking instanceof WolfDen) return;
            else world.delete(nonBlocking);
        }
        wolfDen = new WolfDen(wolfGang);
        new SpawningAgent(world).spawnActorAtLocation(wolfDen, getLocation());
        wolfGang.wolfDenCreated(world, wolfDen);
    }


    public void setWolfDen(WolfDen wolfDen) {
        if (wolfDen == null) {
            throw new NullPointerException("In SetWolfDen(): WolfDen is null");
        }
        if (this.wolfDen != null) {
            System.out.println("setWolfDen(): WolfDen is already set");
            return;
        }
        this.wolfDen = wolfDen;
    }

    protected void goIntoDen(World world) {
        if (wolfGang.denLocation == null) return;
        Location wolfLocation = getLocation();

        if (distance(wolfLocation, wolfGang.denLocation) <= 2 && isOnMap) {
            updateOnMap(world, wolfLocation, false);
            wolfDen.wolfEnteredDen(this);
        }
        else {
            Location moveToTile = getMoveToTile(world, wolfLocation, wolfGang.denLocation);
            if (moveToTile != null) world.move(this, moveToTile);
        }

        //if (isOnMap && world.getCurrentTime() == 19) System.out.println(distance(wolfLocation, wolfGang.denLocation));
    }

    protected void exitDen(World world) {
        if (wolfGang == null && wolfGang.denLocation == null) return;

        TileFinder tileFinder = new TileFinder(world);
        Location spawnAt = tileFinder.getEmptyTileAroundActor(wolfDen, true);
        if (spawnAt == null) return;

        updateOnMap(world, spawnAt, true);
        wolfDen.wolfLeftDen(this);
    }

    @Override
    public void die(World world) {
        wolfGang.wolfDied(this);
        world.delete(this);
        dead = true;
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

    /*@Override
    protected List<WorldActor> findFoodFromSource(World world, Location[] neighbours) {
        List<WorldActor> worldActorList = new ArrayList<>();
        for (Location location : neighbours) {
            Object o =  world.getTile(location);
            if (o instanceof Rabbit)
                worldActorList.add((WorldActor) o);
        }
        return worldActorList;
    }*/
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

    public WolfGang getWolfGang() {return wolfGang;}

    public boolean isAlpha() {return wolfType.equals(WolfType.ALPHA);}

    @Override
    public DisplayInformation getInformation() {
        DisplayInformation di = displayInformations.get(animalState).get(animalSize);

        if (wolfType == WolfType.ALPHA) {
            di = new DisplayInformation(Color.pink, animalState.equals(AnimalState.AWAKE) ? "alpha-wolf" : "alpha-wolf-sleeping");
        }

        return di;
    }

    @Override
    protected boolean isAnimalEnemy(Predator possibleEnemy) {
        if (possibleEnemy instanceof Wolf wolf) {
            return (wolf.getWolfGang() != this.wolfGang);
        }
        else if (possibleEnemy instanceof Bear){
            List<Wolf> nearbyWolfsFromGang = new ArrayList<>();
            wolfGang.getNearbyWolfsFromGang(this, nearbyWolfsFromGang);

            return nearbyWolfsFromGang.size() >= 3;
        }
        return false;
    }

}
