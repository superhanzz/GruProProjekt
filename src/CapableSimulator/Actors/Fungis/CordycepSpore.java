package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.Animals.Animal;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CordycepSpore extends FungiSpore{

    private Animal host;

    public CordycepSpore(World world, Animal host) {
        super("cordycep", world);
        this.host = host;
    }

    @Override
    public void act(World world) {
        if (host.getNearestActor(lookForNonInfectedOfSameType()) instanceof Animal animal) {
            host.moveTowardsNonInfected(animal);
        }
    }

    @Override
    protected void doEverySimulationStep() {}

    private List<Animal> lookForNonInfectedOfSameType() {
        List<Animal> animalsOfSameType = new ArrayList<>();
        Map<Object, Location> entities = world.getEntities();
        for (Object o : entities.keySet()) {
            if (entities.get(o) != null && (o.getClass() == host.getClass())) {
                Animal animal = (Animal) o;
                if (!animal.isInfected())
                    animalsOfSameType.add(animal);
            }
        }
        return animalsOfSameType;
    }


}
