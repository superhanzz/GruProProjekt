package CapableSimulator;

import CapableSimulator.Actors.*;
import FunctionLibrary.CapableFunc;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapableSim {

    int worldSize;
    World world;
    Program program;

    String inputDataFilePath;
    int simulationSteps;
    int displaySize;
    int simulationDelay;

    int dayNumber;
    int actorSpawnCycle;

    DayNightStatus dayNightStatus;

    Map<String, InputFileStruct> inputMap;
    Map<String, Set<Wolf>> allWolfgangs;

    List<BerryBush> bushList;


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



    public CapableSim(int simulationSteps, int displaySize, int simulationDelay, String inputDataFilePath) {
        this.simulationSteps = simulationSteps;
        this.displaySize = displaySize;
        this.simulationDelay = simulationDelay;
        this.inputDataFilePath = inputDataFilePath;

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

        dayNightStatus = world.isDay() ? DayNightStatus.DAY : DayNightStatus.NIGHT;
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
        worldSize = parser.getWorldSize(inputMap); // Retrieves the world sizeinputMap = CapableFunc.parseInputsFromFile2(new  File(inputDataFilePath));


        bushList = new ArrayList<>();

        if (program == null) {
            setupSimulation();
        }

        for (String key : inputMap.keySet()) {
            InputFileStruct input = inputMap.get(key);
            Pattern pattern = Pattern.compile("([A-Za-z]+)"); // Only accepts the first spawning cycle i.e. only entries where the kay does NOT contain any numbers
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches())
                generateActors2(inputMap.get(key), world);
        }

        program.show();
        List<Double> times = new ArrayList<>();
        for (int i = 0; i < simulationSteps; i++){
            double startTime = System.nanoTime();
            /*System.out.println(i);

            Set<WorldActor> wolfs = CapableFunc.getAllWorldActorsAsMap(world, new ArrayList<>(List.of("wolf")), true).get("wolf");
            Set<WolfGang> wolfGangs = new HashSet<>();
            for (WorldActor actor : wolfs) {
                wolfGangs.add(((Wolf) actor).getWolfGang());
            }
            for (WolfGang wolfGang : wolfGangs) {
                wolfGang.cleanGangList(world);
            }*/


            if (world.getCurrentTime() == 9){
                Map<Object, Location> entities = world.getEntities();
                for (Object entity : entities.keySet()) {
                    if (entity instanceof Animals && entities.get(entity) == null) {
                        //world.delete(entity);
                        //System.out.println("Removed entity" + entity);
                    }
                }
                //CapableFunc.getAllAnimals(world);

                List<Animals> animals = CapableFunc.getAllAnimals(world);
                for (Animals animal : animals) {
                    animal.almostNight(world);
                }

                //animals.forEach(animal -> {animal.almostNight(world);});
            }
            else if (world.getCurrentTime() == 14) {
                Set<WorldActor> Dens = CapableFunc.getAllWorldActorsAsMap(world, new ArrayList<>(List.of("wolfDen")), true).get("wolfDen");
                for (WorldActor actor : Dens) {
                    //System.out.println("TryMate");
                    ((WolfDen) actor).makeCup(world);
                }
            }


            bushList.forEach(bush -> {
                bush.trySpawnBerrys(program);
            });

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
            program.simulate();

            double endTime = System.nanoTime();
            times.add((endTime - startTime) / 1000000.0);
        }
        double averageTime = times.stream().mapToDouble(Double::doubleValue).sum() / times.size();
        System.out.println(averageTime);
    }



    void onDayNightChange(DayNightStatus dayNightStatus){
        List<Animals> animals = CapableFunc.getAllAnimals(world);
        List<WolfDen> wolfDens = new ArrayList<>();

        switch (dayNightStatus){
            case DAY:

                animals.forEach((animal) -> {animal.onDay(world);});

                dayNumber++;
                delayedSpawns(world);
                break;
            case NIGHT:
                //System.out.println("it has become night");
                animals.forEach((animal) -> {animal.onNight(world);});
                break;
            default:
                return;
        }
    }



}
