package CapableSimulator;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Animals implements Actor {

    boolean isOnMap;

    public record Vector(int x, int y) {}


    @Override
    public void act(World world) {

    }

    public void onDay(World world) {

    }

    public void onNight(World world) {

    }

    public void almostNight(World world) {

    }

    public Location getClosestTile(World world, Location tileLocation) {
        Set<Location> tiles = world.getEmptySurroundingTiles(tileLocation);
        if (tiles.isEmpty()) return null;

        Location source = world.getLocation(this);
        Location shortestTile = new Location(1000000, 1000000);

        for (Location l : tiles) {
            if (distance(source, l) < distance(source, shortestTile))
                shortestTile = l;
        }

        return shortestTile;
    }

    double distance(Location A, Location B) {
        Location distanceVector = new Location((A.getX() - B.getX()),(A.getY() - B.getY()));
        double distance = Math.sqrt(Math.pow(distanceVector.getX(), 2) + Math.pow(distanceVector.getY(), 2));
        return distance;
    }

    public void die(World world) {
        world.delete(this);
    }

    public void updateOnMap(World world, Location location, boolean putOnMap) {
        if (putOnMap) {
            if (world.isTileEmpty(location)) {
                world.setTile(location, this);
                isOnMap = true;
            }
        }
        else {
            world.remove(this);
            isOnMap = false;
        }
    }
}
