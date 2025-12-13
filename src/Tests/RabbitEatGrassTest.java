package Tests;


import CapableSimulator.Actors.Grass;
import CapableSimulator.Actors.Rabbit;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitEatGrassTest {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
    }

    /**
     * Tests whether the rabbit can eat grass or not.
     * */
    /*
    @RepeatedTest(10)
    void eatTest() {
        // Creates one rabbit and one grass and a location at (0,0).
        Rabbit r = new Rabbit(1);
        Grass grass = new Grass();
        Location location = new Location(1, 1);

        // Inserts the rabbit and grass into the world at (0,0).
        world.setTile(location, grass);
        world.setTile(location, r);

        // Checks if the grass is actually at the location under the rabbit.
        assertNotNull(world.getNonBlocking(world.getLocation(r)));

        // Executes the method where the rabbit eats grass.
        r.testEatGrass(world);

        // Checks if the grass under the rabbit has been eaten.
        assertNull(world.getNonBlocking(world.getLocation(r)));

        // Checks if the rabbit is alive, that is that the rabbit has not been deleted, and is still in the worlds entity map.
        assertTrue(world.getEntities().containsKey(r));
    }


    *//**
     * Tests if the rabbit can die, and then if the actor is actually deleted from the world
     * *//*
    @RepeatedTest(10)
    void dieTest() {
        // Creates a rabbit and inserts it on the map at (0,0)
        Rabbit r = new Rabbit();
        Location location = new Location(0, 0);
        world.setTile(location, r);

        // Checks if the rabbit is in the world
        boolean rabbitInWorld = world.getTile(location) != null;
        assertTrue(rabbitInWorld); // Evaluates whether the rabbit is in the world before the rabbit die.

        // Executes the die() method in the rabbit.
        r.die(world);
        assertFalse(world.getEntities().containsKey(r)); // Evaluates whether the rabbit is completely removed from the world.
    }


    *//**
     * Tests whether the rabbit dies if it does not eat grass before it runs out of energy.
     * *//*
    @RepeatedTest(10)
    void notEatGrassTest() {
        // Creates a rabbit with a special constructor, where the rabbits starting energy can be defined.
        Rabbit r = new Rabbit(0);
        Location location = new Location(1, 1); // Creates a location at (1,1).
        world.setTile(location, r); // Inserts the rabbit into the world, at (1,1).

        // Checks if the rabbit is alive, that is that it is actually in the world's entity map.
        assertTrue(world.getEntities().containsKey(r));
        r.act(world); // Executes the act() method in the rabbit, which is the method wherein the rabbits behavior is defined.

        // Checks if the rabbit has died, that is the rabbit is not in the worlds entity map.
        assertFalse(world.getEntities().containsKey(r));
    }
    */

    @AfterEach
    public void tearDown() {

    }
}
