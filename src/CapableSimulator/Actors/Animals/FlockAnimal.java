package CapableSimulator.Actors.Animals;

import CapableSimulator.Actors.Shelter.AnimalShelter;

public interface FlockAnimal {

    /** Sets the animal flock variabel */
    void setFlock(AnimalFlock flock);

    /** Updates the flock leader variabel, if the old flock leader dies */
    void newFlockLeader(Animal newLeader);

    /** Sets the flock shelter variabel */
    void setFlockShelter(AnimalShelter shelter);

    /* Retrieves the reference to the flock shelter */
    AnimalShelter getShelter();
}
