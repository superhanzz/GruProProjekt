package CapableSimulator.Utils;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.*;
import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Putin;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Plants.BerryBush;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.Actors.Shelter.Burrow;
import CapableSimulator.Actors.Shelter.WolfDen;
import CapableSimulator.CapableWorld;
import itumulator.world.Location;

import java.util.*;

public class WorldUtils {

    CapableWorld world;

    public WorldUtils(CapableWorld world) {
        this.world = world;
    }

    public int getNumOfActors(String actorType) {
        int numOfActors = 0;
        Object[] actors = world.getEntities().keySet().toArray(new Object[0]);

        switch (actorType){
            case "grass":
                for(Object actor : actors){
                    if(actor instanceof Grass) numOfActors++;
                }
                break;
            case "rabbit":
                for(Object actor : actors){
                    if(actor instanceof Rabbit) numOfActors++;
                }
                break;
            case "burrow":
                for(Object actor : actors){
                    if(actor instanceof Burrow) numOfActors++;
                }
                break;
            case "wolf":
                for(Object actor : actors){
                    if(actor instanceof Wolf) numOfActors++;
                }
                break;
            case "bear":
                for(Object actor : actors){
                    if(actor instanceof Bear) numOfActors++;
                }
                break;
            case "berry":
                for(Object actor : actors){
                    if(actor instanceof BerryBush) numOfActors++;
                }
                break;
        }

        return numOfActors;
    }



    public List<Animal> getAllAnimals(){
        List<Animal> animals = new ArrayList<>();
        Map<String , Set<WorldActor>> worldActors = getAllWorldActorsAsMap(getAllAnimalTypes(),true);
        for (String actorType : worldActors.keySet()) {
            for (WorldActor actor : worldActors.get(actorType)) {
                if (actor instanceof Animal) {
                    animals.add((Animal) actor);
                }
            }
        }
        return animals;
    }

    /** Return a map consisting of actorTypes and sets of all the WorldActors in the world, with no filter
     * */
    public Map<String, Set<WorldActor>> getAllWorldActorsAsMap() {
        return getAllWorldActorsAsMap(null, false);
    }

    public Map<String, Set<WorldActor>> getAllWorldActorsAsMap(List<String> filter) {
        return getAllWorldActorsAsMap(filter, false);
    }

    public Map<String, Set<WorldActor>> getAllWorldActorsAsMap(boolean onlyActorsOnMap) {
        return getAllWorldActorsAsMap(null, onlyActorsOnMap);
    }

    /** Return a map consisting of actorTypes and sets of all the WorldActors in the world, filtered
     * */
    public Map<String, Set<WorldActor>> getAllWorldActorsAsMap(List<String> filter, boolean onlyActorsOnMap) {
        Map<String, Set<WorldActor>> allActorsInWorld = new HashMap<>();

        // Prepares the return map
        for(String actorType : getAllWorldActorTypes()){

            // filters if there is a filter
            if (filter == null || filter.contains(actorType)) {
                allActorsInWorld.put(actorType, new HashSet<>());
                //System.out.println(actorType);
            }
        }

        Map<Object, Location> Actors = world.getEntities();

        for(Object actor : Actors.keySet()){
            String actorType = "";

            if (Actors.get(actor) != null || !onlyActorsOnMap) {

                /* If the filter is not empty the actors actorType is retrieved */
                if (filter != null) {
                    if (actor instanceof WorldActor) {
                        actorType = ((WorldActor) actor).getActorType();
                        //System.out.println(actor.toString() + ": " + actorType);
                    }
                }

                /* filters the actors if there is a filter */
                if (filter == null || filter.contains(actorType)) {
                    //if (filter != null) System.out.println(actorType);

                    switch (actor) {
                        case Wolf w -> allActorsInWorld.get("wolf").add(w);
                        case Rabbit r -> allActorsInWorld.get("rabbit").add(r);
                        case Bear b -> allActorsInWorld.get("bear").add(b);
                        case Grass g -> allActorsInWorld.get("grass").add(g);
                        case BerryBush b -> allActorsInWorld.get("berryBush").add(b);
                        case Burrow b -> allActorsInWorld.get("burrow").add(b);
                        case Putin p -> allActorsInWorld.get("putin").add(p);
                        case WolfDen w -> allActorsInWorld.get("wolfDen").add(w);
                        default -> {
                            break;
                        }
                    }
                }
            }
        }
        //System.out.println("Number of food sources: " + allActorsInWorld.size());
        //System.out.println();
        return  allActorsInWorld;
    }




    /* ----- ----- ----- ----- ----- ----- ----- */
    // TODO find the correct place for these methods

    /* Not the final place for this */
    public static List<String> getAllWorldActorTypes() {
        List<String> actorTypes = new ArrayList<>();
        actorTypes.add("grass");
        actorTypes.add("rabbit");
        actorTypes.add("bear");
        actorTypes.add("berry");
        actorTypes.add("wolf");
        actorTypes.add("burrow");
        actorTypes.add("wolfDen");
        actorTypes.add("putin");
        return actorTypes;
    }
    /* Not the final place for this */
    public static List<String> getAllAnimalTypes() {
        List<String> actorTypes = new ArrayList<>();
        actorTypes.add("rabbit");
        actorTypes.add("bear");
        actorTypes.add("wolf");
        actorTypes.add("putin");
        return actorTypes;
    }

}
