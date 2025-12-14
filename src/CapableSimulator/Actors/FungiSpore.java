package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FungiSpore extends WorldActor {

    private final static int SPREAD_RADIUS = 3;
    /** */
    private final static float SPREAD_CHANCE = 0.1f;

    public FungiSpore(CapableWorld world) {
        super("fungi_spore", world);
    }

    @Override
    public void act(World world) {

    }

    /** Tries to spread the fungi onto other actors within a certain radius: "SPREAD_RADIUS" = {@value SPREAD_RADIUS} of the sporeLocation.
     *  For each non-infected actor found, then it tries to infect that with a probability of: "SPREAD_CHANCE" = {@value SPREAD_CHANCE} out of 1
     * @param sporeLocation is the location from where to spread from */
    public void spread(Location sporeLocation) {
        List<Fungi> surroundingFungiActors = getSurroundingFungiActors(sporeLocation, SPREAD_RADIUS);
        List<Fungi> nonInfectedActors = new ArrayList<>();
        sortFungiActors(surroundingFungiActors, new ArrayList<>(), nonInfectedActors);

        Random rand = new Random();
        for (Fungi fungiActor : nonInfectedActors) {
            if (rand.nextDouble(1) < SPREAD_CHANCE) {
                fungiActor.becomeInfected();
                //System.out.println("Actor got infected");
            }
        }
    }

    /** Gets all the fungi actors around a location.
     * @param sporeLocation is the location from where the search occurs
     * @param radius is the radius to search within
     */
    private List<Fungi> getSurroundingFungiActors(Location sporeLocation, int radius) {
        List<Fungi> surroundingFungiActors = new ArrayList<>();

        for (Location location : world.getSurroundingTiles(sporeLocation, radius)) {
            if (world.getTile(location) instanceof Fungi fungi) {
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

    public static float getSpreadChance() {
        return SPREAD_CHANCE;
    }

    @Override
    public int getEnergyValue() { return 0; }

    @Override
    public DisplayInformation getInformation() { return null; }
}
