package Tests;

import CapableSimulator.CapableSim;
import CapableSimulator.Grass;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;


import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GrassGrowthChanceTest {

    World world;
    int sampleSize;
    int grassGrowths;
    double expectedGrowthChance;

    @BeforeEach
    void setUp() {
        world = new World(3);
        sampleSize = 1000;
        grassGrowths = 0;

        // Retrieves the expected grass growth chance from a grass actor, the destroys that instance.
        Grass g = new Grass();
        expectedGrowthChance = g.getGrowthChance();
        g = null;
    }

    @RepeatedTest(1000)
    void GrassGrowthChanceTest() {

        for (int i = 0; i <= sampleSize; i++) {
            Grass motherGrass = new Grass();
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
