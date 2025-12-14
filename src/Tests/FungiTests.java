package Tests;

import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.FungiSpore;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
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
    void fungiSpreadSporesAndProbabilityTest() {
        Carcass carrier = new Carcass(world, 100, CapableEnums.AnimalSize.ADULT);
        Location carrierLocation = new Location(3,3);
        world.setTile(carrierLocation, carrier);

        float spreadChance = FungiSpore.getSpreadChance();

        carrier.becomeInfected();
        assertTrue(carrier.isInfected());   // Checks if the carrier is actually infected

        int numberOfInfections = 0;
        for (int i = 0; i < probability_TestSampleSize; i++) {
            Carcass receiver = new Carcass(world, 100, CapableEnums.AnimalSize.ADULT);
            Location receiverLocation = new Location(2,3);
            world.setTile(receiverLocation, receiver);

            assertFalse(receiver.isInfected());   // Checks if the receiver is infected before testing

            carrier.spreadSpores(world);

            if (receiver.isInfected()) numberOfInfections ++;

            world.delete(receiver);
        }

        float actualSpreadProbability = ((numberOfInfections * 1.0f) / (probability_TestSampleSize * 1.0f));
        assertEquals(spreadChance, actualSpreadProbability, 0.01f);
    }

    @AfterEach
    void tearDown() {

    }

}
