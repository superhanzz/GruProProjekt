package CapableSimulator.Actors;


import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldActor implements Actor, DynamicDisplayInformationProvider {

    private final String actorType;

    protected World world;

    /** Default constructor.
     * @param actorType The actors type.
     * @param world The world wherein the actor exists.
     */
    protected WorldActor(String actorType, World world) {
        this.actorType = actorType;
        this.world = world;
    }

    /** Gets the actors actorType.
     * @return Returns the actors actorType.
     */
    public String getActorType() {return actorType;}

    /** Retrieves the actors energy values.
     * @return Returns the energy actors variable if it has one, otherwise just a static value.
     */
    public abstract int getEnergyValue();

    /**
     * @return Returns the location of the actor if it is on the map, if it isn't returns null.
     * @throws NullPointerException Throws exception if world is null.
     */
    public Location getLocation() {
        if (world == null)
            throw new NullPointerException("In getLocation(): World is null");

        if (world.isOnTile(this))
            return world.getLocation(this);
        else
            return null;
    }


    public static List<String> getAllWorldActorTypes() {
        List<String> actorTypes = getAllBlockingActorTypes();
        actorTypes.add("grass");
        actorTypes.add("burrow");
        actorTypes.add("wolfDen");
        actorTypes.add("putinEgg");
        return actorTypes;
    }

    public static List<String> getAllBlockingActorTypes() {
        List<String> actorTypes = getAllAnimalTypes();
        actorTypes.add("berry");
        actorTypes.add("carcass");
        actorTypes.add("fungus");
        actorTypes.add("fertilTile");
        return actorTypes;
    }

    public static List<String> getAllAnimalTypes() {
        List<String> actorTypes = getAllPredatorTypes();
        actorTypes.add("rabbit");
        return actorTypes;
    }

    public static List<String> getAllPredatorTypes() {
        List<String> actorTypes = new ArrayList<>();
        actorTypes.add("bear");
        actorTypes.add("wolf");
        actorTypes.add("putin");
        actorTypes.add("bertin");
        return actorTypes;
    }
}
