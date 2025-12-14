package CapableSimulator.Utils;

import CapableSimulator.CapableWorld;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathFinder {

    CapableWorld world;

    public PathFinder(CapableWorld world) {
        this.world = world;
    }
    /* ----- ----- ----- ----- PATHFINDING ----- ----- ----- ----- */

    public Location getClosestTile(Location tileLocation) {
        Set<Location> tiles = world.getEmptySurroundingTiles(tileLocation);
        if (tiles.isEmpty()) return null;

        Location source = world.getLocation(this);
        Location shortestTile = new Location(Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (Location l : tiles) {
            if (distance(source, l) < distance(source, shortestTile))
                shortestTile = l;
        }

        return shortestTile;
    }

    public double distance(Location A, Location B) {
        Location distanceVector = new Location((A.getX() - B.getX()),(A.getY() - B.getY()));
        double distance = Math.sqrt(Math.pow(distanceVector.getX(), 2) + Math.pow(distanceVector.getY(), 2));
        return (Math.ceil(distance));
    }

    public void getPossibleMovesForAxis(int axis, List<Integer> possibleMovesList) {
        if (axis == 0) {       // no movement on the given axis
            possibleMovesList.add(0);
            possibleMovesList.add(1);
            possibleMovesList.add(-1);
        }
        else if (axis > 1) {   // movement in the given axis is positive
            possibleMovesList.add(1);
            possibleMovesList.add(0);
        }
        else {  // movement in the given axis is negative
            possibleMovesList.add(-1);
            possibleMovesList.add(0);
        }
    }

    public Location getMoveToTile(Location fromLocation, Location goalLocation) {
        // DEBUG COLORS FOR printf
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";

        Location movementVector = getMovementVector(fromLocation, goalLocation);

        // all possible movements that where the wolf still moves towards the alpha or a target
        List<Integer> possibleMoves_X = new ArrayList<>();  // this list is ordered based on most ideal movement direction
        List<Integer> possibleMoves_Y = new ArrayList<>();  // this list is ordered based on most ideal movement direction

        // x-axis
        getPossibleMovesForAxis(movementVector.getX(), possibleMoves_X);
        getPossibleMovesForAxis(movementVector.getY(), possibleMoves_Y);

        //
        List<Location> moveToLocations = new ArrayList<>();
        // Check which of the possibilities are free
        Location testMoveTo;
        Location moveToLocation = null;
        for (Integer dx : possibleMoves_X) {
            for (Integer dy : possibleMoves_Y) {
                int x = Math.clamp(fromLocation.getX() + dx, 0, world.getSize() - 1);
                int y = Math.clamp(fromLocation.getY() + dy, 0, world.getSize() - 1);

                testMoveTo = new Location(x, y);

                /*if (false){
                    System.out.println("(" + dx + "," + dy + ") -> (" + x + "," + y + ")\t\t Original distance was: " + distance);
                    double newDistance = distance(testMoveTo, alphaLocation);
                    System.out.println("\t\t\t\t\t New distance is: " + distance(testMoveTo, alphaLocation));

                    boolean isShorter = distance >= newDistance;
                    String truth = isShorter ? GREEN + "Shorter" + RESET : RED + "Longer" + RESET;
                    System.out.printf("\t\t\t\t\t " + truth + "%n");
                }*/
                //if (x > 9 || y > 9) throw new RuntimeException(x + "," + y + "\t not a valid tile");


                Object o = world.getTile(testMoveTo);
                if (o == null || o instanceof NonBlocking) {
                    moveToLocations.add(testMoveTo);
                }
                /*
                if (o instanceof NonBlocking || !(o instanceof Animals)) {
                    moveToLocations.add(testMoveTo);
                }
                */
            }
        }
        if (!moveToLocations.isEmpty()) {
            double shortestDistance = Double.MAX_VALUE;
            for (Location location : moveToLocations) {
                double distance = distance(location, goalLocation);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    moveToLocation = location;
                }
            }
        }
        return moveToLocation;
    }

    public Location getMovementVector(Location start, Location end) {
        int x = end.getX() - start.getX();
        int y = end.getY() - start.getY();
        return new Location(x, y);
    }

}
