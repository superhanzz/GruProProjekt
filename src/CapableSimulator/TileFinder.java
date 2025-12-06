package CapableSimulator;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileFinder {


    Location getEmptyTile(World world, boolean isForBlockingActor) {
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


}
