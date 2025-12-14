package CapableSimulator;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.*;
import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Putin;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.Actors.Shelter.Burrow;
import CapableSimulator.Actors.Shelter.WolfDen;
import CapableSimulator.EventHandeling.Dispacher;
import CapableSimulator.Utils.*;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.simulator.Simulator;
import itumulator.world.World;

import java.util.*;
import java.util.List;
import java.util.function.Supplier;


public class CapableSim {

    int worldSize;
    World world;
    Program program;
    Simulator simulator;

    String inputDataFilePath;
    int simulationSteps;
    int displaySize;
    int simulationDelay;

    int dayNumber;
    int actorSpawnCycle;

    /** The dispacher is the binding element between this and the ITUmulator's simulator */
    Dispacher<Void> simStepDispacher;

    private SpawningAgent spawningAgent;
    private WorldUtils worldUtils;
    private EntityHandler entityHandler;

    CapableEnums.DayNightStatus dayNightStatus;

    Map<String, InputFileStruct> inputMap;





    public enum ActorTypes {
        GRASS,
        RABBIT,
        WOLF,
        BURROW,
        BEAR,
        CARCASS,
        PUTIN;

    }

    /* ----- Time Specific Events ----- */
    private static final int time_JustBeforeNight = 9;
    private static final int time_WolfMatingTime = 14;
    private static final int time_NightFall = 10;
    private static final int time_DayBreak = 0;


    /* A map of all the Actor's constructors, though lambda's */
    private static final Map<String, Supplier<Actor>> actorConstructorRegistry = new HashMap<>();
    /**
     * Run when the CapableSimulator.CapableSim class is loaded
     * The 'Class::new' is a constructor reference or a lambda
     **/

    /**
     * Når vi først loader klassen CapableSim vil koden indenfor {} blive kørt.
     * Når koden kører vil der blive regristreret en reference til den tilhørende constructor uden parametre,
     * hvor at strengen er nøglen til den tilhørende constructor.
     **/
    /*
    static {
        actorConstructorRegistry.put("grass", Grass::new); // Her bliver der skabt en reference til "new Grass()" uden parametre, hvor "grass" er nøglen.
        actorConstructorRegistry.put("rabbit", Rabbit::new);
        actorConstructorRegistry.put("burrow", Burrow::new);
        actorConstructorRegistry.put("wolf", Wolf::new);
        actorConstructorRegistry.put("bear", Bear::new);
        actorConstructorRegistry.put("berry", BerryBush::new);
        actorConstructorRegistry.put("putin", Putin::new);
    }
    */


    /**
     * Her laver vi et Map der består af en actor type og en hvilken somhelst type som implementerer Actor interfacet.
     */
    /* A map of all the Actor's class references */
    private static final Map<ActorTypes, Class<? extends Actor>> actorClassTypes = new HashMap<>();
    static {
        actorClassTypes.put(ActorTypes.GRASS, Grass.class); // Her bliver actor typen Grass placeret inde i mappet, hvor Grass peger på Grass.class
        actorClassTypes.put(ActorTypes.RABBIT, Rabbit.class);
        actorClassTypes.put(ActorTypes.BURROW, Burrow.class);
        actorClassTypes.put(ActorTypes.WOLF, Wolf.class);
        actorClassTypes.put(ActorTypes.BEAR, Bear.class);
        actorClassTypes.put(ActorTypes.PUTIN, Putin.class);
    }

    /* ----- ----- ----- ----- Constructors ----- ----- ----- ----- */

    public CapableSim(int simulationSteps, int displaySize, int simulationDelay, String inputDataFilePath) {
        this.simulationSteps = simulationSteps;
        this.displaySize = displaySize;
        this.simulationDelay = simulationDelay;
        this.inputDataFilePath = inputDataFilePath;

        spawningAgent = null;

        world = null;
        program = null;
        worldSize = 0;
        actorSpawnCycle = 0;
        dayNumber = 0;
    }

    public CapableSim(World world, int worldSize) {
        this.world = world;
        program = null;
        this.worldSize = worldSize;
        actorSpawnCycle = 0;
        dayNumber = 0;
    }

    /* ----- ----- ----- ----- Pre-Simulation ----- ----- ----- ----- */

    /**
     * Denne metode instantiere et nyt Program, derudover gemmer den også hvorvidt det er dag eller nat, og derefter kalder setUpDisplayInformation();
     */
    /*void setupSimulation() {

        program = new Program(worldSize, displaySize, simulationDelay);
        world = program.getWorld();
        simulator = program.getSimulator();


        spawningAgent = new SpawningAgent(world);
        worldUtils = new WorldUtils(world);
        entityHandler = new EntityHandler(world);



        dayNightStatus = world.isDay() ? CapableEnums.DayNightStatus.DAY : CapableEnums.DayNightStatus.NIGHT;

        simStepDispacher = simulator.getStartDispacher();
    }*/


    /**
     * Denne metode runSimulation, er den som kører simuleringen. Her siger den altså at hvis der ikke er givet noget program,
     * vil den køre setupSimlation.
     *
     * Derefter vil den skabe vores Actors ud fra inputfilen som defineres i constructoren.
     * Efter det viser den programet og så opsættes et loop hvori hver iteration er 'simulerings skridtene'.
     * for iteration bliver der kontrolleret om det er dag,nat eller ved at blive nat.
     *
     * Til sidst eksekveres itumulator's simulerings kode.(eksempelvis act() metoden)
     */
    /*public void runSimulation(){
        *//* Parses the input file into a map *//*
        Parser parser = new Parser();
        inputMap = parser.parseInputsFromFile(new  File(inputDataFilePath));
        worldSize = parser.getWorldSize(inputMap); // Retrieves the world size and removes that entry from the input map

        if (program == null) {
            setupSimulation();
        }

        // Spawns all the actors specified in the input file, it only spawns the first entry of a given actor type
        Map<String, List<WorldActor>> allSpawnedActors;
        allSpawnedActors = spawningAgent.handleSpawnCycle(inputMap, true);
        entityHandler.initWorldActorContainer(allSpawnedActors);


        EventListener<Void> dis_Start = this::startSim;
        simStepDispacher.addEventListener(dis_Start);

        program.show();
        //program.run();
        if (false) {
            // timer thing
            *//*
            List<Double> times = new ArrayList<>();
            double startTime = System.nanoTime();
            double endTime = System.nanoTime();
            times.add((endTime - startTime) / 1000000.0);
            double averageTime = times.stream().mapToDouble(Double::doubleValue).sum() / times.size();  // Calculates the average time of a simulation step
            System.out.println(averageTime);
            *//*
        }
    }*/

    /* ----- ----- ----- ----- Simulation ----- ----- ----- ----- */

    /*public void simulationStep() {
        entityHandler.updateWorldActorContainer();    // Should be first to be executed on each simulation step
        //System.out.println(simulator.getSteps()); // prints the current simulation step

        switch (world.getCurrentTime()) {
            case time_DayBreak:
                dayNightStatus = CapableEnums.DayNightStatus.DAY;
                onDayNightChange();
                break;

            case time_NightFall:
                dayNightStatus = CapableEnums.DayNightStatus.NIGHT;
                onDayNightChange();
                break;

            case time_JustBeforeNight:
                // checks if animals doesn't apper to return onto the map on day break. deletes animal
                if (false) {
                    removeStuckActors();
                }
                // Handles the events surrounding it almost beeing night
                onAlmostNight();
                break;

            case time_WolfMatingTime:
                initiateWolfMating();
                break;

            default:
                break;
        }

        // Tries to spawn berrys on all the bushes in the world
        if (entityHandler.containsActor("berry")) {
            Map<String, List<WorldActor>> worldActorContainer = entityHandler.getWorldActorContainer();
            for (WorldActor actor : worldActorContainer.get("berry")) {
                if (actor instanceof BerryBush bush) {
                    bush.trySpawnBerrys();
                }
            }
        }
    }*/

    /*private void removeStuckActors() {
        Map<Object, Location> entities = world.getEntities();
        Map<String, List<WorldActor>> worldActorContainer = entityHandler.getWorldActorContainer();

        for (String key : worldActorContainer.keySet()) {
            if (CapableFunc.getAllAnimalTypes().contains(key)) {
                for (WorldActor actor : worldActorContainer.get(key)) {
                    if (entities.containsKey(actor) && entities.get(actor) == null) {
                        world.delete(actor);
                        System.out.println("Removed entity" + actor);
                    }
                }
            }
        }
    }*/


    /* ----- ----- ----- ----- Events ----- ----- ----- ----- */

    /** Handles the execution of event's connected to when it goes from day to night, and night to day */
    void onDayNightChange(){
        List<Animal> animals = worldUtils.getAllAnimals();
        switch (dayNightStatus){
            case DAY:
                //animals.forEach((animal) -> {animal.onDawn(world);});
                dayNumber++;

                if (dayNumber > 0) spawningAgent.handleSpawnCycle(inputMap, false);
                break;
            case NIGHT:
                //System.out.println("it has become night");
                //animals.forEach((animal) -> {animal.onNightFall(world);});
                break;
            default:
                return;
        }
    }

    private void onAlmostNight() {
        for (Animal animal : worldUtils.getAllAnimals()) {
            animal.onDusk();
        }
    }

    /** Initiates the mating of wolf's */
    private void initiateWolfMating() {
        Map<String, List<WorldActor>> worldActorContainer = entityHandler.getWorldActorContainer();

        List<WorldActor> wolfDens = worldActorContainer.get("wolfDen");
        for (WorldActor actor : wolfDens) {
            if (actor instanceof WolfDen wolfDen) {
                //System.out.println("Trying to mate in WolfDen: " + wolfDen);
                wolfDen.makeCup();
            }
        }
    }

    /** Executed though the dispacher in the simulator, is called at each simulation step */



    /* ----- ----- ----- ----- Getters ----- ----- ----- ----- */

    public SpawningAgent getSpawningAgent() {return spawningAgent;}

}
