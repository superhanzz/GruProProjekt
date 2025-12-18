package CapableSimulator;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Fungis.Fungi;
import CapableSimulator.Actors.Shelter.WolfDen;
import CapableSimulator.Actors.WorldActor;
import CapableSimulator.Utils.*;
import itumulator.display.Canvas;
import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.List;
import java.util.Map;

public class CapableSimulator extends Simulator {

    private SpawningAgent spawningAgent;
    private WorldUtils worldUtils;
    private Parser parser;

    //private Map<String, InputFileStruct> inputMap;
    private CapableEnums.DayNightStatus dayNightStatus;

    private boolean preSetupComplete = false;
    private int dayNumber;

    /* ----- Time Specific Events ----- */
    private static final int time_Dusk = 9;
    private static final int time_Midnight = 14;
    private static final int time_NightFall = 10;
    private static final int time_Dawn = 0;

    /* ----- ----- ----- ----- Constructors ----- ----- ----- ----- */
    public  CapableSimulator(World world, Canvas canvas, int delay) {
        super(world, canvas, delay);
    }

    /* ----- ----- ----- ----- Pre-Simulation ----- ----- ----- ----- */

    /** Prepares everything needed for the simulation, and spawns all the initial WorldActors into the world.
     * */
    public void prepareSimulation() {
        if (parser == null) throw new NullPointerException("Parser has not been set");

        spawningAgent = new SpawningAgent(getWorld());
        worldUtils = new WorldUtils(getWorld());

        dayNightStatus = getWorld().isDay() ? CapableEnums.DayNightStatus.DAY : CapableEnums.DayNightStatus.NIGHT;

        spawningAgent.handleSpawnCycle(parser.getInputMap(), true); // spawn all initial actors into the world

        spawningAgent.handleSpawnCycle(parser.getInputMap(), false);

        dayNumber = 0;

        preSetupComplete = true;
    }

    /* ----- ----- ----- ----- Simulation ----- ----- ----- ----- */

    public void runSimulation() {
        runSimulation(CapableEnums.SimulationType.NORMAL);

    }

    /** Initiates the simulation
     * @param simulationType The type of simulation, NORMAL is the default
     * */
    public void runSimulation(CapableEnums.SimulationType simulationType) {
        if (!preSetupComplete) prepareSimulation();
        switch (simulationType) {
            case NORMAL:
                //run();
                break;
            case TEST:
                System.out.println("Testing Simulator");
                break;
        }
    }

    @Override
    public void simulate() {
        super.simulate();

        //System.out.println(getSteps());

        // Initiates time based events
        switch (getDayNightStatus()) {
            case DAWN:
                onDawn();
                break;

            case DUSK:
                onDusk();
                break;

            case NIGHT:
                onNightFall();
                break;

            case MIDNIGHT:
                onMidnight();
                break;

            default:
                break;
        }

        if (getWorld().getCurrentTime() % 5 == 0) {
            fungiSpreadSporesOfType(CapableEnums.FungiType.FUNGUS);
            //fungiSpreadSporesOfType(CapableEnums.FungiType.CORDYCEP);
        }
    }

    /* ----- ----- ----- ----- Event Handling ----- ----- ----- ----- */

    /** Handels the events related to dawn */
    private void onDawn() {
        for (Animal animal : worldUtils.getAllAnimals()) {
            animal.onDawn();
        }
    }

    /** Handels the events related to dusk */
    private void onDusk() {
        for (Animal animal : worldUtils.getAllAnimals()) {
            animal.onDusk();
        }
    }

    /** Handels the events related to nightfall */
    private void onNightFall() {
        for (Animal animal : worldUtils.getAllAnimals()) {
            animal.onNightFall();
        }
    }

    /** Handels the events related to midnight */
    private void onMidnight() {
        initiateWolfMating();
    }

    /** Initiates the mating of wolf's */
    private void initiateWolfMating() {
        Map<Object, Location> entities = getWorld().getEntities();
        for (Object o : entities.keySet()) {
            if (o instanceof WolfDen wolfDen) {
                wolfDen.makeCup();
            }
        }
    }

    private void fungiSpreadSporesOfType(CapableEnums.FungiType fungiType) {
        for (Object o : getWorld().getEntities().keySet()) {
            if  (o instanceof Fungi fungi && fungi.isCarrierOfType(fungiType)) {
                fungi.spreadSpores(getWorld());
            }
        }
    }

    public CapableEnums.DayNightStatus getDayNightStatus() {
        CapableEnums.DayNightStatus status;

        switch (getWorld().getCurrentTime()) {
            case time_Dawn:
                status = CapableEnums.DayNightStatus.DAWN;
                break;
            case time_Dusk:
                status = CapableEnums.DayNightStatus.DUSK;
                break;
            case time_NightFall:
                status = CapableEnums.DayNightStatus.NIGHT;
                break;
            case time_Midnight:
                status = CapableEnums.DayNightStatus.MIDNIGHT;
                break;
            default:
                status = CapableEnums.DayNightStatus.DAY;
        }
        return status;
    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    public SpawningAgent getSpawningAgent() {
        return spawningAgent;
    }

    /* ----- ----- ----- ----- Setters ----- ----- ----- ----- */

    public void setParser(Parser parser) {this.parser = parser;}
}
