package CapableSimulator;

import FunctionLibrary.CapableFunc;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
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

        //setUpDisplayInformation();
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
        inputMap = CapableFunc.parseInputsFromFile2(new  File(inputDataFilePath));
        worldSize = CapableFunc.getWorldSize(inputMap); // Retrieves the world size

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
            //System.out.println(i);

            if (world.getCurrentTime() == 4) {
                Set<WorldActor> Dens = CapableFunc.getAllWorldActorsAsMap(world, new ArrayList<>(List.of("wolfDen")), true).get("wolfDen");
                for (WorldActor actor : Dens) {
                    System.out.println("TryMate");
                    ((WolfDen) actor).makeCup(world);
                }
            }
            else if (world.getCurrentTime() == 9){
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

    public void delayedSpawns(World world) {
        Pattern pattern = Pattern.compile("([A-Za-z]+"+actorSpawnCycle+")"); // Makes sure that only actor of the given spawn cycle spawns
        for (String key :  inputMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                generateActors2(inputMap.get(key), world);
            }
        }
        actorSpawnCycle++;
    }

    public void generateActors2(InputFileStruct iFS, World world){
        System.out.println(iFS.actorType);
        Supplier<Actor> actorConstructor = actorConstructorRegistry.get(iFS.actorType);
        if (actorConstructor == null) {
            System.out.println("Tried to create an unknown actor: " + iFS.actorType);
            return;
        }


        if(iFS.actorType.equals("wolf")) {
            WolfGang gang = null;
            Wolf alpha = null;
            for (int i = 0; i < iFS.getSpawnAmount(); i++){
                Location location = getEmptyTile(world);
                if (location != null) {
                    Wolf o = new Wolf(gang, (gang == null));
                    if (gang == null) alpha = o;

                    if (gang == null) gang = new WolfGang(o);
                    else gang.addWolfToGang(o);
                    o.updateOnMap(world, location, true);
                    //world.setTile(location, o);
                }
                else
                    System.out.println("Failed to create an actor of type " + iFS.actorType);
                try {
                    gang.setNewAlpha(alpha);
                } catch (NullPointerException e) {
                    System.out.println("Tried to call setNewAlpha on alpha, but 'gang' is null. \t" + e.getMessage());
                }
            }
            return;
        } else if (iFS.actorType.equals("bear")) {
            for (int i = 0; i < iFS.getSpawnAmount(); i++){
                Location location = (iFS.staticSpawnLocation != null) ? iFS.staticSpawnLocation : getEmptyTile(world);
                if (location != null) {
                    Bear b = new Bear(iFS.staticSpawnLocation != null ? iFS.staticSpawnLocation : location);
                    world.setTile(location, b);
                }
                else
                    System.out.println("Failed to create an actor of type " + iFS.actorType);
            }
            return;
        }
        else if (iFS.actorType.equals("berry")) {
            for (int i = 0; i < iFS.getSpawnAmount(); i++){
                Location location = (iFS.staticSpawnLocation != null) ? iFS.staticSpawnLocation : getEmptyTile(world);
                if (location != null) {
                    BerryBush b = new BerryBush();
                    bushList.add(b);
                    world.setTile(location, b);
                }
                else
                    System.out.println("Failed to create an actor of type " + iFS.actorType);
            }
            return;
        }
        for (int i = 0; i < iFS.getSpawnAmount(); i++){
            Location location = getEmptyTile(world);
            if (location != null) {
                Object o = actorConstructor.get();
                world.setTile(location, o);
            }
            else
                System.out.println("Failed to create an actor of type " + iFS.actorType);
        }


    }

    public void generateActors(String actorType, int amount, World world){
        Supplier<Actor> actorConstructor = actorConstructorRegistry.get(actorType);
        if (actorConstructor == null) {
            System.out.println("Tried to create an unknown actor: " + actorType);
            return;
        }
        if(actorType.equals("wolf")) {
            Set<Actor> wolfs = new HashSet<>();
            for (int i = 0; i < amount; i++){
                Location location = getEmptyTile(world);
                if (location != null) {
                    Wolf o = new Wolf(wolfs);
                    wolfs.add(o);
                    world.setTile(location, o);
                }
                else
                    System.out.println("Failed to create an actor of type " + actorType);
            }
            return;
        }
        for (int i = 0; i < amount; i++){
            Location location = getEmptyTile(world);
            if (location != null) {
                Object o = actorConstructor.get();
                world.setTile(location, o);
            }
            else
                System.out.println("Failed to create an actor of type " + actorType);
        }

        /*
        switch (actorType){
            case "grass":
                for(int i = 0; i < amount; i++) {
                    Location location = getEmptyTile(world);
                    if (location != null) world.setTile(location, new CapableSimulator.Grass());
                    else System.out.println("Failed to create an actor of type " + actorType);
                };
                break;

            case "rabbit":
                for(int i = 0; i < amount; i++) {
                    Location location = getEmptyTile(world);
                    if (location != null) world.setTile(location, new CapableSimulator.Rabbit());
                    else System.out.println("Failed to create an actor of type " + actorType);
                };
                break;

            case "burrow":
                for(int i = 0; i < amount; i++) {
                    Location location = getEmptyTile(world);
                    if (location != null) world.setTile(location, new CapableSimulator.Burrow());
                    else System.out.println("Failed to create an actor of type " + actorType);
                };
                break;

            default:
                System.out.println("Tried to create an unknown actor: "  + actorType);
                break;
        }
        */
    }

    Location getEmptyTile(World world){
        //TODO Make this function chose between the free tiles in the world, by generating a map symbolizing all the possible tiles and then subtracting all entities, and chosing a random tile from the remaining.

        Random rand = new Random();
        boolean isEmpty = false;
        Location emptyTile = null;
        int loopCounter = 0;

        /* Tries to find an empty tile in the world. If it attempts more than a certain amount of times without success, it breaks and returns null */
        while(!isEmpty){
            emptyTile = new Location(rand.nextInt(worldSize - 1), rand.nextInt(worldSize - 1));
            isEmpty = world.getTile(emptyTile) == null;

            if(loopCounter >= 100) {
                System.out.println("Couldn't find empty tile");
                break;
            }
            loopCounter++;
        }

        return emptyTile ;
    }

    public Map<String, Integer> parseInputFile(String path){
        Map<String, Integer> map = new HashMap<>();
        File inputFile = new File(path);
        int debugLine = 0;

        try(Scanner sc = new Scanner(inputFile)){

            worldSize = Integer.parseInt(sc.nextLine()); // The First line is always the world size

            /** Iterates though each line of the input file and parses the line into the map<String, Integer> **/
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if (line.isEmpty()) break;

                String[] Words = line.split(" ");
                if (map.containsKey(Words[0])) throw new RuntimeException("paraseInputFile() read the same identifier more than once: " + Words[0]);

                /* If the quantity is between 2 values, it generates a random number between the 2 */
                if (Words[1].contains("-")) {
                    Random rand = new Random();
                    String[] interval = Words[1].split("-");
                    Words[1] = Integer.toString(rand.nextInt(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
                }

                map.put(Words[0], Integer.parseInt(Words[1]));
            }
        }catch(Exception e){
            System.out.println("Exception on line: " + debugLine + "   " + e.getMessage());
        }

        return map;
    }

    /**
     * Return the amount of the given actor type in the world
     * */
    public int getNumOfActors(ActorTypes actorType) {
        int numOfActors = 0;
        Object[] actors = world.getEntities().keySet().toArray(new Object[0]);

        Class<? extends Actor> actorClass = actorClassTypes.get(actorType); // retrieves the "Class".class for the given class
        if (actorClass == null) return 0;

        for(Object actor : actors){
            if(actorClass.isInstance(actor)) numOfActors++;
        }

        return numOfActors;
    }

    public int getNumOfActors(String actorType) {
        int numOfActors = 0;
        Object[] actors = world.getEntities().keySet().toArray(new Object[0]);

        switch (actorType){
            case "grass":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Grass) numOfActors++;
                }
                break;
            case "rabbit":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Rabbit) numOfActors++;
                }
                break;
            case "burrow":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Burrow) numOfActors++;
                }
                break;
            case "wolf":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Wolf) numOfActors++;
                }
                break;
            case "bear":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Bear) numOfActors++;
                }
            case "berry":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Bear) numOfActors++;
                }
                break;
            case "putin":
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Putin) numOfActors++;
                }
                break;
        }

        return numOfActors;
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
