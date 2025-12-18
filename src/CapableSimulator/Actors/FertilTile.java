package CapableSimulator.Actors;

import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.Utils.SpawningAgent;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;

public class FertilTile extends WorldActor implements NonBlocking {

    private static final DisplayInformation di = new DisplayInformation(Color.cyan);

    public FertilTile(World world) {
        super("fertilTile", world);
    }

    @Override
    public void act(World world) {
        spawnGrass();
    }

    private void spawnGrass() {
        System.out.println("Fertil tile: spawnGrass");
        Location location = world.getLocation(this);
        world.delete(this);
        Grass grass = new Grass(world);
        new SpawningAgent(world).spawnActorAtLocation(grass, location);
    }

    @Override
    public void doEverySimulationStep() {}

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return di;
    }

}
