package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AnimalFlock {

    protected Animals flockLeader;
    CapableWorld world;
    private AnimalShelter shelter;

    //DisplayInformation displayInfo = new DisplayInformation(Color.cyan);

    protected final List<Animals> flockMembers = new ArrayList<>();
    protected final List<Animals> deadFlockMembers = new ArrayList<>();

    public AnimalFlock(CapableWorld world){
        this.world = world;

        flockLeader = null;
        shelter = null;
    }

    /* ----- ----- ----- ----- Shelter ----- ----- ----- -----*/

    /**
     * @param shelter
     * @throws NullPointerException
     */
    protected void flockShelterCreated(AnimalShelter shelter) {
        if (shelter == null) {
            throw new NullPointerException("AnimalShelter is null");
        }

        setFlockShelter(shelter);

    }

    private void setFlockShelter(AnimalShelter shelter){
        this.shelter = shelter;

        for (Animals animal : flockMembers) {
            if (animal instanceof FlockAnimal flockAnimal) {
                flockAnimal.setFlockShelter(shelter);
            }
        }
    }

    /* ----- ----- ----- ----- Flock Members ----- ----- ----- ----- */

    public void flockMemberDied(Animals animal) {
        if (animal == flockLeader) {
            if (!flockMembers.isEmpty()) {
                findNewFlockLeader();
            }
            else {
                System.out.println("A flock has gone extinct :(");
                world.delete(this);
            }
        }
        else {
            deadFlockMembers.add(animal);
        }
    }

    private void findNewFlockLeader() {
        setNewFlockLeader(flockMembers.getFirst());
        flockMembers.remove(flockLeader);
    }

    protected void setNewFlockLeader(Animals animal) {
        flockLeader = animal;
        communicateNewFlockLeader();
    }

    protected void communicateNewFlockLeader() {
        for (Animals animal : flockMembers) {
            if (animal instanceof FlockAnimal flockAnimal) {
                flockAnimal.newFlockLeader(flockLeader);
            }
        }
    }

    public void addNewFlockMember(Animals newFlockMember) {
        if (flockLeader == null) {
            setNewFlockLeader(newFlockMember);
        }
        else {
            flockMembers.add(newFlockMember);
        }

        // Sets the flock leader in the new member
        if (newFlockMember instanceof FlockAnimal flockAnimal) {
            flockAnimal.newFlockLeader(flockLeader);
            flockAnimal.setFlock(this);

            if (shelter != null) flockAnimal.setFlockShelter(shelter);
        }
    }

    protected void removeFlockMember(Animals newFlockMember) {
        flockMembers.remove(newFlockMember);
    }

    public void clearDeadFlockMembers() {
        deadFlockMembers.forEach(flockMembers::remove);
        deadFlockMembers.clear();
    }

    public boolean isMemberOfFlock(Animals animal) {
        return (flockMembers.contains(animal) || animal == flockLeader);
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----------*/

    public Location getShelterLocation() {
        return world.getLocation(shelter);
    }

    protected AnimalShelter getShelter() { return shelter; }



}
