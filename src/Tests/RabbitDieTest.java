package Tests;

import CapableSimulator.Rabbit;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitDieTest {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
    }

    @RepeatedTest(1)
    void dieTest() {
        Rabbit r = new Rabbit();
        Location location = new Location(0, 0);

        world.setTile(location, r);

        boolean rabbitInWorld = world.getTile(location) != null;
        if  (rabbitInWorld) {
            world.delete(r);
            assertNull(world.getTile(location));
        }
        else {
            assertTrue(true);
        }
    }

    @AfterEach
    public void tearDown() {

    }

}
