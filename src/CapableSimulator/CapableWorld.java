package CapableSimulator;

import CapableSimulator.Actors.AnimalFlock;
import CapableSimulator.Actors.WolfDen;
import CapableSimulator.Actors.WolfGang;
import CapableSimulator.Actors.WorldActor;
import CapableSimulator.Utils.CapableEnums;
import FunctionLibrary.CapableFunc;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapableWorld extends World {

    private final Map<String, List<WorldActor>> sortedEntities = new HashMap<>();
    private final ArrayList<AnimalFlock> flocks = new ArrayList<>();

    /* ----- Time Specific Events ----- */
    private static final int time_Dawn = 0;
    private static final int time_Dusk = 9;
    private static final int time_NightFall = 10;
    private static final int time_Midnight = 14;



    public CapableWorld(int size) {
        super(size);

        initSortedEntities();
    }

    public CapableEnums.DayNightStatus getDayNightStatus() {
        CapableEnums.DayNightStatus status;

        switch (getCurrentTime()) {
            case time_Dawn:
                status = CapableEnums.DayNightStatus.DAWN;
                break;
            case time_Dusk:
                status = CapableEnums.DayNightStatus.DUSK;
                break;
            case time_NightFall:
                status = CapableEnums.DayNightStatus.NIGHT;
                break;
            case time_Midnight:
                status = CapableEnums.DayNightStatus.MIDNIGHT;
                break;
            default:
                status = CapableEnums.DayNightStatus.DAY;
        }
        return status;
    }

    /* ----- ----- ----- ----- Data Containers ----- ----- ----- ----- */

    /** Initiates the sortedEntities map, which means adding an entry for each of the WorldActor types,
     *  and instantiating the ArrayList */
    private void initSortedEntities() {
        for (String key : CapableFunc.getAllWorldActorTypes()) {
            sortedEntities.put(key, new ArrayList<>());
        }
    }

    /** Adds the worldActor to the sortedEntities map, under the actors actorType */
    private void addToSortedEntities(WorldActor actor) {
        sortedEntities.get(actor.getActorType()).add(actor);
    }

    /** Removes the actor from the array associated with the actorType */
    private void removeFromSortedEntities(WorldActor actor) {
        sortedEntities.get(actor.getActorType()).remove(actor);
    }

    public void addAnimalFlock(AnimalFlock flock) {
        flocks.add(flock);
    }

    public void removeAnimalFlock(AnimalFlock flock) {
        flocks.remove(flock);
    }

    /* ----- ----- ----- ----- Entity List Related Overrides ----- ----- ----- ----- */

    @Override
    public void setTile(Location location, Object object) {
        super.setTile(location, object);
        //if (contains(object)) return;

        if (object instanceof WorldActor actor) {
            addToSortedEntities(actor);
        }
    }

    @Override
    public void add(Object object) {
        super.add(object);

        if (object instanceof WorldActor actor) {
            addToSortedEntities(actor);
        }
    }

    @Override
    public void remove(Object object) {
        super.remove(object);

        if (object instanceof WorldActor actor) {
            removeFromSortedEntities(actor);
        }
    }

    @Override
    public void delete(Object object) {
        super.delete(object);

        if (object instanceof WorldActor actor) {
            removeFromSortedEntities(actor);
        }
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    public Map<String, List<WorldActor>> getSortedEntities() {
        return sortedEntities;
    }

    public ArrayList<AnimalFlock> getFlocks() {
        return flocks;
    }
}
