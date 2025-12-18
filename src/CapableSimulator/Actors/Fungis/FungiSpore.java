package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class FungiSpore extends WorldActor {

    protected World world;

    private final static int SPREAD_RADIUS = 3;
    /** */
    private final static double SPREAD_CHANCE = 0.1;

    private CapableEnums.FungiType fungiType;

    private static final Map<Class<? extends WorldActor>, CapableEnums.FungiType> carrierMap = new HashMap<>();
    static {
        carrierMap.put(Carcass.class, CapableEnums.FungiType.FUNGUS);
        carrierMap.put(Fungus.class, CapableEnums.FungiType.FUNGUS);
        carrierMap.put(Animal.class, CapableEnums.FungiType.CORDYCEP);
    }

    public FungiSpore(World world) {
        super("fungi_spore", world);
        this.world = world;

        fungiType = CapableEnums.FungiType.FUNGUS;
    }

    public FungiSpore(String actorType, World world) {
        super(actorType, world);
        this.world = world;
        if (actorType.equals("cordycep")) {
            fungiType = CapableEnums.FungiType.CORDYCEP;
        }
        else {
            fungiType = CapableEnums.FungiType.FUNGUS;
        }
    }

    @Override
    public void act(World world) {

    }

    @Override
    protected void doEverySimulationStep() {}

    /** Tries to spread the fungi onto other actors within a certain radius: "SPREAD_RADIUS" = {@value SPREAD_RADIUS} of the sporeLocation.
     *  For each non-infected actor found, then it tries to infect that with a probability of: "SPREAD_CHANCE" = {@value SPREAD_CHANCE} out of 1
     * @param sporeLocation is the location from where to spread from */
    public void spread(Location sporeLocation) {
        spread(sporeLocation, SPREAD_CHANCE);
    }

    /** Tries to spread the fungi onto other actors who can carry the same fungi type within a certain radius of the sporeLocation.
     * @param sporeLocation The location from where to spread from.
     * @param spreadChance The chance of successfully spreading to a specifik actor.
     */
    public void spread(Location sporeLocation, double spreadChance) {
        List<Fungi> surroundingFungiActors = getSurroundingFungiActors(sporeLocation, fungiType, SPREAD_RADIUS);
        List<Fungi> nonInfectedActors = new ArrayList<>();
        sortFungiActors(surroundingFungiActors, new ArrayList<>(), nonInfectedActors);

        Random rand = new Random();
        for (Fungi fungiActor : nonInfectedActors) {
            if (rand.nextDouble(1) < spreadChance) {
                fungiActor.becomeInfected();
                //System.out.println("Actor got infected");
            }
        }
    }

    /** Gets all the fungi actors around a location.
     * @param sporeLocation is the location from where the search occurs
     * @param radius is the radius to search within
     */
    private List<Fungi> getSurroundingFungiActors(Location sporeLocation, CapableEnums.FungiType wantedFungiType, int radius) {
        List<Fungi> surroundingFungiActors = new ArrayList<>();

        for (Location location : world.getSurroundingTiles(sporeLocation, radius)) {
            if (world.getTile(location) instanceof Fungi fungi) {
                if (fungi.isCarrierOfType(wantedFungiType))
                    surroundingFungiActors.add(fungi);
            }
        }

        return surroundingFungiActors;
    }

    /** Sorts a list af fungi actors into 2 lists, infected and non-infected.
     * @param fungiActors is the list to sort
     * @param infectedActors is the output list for the infected fungi actors
     * @param nonInfectedActors is the list for the non-infected fungi actors
     * */
    private void sortFungiActors(List<Fungi> fungiActors, List<Fungi> infectedActors, List<Fungi> nonInfectedActors) {
        for (Fungi fungi : fungiActors) {
            if (fungi.isInfected()) infectedActors.add(fungi);
            else  nonInfectedActors.add(fungi);
        }
    }

    /* ----- ----- Getters ----- ----- */

    public static double getSpreadChance() {
        return SPREAD_CHANCE;
    }

    public CapableEnums.FungiType getFungiType() {
        return fungiType;
    }

    public static CapableEnums.FungiType getCanCarryType(Class<? extends WorldActor> actorClass) {
        return carrierMap.get(actorClass);
    }

    @Override
    public int getEnergyValue() { return 0; }

    @Override
    public DisplayInformation getInformation() { return null; }
}
