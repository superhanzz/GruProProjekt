package CapableSimulator.Actors;

import CapableSimulator.Actors.Fungis.Fungi;
import CapableSimulator.Actors.Fungis.FungiSpore;
import CapableSimulator.Actors.Fungis.Fungus;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Carcass extends WorldActor implements Fungi {

    private int energy;
    private final static int MAX_ENERGY_CONSUME_AMOUNT = 5;

    FungiSpore fungiSpore;

    DisplayInformation diCarcass = new DisplayInformation(Color.BLACK, "carcass");
    DisplayInformation diCarcassSmall = new DisplayInformation(Color.BLACK, "carcass-small");

    protected CapableEnums.AnimalSize size;
    private int age;

    /* ----- ----- ----- ----- Constructors ----- ----- ----- ----- */

    public Carcass(CapableWorld world) {
        super("carcass",  world);
        this.energy = 0;
        this.size = CapableEnums.AnimalSize.ADULT;
    }

    public Carcass(CapableWorld world, int energy, CapableEnums.AnimalSize size) {
        super("carcass", world);
        this.energy = energy;
        this.size = size;
        age = 0;
    }

    /* ----- ----- ----- ----- Behavior ----- ----- ----- ----- */

    public int getConsumed(World world, int consumerMissingEnergy) {
        int consumeAmount = Math.clamp(MAX_ENERGY_CONSUME_AMOUNT, 0 , energy);

        if (consumerMissingEnergy < consumeAmount) {
            consumeAmount = consumeAmount - (consumeAmount - consumerMissingEnergy);
        }
        energy -= consumeAmount;
        if (energy <= 0) {
            decompose();
        }
        return consumeAmount;
    }

    @Override
    public void act(World world) {
        super.act(world);
        age++;
        if (age >= 30) {world.delete(this);}
    }

    /** Handles when the carcass has run out of energy and should be removed from the map.
     *  If the carcass is infected by a fungi then it becomes a fungus, otherwise it disappears */
    public void decompose() {
        switch(fungiState) {
            case NORMAL:
                world.delete(this);
                break;
            case FUNGI:
                becomeFungus();
                break;
        }
    }

    /** Removes this actor from the world map, and spawns a new fungus at the location where this actor was */
    private void becomeFungus() {
        Location decomposeLocation = getLocation();
        world.delete(this);
        Fungus fungus = new Fungus(world);
        world.setTile(decomposeLocation, fungus);
    }

    /* ----- ----- ----- ----- Fungi Related ----- ----- ----- ----- */

    @Override
    public void becomeInfected() {
        fungiSpore = new FungiSpore(world);
        fungiState = CapableEnums.FungiState.FUNGI;
    }

    @Override
    public boolean isInfected() {
        return (fungiSpore != null);
    }

    @Override
    public FungiSpore getFungiSpore() {
        return fungiSpore;
    }

    /* ----- ----- ----- ----- Events ----- ----- ----- ----- */

    @Override
    public void spreadSpores(CapableWorld world) {
        if (!isInfected()) return;

        fungiSpore.spread(world.getLocation(this));
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    @Override
    public int getEnergyValue(){
        return energy;
    }

    @Override
    public DisplayInformation getInformation(){
        //System.out.println("et eller andet");
        if(size.equals(CapableEnums.AnimalSize.ADULT)) return diCarcass;
        else return diCarcassSmall;
    }
}
