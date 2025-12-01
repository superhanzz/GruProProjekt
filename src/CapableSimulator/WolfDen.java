package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class WolfDen extends WorldActor implements NonBlocking {

    WolfGang owners;

    DisplayInformation di = new DisplayInformation(Color.ORANGE, "hole");

    public WolfDen(WolfGang owners) {
        this.owners = owners;
    }

    @Override
    public int getEnergyValue() {
        return 0;
    }

    @Override
    public void act(World world) {

    }

    @Override
    public DisplayInformation getInformation() {
        return di;
    }
}
