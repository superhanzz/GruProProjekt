package Tests;

import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Predator;
import CapableSimulator.Actors.Animals.Predators.Putin;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class CombatTests {
    World world;

    @BeforeEach
    void setup() {
        world = new World(5);
    }

    /* Test if 2 wolf's can attack each other, and one gets killed */
    @RepeatedTest(1)
    void wolfAttackWolfTest() {
        Predator instigator = new Wolf(world, 20, 0, 0);
        Location instigatorLocation = new Location(2, 2);
        instigator.updateOnMap(instigatorLocation, true);

        Predator attackedWolf = new Wolf(world, 20, 0, 0);
        Location attackedLocation = new Location(1, 1);
        attackedWolf.updateOnMap(attackedLocation, true);

        attackedWolf.doEverySimulationStep();

        instigator.act(world);

        if (world.getEntities().containsKey(instigator)) {
            assertFalse(world.getEntities().containsKey(attackedWolf));
        }
        else {
            assertFalse(world.getEntities().containsKey(instigator));
        }
    }

    @RepeatedTest(1)
    void BearAttackBearTest() {
        Location instigatorLocation = new Location(2, 2);
        Predator instigator = new Bear(world, instigatorLocation, 20, 100, 0);
        instigator.updateOnMap(instigatorLocation, true);

        Location attackedLocation = new Location(1, 1);
        Predator attackedBear = new Bear(world, attackedLocation,20, 100, 0);
        attackedBear.updateOnMap(attackedLocation, true);

        attackedBear.doEverySimulationStep();

        instigator.act(world);

        if (world.getEntities().containsKey(instigator)) {
            assertFalse(world.getEntities().containsKey(attackedBear));
        }
        else {
            assertFalse(world.getEntities().containsKey(instigator));
        }
    }

    @RepeatedTest(1)
    void BearAttackWolf_SingleTest() {
        Location instigatorLocation = new Location(2, 2);
        Predator instigator = new Bear(world, instigatorLocation,20, 0, 0);
        instigator.updateOnMap(instigatorLocation, true);

        Predator attackedWolf = new Wolf(world, 20, 0, 0);
        Location attackedLocation = new Location(1, 1);
        attackedWolf.updateOnMap(attackedLocation, true);

        attackedWolf.doEverySimulationStep();

        instigator.act(world);

        if (world.getEntities().containsKey(instigator)) {
            assertFalse(world.getEntities().containsKey(attackedWolf));
        }
        else {
            assertFalse(world.getEntities().containsKey(instigator));
        }
    }

    @RepeatedTest(1)
    void PutinAttackWolfTest() {
        Location instigatorLocation = new Location(2, 2);
        Predator instigator = new Putin(world, 20, 20);
        instigator.updateOnMap(instigatorLocation, true);

        Location attackedLocation = new Location(1, 1);
        Predator attackedWolf = new Wolf(world,20, 100, 0);
        attackedWolf.updateOnMap(attackedLocation, true);

        attackedWolf.doEverySimulationStep();

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
