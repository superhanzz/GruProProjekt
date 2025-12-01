package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    //Default constructor for wolf, used in the actorConstructorRegistry
    public Wolf() {
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;

        this.animalSize = AnimalSize.BABY;
        this.animalState = AnimalState.AWAKE;

        setupDisplayInformations();
    }

    //Wolf constructor used for instantiating new wolves, with a pack as parameter.
    public Wolf(Set<Actor> wolfgang) {
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
        //this.wolfGang = wolfgang;

        this.animalSize = AnimalSize.BABY;
        this.animalState = AnimalState.AWAKE;

        setupDisplayInformations();
    }

    public Wolf(WolfGang wolfgang, boolean isAlpha) {
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

        if (wolfType == WolfType.ALPHA) {
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

    public void followAlpha(World world, Location alphaLocation) {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";

        Location wolfLocation = world.getLocation(this);
        double distance =distance(wolfLocation, alphaLocation);
        if (distance >= wolfGang.radiusAroundAlpha) { // if the wolf is within the allowed radius of the alpha
            //something
        }

        Location movementVector = getMovementVector(wolfLocation, alphaLocation);


        // all possible movements that where the wolf still moves towards the alpha or a target
        List<Integer> possibleMoves_X = new ArrayList<>();  // this list is ordered based on most ideal movement direction
        List<Integer> possibleMoves_Y = new ArrayList<>();  // this list is ordered based on most ideal movement direction

        // x-axis
        if (movementVector.getX() == 0) {       // only movement in the y-axis
            possibleMoves_X.add(0);
            possibleMoves_X.add(1);
            possibleMoves_X.add(-1);
        }
        else if (movementVector.getX() > 1) {   // movement in the x-axis is positive
            possibleMoves_X.add(1);
            possibleMoves_X.add(0);
        }
        else {  // movement in the x-axis is positive
            possibleMoves_X.add(-1);
            possibleMoves_X.add(0);
        }



        // y-axis
        if (movementVector.getY() == 0) {       // no movement in the y-axis
            possibleMoves_Y.add(0);
            possibleMoves_Y.add(1);
            possibleMoves_Y.add(-1);
        }
        else if (movementVector.getY() > 1) {   // movement in the y-axis is positive
            possibleMoves_Y.add(1);
            possibleMoves_Y.add(0);
        }
        else {  // movement in the y-axis is negative
            possibleMoves_Y.add(-1);
            possibleMoves_Y.add(0);
        }

        // Check which of the possibilities are free
        Location testMoveTo;
        Location moveToLocation = null;
        for (Integer dx : possibleMoves_X) {
            for (Integer dy : possibleMoves_Y) {
                int x = Math.clamp(wolfLocation.getX() + dx, 0, world.getSize() - 1);
                int y = Math.clamp(wolfLocation.getY() + dy, 0, world.getSize() - 1);

                testMoveTo = new Location(x, y);

                if (false){
                    System.out.println("(" + dx + "," + dy + ") -> (" + x + "," + y + ")\t\t Original distance was: " + distance);
                    double newDistance = distance(testMoveTo, alphaLocation);
                    System.out.println("\t\t\t\t\t New distance is: " + distance(testMoveTo, alphaLocation));

                    boolean isShorter = distance >= newDistance;
                    String truth = isShorter ? GREEN + "Shorter" + RESET : RED + "Longer" + RESET;
                    System.out.printf("\t\t\t\t\t " + truth + "%n");
                }


                //if (x > 9 || y > 9) throw new RuntimeException(x + "," + y + "\t not a valid tile");
                Object o = world.getTile(testMoveTo);
                if (o instanceof NonBlocking || !(o instanceof Animals)) {
                    moveToLocation = new Location(x, y); //locationAddition(testMoveTo, wolfLocation);
                    break;
                }
            }
            if (moveToLocation != null && moveToLocation != wolfLocation) break;
        }
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
        world.setTile(world.getLocation(this), wolfDen);
    }

    public void setWolfDen(WolfDen wolfDen) {
        this.wolfDen = this.wolfDen ==  null ? wolfDen : this.wolfDen;
    }

    protected void goIntoDen(World world) {
        if (distance(getLocation(world), wolfGang.denLocation) < 2) {
            //world.remove(this);

        }
        //else ()
    }

    @Override
    public void die(World world) {
        wolfGang.wolfDied(this);
        world.delete(this);
    }

    @Override
    public void onDay(World world) {
        animalState = AnimalState.AWAKE;
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
