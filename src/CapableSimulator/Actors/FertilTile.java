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

    /** Default constructor.
     * @param world The world wherein the actor exists.
     */
    public FertilTile(World world) {
        super("fertilTile", world);
    }

    @Override
    public void act(World world) {
        spawnGrass();
    }

    /** Spawns a grass actor at the location of the tile, then this actor is deleted.
     */
    private void spawnGrass() {
        System.out.println("Fertil tile: spawnGrass");
        Location location = world.getLocation(this);
        world.delete(this);
        Grass grass = new Grass(world);
        SpawningAgent.spawnActorAtLocation(world, grass, location);
    }

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return di;
    }

}
