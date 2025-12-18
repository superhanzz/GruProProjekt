package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.Carcass;

import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.SpawningAgent;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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

    DisplayInformation diBertin = new DisplayInformation(Color.blue, "bertin");
    DisplayInformation diBertinFungi = new DisplayInformation(Color.blue, "bertin-fungi");


    /** Default constructor.
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal.
     * @param age The age of the animal.
     * @param MAX_ENERGY The maximum amount of energy the animal can have.
     */
    public Beartin(World world, int energy, int age, int MAX_ENERGY) {
        super("beartin", world, energy, age, MAX_ENERGY);

        setAnimalSize(CapableEnums.AnimalSize.ADULT);
        setAnimalState(world.isDay() ? CapableEnums.AnimalState.AWAKE : CapableEnums.AnimalState.SLEEPING);

    }

    /* ----- ----- ----- ----- Behavior ----- ----- ----- ----- */

    @Override
    public void act(World world){
        super.act(world);
        if (isDead()) return;

        if (isOnMap()) {
            if (!(tryFight() || lookForFood(1))) {
                move();
            }
        }

    }

    @Override
    public Carcass die() {
        Location location = getLocation();
        PutinEgg egg = new PutinEgg(world);

        if (isInfected()) {
            spreadSpores(world);
            world.delete(this);
        }
        updateOnMap(null, false);
        SpawningAgent.spawnActorAtLocation(world, egg, location);
        setDead();

        return null;
    }


    /* ----- ----- ----- ----- Fighting ----- ----- ----- ----- */

    @Override
    public double getStrengthValue() {
        double strength = 0;
        strength += strengthBonus_AnimalSize.get(getAnimalSize());
        strength += strengthBonus_FungiState.get(getFungiState());
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
        setAnimalState(CapableEnums.AnimalState.AWAKE);
    }

    @Override
    public void onNightFall(){
        setAnimalState(CapableEnums.AnimalState.SLEEPING);
    }

    @Override
    public void onDusk() {

    }

    /* ----- ----- ----- Getters and Setters ----- ----- ----- */

    @Override
    public DisplayInformation getInformation() {
        if (isInfected())
            return diBertinFungi;
        else
            return diBertin;
    }


}
