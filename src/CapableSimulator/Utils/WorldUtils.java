package CapableSimulator.Utils;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.*;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class WorldUtils {

    World world;

    public WorldUtils(World world) {
        this.world = world;
    }

    /**
     * @param actorType Is the type of actor to search for.
     * @param onMap Determines whether to only search for objects currently on the map, or all objects in the world entity list.
     */
    public int getNumOfActors(String actorType, boolean onMap) {
        int numOfActors = 0;
        List<Object> actors;

        if (onMap)
            actors = getAllObjectOnWorldMap();
        else
            actors = new ArrayList<>(world.getEntities().keySet());

        for (Object o : actors) {
            if (o instanceof WorldActor actor && actor.getActorType().equals(actorType)) {
                numOfActors++;
            }
        }
        return numOfActors;
    }

    /**
     * @return Returns a list of all the objects currently on the world map.
     */
    public List<Object> getAllObjectOnWorldMap() {
        List<Object> actors = new ArrayList<>();
        Map<Object, Location> entities = world.getEntities();

        for (Object o : entities.keySet())
            if (entities.get(o) != null)
                actors.add(entities.get(o));

        return actors;
    }

    /**
     * @return Returns a list of all the animals in the world*/
    public List<Animal> getAllAnimals(){
        List<Animal> animals = new ArrayList<>();
        Map<Object, Location> entities = world.getEntities();
        for (Object actor : entities.keySet()) {
            if (actor instanceof Animal animal) animals.add(animal);
        }
        return animals;
    }

}
