package Tests;

import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.CapableWorld;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;


import static org.junit.jupiter.api.Assertions.*;

public class GrassGrowthChanceTest {

    CapableWorld world;
    int sampleSize;
    int grassGrowths;
    double expectedGrowthChance;


    @BeforeEach
    void setUp() {
        world = new CapableWorld(3);
        sampleSize = 1000;
        grassGrowths = 0;

        // Retrieves the expected grass growth chance from a grass actor, the destroys that instance.
        Grass g = new Grass(world);
        expectedGrowthChance = g.getGrowthChance();
        g = null;
    }

    @RepeatedTest(1000)
    void GrassGrowthChanceTest() {

        for (int i = 0; i <= sampleSize; i++) {
            Grass motherGrass = new Grass(world);
            Location location = new Location(1, 1);

            world.setTile(location, motherGrass);

            motherGrass.act(world);

            grassGrowths += (world.getEntities().size() > 1) ? 1 : 0;
            for (Object object : world.getEntities().keySet()) {
                world.delete(object);
            }

        }

        assertEquals(0.01, (grassGrowths / sampleSize), 0.01);
    }

    @AfterEach
    void tearDown() {

    }
}
