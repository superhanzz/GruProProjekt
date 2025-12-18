package CapableSimulator.Actors.Shelter;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Animals.Rabbit;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Burrow extends AnimalShelter implements NonBlocking {

    private final List<Rabbit> inhabbitors = new ArrayList<>();


    private DisplayInformation diBurrow = new DisplayInformation(Color.blue, "hole-small");

    public Burrow(World world){
        super("burrow", world);
    }

    @Override
    public void act(World world) {

    }

    @Override
    public void animalEnteredShelter(Animal animal) {
        if (animal instanceof Rabbit rabbit) {
            super.animalEnteredShelter(animal);
            addBurrowInhabitant(rabbit);
        }
    }

    public void addBurrowInhabitant(Rabbit rabbit){
        inhabbitors.add(rabbit);
    }

    public void removeBurrowInhabitant(Rabbit rabbit){
        inhabbitors.remove(rabbit);
    }


    /* ----- ----- ----- Getters & Setters ----- ----- ----- */

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return diBurrow;
    }
}
