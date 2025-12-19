package CapableSimulator.Actors.Animals;

import CapableSimulator.Actors.Shelter.AnimalShelter;

import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimalFlock {

    protected Animal flockLeader;
    protected World world;
    private AnimalShelter shelter;

    //DisplayInformation displayInfo = new DisplayInformation(Color.cyan);

    protected final List<Animal> flockMembers = new ArrayList<>();
    protected final List<Animal> deadFlockMembers = new ArrayList<>();

    public AnimalFlock(World world){
        this.world = world;

        flockLeader = null;
        shelter = null;
    }

    /* ----- ----- ----- ----- Flock Things ----- ----- ----- -----*/

    /**
     * */
    public double getFlockRadius(){
       double radius = 0.0;
       Location flockCenter = flockLeader.getLocation();
        PathFinder pathFinder = new PathFinder(world);

       double radiusSum = 0.0;
       for (Animal animal : flockMembers){
           radiusSum += pathFinder.distance(flockCenter, animal.getLocation());
       }
       radius = (radiusSum / ((getNumOfMembersInFlock() - 1) * 1.0));

       return radius;
    }

    /* ----- ----- ----- ----- Shelter ----- ----- ----- -----*/

    /**Method for determining if shelter is created before setting it
     * @param shelter
     * @throws NullPointerException
     */
    public void flockShelterCreated(AnimalShelter shelter) {
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
        if (getFlockMembers().isEmpty() && animal == flockLeader) {
            killFlock();
        }
        else if (getFlockMembers().isEmpty()) {
            // Do nothing
        }
        else if (animal == flockLeader) {
            findNewFlockLeader();
        }
        else {
            deadFlockMembers.add(animal);
        }
    }

    private void killFlock() {
        return;
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
        deadFlockMembers.forEach(this::removeFlockMember);
        deadFlockMembers.clear();
    }

    public boolean isMemberOfFlock(Animal animal) {
        return (flockMembers.contains(animal) || animal == flockLeader);
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----------*/

    public Location getShelterLocation() {
        return world.getLocation(shelter);
    }

    public AnimalShelter getShelter() { return shelter; }

    public List<Animal> getFlockMembers() {
        List<Animal> members = new ArrayList<>();
        members.addAll(flockMembers);
        members.removeAll(deadFlockMembers);

        return members;
    }

    public int getNumOfMembersInFlock() {
        return (flockMembers.size() + 1);
    }



}
