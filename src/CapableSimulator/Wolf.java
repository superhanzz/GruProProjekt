package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Wolf extends Predator {

    //the pack that the wolf is part of
    //Set<Actor> wolfGang;
    WolfGang wolfGang;

    protected Wolf alpha;

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

    public Wolf(WolfGang wolfgang) {
        this.energy = 20;
        this.maxEnergy = 30;
        this.age = 0;
        this.wolfGang = wolfgang;

        this.animalSize = AnimalSize.BABY;
        this.animalState = AnimalState.AWAKE;

        setupDisplayInformations();
    }

    //Act method implemented from Actor, every step the wolf is updated and methods are called.
    @Override
    public void act(World world){

        if (wolfType == WolfType.ALPHA) {
            lookForFood(world, 2);
            wolfGang.alphaMoved(world.getLocation(this));

            doEverySimStep(world);
            return;
        }

        //lookForFood(world, 2);

    }

    private void doEverySimStep(World world){
        energy--;
        if(energy <= 0) this.die(world);
        age++;
        if (animalSize == AnimalSize.BABY && age > 10) animalSize = AnimalSize.ADULT;
    }

    public void followAlpha(Location location){

    }

    void setupDisplayInformations(){
        displayInformations.get(AnimalState.AWAKE).put(AnimalSize.BABY, new DisplayInformation(Color.pink, "wolf-small"));
        displayInformations.get(AnimalState.AWAKE).put(AnimalSize.ADULT, new DisplayInformation(Color.magenta, "wolf"));

        displayInformations.get(AnimalState.SLEEPING).put(AnimalSize.BABY, new DisplayInformation(Color.pink, "wolf-sleeping"));
        displayInformations.get(AnimalState.SLEEPING).put(AnimalSize.ADULT, new DisplayInformation(Color.magenta, "wolf-small-sleeping"));
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

    @Override
    public DisplayInformation getInformation() {
        return displayInformations.get(animalState).get(animalSize);
    }
}
