package Tests;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Shelter.WolfDen;

import CapableSimulator.Utils.WorldUtils;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalReproduceTest {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
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
    @RepeatedTest(1)
    public void testWolfReproduce() {
        int testSampleSize = 10000;

        int numberOfNewSuccessfulCups = 0;
        for (int i = 0; i < testSampleSize; i++) {

            Wolf alpha = new Wolf(world, 10, 0, 20);
            Wolf npc = new Wolf(world, 10, 0, 20);

            WolfGang gang = new WolfGang(world);

            gang.addNewFlockMember(alpha);
            gang.addNewFlockMember(npc);

            alpha.updateOnMap(new Location(1, 1), true);
            npc.updateOnMap(new Location(0, 1), true);

            alpha.doEverySimStep();
            npc.doEverySimStep();

            alpha.onDusk();
            alpha.onNightFall();
            npc.onNightFall();


            int amountBefore = WorldUtils.getNumOfActors(world,"wolf", false); //worldUtils.getAllWorldActorsAsMap().get("wolf").size();

            WolfDen den = null;
            if(alpha.getShelter() instanceof WolfDen wolfDen)
                den = wolfDen;

            if (den != null) {
                den.makeCup();
            }

            int amountAfter = WorldUtils.getNumOfActors(world,"wolf", false);
            if (amountBefore != amountAfter) numberOfNewSuccessfulCups++;

            for (Object o : world.getEntities().keySet())
                world.delete(o);
        }

        double ActualReproduceChance = (numberOfNewSuccessfulCups * 1.0) / (testSampleSize * 1.0);
        assertEquals(0.5, ActualReproduceChance, 0.025);
    }

    @RepeatedTest(1)
    public void bearReproduceTest() {
        Location bear1Location = new Location(0,1);
        Bear bear1 = new Bear(world, 10,10,20, bear1Location);
        bear1.updateOnMap(bear1Location, true);
        bear1.doEverySimStep();

        Location bear2Location = new Location(1,1);
        Bear bear2 = new Bear(world, 10,10,20, bear2Location);
        bear2.updateOnMap(bear2Location, true);
        bear2.doEverySimStep();

        int numberOfBearsPreMating = WorldUtils.getNumOfActors(world,"bear", false);

        bear1.act(world);

        int numberOfBearsPostMating = WorldUtils.getNumOfActors(world,"bear", false);

        assertTrue(numberOfBearsPreMating < numberOfBearsPostMating);
    }


    // TODO
    @RepeatedTest(1)
    public void putinBecomeEggTest() {}

    @RepeatedTest(1)
    public void putinHatchFromEggTest() {}


    @AfterEach
    public void tearDown() {
        world = null;
    }
}
