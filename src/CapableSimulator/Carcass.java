package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;

import java.awt.*;

public class Carcass extends WorldActor {

    private final int energy;

    DisplayInformation diCarcass = new DisplayInformation(Color.BLACK, "carcass");
    DisplayInformation diCarcassSmall = new DisplayInformation(Color.BLACK, "carcass-small");

    protected AnimalSize size;
    private int age;

    public Carcass(int energy, AnimalSize size) {
        super("carcass");
        this.energy = energy;
        this.size = size;
        age = 0;
    }

    public Carcass() {
        super("carcass");
        this.energy = 0;
        this.size = AnimalSize.ADULT;
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
        if(size.equals(AnimalSize.ADULT)) return diCarcass;
        else return diCarcassSmall;
    }
}
