package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class BerryBush extends WorldActor {

    private final int energyValue;

    private final double berrySpawnChance;

    private boolean hasBerrys;

    private DisplayInformation diBush = new DisplayInformation(Color.cyan, "bush");
    private DisplayInformation diBerry = new DisplayInformation(Color.cyan, "bush-berries");

    public BerryBush(CapableWorld world) {
        super("berry", world);
        this.energyValue = 5;
        this.hasBerrys = false;
        this.berrySpawnChance = 0.10;
    }

    @Override
    public int getEnergyValue() {
        return energyValue;
    }

    @Override
    public void act(World world) {

    }

    public void trySpawnBerrys() {
        if (hasBerrys) return;

        Random rand = new Random();
        if (rand.nextDouble() < (1 - berrySpawnChance)) { // TODO test that the spawn chance is actually true
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
}
