package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.FertilTile;

import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.SpawningAgent;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Fungus extends WorldActor implements Fungi {

    int energy;
    CapableEnums.AnimalSize size;
    private final FungiSpore fungiSpore;

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
    public Fungus(World world) {
        super("fungus", world );
        this.energy = DEFAULT_ENERGY_VALUE;
        this.size = DEFAULT_SIZE;

        fungiSpore = new FungiSpore(world);
    }

    /** This constructor should be used when converting a 'Carcass' to a fungi.
     *  The fungi's size decides the 'energy' value.
     * */
    public Fungus(World world, CapableEnums.AnimalSize size) {
        super("fungus", world);
        this.size = size;

        fungiSpore = new FungiSpore(world);

        this.energy = switch (size) {
            case BABY ->  (DEFAULT_ENERGY_VALUE / 2);
            case ADULT ->   DEFAULT_ENERGY_VALUE;
        };
    }
    /** No actual use at the moment.
     * */
    public Fungus(World world, CapableEnums.AnimalSize size, int energy) {
        super("fungus", world);
        this.energy = energy;
        this.size = size;

        fungiSpore = new FungiSpore(world);
    }

    public Fungus(World world, int energy) {
        super("fungus", world);
        this.energy = energy;
        this.size = DEFAULT_SIZE;

        fungiSpore = new FungiSpore(world);
    }
    /* ----- ----- ----- Behavior ----- ----- ----- */

    @Override
    public void act(World world) {
        doEverySimulationStep();
    }

    @Override
    protected void doEverySimulationStep() {
        if (world.isNight()) return;

        energy--;
        if (energy <=0 ) {
            decompose();
        }
    }

    public void decompose() {
        Location location = world.getLocation(this);
        world.delete(this);
        if (world.getNonBlocking(location) == null) {
            becomeFertilTile(location);
        }
    }

    public void becomeFertilTile(Location location) {
        FertilTile ft = new FertilTile(world);
        SpawningAgent.spawnActorAtLocation(world, ft, location);
    }

    /* ----- ----- ----- Getters and Setters ----- ----- ----- */

    @Override
    public void spreadSpores(World world) {
        //System.out.println("Spreading spore in fungus");
        fungiSpore.spread(world.getLocation(this));
    }

    @Override
    public void becomeInfected() {}

    @Override
    public boolean isInfected() {
        return true;
    }

    @Override
    public FungiSpore getFungiSpore() {
        return fungiSpore;
    }

    @Override
    public boolean isCarrierOfType(CapableEnums.FungiType fungiType){
        return FungiSpore.getCanCarryType(this.getClass()).equals(fungiType);
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
