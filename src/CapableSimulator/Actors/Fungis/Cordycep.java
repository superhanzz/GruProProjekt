package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.Animals.Animal;

public interface Cordycep extends Fungi{
    /** Interfaces with the movement logik, to move the host towards a non-infected actor
     * @param animal Reference to the animal that is to be moved towards.
     */
    void moveTowardsNonInfected(Animal animal);
}
