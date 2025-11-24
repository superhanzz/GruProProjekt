package Tests;

import CapableSimulator.Burrow;
import CapableSimulator.Rabbit;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitHoleTest {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(1);
    }

    @RepeatedTest(100)
    public void rabbitDigHoleTest() {
        // Creates a rabbit and inserts it on the map on tile (0,0)
        Rabbit r = new Rabbit();
        world.setTile(new Location(0, 0), r);

        // Checks if there is a hole on tile (0,0) before the actual test
        boolean isThereHole_Before = world.getNonBlocking(new Location(0,0)) instanceof Burrow;
        assertFalse(isThereHole_Before); // asserts whether there actually is a hole before the test

        // Executes the Burrow digging method in the rabbit
        r.digBurrow(world);

        // checks if there is a burrow after the rabbit dug one
        boolean isThereHole_After = world.getNonBlocking(new Location(0,0)) instanceof Burrow;
        assertTrue(isThereHole_After); // Evaluates whether there was dug a burrow, we expect that there has
    }

    @AfterEach
    public void tearDown() {
        world = null;
    }
}
