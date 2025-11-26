package CapableSimulator;

import itumulator.world.Location;
import itumulator.world.World;

public class Bear extends Predator {

    Location territoryCenter;
    int territoryRadius;

    public Bear() {
        this.territoryCenter = new Location(0, 0);
        this.territoryRadius = 4;
        this.actorType = "bear";
    }

    public Bear(Location territoryCenter) {
        this.territoryCenter = territoryCenter;
        this.territoryRadius = 4;
        this.actorType = "bear";
    }


    @Override
    public void act(World world) {

        lookForFood(world, 2);
    }
}
