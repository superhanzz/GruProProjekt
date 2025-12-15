package Tests;


import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.Fungis.FungiSpore;
import CapableSimulator.Actors.Fungis.Fungus;
import CapableSimulator.Actors.Plants.BerryBush;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitEatGrassTest {

    CapableWorld world;

    @BeforeEach
    public void setup() {
        world = new CapableWorld(3);
    }

    /**
     * Tests look for food function, i.e. look for grass, go to grass and eat grass.
     * */
    @RepeatedTest(1)
    void RabbitEatTest() {
        Rabbit rabbit = new Rabbit(world);
        Location location = new Location(1,1);
        rabbit.updateOnMap(location, true);

        PathFinder pathFinder = new PathFinder(world);
        Location grassLocation = pathFinder.getEmptyTileAroundLocation(rabbit.getLocation(), 1);

        Grass grass = new Grass(world);
        world.setTile(grassLocation, grass);

        assertTrue(world.getNonBlocking(grassLocation) == grass);
        rabbit.lookForFood(1);
        assertNull(world.getNonBlocking(grassLocation));
    }

    @RepeatedTest(1)
    void WolfEatFromCarcassTest() {
        Carcass carcass = new Carcass(world);
        Location carcassLocation = new Location(1,1);
        world.setTile(carcassLocation, carcass);

        Wolf wolf = new Wolf(world);
        Location wolfLocation = new Location(0,1);
        wolf.updateOnMap(wolfLocation, true);

        int carcassInitEnergy = carcass.getEnergyValue();
        wolf.lookForFood(1);
        int carcassEndEnergy = carcass.getEnergyValue();

        assertTrue(carcassEndEnergy < carcassInitEnergy);
    }

    @RepeatedTest(1)
    void bearEatFromCarcassTest() {
        Carcass carcass = new Carcass(world);
        Location carcassLocation = new Location(1,1);
        world.setTile(carcassLocation, carcass);

        Bear bear = new Bear(world);
        Location wolfLocation = new Location(0,1);
        bear.updateOnMap(wolfLocation, true);

        int carcassInitEnergy = carcass.getEnergyValue();
        bear.lookForFood(1);
        int carcassEndEnergy = carcass.getEnergyValue();

        assertTrue(carcassEndEnergy < carcassInitEnergy);
    }

    @RepeatedTest(1)
    void bearEatFromBerryBushTest() {
        BerryBush bush = new BerryBush(world);
        Location bushLocation = new Location(1,1);
        world.setTile(bushLocation, bush);

        while (!bush.getBerryStatus()) {
            bush.trySpawnBerrys();
        }

        Bear bear = new Bear(world);
        Location wolfLocation = new Location(0,1);
        bear.updateOnMap(wolfLocation, true);

        assertTrue(bush.getBerryStatus());
        bear.lookForFood(1);
        assertFalse(bush.getBerryStatus());
    }


    /**
     * Tests whether the rabbit dies if it does not eat grass before it runs out of energy.
     * */
    @RepeatedTest(1)
    void notEatGrassTest() {

    }


    @AfterEach
    public void tearDown() {

    }
}
