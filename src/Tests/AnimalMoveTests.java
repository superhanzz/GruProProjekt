package Tests;

import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.PathFinder;
import CapableSimulator.Utils.TileFinder;
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

    @BeforeEach
    public void setup() {
        testSampleSize = 100;
        animalAmount = 5;
        worldSize = 20;
        simSteps = 200;
    }

    /* Move method test not wolf */
    @RepeatedTest(10)
    public void animalMoveTest_NotWolf() {
        CapableWorld world = new CapableWorld(worldSize);

        PathFinder pathFinder = new PathFinder(world);

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

            double distanceFromCenter = pathFinder.distance(bear.getTerritoryCenter(),  animalLocationPostMove);

            assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());

            assertTrue(distanceFromCenter <= bear.getTerritoryRadius());
        }

    }

    /* Wolf follow Alpha test */
    @RepeatedTest(1)
    public void WolfFollowAlphaTest() {
        CapableWorld world = new CapableWorld(worldSize);

        PathFinder pathFinder = new PathFinder(world);

        Wolf alpha = new Wolf(world);
        Wolf npc = new Wolf(world);
        WolfGang gang = new WolfGang(world);

        gang.addNewFlockMember(alpha);
        gang.addNewFlockMember(npc);

        alpha.updateOnMap(new Location(1,1), true);
        npc.updateOnMap(new Location(10,10), true);

        Location alphaLocationPreMove = alpha.getLocation();
        Location npcLocationPreMove = npc.getLocation();

        double npcDistanceFromAlphaPreMove = pathFinder.distance(alphaLocationPreMove, npcLocationPreMove);

        alpha.act(world);

        Location alphaLocationPostMove = alpha.getLocation();
        Location npcLocationPostMove = npc.getLocation();

        double npcDistanceFromAlphaPostMove = pathFinder.distance(alphaLocationPostMove, npcLocationPostMove);

        assertTrue(alphaLocationPreMove.getX() != alphaLocationPostMove.getX() || alphaLocationPreMove.getY() != alphaLocationPostMove.getY());

        assertTrue(npcLocationPreMove.getX() != npcLocationPostMove.getX() || npcLocationPreMove.getY() != npcLocationPostMove.getY());

        assertTrue(npcDistanceFromAlphaPreMove >= npcDistanceFromAlphaPostMove);
    }

    @AfterEach
    public void tearDown() {

    }

}
