package Tests;

import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.PathFinder;
import CapableSimulator.Utils.TileFinder;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalMoveTests {

    CapableWorld world;
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

    /* Move method test for rabbit */
    @RepeatedTest(1)
    public void rabbitMoveTest() {
        world = new CapableWorld(worldSize);
        PathFinder pathFinder = new PathFinder(world);
        TileFinder tileFinder = new TileFinder(world);

        Rabbit rabbit = new Rabbit(world);
        rabbit.updateOnMap(tileFinder.getEmptyTile(world, true), true);

        Location animalLocationPreMove = rabbit.getLocation();
        rabbit.move();
        Location animalLocationPostMove = rabbit.getLocation();

        assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());

        world.delete(rabbit);
    }

    /* Move method test not wolf */
    @RepeatedTest(1)
    public void wolfMoveTest() {
        world = new CapableWorld(worldSize);
        PathFinder pathFinder = new PathFinder(world);
        TileFinder tileFinder = new TileFinder(world);

        Wolf wolf = new Wolf(world);
        wolf.updateOnMap(tileFinder.getEmptyTile(world, true), true);

        Location animalLocationPreMove = wolf.getLocation();
        wolf.move();
        Location animalLocationPostMove = wolf.getLocation();

        assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());
    }

    /* Single wolf follow an alpha wolf test */
    @RepeatedTest(1)
    public void wolfFollowAlphaTest() {
        world = new CapableWorld(worldSize);

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

    /*  Tests if a flock of wolf's move as a flock, i.e. the alpha wolf decides where the flock is going,
        and the other wolfs in the flock stay within a certain radius of the alpha */
    @RepeatedTest(1)
    public void wolfsMoveAsFlockTest() {
        world = new CapableWorld(worldSize);

        Wolf alpha = new Wolf(world);
        WolfGang gang = new WolfGang(world);
        Location alphaInitLocation = new Location(3,3);
        gang.addNewFlockMember(alpha);
        alpha.updateOnMap(alphaInitLocation, true);

        int numOfWolfsInGang = 6;
        for (int i = 0; i < numOfWolfsInGang; i++) {
            Wolf wolf = new Wolf(world);
            Location npcSpawnLocation = PathFinder.getEmptyTileAroundLocation(world, alphaInitLocation, 3);
            wolf.updateOnMap(npcSpawnLocation, true);
            gang.addNewFlockMember(wolf);
        }

        double averageRadius = 0.0;
        double radiusSum = 0.0;

        int numOfMovesInTest = 500;
        for (int i = 0; i < numOfMovesInTest; i++) {
            alpha.lookForFood(1);
            gang.alphaMoved(alpha.getLocation());
            double radius = gang.getFlockRadius();
            radiusSum += radius;
            //System.out.println(radius);
        }
        averageRadius = radiusSum / (numOfMovesInTest * 1.0);

        //System.out.println();
        //System.out.println(averageRadius);

        assertTrue(gang.getAllowedRadiusAroundAlpha() > averageRadius);
    }

    /* Tests bears move method and tests whether they stay within their territory */
    @RepeatedTest(1)
    void bearMoveTest() {
        world = new CapableWorld(worldSize);
        PathFinder pathFinder = new PathFinder(world);
        TileFinder tileFinder = new TileFinder(world);

        Location bearSpawnLocation = tileFinder.getEmptyTile(world, true);
        Bear bear = new Bear(world, bearSpawnLocation);
        bear.updateOnMap(bearSpawnLocation, true);

        Location animalLocationPreMove = null;
        Location animalLocationPostMove = null;

        for (int i = 0; i < testSampleSize; i++) {
            animalLocationPreMove = bear.getLocation();
            bear.move();
            animalLocationPostMove = bear.getLocation();

            double distanceFromCenter = pathFinder.distance(bear.getTerritoryCenter(),  animalLocationPostMove);

            assertTrue(animalLocationPreMove.getX() != animalLocationPostMove.getX() || animalLocationPreMove.getY() != animalLocationPostMove.getY());

            assertTrue(distanceFromCenter <= bear.getTerritoryRadius());
        }
    }

    @AfterEach
    public void tearDown() {

    }

}
