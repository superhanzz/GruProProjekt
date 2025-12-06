package CapableSimulator.Actors;

import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WolfDen extends WorldActor implements NonBlocking {

    WolfGang owners;
    private Set<Wolf> wolfsInDen;
    private final double procreationSuccessChance;

    DisplayInformation di = new DisplayInformation(Color.ORANGE, "hole");


    public WolfDen(WolfGang owners) {
        super("wolfDen");
        this.owners = owners;
        wolfsInDen = new HashSet<>();
        procreationSuccessChance = 1;
    }

    public void wolfEnteredDen(Wolf wolf) {
        wolfsInDen.add(wolf);
    }

    public void wolfLeftDen(Wolf wolf) {
        wolfsInDen.remove(wolf);
    }

    public void makeCup(World world) {
        if (wolfsInDen.size() < 2) return;  // there needs to be more than 2 wolf's in the den before procreation can occur
        // checks the number of grown wolf's in the den
        int grownWolfsInDen = 0;
        for (Wolf wolf : wolfsInDen) {
            if (wolf.isAnimalAdult()) grownWolfsInDen++;
        }
        if (grownWolfsInDen < 2) return;    // There needs to be more than 2 grown wolf's in the den before procreation can occur
        if (new Random().nextDouble() < procreationSuccessChance) {
            Wolf cup = new Wolf(owners, owners.Alpha, this, AnimalSize.BABY, AnimalState.SLEEPING);
            owners.addWolfToGang(cup);
            System.out.println("Wolf cup created");
            world.add(cup);
        }
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
