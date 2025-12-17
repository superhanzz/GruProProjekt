package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public class Beartin extends Predator {

    private static final EnumMap<CapableEnums.AnimalSize, Double> strengthBonus_AnimalSize = new EnumMap<>(CapableEnums.AnimalSize.class);
    static {
        strengthBonus_AnimalSize.put(CapableEnums.AnimalSize.BABY, 50.0);
        strengthBonus_AnimalSize.put(CapableEnums.AnimalSize.ADULT, 70.0);
    }
    private static final EnumMap<CapableEnums.FungiState, Double> strengthBonus_FungiState = new EnumMap<>(CapableEnums.FungiState.class);
    static {
        strengthBonus_FungiState.put(CapableEnums.FungiState.NORMAL, 0.0);
        strengthBonus_FungiState.put(CapableEnums.FungiState.FUNGI, 5.0);
    }

    DisplayInformation diPutin = new DisplayInformation(Color.blue, "bertin");
    //DisplayInformation diPutinSleeping = new DisplayInformation(Color.blue, "putin-sleeping");



    public Beartin(CapableWorld world, int energy, int age, int MAX_ENERGY) {
        super("beartin", world, energy, age, MAX_ENERGY);

        animalSize = CapableEnums.AnimalSize.ADULT;
        animalState = world.isDay() ? CapableEnums.AnimalState.AWAKE : CapableEnums.AnimalState.SLEEPING;
    }



    @Override
    public void act(World world){
        super.act(world);
        if (isDead()) return;

        if(isInfected()) {

        }
        else if (isOnMap()) {
            if (!(tryFight() || lookForFood(1))) {
                move();
            }
        }

    }

    @Override
    protected void doEverySimulationStep() {}




    /* ----- ----- ----- ----- Fighting ----- ----- ----- ----- */

    @Override
    protected boolean tryFight() {
        return super.tryFight();
    }

    @Override
    public double getStrengthValue() {
        double strength = 0;
        strength += strengthBonus_AnimalSize.get(animalSize);
        strength += strengthBonus_FungiState.get(fungiState);
        return strength;
    }

    @Override
    protected boolean isAnimalEnemy(Predator possibleEnemy) {
        switch (possibleEnemy) {
            case Wolf wolf -> {
                List<Wolf> nearbyWolfs = new ArrayList<>();
                wolf.getWolfGang().getNearbyWolfsFromGang(wolf,  nearbyWolfs);
                return nearbyWolfs.size() < 4;
            }
            case Bear bear -> { return true; }
            case Putin putin -> { return true; }
            default -> {
                return false;
            }
        }

    }
    /* ----- ----- ----- ----- Events ----- ----- ----- ----- */

    @Override
    public void onDawn(){
        animalState = CapableEnums.AnimalState.AWAKE;
    }

    @Override
    public void onNightFall(){
        animalState = CapableEnums.AnimalState.SLEEPING;
    }

    @Override
    public void onDusk() {





    }

    /* ----- ----- ----- Getters and Setters ----- ----- ----- */

    @Override
    public DisplayInformation getInformation() {
        return diPutin;
    }


}
