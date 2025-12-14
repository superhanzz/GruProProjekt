package Tests;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Wolf;
import CapableSimulator.Actors.Shelter.WolfDen;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.WorldUtils;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalReproduceTest {

    CapableWorld world;

    @BeforeEach
    public void setup() {
        world = new CapableWorld(3);
    }


    @RepeatedTest(1)
    public void testRabbitReproduce() {

        /* Creates 2 parrent rabbits with a constructer that set the minimum age required for mating, and the pregnancy cooldown */
        Rabbit r1 = new Rabbit(world,10 ,0, 20);
        Rabbit r2 = new Rabbit(world,10, 0, 20);

        /* Puts the two rabbits in the world, next to each other */
        r1.updateOnMap(new Location(0,0), true);
        r2.updateOnMap(new Location(1,0), true);

        r1.doEverySimStep();
        r2.doEverySimStep();

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

    /* Wolf reproduce test */
    @RepeatedTest(100)
    public void testWolfReproduce() {
        int testSampleSize = 10000;

        int numberOfNewSuccessfulCups = 0;
        for (int i = 0; i < testSampleSize; i++) {
            world = new CapableWorld(3);

            Wolf alpha = new Wolf(world, 10, 0, 20);
            Wolf npc = new Wolf(world, 10, 0, 20);

            WolfGang gang = new WolfGang(world);

            WorldUtils worldUtils = new WorldUtils(world);

            world.addAnimalFlock(gang);

            gang.addNewFlockMember(alpha);
            gang.addNewFlockMember(npc);

            alpha.updateOnMap(new Location(1, 1), true);
            npc.updateOnMap(new Location(0, 1), true);

            alpha.doEverySimStep();
            npc.doEverySimStep();

            alpha.onDusk();
            alpha.onNightFall();
            npc.onNightFall();

            int amountBefore = worldUtils.getAllWorldActorsAsMap().get("wolf").size();

            WolfDen den = null;
            for (WorldActor actor : worldUtils.getAllWorldActorsAsMap().get("wolfDen")) {
                if (actor instanceof WolfDen wolfDen) {
                    den = wolfDen;
                    break;
                }
            }
            if (den != null) {
                den.makeCup();
            }

            int amountAfter = worldUtils.getAllWorldActorsAsMap().get("wolf").size();
            if (amountBefore != amountAfter) numberOfNewSuccessfulCups++;
        }

        double ActualReproduceChance = (numberOfNewSuccessfulCups * 1.0) / (testSampleSize * 1.0);
        assertEquals(0.5, ActualReproduceChance, 0.025);
    }


    @AfterEach
    public void tearDown() {
        world = null;
    }
}
