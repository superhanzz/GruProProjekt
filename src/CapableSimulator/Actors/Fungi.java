package CapableSimulator.Actors;

import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.awt.*;

public class Fungi extends WorldActor {

    int energy;
    CapableEnums.AnimalSize size;

    /** The default energy the fungi will spawn with, the value is for a big fungi, if a fungi i specified to be small, the value is halvfed.
     * */
    private static final int DEFAULT_ENERGY_VALUE = 20;

    /** The default size of a fungi when spawned.
     * */
    private static final CapableEnums.AnimalSize DEFAULT_SIZE = CapableEnums.AnimalSize.ADULT;

    /** The display information for a big fungi
     * */
    DisplayInformation diBigFungi = new DisplayInformation(Color.YELLOW, "fungi");
    /** The display information for a small fungi
     * */
    DisplayInformation diSmallFungi = new DisplayInformation(Color.YELLOW, "fungi-small");


    /* ----- ----- ----- Constructors ----- ----- ----- */
    /** The default constructor where the only param is the actorType.
     *  Both 'energy' and 'size' is set to default value
     * */
    public Fungi(String actorType) {
        super(actorType);
        this.energy = DEFAULT_ENERGY_VALUE;
        this.size = DEFAULT_SIZE;
    }

    /** This constructor should be used when converting a 'Carcass' to a fungi.
     *  The fungi's size decides the 'energy' value.
     * */
    public Fungi(String actorType, CapableEnums.AnimalSize size) {
        super(actorType);
        this.size = size;

        this.energy = switch (size) {
            case BABY ->  (DEFAULT_ENERGY_VALUE / 2);
            case ADULT ->   DEFAULT_ENERGY_VALUE;
        };
    }
    /** No actual use at the moment.
     * */
    public Fungi(String actorType, CapableEnums.AnimalSize size, int energy) {
        super(actorType);
        this.energy = energy;
        this.size = size;
    }

    /* ----- ----- ----- Behavior ----- ----- ----- */

    @Override
    public void act(World world) {

    }

    private void spreadSpores() {

    }


    /* ----- ----- ----- Getters and Setters ----- ----- ----- */


    @Override
    public int getEnergyValue() {
        return energy;
    }

    @Override
    public DisplayInformation getInformation() {
        DisplayInformation returnValue = switch (size) {
            case BABY -> diSmallFungi;
            case ADULT -> diBigFungi;
        };
        return returnValue;
    }
}
