package Tests;

import CapableSimulator.Actors.Plants.BerryBush;
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
        sampleSize = 10000;
        grassGrowths = 0;

        // Retrieves the expected grass growth chance from a grass actor, the destroys that instance.
        Grass g = new Grass(world);
        expectedGrowthChance = g.getGrowthChance();
        g = null;
    }

    @RepeatedTest(100)
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

    @RepeatedTest(100)
    void berryGrowthChanceTest() {
        double berrySpawnChance = BerryBush.getBerrySpawnChance();

        int numberOfBerriesSpawns = 0;
        for (int i = 0; i <= sampleSize; i++) {
            BerryBush bush = new BerryBush(world);
            Location location = new Location(1, 1);
            world.setTile(location, bush);
            assertFalse(bush.getBerryStatus());
            bush.act(world);
            if (bush.getBerryStatus()) numberOfBerriesSpawns++;
            world.delete(bush);
        }

        double actualSpawnChance = (numberOfBerriesSpawns * 1.0) / (sampleSize * 1.0);
        assertEquals(berrySpawnChance, actualSpawnChance, 0.01);

    }

    @AfterEach
    void tearDown() {

    }
}
