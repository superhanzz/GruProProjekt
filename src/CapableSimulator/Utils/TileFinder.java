package CapableSimulator.Utils;

import CapableSimulator.Actors.WorldActor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class TileFinder {

    World world;

    public TileFinder(World world) {
        this.world = world;
    }

    public Location getEmptyTile(World world, boolean isForBlockingActor) {
        //TODO Make this function chose between the free tiles in the world, by generating a map symbolizing all the possible tiles and then subtracting all entities, and chosing a random tile from the remaining.

        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        List<Location> freeTiles = new ArrayList<>();
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                freeTiles.add(new Location(i, j));
            }
        }
        for (Location location : world.getEntities().values()) {
            if (isForBlockingActor) {
                if (freeTiles.contains(location) && !world.isTileEmpty(location)) freeTiles.remove(location);
            } else {
                if (freeTiles.contains(location) && world.getNonBlocking(location) != null) freeTiles.remove(location);
            }
        }

        for (Location location : freeTiles) {
            if (isForBlockingActor) {
                if (!world.isTileEmpty(location))
                    System.out.printf(RED + "Occupied Tile in Map " + RESET + "%n");  // DEBUG
            } else {
                if (!world.isTileEmpty(location) && world.getNonBlocking(location) != null)
                    System.out.printf(RED + "Occupied Tile in Map " + RESET + "%n");  // DEBUG
            }

        }

        Location emptyTile = freeTiles.get(new Random().nextInt(freeTiles.size()));
        return emptyTile;
    }

    public Location getEmptyTileAroundActor(WorldActor actor, boolean isForBlockingActor) {
        if (world == null || actor == null) {
            if (world == null) throw new NullPointerException("In getEmptyTileAroundActor(): World is null");
            else throw new NullPointerException("In getEmptyTileAroundActor(): Actor is null");
        }

        Location emptyTile;
        Set<Location> neighboringTiles;
        List<Location> emptyTiles;

        if (isForBlockingActor) {
            neighboringTiles = world.getEmptySurroundingTiles(world.getLocation(actor));

            if (neighboringTiles.isEmpty()) return null;
            emptyTiles =  new ArrayList<>(neighboringTiles);

            emptyTile = emptyTiles.get(new Random().nextInt(emptyTiles.size()));
        }
        else {
            neighboringTiles = world.getSurroundingTiles(world.getLocation(actor));
            emptyTiles =  new ArrayList<>();
            for (Location tile : neighboringTiles) {
                if (world.getNonBlocking(tile) == null) emptyTiles.add(tile);
            }
            if (emptyTiles.isEmpty()) return null;

            emptyTile = emptyTiles.get(new Random().nextInt(emptyTiles.size()));
        }

        return emptyTile;
    }


}
