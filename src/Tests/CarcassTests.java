package Tests;

import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Carcass;

import CapableSimulator.Actors.Fungis.Fungus;


import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class CarcassTests {

    World world;

    @BeforeEach
    void setup() {
        world = new World(5);
    }

    @RepeatedTest(1)
    void animalTurnToCarcassTest() {
        Rabbit rabbit = new Rabbit(world);
        Location rabbitLocation = new Location(3,3);
        rabbit.updateOnMap(rabbitLocation, true);

        assertTrue(world.isOnTile(rabbit));
        rabbit.die();
        assertFalse(world.getEntities().containsKey(rabbit));

        Carcass carcass = null;
        if (world.getTile(rabbitLocation) instanceof Carcass c) carcass = c;
        assertNotNull(carcass);
    }

    @RepeatedTest(1)
    void CarcassDecomposeTest() {
        Carcass carcass = new Carcass(world);
        Location carcassLocation = new Location(3,3);
        world.setTile(carcassLocation, carcass);
        assertTrue(world.isOnTile(carcass));

        carcass.decompose();
        assertFalse(world.getEntities().containsKey(carcass));
    }

    @RepeatedTest(1)
    void CarcassTurnToFungiTest() {
        Carcass carcass = new Carcass(world);
        Location carcassLocation = new Location(3,3);
        world.setTile(carcassLocation, carcass);
        assertTrue(world.isOnTile(carcass));

        carcass.becomeInfected();
        assertTrue(carcass.isInfected());

        carcass.decompose();
        assertFalse(world.getEntities().containsKey(carcass));

        Fungus fungus = null;
        if (world.getTile(carcassLocation) instanceof Fungus f) fungus = f;
        assertNotNull(fungus);
    }

    @RepeatedTest(1)
    void cordycepSporeSpreadProbabilityTest() {

    }

    @AfterEach
    void tearDown() {

    }
}
