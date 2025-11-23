package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Random;

public class Rabbit implements Actor {

    private int energy;

    public Rabbit() {
        this.energy = 15;
    }

    @Override
    public void act(World world) {
        lookForFood(world);
        energy--;
        if(energy <= 0) {
            die(world);
        }
    }

    private void lookForFood(World world) {
        Location[] emptyTiles = world.getEmptySurroundingTiles(world.getLocation(this)).toArray(new Location[0]);
        if (emptyTiles.length <= 0) {
            return;
        }
        Random rand = new Random();
        Location searchLocation = emptyTiles[rand.nextInt(emptyTiles.length)];
        world.move(this, searchLocation);

        Grass foundGrass = searchTile(world);
        if (foundGrass != null) {
            eatGrass(world, foundGrass);
        }
    }

    private void tryReproduce(World world) {

    }

    private Grass searchTile(World world) {
        Object nonBlockingObject = world.getNonBlocking(world.getLocation(this));
        return nonBlockingObject instanceof Grass ? (Grass) nonBlockingObject : null;
    }

    private void eatGrass(World world,  Grass grass) {
        world.delete(grass);
        energy++;
    }

    void die(World world) {
        world.delete(this);
    }
}
