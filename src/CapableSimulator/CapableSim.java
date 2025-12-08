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
        inputMap = parser.parseInputsFromFile2(new  File(inputDataFilePath));
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
        if (false) {


            // Actual simulation

            List<Double> times = new ArrayList<>();
            for (int i = 0; i < simulationSteps; i++) {
                double startTime = System.nanoTime();

                simulationStep();

                double endTime = System.nanoTime();
                times.add((endTime - startTime) / 1000000.0);
            }

            // timer thing
            double averageTime = times.stream().mapToDouble(Double::doubleValue).sum() / times.size();  // Calculates the average time of a simulation step
            System.out.println(averageTime);
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


        if (world.getCurrentTime() == 9){
            Map<Object, Location> entities = world.getEntities();

            for (String key : worldActorContainer.keySet()) {
                if (CapableFunc.getAllAnimalTypes().contains(key)) {
                    for (WorldActor actor : worldActorContainer.get(key)) {
                        if (entities.containsKey(actor) && entities.get(actor) == null) {
                            //world.delete(entity);
                            //System.out.println("Removed entity" + entity);
                        }
                    }
                }
            }
            //CapableFunc.getAllAnimals(world);

            List<Animals> animals = worldUtils.getAllAnimals();
            for (Animals animal : animals) {
                animal.almostNight(world);
            }

            //animals.forEach(animal -> {animal.almostNight(world);});
        }
        else if (world.getCurrentTime() == 14) {
            /*Set<WorldActor> Dens = CapableFunc.getAllWorldActorsAsMap(world, new ArrayList<>(List.of("wolfDen")), true).get("wolfDen");
            for (WorldActor actor : Dens) {
                //System.out.println("TryMate");
                ((WolfDen) actor).makeCup(world);
            }*/
        }

        // Tries to spawn berrys on all the bushes in the world
        if (!worldActorContainer.containsKey("berry")) {
            for (WorldActor actor : worldActorContainer.get("berry")) {
                if (actor instanceof BerryBush bush) {
                    bush.trySpawnBerrys();
                }
            }
        }

        switch (dayNightStatus) {
            case DAY:
                if (!world.isDay()) {
                    dayNightStatus = DayNightStatus.NIGHT;
                    onDayNightChange(dayNightStatus);
                }
                break;
            case NIGHT:
                if (!world.isNight()) {
                    dayNightStatus = DayNightStatus.DAY;
                    onDayNightChange(dayNightStatus);
                }
                break;
        }
    }

    public void startSim(Void un) {
        //System.out.println("Starting simulation");
        simulationStep();
    }



    void onDayNightChange(DayNightStatus dayNightStatus){
        List<Animals> animals = worldUtils.getAllAnimals();
        List<WolfDen> wolfDens = new ArrayList<>();

        switch (dayNightStatus){
            case DAY:

                animals.forEach((animal) -> {animal.onDay(world);});

                dayNumber++;
                //delayedSpawns(world);
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
                System.out.println("actorType: " + actorType);
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

            if (actorType == null) throw new NullPointerException("actorType is null in actor: " + actor.toString());

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
                    System.out.println("Actor to be removed: " + actor.toString());
                }
            }
        }
        return deletedActors;
    }



    public SpawningAgent getSpawningAgent() {return spawningAgent;}

}
