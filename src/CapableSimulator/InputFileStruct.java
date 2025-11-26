package CapableSimulator;

import itumulator.world.Location;

public class InputFileStruct {

    // The actorType
    public String actorType;

    // Amount to spawn
    public int minAmount;
    public int maxAmount;

    // If a static spawn i declared
    public Location staticSpawnLocation;

    // If 2 or more entries of the same type is present in the input file
    public boolean isDelayedSpawn;


    /**
     * */
    /*InputFileStruct(String actorType, int minAmount, boolean isDelayedSpawn) {
        this.actorType = actorType;
        this.minAmount = minAmount;
        this.isDelayedSpawn = isDelayedSpawn;

        this.maxAmount = 0;
        this.staticSpawnLocation = null;
    }*/

    /**
     * */
    /*InputFileStruct(String actorType, int minAmount, int maxAmount, boolean isDelayedSpawn) {
        this.actorType = actorType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.isDelayedSpawn = isDelayedSpawn;

        this.staticSpawnLocation = null;
    }*/

    /**
     * */
    public InputFileStruct(String actorType, int minAmount, int maxAmount, Location staticSpawnLocation,  boolean isDelayedSpawn) {
        this.actorType = actorType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.staticSpawnLocation = staticSpawnLocation;
        this.isDelayedSpawn = isDelayedSpawn;
    }

    /*InputFileStruct(InputFileStruct other, boolean isDelayedSpawn) {
        this.actorType = other.actorType;
        this.minAmount = other.minAmount;
        this.maxAmount = other.maxAmount;
        this.staticSpawnLocation = other.staticSpawnLocation;
        this.isDelayedSpawn = isDelayedSpawn == ;
    }*/



}
