package CapableSimulator.Actors.Fungis;

import CapableSimulator.Actors.EnergyConsumer;
import CapableSimulator.Actors.FertilTile;

import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.SpawningAgent;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Fungus extends WorldActor implements Fungi, EnergyConsumer {

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

    /** The default constructor.
     * @param world The world wherein the actor exists.
     */
    public Fungus(World world) {
        super("fungus", world );
        this.energy = DEFAULT_ENERGY_VALUE;
        this.size = DEFAULT_SIZE;

        fungiSpore = new FungiSpore(world);
    }

    /** This constructor should be used when converting a 'Carcass' to a fungi.
     * @param world The world wherein the actor exists.
     * @param size The size the fungus should be.
     */
    public Fungus(World world, CapableEnums.AnimalSize size) {
        super("fungus", world);
        this.size = size;

        fungiSpore = new FungiSpore(world);

        this.energy = switch (size) {
            case BABY ->  (DEFAULT_ENERGY_VALUE / 2);
            case ADULT ->   DEFAULT_ENERGY_VALUE;
        };
    }
    /** Constructor for testing.
     * @param world The world wherein the actor exists.
     * @param size The size the fungus should be.
     * @param energy The initial energy in the fungus.
     */
    public Fungus(World world, CapableEnums.AnimalSize size, int energy) {
        super("fungus", world);
        this.energy = energy;
        this.size = size;

        fungiSpore = new FungiSpore(world);
    }

    /** Constructor for testing.
     * @param world The world wherein the actor exists.
     * @param energy The initial energy in the fungus.
     */
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

    /** Handles when the fungus has decomposed i.e. it has no more energy.
     */
    public void decompose() {
        Location location = world.getLocation(this);
        world.delete(this);
        if (world.getNonBlocking(location) == null) {
            makeFertilTile(location);
        }
    }

    /** Spawns a fertile tile at the given location.
     * @param location The location to spawn the fertile tile at.
     * @throws NullPointerException throws exception if location is null.
     */
    public void makeFertilTile(Location location) {
        if (location == null)
            throw new NullPointerException("Location is null");

        FertilTile ft = new FertilTile(world);
        SpawningAgent.spawnActorAtLocation(world, ft, location);
    }

    /* ----- ----- ----- Fungi ----- ----- ----- */

    @Override
    public void spreadSpores(World world) {
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

    /* ----- ----- ----- ----- Energy Consumer ----- ----- ----- ----- */

    @Override
    public void doEverySimulationStep() {
        if (world.isNight()) return;

        energy -= getDecayRate();
        if (energy <=0 ) {
            decompose();
        }
    }

    @Override
    public int getDecayRate() {
        return 1;
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
