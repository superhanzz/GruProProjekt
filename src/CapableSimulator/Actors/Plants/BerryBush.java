package CapableSimulator.Actors.Plants;

import CapableSimulator.Actors.WorldActor;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class BerryBush extends WorldActor {

    private final int energyValue;

    private static final double BERRY_SPAWN_CHANCE = 0.1;

    private boolean hasBerrys;

    private DisplayInformation diBush = new DisplayInformation(Color.cyan, "bush");
    private DisplayInformation diBerry = new DisplayInformation(Color.cyan, "bush-berries");

    public BerryBush(World world) {
        super("berry", world);
        this.energyValue = 5;
        this.hasBerrys = false;
    }

    @Override
    public int getEnergyValue() {
        return energyValue;
    }

    @Override
    public void act(World world) {
        trySpawnBerrys();
    }

    public void trySpawnBerrys() {
        if (hasBerrys) return;

        Random rand = new Random();
        if (rand.nextDouble() < BERRY_SPAWN_CHANCE) {
            hasBerrys = true;
        }
    }

    public boolean getBerryStatus(){
        return hasBerrys;
    }

    public int getEaten() {
        if (!hasBerrys) return 0;

        hasBerrys = false;
        return energyValue;
    }

    public void updateBerryStatus(boolean berryStatus){
        if(berryStatus){
            hasBerrys = false;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return (hasBerrys ? diBerry : diBush);
    }

    public static double getBerrySpawnChance(){
        return BERRY_SPAWN_CHANCE;
    }
}
