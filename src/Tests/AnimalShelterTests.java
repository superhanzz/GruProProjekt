package Tests;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Shelter.Burrow;
import CapableSimulator.Actors.Shelter.WolfDen;

import CapableSimulator.Utils.PathFinder;
import CapableSimulator.Utils.WorldUtils;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalShelterTests {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
    }

    // test whether the rabbit can dig a burrow, and also if it is possible for a rabbit to stand on a burrow

    @RepeatedTest(1)
    public void rabbitDigHoleTest() {
        // Creates a rabbit and inserts it on the map on tile (0,0)
        Rabbit r = new Rabbit(world);
        r.updateOnMap(new Location(0, 0), true);

        // Checks if there is a burrow on tile (0,0) before the actual test
        boolean isThereHole_Before = world.getNonBlocking(new Location(0,0)) instanceof Burrow;
        assertFalse(isThereHole_Before); // asserts whether there actually is a burrow before the test

        // Executes the Burrow digging method in the rabbit
        r.digBurrow();

        // checks if there is a burrow after the rabbit dug one
        boolean isThereHole_After = world.getNonBlocking(new Location(0,0)) instanceof Burrow;
        assertTrue(isThereHole_After); // Evaluates whether there was dug a burrow, we expect that there has

        // deletes all the actors in the world, for the next test
        world.getEntities().forEach((o, l) -> {
            world.delete(o);
        });
    }

    // Tests whether 2 rabbits can share a burrow.
    @RepeatedTest(1)
    public void rabbitShareBurrowTest() {
        // Creates rabbit one and inserts in on the map at (0,1)
        Rabbit r1 = new Rabbit(world);
        Location r1Loc = new Location(0, 1);
        r1.updateOnMap(r1Loc, true);
        // Creates rabbit two and inserts in on the map at (2,1)
        Rabbit r2 = new Rabbit(world);
        Location r2Loc = new Location(2, 1);
        r2.updateOnMap(r2Loc, true);
        // Creates burrow and inserts in on the map at (1,1)
        Burrow b1 = new Burrow(world);
        Location b1Loc = new Location(1, 1);
        world .setTile(b1Loc, b1);

        // Checks if either of the rabbits has a burrow, expected is false
        assertNull(r1.getBurrow());
        assertNull(r2.getBurrow());

        // Executes the method where the rabbits findes a burrow or digs one, since there is a burrow beside them, they should connect with b1
        r1.onNightFall();
        r2.onNightFall();

        // Checks whether the rabbits burrow reference are both equal to b1, expected is true
        assertSame(b1,r1.getBurrow());
        assertSame(b1,r2.getBurrow());

        // deletes all the actors in the world, for the next test
        world.getEntities().forEach((o, l) -> {
            world.delete(o);
        });
    }


    /**
     * Test that the rabbit go to it's burrow when it's about to turn to night
     * */
    @RepeatedTest(1)
    public void rabbitGoTowardsBurrowTest() {
        // creates a rabbit and inserts it onto the map at (0,1)
        Rabbit r1 = new Rabbit(world);
        Location r1Loc = new Location(0, 0);
        r1.updateOnMap(r1Loc, true);

        // creates a burrow and inserts it onto the map at (1, 0)
        Burrow b1 = new Burrow(world);
        Location b1Loc = new Location(1, 0);
        world.setTile(b1Loc, b1);

        // Executes the method where the rabbit connects with an existing burrow or digs a new one
        r1.onDusk();
        r1.onNightFall();
        assertSame(b1,r1.getBurrow());  // checks that the rabbit connected with the burrow

        // makes the rabbit come out of the burrow and back onto the map so that it can move
        r1.onDawn();
        world.move(r1, new Location(2, 2)); // makes the rabbit move to the lower right corner of the map

        // Executes the method that makes the rabbit go towards it's burrow just before the nightfall
        r1.onDusk();
        assertTrue(world.getSurroundingTiles(b1Loc).contains(world.getLocation(r1)));   // Checks whether the rabbit is within one of the surrounding tiles of the burrow

        // deletes all the actors in the world, for the next test
        world.getEntities().forEach((o, l) -> {
            world.delete(o);
        });
    }

    @RepeatedTest(1)
    public void wolfMakeDen_EnterDen_LeaveDen_Test() {
        Wolf w = new Wolf(world);
        WolfGang gang = new WolfGang(world);
        Location wLoc = new Location(1, 1);

        gang.addNewFlockMember(w);
        w.updateOnMap(new Location(1, 1), true);

        assertNull(world.getNonBlocking(wLoc));

        w.onDusk();
        w.onNightFall();

        assertNotNull(world.getNonBlocking(wLoc));
        assertFalse(world.isOnTile(w));

        w.onDawn();

        assertTrue(world.isOnTile(w));
    }

    @RepeatedTest(1)
    public void wolfGoTowardsDenTest() {
        PathFinder pathFinder = new PathFinder(world);

        world =  new World(10);

        Wolf w = new Wolf(world);
        WolfGang gang = new WolfGang(world);
        gang.addNewFlockMember(w);

        Location wLoc = new Location(0, 0);
        Location wLoc2 = new Location(9, 9);

        w.updateOnMap(wLoc, true);
        w.onDusk(); // Digs WolfDen

        world.move(w, wLoc2);   // moves to other side of the map

        int steps = 0;
        while (world.isOnTile(w)) {
            double distanceBeforeMove = pathFinder.distance(w.getLocation(),  wLoc);
            w.onNightFall();

            if (!world.isOnTile(w)) break;

            double distanceAfterMove = pathFinder.distance(w.getLocation(),  wLoc);
            assertTrue(distanceBeforeMove > distanceAfterMove);
            steps++;
        }
    }

    @RepeatedTest(1)
    void wolfFlockShareDenTest() {
        world = new World(10);

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

        alpha.onDusk(); // alpha digs den

        WolfDen den = null;
        if (alpha.getShelter() instanceof WolfDen wolfDen) {
            den = wolfDen;
        }
        assertNotNull(den);

        // Check if all the members of the wolf gang actually has the same den
        for (Animal animal : gang.getFlockMembers()) {
            if (animal instanceof Wolf npc) {
                assertEquals(den, npc.getShelter());
            }
        }

        // Checks if all the member enters the same den
        int num = 0;
        while (anyWolfsOnMap()) {
            for (Animal animal : WorldUtils.getAllAnimals(world)) {
                animal.onNightFall();
            }
            num++;
            if (num > 100) break;
        }

        for (Animal animal : den.getAnimalsInShelter()) {
            assertTrue(gang.isMemberOfFlock(animal));
        }
    }

    private boolean anyWolfsOnMap() {
        for (Object o : WorldUtils.getAllObjectOnWorldMap(world)) {
            if (o instanceof Wolf wolf) {
                return true;
            }
        }
        return false;
    }



    @AfterEach
    public void tearDown() {
        world =  null;
    }
}
