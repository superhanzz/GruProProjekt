package Tests;

import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.PathFinder;
import CapableSimulator.Utils.TileFinder;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class CombatTests {
    CapableWorld world;

    @BeforeEach
    void setup() {
        world = new CapableWorld(5);
    }

    /* Test if 2 wolf's can attack each other, and one gets killed */
    @RepeatedTest(1)
    void wolfAttackWolfTest() {
        Wolf instigator = new Wolf(world, 20, 0, 0);
        Location instigatorLocation = new Location(2, 2);
        instigator.updateOnMap(instigatorLocation, true);

        Wolf attackedWolf = new Wolf(world, 20, 0, 0);
        Location attackedLocation = new Location(1, 1);
        attackedWolf.updateOnMap(attackedLocation, true);

        attackedWolf.doEverySimStep();

        instigator.act(world);

        if (world.getEntities().containsKey(instigator)) {
            assertFalse(world.getEntities().containsKey(attackedWolf));
        }
        else {
            assertFalse(world.getEntities().containsKey(instigator));
        }
    }

    @AfterEach
    void tearDown() {

    }
}
