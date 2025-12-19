package CapableSimulator.Actors;

import CapableSimulator.Actors.Fungis.Fungi;
import CapableSimulator.Actors.Fungis.FungiSpore;
import CapableSimulator.Actors.Fungis.Fungus;
import CapableSimulator.Utils.CapableEnums;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class Carcass extends WorldActor implements Fungi, EnergyConsumer {

    private int energy;

    private final static int MAX_ENERGY_CONSUME_AMOUNT = 5;

    private static final int DEFAULT_START_ENERGY = 5;

    private final CapableEnums.AnimalSize size;
    private CapableEnums.FungiState fungiState;

    private FungiSpore fungiSpore;

    private static final DisplayInformation diCarcass = new DisplayInformation(Color.BLACK, "carcass");
    private static final DisplayInformation diCarcassSmall = new DisplayInformation(Color.BLACK, "carcass-small");

    /* ----- ----- ----- ----- Constructors ----- ----- ----- ----- */

    /** Default constructor, where energy initialised as {@value DEFAULT_START_ENERGY}, and the carcass size is set as big.
     *  The actor type is set as "carcass".
     * @param world The world wherein the actor exists.
     */
    public Carcass(World world) {
        super("carcass",  world);

        energy = DEFAULT_START_ENERGY;
        size = CapableEnums.AnimalSize.ADULT;
        fungiState = CapableEnums.FungiState.NORMAL;
    }

    /**
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal.
     * @param size The size of the actor.
     */
    public Carcass(World world, int energy, CapableEnums.AnimalSize size) {
        super("carcass", world);

        this.energy = energy;
        this.size = size;
        fungiState = CapableEnums.FungiState.NORMAL;
    }

    /* ----- ----- ----- ----- Behavior ----- ----- ----- ----- */

    @Override
    public void act(World world) {
        doEverySimulationStep();
    }


    /** Consumes a portion of the carcass, the amount consumed is decided by the instigators missing energy and
     * a maximal amount: {@value MAX_ENERGY_CONSUME_AMOUNT}.
     * If the whole carcass is consumed i.e. energy = 0, the carcass decomposes.
     * @param consumerMissingEnergy is the instigators missing energy amount.
     * @return Return the consumed amount of energy, the maximal amount is: {@value MAX_ENERGY_CONSUME_AMOUNT}.
     */
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

    /** Handles when the carcass has been fully consumed, and should be removed from the map.
     *  If the carcass is infected by a fungi then it becomes a fungus, otherwise it disappears .
     */
    public void decompose() {
        switch(getFungiState()) {
            case NORMAL:
                world.delete(this);
                break;
            case FUNGI:
                becomeFungus();
                break;
        }
    }

    /** Removes this actor from the world map, and spawns a new fungus at the location where the carcass was.
     */
    private void becomeFungus() {
        Location decomposeLocation = getLocation();
        world.delete(this);
        Fungus fungus = new Fungus(world);
        world.setTile(decomposeLocation, fungus);
    }

    /* ----- ----- ----- ----- Fungi Related ----- ----- ----- ----- */

    @Override
    public void spreadSpores(World world) {
        if (!isInfected()) return;

        fungiSpore.spread(world.getLocation(this));
    }

    @Override
    public void becomeInfected() {
        fungiSpore = new FungiSpore(world);
        setFungiState(CapableEnums.FungiState.FUNGI);
    }

    @Override
    public boolean isInfected() {
        return (fungiSpore != null);
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
        energy -= getDecayRate();
        if (energy <= 0) {
            decompose();
        }
    }

    @Override
    public int getDecayRate() {
        return 1;
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    @Override
    public int getEnergyValue(){
        return energy;
    }

    @Override
    public DisplayInformation getInformation(){
        //System.out.println("et eller andet");
        if(size.equals(CapableEnums.AnimalSize.ADULT))
            return diCarcass;
        else return
                diCarcassSmall;
    }

    /** Gets the actors fungi state.
     * @return Returns the actors fungi state.
     */
    public CapableEnums.FungiState getFungiState() {
        return fungiState;
    }

    /** Updates the actors fungi state.
     * @param fungiState The new fungi state.
     */
    private void setFungiState(CapableEnums.FungiState fungiState) {
        this.fungiState = fungiState;
    }
}
