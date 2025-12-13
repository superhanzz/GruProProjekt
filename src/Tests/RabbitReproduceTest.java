package Tests;

import CapableSimulator.Actors.Rabbit;
import CapableSimulator.CapableWorld;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitReproduceTest {

    CapableWorld world;

    @BeforeEach
    public void setup() {
        world = new CapableWorld(2);
    }


    @RepeatedTest(100)
    public void testRabbitReproduce() {

        /* Creates 2 parrent rabbits with a constructer that set the minimum age required for mating, and the pregnancy cooldown */
        Rabbit r1 = new Rabbit(world,0, 20);
        Rabbit r2 = new Rabbit(world,0, 20);

        /* Puts the two rabbits in the world, next to each other */
        r1.updateOnMap(new Location(0,0), true);
        r2.updateOnMap(new Location(1,0), true);

        // Checks how manny actors are en the world, expected is 2
        int entitiesInWorld_1 = world.getEntities().size();
        assertEquals(2, entitiesInWorld_1); // Checks if there really is only 2 actors, just to be certain

        // Executes the mating method in rabbit 1
        r1.findMate(1);

        // Retrieves the new number of actors in the world
        int entitiesInWorld_2 = world.getEntities().size();

        // checks if the number of entities in the world has increased, we expect that it has by one
        assertTrue(entitiesInWorld_1 < entitiesInWorld_2);

        // Executes the mating method in rabbit 2
        r2.findMate(1);

        // Retrieves the new number of actors in the world
        int  entitiesInWorld_3 = world.getEntities().size();

        // checks if the number of entities in the world has increased, we expect that it has not
        assertEquals(3, entitiesInWorld_3);
    }


    @AfterEach
    public void tearDown() {
        world = null;
    }
}
