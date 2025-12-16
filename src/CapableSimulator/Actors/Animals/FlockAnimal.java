package CapableSimulator.Actors.Animals;

import CapableSimulator.Actors.Shelter.AnimalShelter;

public interface FlockAnimal {

    void setFlock(AnimalFlock flock);

    void newFlockLeader(Animal newLeader);

    void setFlockShelter(AnimalShelter shelter);

    AnimalShelter getShelter();
}
