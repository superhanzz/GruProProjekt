package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Bear extends Predator {

    Location territoryCenter;
    int territoryRadius;

    DisplayInformation diBear = new DisplayInformation(Color.BLACK, "bear");

    public Bear() {
        this.territoryCenter = new Location(0, 0);
        this.territoryRadius = 6;
        this.actorType = "bear";
    }

    public Bear(Location territoryCenter) {
        this.territoryCenter = territoryCenter;
        this.territoryRadius = 1;
        this.actorType = "bear";
    }

    @Override
    protected List<WorldActor> findFoodFromSource(World world, Location[] neighbours) {
        List<WorldActor> worldActorList = new ArrayList<>();
        for (Location location : neighbours) {
            Object o =  world.getTile(location);
            if (o instanceof Wolf || o instanceof Rabbit)
                worldActorList.add((WorldActor) o);
        }
        return worldActorList;
    }

    @Override
    public void move(World world){
        Set<Location> territory = world.getSurroundingTiles(territoryCenter, territoryRadius);
        Location[] neighbours = getPossibleFoodTiles(world, 2);
        List<Location> validNeighbours = new ArrayList<>();
        for (Location neighbour : neighbours) {
            if (territory.contains(neighbour) && world.isTileEmpty(neighbour)) validNeighbours.add(neighbour);
        }
        Location searchLocation;
        if (validNeighbours.isEmpty()) {
            Location nearestLocation = getClosestTile(world,territoryCenter);
            if (nearestLocation == null) return;
            searchLocation =  nearestLocation;
        }
        else {
            Random rand = new Random();
            searchLocation = validNeighbours.get(rand.nextInt(validNeighbours.size()));
        }
        world.move(this, searchLocation);
    }
    // TODO make test that test if a bear moves out of it territory.

    @Override
    public void act(World world) {

        lookForFood(world, 2);
    }

    @Override
    public DisplayInformation getInformation() {
        return diBear;
    }
}
