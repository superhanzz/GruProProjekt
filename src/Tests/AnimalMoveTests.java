package Tests;

import CapableSimulator.Actors.Bear;
import CapableSimulator.Actors.Rabbit;
import CapableSimulator.Actors.Wolf;
import CapableSimulator.Actors.WolfGang;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.TileFinder;
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
        testSampleSize = 100;
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

    /* Move method test not wolf */
    @RepeatedTest(10)
    public void animalMoveTest_NotWolf() {
        CapableWorld world = new CapableWorld(worldSize);

        TileFinder tileFinder = new TileFinder(world);

        Rabbit rabbit = new Rabbit(world);
        rabbit.updateOnMap(tileFinder.getEmptyTile(world, true), true);

        Location animalLocationPreMove = rabbit.getLocation();
        rabbit.move(world);
        Location animalLocationPostMove = rabbit.getLocation();

        assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());

        world.delete(rabbit);

        Bear bear = new Bear(world);
        bear.updateOnMap(tileFinder.getEmptyTile(world, true), true);

        for (int i = 0; i < testSampleSize; i++) {
            animalLocationPreMove = bear.getLocation();
            bear.move(world);
            animalLocationPostMove = bear.getLocation();

            double distanceFromCenter = Math.ceil(bear.distance(bear.getTerritoryCenter(),  animalLocationPostMove));

            assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());

            assertTrue(distanceFromCenter <= bear.getTerritoryRadius());
        }

    }

    /* Wolf follow Alpha test */
    @RepeatedTest(1)
    public void WolfFollowAlphaTest() {
        CapableWorld world = new CapableWorld(worldSize);

        Wolf alpha = new Wolf(world);
        Wolf npc = new Wolf(world);
        WolfGang gang = new WolfGang(world);

        gang.addNewFlockMember(alpha);
        gang.addNewFlockMember(npc);

        alpha.updateOnMap(new Location(1,1), true);
        npc.updateOnMap(new Location(10,10), true);

        Location alphaLocationPreMove = alpha.getLocation();
        Location npcLocationPreMove = npc.getLocation();

        double npcDistanceFromAlphaPreMove = npc.distance(alphaLocationPreMove, npcLocationPreMove);

        alpha.act(world);

        Location alphaLocationPostMove = alpha.getLocation();
        Location npcLocationPostMove = npc.getLocation();

        double npcDistanceFromAlphaPostMove = npc.distance(alphaLocationPostMove, npcLocationPostMove);

        assertTrue(alphaLocationPreMove.getX() != alphaLocationPostMove.getX() || alphaLocationPreMove.getY() != alphaLocationPostMove.getY());

        assertTrue(npcLocationPreMove.getX() != npcLocationPostMove.getX() || npcLocationPreMove.getY() != npcLocationPostMove.getY());

        assertTrue(npcDistanceFromAlphaPreMove >= npcDistanceFromAlphaPostMove);
    }

    @AfterEach
    public void tearDown() {

    }

}
