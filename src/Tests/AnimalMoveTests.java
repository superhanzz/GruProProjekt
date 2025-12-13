package Tests;

import CapableSimulator.Actors.Wolf;
import CapableSimulator.Actors.WolfGang;
import FunctionLibrary.CapableFunc;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalMoveTests {

    World world;
    int testSampleSize;
    int animalAmount;
    int worldSize;
    int simSteps;

    List<Double> allNoMovesProp = new ArrayList<>();

    @BeforeEach
    public void setup() {
        testSampleSize = 10;
        animalAmount = 5;
        worldSize = 20;
        simSteps = 200;
    }

    /** tests that the wolf doesn't accidently get's removed from the map when trying to move, and also tests the propability of a wolf not moving during a step isn't too high
     * in the test wolf's must not be able to die.
     * */
    /*
    @RepeatedTest(10)
    public void wolfMoveTestNotDeletingEntities() {

        // makes a clean enviroment for each test.
        for (int i = 0; i < testSampleSize; i++) {
            world = new World(worldSize);

            // creates an alpha wolf and a wolf gang
            WolfGang gang = null;
            Wolf alpha = new Wolf(gang, true);
            world.setTile(CapableFunc.getEmptyTile(world, worldSize), alpha);
            gang = new WolfGang(alpha);

            // creates all the NPC wolfs
            for (int w = 0; w < animalAmount; w++) {
                Wolf wolf = new Wolf(gang, false);
                gang.addWolfToGang(wolf);
                world.setTile(CapableFunc.getEmptyTile(world, worldSize), wolf);
            }

            // get's the number of wolfs in the world before any behavior has been executed
            int numberOfWolfs_B = CapableFunc.getNumOfActors(world, "wolf");

            int timesMaxRadiusBroken = 0;

            int timesAnimalNoMove = 0;  // kepping track of times a wolf hasn't moved

            // the loop wherein all the wolf behavior is executed
            for (int s = 0; s < simSteps; s++) {
                // list of all wolf locations before behavior is executed
                List<Location> allPrevLocations = new ArrayList<>();
                allPrevLocations.add(world.getLocation(alpha));
                for (Wolf w : gang.NPCs) {
                    allPrevLocations.add(world.getLocation(w));
                }

                // wolf behavior is executed
                alpha.act(world);

                // list of all wolf locations after behavior is executed
                List<Location> allAfterLocations = new ArrayList<>();
                allAfterLocations.add(world.getLocation(alpha));
                for (Wolf w : gang.NPCs) {
                    allAfterLocations.add(world.getLocation(w));
                }

                // checks if any wolf hasn't moved
                for (int k = 0; k < animalAmount; k++) {
                    if (allPrevLocations.get(k).getX() == allAfterLocations.get(k).getX() && allPrevLocations.get(k).getY() == allAfterLocations.get(k).getY()) timesAnimalNoMove++;
                }

                // not currently used
                if (s > 30){
                    for (Wolf w : gang.NPCs) {
                        if (w.distance(world.getLocation(w), world.getLocation(alpha)) < 7.0) timesMaxRadiusBroken++;
                    }
                }
            }

            // calculates the probability of a wolf not moving in a sim step
            double noMoveProp = timesAnimalNoMove * 1.0 / (animalAmount * simSteps * 1.0);
            allNoMovesProp.add(noMoveProp); // enters the prob into the global array

            //System.out.println(noMoveProp);

            int numberOfWolfs_A = CapableFunc.getNumOfActors(world, "wolf"); // get's the number of wolf's after all the sim steps has occurred

            assertEquals(numberOfWolfs_B, numberOfWolfs_A); // evaluates the number of wolf's

            //System.out.println(timesAnimalNoMove);
            //System.out.println(timesMaxRadiusBroken);
            //assertTrue(timesMaxRadiusBroken < 100);
            //assertTrue(w.distance(world.getLocation(w), world.getLocation(alpha)) < 7);

        }
        // after each test the average probability of a wolf not moving is calculated, the bigger the sample size the more precises the probability.
        double avNoMoveProp = allNoMovesProp.stream().mapToDouble(Double::doubleValue).sum() / allNoMovesProp.size();
        allNoMovesProp.clear(); // clears the map for the next test run.
        System.out.println(avNoMoveProp);
    }
    */

    @AfterEach
    public void tearDown() {

    }

}
