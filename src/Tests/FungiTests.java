package Tests;

import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.FertilTile;
import CapableSimulator.Actors.Fungis.Fungi;
import CapableSimulator.Actors.Fungis.FungiSpore;
import CapableSimulator.Actors.Fungis.Fungus;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.SpawningAgent;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class FungiTests {

    CapableWorld world;
    int probability_TestSampleSize = 10000;

    @BeforeEach
    void setup() {
        world = new CapableWorld(5);
    }

    @RepeatedTest(1)
    void fungiSporesSpreadProbabilityTest() {
        Carcass carrier = new Carcass(world, 100, CapableEnums.AnimalSize.ADULT);
        Location carrierLocation = new Location(2,2);
        world.setTile(carrierLocation, carrier);

        double spreadChance = FungiSpore.getSpreadChance();

        carrier.becomeInfected();
        assertTrue(carrier.isInfected());   // Checks if the carrier is actually infected

        int numberOfInfections = 0;
        for (int i = 0; i < probability_TestSampleSize; i++) {
            Carcass receiver = new Carcass(world, 100, CapableEnums.AnimalSize.ADULT);
            Location receiverLocation = new Location(1,2);
            world.setTile(receiverLocation, receiver);

            assertFalse(receiver.isInfected());   // Checks if the receiver is infected before testing

            carrier.spreadSpores(world);

            if (receiver.isInfected()) numberOfInfections ++;

            world.delete(receiver);
        }

        double actualSpreadProbability = ((numberOfInfections * 1.0) / (probability_TestSampleSize * 1.0));
        assertEquals(spreadChance, actualSpreadProbability, 0.01);
    }

    @RepeatedTest(1)
    void fungusSpreadToCarcassProbabilityTest() {
        SpawningAgent spawningAgent = new SpawningAgent(world);

        double spreadChance = FungiSpore.getSpreadChance();

        int numberOfInfections = 0;
        for (int i = 0; i < probability_TestSampleSize; i++) {
            Fungus fungus = new Fungus(world);
            Location fungusLocation = new Location(2,2);


            Carcass carcass = new Carcass(world);
            Location carcassLocation = new Location(1,2);

            spawningAgent.spawnActorAtLocation(fungus, fungusLocation);
            spawningAgent.spawnActorAtLocation(carcass, carcassLocation);

            assertFalse(carcass.isInfected());

            for (Object o : world.getEntities().keySet()) {
                if (o instanceof Fungi fungi) fungi.spreadSpores(world);
            }

            if (carcass.isInfected()) numberOfInfections ++;

            world.delete(fungus);
            world.delete(carcass);
        }

        double actualSpreadProbability = ((numberOfInfections * 1.0) / (probability_TestSampleSize * 1.0));
        assertEquals(spreadChance, actualSpreadProbability, 0.01);
    }

    /* Test whether a fungus can decompose (disappers from the world) and the tile (occupied by the fungus) becomes fertil and then a grass spawns at the location*/
    @RepeatedTest(1)
    void fungusDecomposeToFertilTileTest() {
        SpawningAgent spawningAgent = new SpawningAgent(world);

        Fungus fungus = new Fungus(world, 1);
        Location fungusLocation = new Location(2,2);

        spawningAgent.spawnActorAtLocation(fungus, fungusLocation);

        assertTrue(world.getSortedEntities().get("fertilTile").isEmpty());

        fungus.act(world);

        assertFalse(world.contains(fungus));
        assertInstanceOf(FertilTile.class, world.getNonBlocking(fungusLocation));

        for (Object o : world.getEntities().keySet()) {
            if (o instanceof Actor actor) actor.act(world);
        }

        assertInstanceOf(Grass.class, world.getNonBlocking(fungusLocation));
    }

    @AfterEach
    void tearDown() {

    }

}
