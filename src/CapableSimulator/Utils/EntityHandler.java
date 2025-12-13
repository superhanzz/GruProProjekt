package CapableSimulator.Utils;

import CapableSimulator.Actors.Animals;
import CapableSimulator.Actors.Wolf;
import CapableSimulator.Actors.WorldActor;
import FunctionLibrary.CapableFunc;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHandler {

    private World world;

    private final Map<String, List<WorldActor>> worldActorContainer = new HashMap<>();

    public EntityHandler(World world) {
        this.world = world;
    }

    public void initWorldActorContainer(Map<String, List<WorldActor>> tempMap){
        if  (tempMap == null || tempMap.isEmpty()) {
            if (tempMap == null) System.out.println("tempMap is null");
            else System.out.println("tempMap is empty");
            return;
        }
        for (String actorType : tempMap.keySet()) {
            worldActorContainer.put(actorType, tempMap.get(actorType));
        }
        for (String actorType : CapableFunc.getAllWorldActorTypes()) {
            if (!worldActorContainer.containsKey(actorType)) {
                worldActorContainer.put(actorType, new ArrayList<>());
                //System.out.println("actorType: " + actorType);
            }
        }

        //System.out.println("Entries in worldActorContainer: " + worldActorContainer.size());
    }

    /** Updates the worldActorContainer, should be called at each simulation step.
     *  Iterates though each entity in the world, and checks if the entity is already is in the worldActorContainer
     *  if it isn't then it is added.
     *  If the world isn't set then it returns.
     * */
    public void updateWorldActorContainer() {
        if (world == null) return;

        // Handels new actors in the world
        Map<Object, Location> worldEntities = world.getEntities();
        for (Object e : worldEntities.keySet()) {
            if(!(e instanceof WorldActor actor)) continue;
            String actorType = actor.getActorType();

            if (actorType == null) throw new NullPointerException("actorType is null in actor: " + actor);

            if(worldActorContainer.containsKey(actorType)) {
                List<WorldActor> list = worldActorContainer.get(actorType);
                if (!(list.contains(actor))) {
                    list.add(actor);
                    //System.out.println("Added actor of type: " + actorType + " to world actor container");
                }
            }
        }

        // Handles the flagging of deleted actors.
        List<WorldActor> removeActorList = getWorldDeletedActors(worldEntities);

        // Debug message
        if (!removeActorList.isEmpty()) {
            System.out.println("Num of actors to remove: " + removeActorList.size());
        }

        for (WorldActor actor : removeActorList) {
            if (actor instanceof Wolf wolf) {
                //wolf.getWolfGang().removeWolfFromGang(wolf);
            }
        }

        // Removes the actors in removeActorList from worldActorContainer
        removeActorList.forEach(worldActor -> {
            if (worldActor instanceof Animals animal) {
                System.out.println("Removing actor: " + animal.actorType + ", with energy: " + animal.getEnergyValue());
            }
            worldActorContainer.get(worldActor.getActorType()).remove(worldActor);
        });
    }

    /** Findes all the worldActors that no longer exist in the world's entity list */
    private List<WorldActor> getWorldDeletedActors(Map<Object, Location> worldEntities) {
        List<WorldActor> deletedActors = new ArrayList<>();
        for (String actorType : worldActorContainer.keySet()) {
            for (WorldActor actor : worldActorContainer.get(actorType)) {
                if (!worldEntities.containsKey(actor)) {
                    deletedActors.add(actor);
                    //System.out.println("Actor to be removed: " + actor.toString());
                }
            }
        }
        return deletedActors;
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    public Map<String, List<WorldActor>> getWorldActorContainer() {
        return worldActorContainer;
    }

    public boolean containsActor(String actorType) {
        return (!worldActorContainer.get(actorType).isEmpty());
    }
}
