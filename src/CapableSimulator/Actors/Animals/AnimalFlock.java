package CapableSimulator.Actors.Animals;

import CapableSimulator.Actors.Shelter.AnimalShelter;
import CapableSimulator.CapableWorld;
import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimalFlock {

    protected Animal flockLeader;
    CapableWorld world;
    private AnimalShelter shelter;

    //DisplayInformation displayInfo = new DisplayInformation(Color.cyan);

    protected final List<Animal> flockMembers = new ArrayList<>();
    protected final List<Animal> deadFlockMembers = new ArrayList<>();

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

        for (Animal animal : flockMembers) {
            if (animal instanceof FlockAnimal flockAnimal) {
                flockAnimal.setFlockShelter(shelter);
            }
        }
    }

    /* ----- ----- ----- ----- Flock Members ----- ----- ----- ----- */

    public void flockMemberDied(Animal animal) {
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

    protected void setNewFlockLeader(Animal animal) {
        flockLeader = animal;
        communicateNewFlockLeader();
    }

    protected void communicateNewFlockLeader() {
        for (Animal animal : flockMembers) {
            if (animal instanceof FlockAnimal flockAnimal) {
                flockAnimal.newFlockLeader(flockLeader);
            }
        }
    }

    public void addNewFlockMember(Animal newFlockMember) {
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

    protected void removeFlockMember(Animal newFlockMember) {
        flockMembers.remove(newFlockMember);
    }

    public void clearDeadFlockMembers() {
        deadFlockMembers.forEach(flockMembers::remove);
        deadFlockMembers.clear();
    }

    public boolean isMemberOfFlock(Animal animal) {
        return (flockMembers.contains(animal) || animal == flockLeader);
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----------*/

    public Location getShelterLocation() {
        return world.getLocation(shelter);
    }

    protected AnimalShelter getShelter() { return shelter; }



}
