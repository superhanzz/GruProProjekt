package CapableSimulator;

import CapableSimulator.Actors.*;
import FunctionLibrary.CapableFunc;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.simulator.Simulator;
import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
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

    Dispacher<Void> simStepDispacher;
    private SpawningAgent spawningAgent;
    private WorldUtils worldUtils;

    DayNightStatus dayNightStatus;

    Map<String, InputFileStruct> inputMap;
    private final Map<String, List<WorldActor>> worldActorContainer = new HashMap<>();




    public enum ActorTypes {
        GRASS,
        RABBIT,
        WOLF,
        BURROW,
        BEAR,
        CARCASS,
        PUTIN;

    }

    public enum DayNightStatus {
        DAY,
        NIGHT;
    }

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
    static {
        actorConstructorRegistry.put("grass", Grass::new); // Her bliver der skabt en reference til "new Grass()" uden parametre, hvor "grass" er nøglen.
        actorConstructorRegistry.put("rabbit", Rabbit::new);
        actorConstructorRegistry.put("burrow", Burrow::new);
        actorConstructorRegistry.put("wolf", Wolf::new);
        actorConstructorRegistry.put("bear", Bear::new);
        actorConstructorRegistry.put("berry", BerryBush::new);
        actorConstructorRegistry.put("putin", Putin::new);
    }


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


    /*public CapableSim(Canvas canvas, int simulationSteps, int displaySize, int simulationDelay, String inputDataFilePath) {
        super(null, canvas, simulationDelay);
        this.simulationSteps = simulationSteps;
        this.displaySize = displaySize;
        this.simulationDelay = simulationDelay;
        this.inputDataFilePath = inputDataFilePath;

        program = null;
        worldSize = 0;
        actorSpawnCycle = 0;
        dayNumber = 0;
    }*/

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

    /**
     * Denne metode instantiere et nyt Program, derudover gemmer den også hvorvidt det er dag eller nat, og derefter kalder setUpDisplayInformation();
     */
    void setupSimulation() {
        program = new Program(worldSize, displaySize, simulationDelay);
        world = program.getWorld();
        simulator = program.getSimulator();


        spawningAgent = new SpawningAgent(world);
        worldUtils = new WorldUtils(world);

        dayNightStatus = world.isDay() ? DayNightStatus.DAY : DayNightStatus.NIGHT;

        simStepDispacher = simulator.getStartDispacher();
    }



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
    public void runSimulation(){

        /* Parses the input file into a map */
        Parser parser = new Parser();
        inputMap = parser.parseInputsFromFile(new  File(inputDataFilePath));
        worldSize = parser.getWorldSize(inputMap); // Retrieves the world size and removes that entry from the input map

        if (program == null) {
            setupSimulation();
        }

        // Spawns all the actors specified in the input file, it only spawns the first entry of a given actor type
        Map<String, List<WorldActor>> allSpawnedActors;
        allSpawnedActors = spawningAgent.handleSpawnCycle(inputMap, true);
        initWorldActorContainer(allSpawnedActors);


        EventListener<Void> dis_Start = this::startSim;
        simStepDispacher.addEventListener(dis_Start);

        program.show();
        //program.run();
        if (false) {
            // timer thing
            /*
            List<Double> times = new ArrayList<>();
            double startTime = System.nanoTime();
            double endTime = System.nanoTime();
            times.add((endTime - startTime) / 1000000.0);
            double averageTime = times.stream().mapToDouble(Double::doubleValue).sum() / times.size();  // Calculates the average time of a simulation step
            System.out.println(averageTime);
            */
        }
    }

    public void simulationStep() {
        // DEBUG THING, Needs to be before updateWorldActorContainer()
        if (false) {
            if ((worldActorContainer.get("wolf").size() > 3)) {
                List<WorldActor> actors = worldActorContainer.get("wolf");
                WorldActor actor = actors.get(new Random().nextInt(actors.size()));
                if (actor instanceof Wolf wolf) {
                    wolf.die(world);
                }
            }
        }

        updateWorldActorContainer();    // Should be first to be executed on each simulation step
        //System.out.println(simulator.getSteps()); // prints the current simulation step

        switch (world.getCurrentTime()) {
            case time_DayBreak:
                dayNightStatus = DayNightStatus.DAY;
                onDayNightChange();
                break;

            case time_NightFall:
                dayNightStatus = DayNightStatus.NIGHT;
                onDayNightChange();
                break;

            case time_JustBeforeNight:
                // checks if animals doesn't apper to return onto the map on day break. deletes animal
                if (false) {
                    Map<Object, Location> entities = world.getEntities();
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
        if (!worldActorContainer.containsKey("berry")) {
            for (WorldActor actor : worldActorContainer.get("berry")) {
                if (actor instanceof BerryBush bush) {
                    bush.trySpawnBerrys();
                }
            }
        }
    }

    public void startSim(Void un) {
        //System.out.println("Starting simulation");
        simulationStep();
    }



    void onDayNightChange(){
        List<Animals> animals = worldUtils.getAllAnimals();
        switch (dayNightStatus){
            case DAY:
                animals.forEach((animal) -> {animal.onDay(world);});
                dayNumber++;

                if (dayNumber > 0) spawningAgent.handleSpawnCycle(inputMap, false);
                break;
            case NIGHT:
                //System.out.println("it has become night");
                animals.forEach((animal) -> {animal.onNight(world);});
                break;
            default:
                return;
        }
    }

    private void initWorldActorContainer(Map<String, List<WorldActor>> tempMap){
        if  (tempMap == null || tempMap.isEmpty()) {
            if (tempMap == null) System.out.println("tempMap is null");
            else System.out.println("tempMap is empty");
            return;
        }
        for (String actorType : tempMap.keySet()) {
            worldActorContainer.put(actorType, tempMap.get(actorType));
        }
        for (String actorType : CapableFunc.getAllWorldActorTypes()) {
            if (!worldActorContainer.containsKey(actorType)) {
                worldActorContainer.put(actorType, new ArrayList<>());
                //System.out.println("actorType: " + actorType);
            }
        }

        //System.out.println("Entries in worldActorContainer: " + worldActorContainer.size());
    }

    /** Updates the worldActorContainer, should be called at each simulation step.
     *  Iterates though each entity in the world, and checks if the entity is already is in the worldActorContainer
     *  if it isn't then it is added.
     *  If the world isn't set then it returns.
     * */
    private void updateWorldActorContainer() {
        if (world == null) return;

        // Handels new actors in the world
        Map<Object, Location> worldEntities = world.getEntities();
        for (Object e : worldEntities.keySet()) {
            if(!(e instanceof WorldActor actor)) continue;
            String actorType = actor.getActorType();

            if (actorType == null) throw new NullPointerException("actorType is null in actor: " + actor);

            if(worldActorContainer.containsKey(actorType)) {
                List<WorldActor> list = worldActorContainer.get(actorType);
                if (!(list.contains(actor))) {
                    list.add(actor);
                    //System.out.println("Added actor of type: " + actorType + " to world actor container");
                }
            }
        }

        // Handles the flagging of deleted actors.
        List<WorldActor> removeActorList = getWorldDeletedActors(worldEntities);

        // Debug message
        if (!removeActorList.isEmpty()) {
            System.out.println("Num of actors to remove: " + removeActorList.size());
        }

        for (WorldActor actor : removeActorList) {
            if (actor instanceof Wolf wolf) {
                wolf.getWolfGang().removeWolfFromGang(wolf);
            }
        }

        // Removes the actors in removeActorList from worldActorContainer
        removeActorList.forEach(worldActor -> {
            worldActorContainer.get(worldActor.getActorType()).remove(worldActor);
        });
    }

    /** Findes all the worldActors that no longer exist in the world's entity list
     * */
    private List<WorldActor> getWorldDeletedActors(Map<Object, Location> worldEntities) {
        List<WorldActor> deletedActors = new ArrayList<>();
        for (String actorType : worldActorContainer.keySet()) {
            for (WorldActor actor : worldActorContainer.get(actorType)) {
                if (!worldEntities.containsKey(actor)) {
                    deletedActors.add(actor);
                    //System.out.println("Actor to be removed: " + actor.toString());
                }
            }
        }
        return deletedActors;
    }

    private void onAlmostNight() {
        for (Animals animal : worldUtils.getAllAnimals()) {
            animal.almostNight(world);
        }
    }

    /** Initiates the mating of wolf's
     * */
    private void initiateWolfMating() {
        List<WorldActor> wolfDens = worldActorContainer.get("wolfDen");
        for (WorldActor actor : wolfDens) {
            if (actor instanceof WolfDen wolfDen) {
                //System.out.println("Trying to mate in WolfDen: " + wolfDen);
                wolfDen.makeCup(world);
            }
        }
    }



    public SpawningAgent getSpawningAgent() {return spawningAgent;}

}
