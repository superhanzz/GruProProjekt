package CapableSimulator.Utils;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.*;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class WorldUtils {

    /**Method for getting total number of Actors
     * @param world
     * @param actorType Is the type of actor to search for.
     * @param onMap Determines whether to only search for objects currently on the map, or all objects in the world entity list.
     * @return Returns the number of the given actor type found.
     */
    public static int getNumOfActors(World world, String actorType, boolean onMap) {
        int numOfActors = 0;
        List<Object> actors;

        if (onMap)
            actors = getAllObjectOnWorldMap(world);
        else
            actors = new ArrayList<>(world.getEntities().keySet());

        for (Object o : actors) {
            if (o instanceof WorldActor actor && actor.getActorType().equals(actorType)) {
                numOfActors++;
            }
        }
        return numOfActors;
    }

    /**Getting all objects currently placed on World
     * @param world
     * @return Returns a list of all the objects currently on the world map.
     */
    public static List<Object> getAllObjectOnWorldMap(World world) {
        List<Object> actors = new ArrayList<>();
        Map<Object, Location> entities = world.getEntities();

        for (Object o : entities.keySet())
            if (entities.get(o) != null)
                actors.add(o);

        return actors;
    }

    /**Method for getting a List of all Animals
     * @param world
     * @return Returns a list of all the animals in the world
     */
    public static List<Animal> getAllAnimals(World world) {
        List<Animal> animals = new ArrayList<>();
        Map<Object, Location> entities = world.getEntities();
        for (Object actor : entities.keySet()) {
            if (actor instanceof Animal animal) animals.add(animal);
        }
        return animals;
    }

    /**Method for getting other Actor shortest distance away
     * @param world The
     * @*/
    public static WorldActor getNearestActor(World world, WorldActor instigator, List<? extends WorldActor> actors) {
        double shortestDistance = Double.MAX_VALUE;
        WorldActor nearestActor = null;

        for (WorldActor actor : actors) {
            double distance = PathFinder.distance(world.getLocation(instigator), world.getLocation(actor));
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestActor = actor;
            }
        }
        return nearestActor;
    }

}
