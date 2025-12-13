package CapableSimulator;

import CapableSimulator.Actors.Animals;
import CapableSimulator.Actors.WolfDen;
import CapableSimulator.Actors.WorldActor;
import CapableSimulator.Utils.*;
import itumulator.display.Canvas;
import itumulator.simulator.Simulator;
import itumulator.world.World;

import java.util.List;
import java.util.Map;

public class CapableSimulator extends Simulator {

    private CapableWorld world;
    private CapableProgram program;

    private SpawningAgent spawningAgent;
    private WorldUtils worldUtils;
    private EntityHandler entityHandler;
    private Parser parser;

    //private Map<String, InputFileStruct> inputMap;
    private CapableEnums.DayNightStatus dayNightStatus;

    private boolean preSetupComplete = false;
    private int dayNumber;

    /* ----- Time Specific Events ----- */
    private static final int time_JustBeforeNight = 9;
    private static final int time_WolfMatingTime = 14;
    private static final int time_NightFall = 10;
    private static final int time_DayBreak = 0;

    /* ----- ----- ----- ----- Constructors ----- ----- ----- ----- */
    public  CapableSimulator(World world, Canvas canvas, int delay) {
        super(world, canvas, delay);
        this.world = (CapableWorld) world;
    }

    /* ----- ----- ----- ----- Pre-Simulation ----- ----- ----- ----- */

    /** Prepares everything needed for the simulation, and spawns all the initial WorldActors into the world.
     * */
    public void prepareSimulation() {
        if (parser == null) throw new NullPointerException("Parser has not been set");

        spawningAgent = new SpawningAgent(world);
        worldUtils = new WorldUtils(world);
        entityHandler = new EntityHandler(world);


        dayNightStatus = world.isDay() ? CapableEnums.DayNightStatus.DAY : CapableEnums.DayNightStatus.NIGHT;

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
                run();
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
        switch (world.getDayNightStatus()) {
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
    }

    /* ----- ----- ----- ----- Event Handling ----- ----- ----- ----- */

    /** Handels the events related to dawn */
    private void onDawn() {
        for (Animals animal : worldUtils.getAllAnimals()) {
            animal.onDawn();
        }
    }

    /** Handels the events related to dusk */
    private void onDusk() {
        for (Animals animal : worldUtils.getAllAnimals()) {
            animal.onDusk();
        }
    }

    /** Handels the events related to nightfall */
    private void onNightFall() {
        for (Animals animal : worldUtils.getAllAnimals()) {
            animal.onNightFall();
        }
    }

    /** Handels the events related to midnight */
    private void onMidnight() {
        //initiateWolfMating();
    }

    /** Initiates the mating of wolf's */
    private void initiateWolfMating() {
        Map<String, List<WorldActor>> map = world.getSortedEntities();



        List<WorldActor> wolfDens = map.get("wolfDen");
        for (WorldActor actor : wolfDens) {
            if (actor instanceof WolfDen wolfDen) {
                //System.out.println("Trying to mate in WolfDen: " + wolfDen);
                wolfDen.makeCup();
            }
        }
    }


    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    public SpawningAgent getSpawningAgent() {
        return spawningAgent;
    }

    /* ----- ----- ----- ----- Setters ----- ----- ----- ----- */

    public void setParser(Parser parser) {this.parser = parser;}

    public void setProgram(CapableProgram program) {this.program = program;}
}
