package Tests;


import CapableSimulator.Grass;
import CapableSimulator.Rabbit;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitEatGrassTest {
    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
    }

    @RepeatedTest(1)
    void eatTest() {
        Rabbit r = new Rabbit(1);
        Grass grass = new Grass();
        Location location = new Location(1, 1);

        world.setTile(location, grass);
        world.setTile(location, r);

        boolean isGrassUnderRabbit = world.getNonBlocking(location) != null;

        r.testEatGrass(world);

        boolean isGrassEaten = world.getNonBlocking(location) == null;

        assertTrue(isGrassUnderRabbit);
        assertTrue(isGrassEaten);

        boolean isRabbitAlive = world.getTile(location) != null;
        assertTrue(isRabbitAlive);


        boolean rabbitInWorld = world.getTile(location) != null;
    }

    @RepeatedTest(1)
    void notEatGrassTest() {
        Rabbit r = new Rabbit(0);
        Location location = new Location(1, 1);

        world.setTile(location, r);

        Set<Object> objects = world.getEntities().keySet();
        boolean isRabbitAlive = objects.contains(r);
        assertTrue(isRabbitAlive);
        r.act(world);

        objects = world.getEntities().keySet();
        boolean isRabbitDead = !objects.contains(r);

        System.out.println(isRabbitDead);
        assertTrue(isRabbitDead);
    }

    @AfterEach
    public void tearDown() {

    }
}
