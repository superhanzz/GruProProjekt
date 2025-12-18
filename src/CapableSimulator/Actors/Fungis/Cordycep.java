package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.WorldActor;

public interface Cordycep extends Fungi{
    void moveTowardsNonInfected(Animal animal);
}
