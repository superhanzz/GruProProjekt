package CapableSimulator.Utils;

import CapableSimulator.Actors.WorldActor;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class TileFinder {

    /**
     * @param world The world wherein the search occurs.
     * @param isForBlockingActor Whether to search for free non-blocking or blocking tiles.
     * @return Returns a random empty tile if one is found, otherwise returns null.
     */
    public static Location getEmptyTile(World world, boolean isForBlockingActor) {
        List<Location> freeTiles = new ArrayList<>();
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                freeTiles.add(new Location(i, j));
            }
        }
        for (Location location : world.getEntities().values()) {
            if (isForBlockingActor) {
                if (freeTiles.contains(location) && !world.isTileEmpty(location))
                    freeTiles.remove(location);
            } else {
                if (freeTiles.contains(location) && world.getNonBlocking(location) != null)
                    freeTiles.remove(location);
            }
        }

        if(freeTiles.isEmpty() && isForBlockingActor)
            System.out.println("ERROR: No tiles found, for blocking actor");

        Location emptyTile = freeTiles.get(new Random().nextInt(freeTiles.size()));
        return emptyTile;
    }

    /**
     * @param world The world wherein the search occurs.
     * @param actor The actor to search around.
     * @param isForBlockingActor Whether to search for free non-blocking or blocking tiles.
     * @return Returns a random empty tile if one is found, otherwise returns null.
     */
    public static Location getEmptyTileAroundActor(World world, WorldActor actor, boolean isForBlockingActor) {
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
