package CapableSimulator.Actors.Shelter;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.WorldActor;
import CapableSimulator.CapableWorld;
import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimalShelter extends WorldActor {

    private Location location;

    private final List<Animal> animalsInShelter = new ArrayList<>();

    public AnimalShelter(String actorType, CapableWorld world) {
        super(actorType, world);
    }

    /* ----- ----- ----- Tracking animals in shelter ----- ----- ----- */

    public void animalEnteredShelter(Animal animal){
        animalsInShelter.add(animal);
    }

    public void animalLeftShelter(Animal animal){
        animalsInShelter.remove(animal);
    }

    /* ----- ----- ----- Getters & Setters ----- ----- ----- */

    /** Get the amount of animals in the shelter.
     * @return Returns the amount of animals in the shelter.
     */
    public int getNumOfAnimalsInShelter(){
        return animalsInShelter.size();
    }

    /** Gets all the animmals in the shelter.
     * @return Returns all the animals currently in the shelter. */
    public List<Animal> getAnimalsInShelter(){
        return animalsInShelter;
    }

    /** Set's the world location of the shelter, should be called after shelter is placed in the world.
     * @throws IllegalArgumentException if the shelter hasn't been placed onto the world map.
     */
    public void setShelterLocation(){
        location = world.getLocation(this);
    }

    /** Get the world location of the shelter.
     * @return Returns the world location of the shelter.
     * @throws NullPointerException if shelter location hasn't been set.
     */
    public Location getShelterLocation(){
        if (location == null)
            throw new NullPointerException("Shelter location is null");
        return location;
    }
}
