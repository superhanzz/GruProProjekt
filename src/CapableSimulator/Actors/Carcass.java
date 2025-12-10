package CapableSimulator.Actors;

import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.world.World;

import java.awt.*;

public class Carcass extends WorldActor {

    private int energy;
    private final static int MAX_ENERGY_CONSUME_AMOUNT = 5;

    DisplayInformation diCarcass = new DisplayInformation(Color.BLACK, "carcass");
    DisplayInformation diCarcassSmall = new DisplayInformation(Color.BLACK, "carcass-small");

    protected CapableEnums.AnimalSize size;
    private int age;

    public Carcass(int energy, CapableEnums.AnimalSize size) {
        super("carcass");
        this.energy = energy;
        this.size = size;
        age = 0;
    }

    public Carcass() {
        super("carcass");
        this.energy = 0;
        this.size = CapableEnums.AnimalSize.ADULT;
    }

    public int getConsumed(World world, int consumerMissingEnergy) {
        int consumeAmount = Math.clamp(MAX_ENERGY_CONSUME_AMOUNT, 0 , energy);

        if (consumerMissingEnergy < consumeAmount) {
            consumeAmount = consumeAmount - (consumeAmount - consumerMissingEnergy);
        }
        energy -= consumeAmount;
        if (energy <= 0) {
            //System.out.println("\t\tCarcass has been consumed!");
            world.delete(this);
        }
        return consumeAmount;
    }

    protected void decompose(World world) {
        switch(fungiState) {
            case NORMAL:
                world.delete(this);
                break;
            case FUNGI:

        }
    }

    @Override
    public void act(World world) {
        super.act(world);
        age++;
        if (age >= 30) {world.delete(this);}
    }

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
