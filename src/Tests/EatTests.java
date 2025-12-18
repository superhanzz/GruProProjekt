package Tests;


import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.Plants.BerryBush;
import CapableSimulator.Actors.Plants.Grass;

import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

public class EatTests {

    World world;

    @BeforeEach
    public void setup() {
        world = new World(3);
    }

    /**
     * Tests look for food function, i.e. look for grass, go to grass and eat grass.
     * */
    @RepeatedTest(1)
    void RabbitEatTest() {
        Rabbit rabbit = new Rabbit(world);
        Location location = new Location(1,1);
        rabbit.updateOnMap(location, true);

        Location grassLocation = PathFinder.getEmptyTileAroundLocation(world, rabbit.getLocation(), 1);

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

        Location bearLocation = new Location(0,1);
        Bear bear = new Bear(world, bearLocation);
        bear.updateOnMap(bearLocation, true);

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

        Location bearLocation = new Location(0,1);
        Bear bear = new Bear(world, bearLocation);
        bear.updateOnMap(bearLocation, true);

        assertTrue(bush.getBerryStatus());
        bear.lookForFood(1);
        assertFalse(bush.getBerryStatus());
    }

    @RepeatedTest(1)
    void wolfHuntRabbitTest() {
        Wolf wolf = new Wolf(world);
        Location wolfLocation = new Location(0,1);
        wolf.updateOnMap(wolfLocation, true);

        Rabbit rabbit = new Rabbit(world);
        Location rabbitLocation = new Location(1,1);
        rabbit.updateOnMap(rabbitLocation, true);

        wolf.lookForFood(1);

        assertFalse(world.getEntities().containsKey(rabbit));
        assertInstanceOf(Carcass.class, world.getTile(rabbitLocation));
    }


    /**
     * Tests whether the rabbit dies if it does not eat grass before it runs out of energy.
     * */
    @RepeatedTest(1)
    void bearHuntRabbitTest() {
        Location bearLocation = new Location(0,1);
        Bear bear = new Bear(world, bearLocation);
        bear.updateOnMap(bearLocation, true);

        Rabbit rabbit = new Rabbit(world);
        Location rabbitLocation = new Location(1,1);
        rabbit.updateOnMap(rabbitLocation, true);

        bear.lookForFood(1);

        assertFalse(world.getEntities().containsKey(rabbit));
        assertInstanceOf(Carcass.class, world.getTile(rabbitLocation));
    }

    @RepeatedTest(1)
    void bearHuntWolfTest() {
        Location bearLocation = new Location(0,1);
        Bear bear = new Bear(world, bearLocation);
        bear.updateOnMap(bearLocation, true);

        Wolf wolf = new Wolf(world);
        Location wolfLocation = new Location(1,1);
        wolf.updateOnMap(wolfLocation, true);

        bear.lookForFood(1);

        assertFalse(world.getEntities().containsKey(wolf));
        assertInstanceOf(Carcass.class, world.getTile(wolfLocation));
    }


    @AfterEach
    public void tearDown() {

    }
}
