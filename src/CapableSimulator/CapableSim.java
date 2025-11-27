package CapableSimulator;

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



    public enum ActorTypes {
        GRASS,
        RABBIT,
        WOLF,
        BURROW,
        BEAR;
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

        setUpDisplayInformation();
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
        inputMap = parseInputsFromFile(new  File(inputDataFilePath));


        if (program == null) {
            setupSimulation();
        }

        for (String key : inputMap.keySet()) {
            InputFileStruct input = inputMap.get(key);

            if (!input.isDelayedSpawn)
                generateActors2(inputMap.get(key), world);
        }

        program.show();
        List<Double> times = new ArrayList<>();
        for (int i = 0; i < simulationSteps; i++){
            double startTime = System.nanoTime();

            if (world.getCurrentTime() == 9){
                Map<Object, Location> entities = world.getEntities();
                for (Object entity : entities.keySet()) {
                    if (entity instanceof Animals && entities.get(entity) == null) {
                        world.delete(entity);
                        System.out.println("Removed entity" + entity);
                    }
                }

                List<Animals> animals = getAllAnimals(world);
                animals.forEach(animal -> {animal.almostNight(world);});
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
            program.simulate();

            double endTime = System.nanoTime();
            times.add((endTime - startTime) / 1000000.0);
        }
        double averageTime = times.stream().mapToDouble(Double::doubleValue).sum() / times.size();
        System.out.println(averageTime);
    }

    public void delayedSpawns(World world) {
        for (String key :  inputMap.keySet()) {
            if (key.contains(String.valueOf(actorSpawnCycle))) {
                generateActors2(inputMap.get(key), world);
            }
        }
        actorSpawnCycle++;
    }

    public void generateActors2(InputFileStruct iFS, World world){
        Supplier<Actor> actorConstructor = actorConstructorRegistry.get(iFS.actorType);
        if (actorConstructor == null) {
            System.out.println("Tried to create an unknown actor: " + iFS.actorType);
            return;
        }
        int amount = iFS.minAmount;
        if (iFS.maxAmount > 0) {
            Random rand = new Random();
            amount = rand.nextInt(iFS.minAmount,  iFS.maxAmount);
        }

        if(iFS.actorType.equals("wolf")) {
            Set<Actor> wolfs = new HashSet<>();
            for (int i = 0; i < iFS.getSpawnAmount(); i++){
                Location location = getEmptyTile(world);
                if (location != null) {
                    Wolf o = new Wolf(wolfs);
                    wolfs.add(o);
                    world.setTile(location, o);
                }
                else
                    System.out.println("Failed to create an actor of type " + iFS.actorType);
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




    public Map<String, InputFileStruct> parseInputsFromFile(File file){
        Map<String, InputFileStruct> map = new HashMap<>();

        try(Scanner sc = new Scanner(file)){
            worldSize = Integer.parseInt(sc.nextLine());

            while(sc.hasNextLine()){
                String line = sc.nextLine();    // saves the input line as a string
                if (line.contains(" ")) {       // Makes sure there is a " " before splitting the line
                    String[] words = line.split(" ");
                    String actorType = words[0];
                    String mapKey = words[0];
                    // Handles amount to spawn
                    int minAmount = 0;
                    int maxAmount = 0;
                    if (line.contains("-")) {
                        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
                        Matcher matcher = pattern.matcher(words[1]);
                        if (matcher.matches()) {
                            minAmount = Integer.parseInt(matcher.group(1));
                            maxAmount = Integer.parseInt(matcher.group(2));
                        }
                    } else {
                        minAmount = Integer.parseInt(words[1]);
                        maxAmount = 0;
                    }

                    // Handles the case of a static spawn being declared in the input file.
                    Location staticSpawnLocation = null;
                    if (line.contains("(") && words.length > 2) {
                        int x = 0;
                        int y = 0;
                        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");
                        Matcher matcher = pattern.matcher(words[2]);

                        if (matcher.matches()) {
                            x = Integer.parseInt(matcher.group(1));
                            y = Integer.parseInt(matcher.group(2));
                        }
                        staticSpawnLocation = new Location(x, y);
                    }
                    boolean isDelayedSpawn = false;
                    // Handles the case of the return map already contains an entry of the same class.
                    if (map.containsKey(words[0])) {
                        isDelayedSpawn = true;

                        // Findes the amount of times the same actory type is present in the map.
                        int numOfSameActorType = 0;
                        for (String key : map.keySet()) {
                            if (map.get(key).actorType == actorType)
                                numOfSameActorType++;
                        }
                        mapKey += String.valueOf(numOfSameActorType); // Updates the mapKey
                    }
                    InputFileStruct iFS = new InputFileStruct(actorType, minAmount, maxAmount, staticSpawnLocation, isDelayedSpawn);

                    map.put(mapKey, iFS);

                }
            }

        }
        catch (Exception e) {
            System.out.println("Error in parseInputsFromFile(), message: " + e.getMessage());
        }

        return map;
    }


    void setUpDisplayInformation() {
        DisplayInformation diGrass = new DisplayInformation(Color.green, "grass");
        program.setDisplayInformation(Grass.class, diGrass);

        DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, diRabbit);

        DisplayInformation diWolf = new DisplayInformation(Color.pink, "alpha-wolf");
        program.setDisplayInformation(Wolf.class, diWolf);

        DisplayInformation diBurrow = new DisplayInformation(Color.blue, "hole-small");
        program.setDisplayInformation(Burrow.class, diBurrow);

        DisplayInformation diBear = new DisplayInformation(Color.BLACK, "bear");
        program.setDisplayInformation(Bear.class, diBear);
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

        /*
        switch (actorType){
            case GRASS:
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Grass) numOfActors++;
                }
                break;
            case RABBIT:
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Rabbit) numOfActors++;
                }
                break;
            case BURROW:
                for(Object actor : actors){
                    if(actor instanceof CapableSimulator.Burrow) numOfActors++;
                }
                break;
        }
        */

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
                break;
        }

        return numOfActors;
    }

    void onDayNightChange(DayNightStatus dayNightStatus){
        List<Animals> animals = getAllAnimals(world);
        switch (dayNightStatus){
            case DAY:
                //System.out.println("it has become day");
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

    List<Animals> getAllAnimals(World world){
        List<Animals> animals = new ArrayList<>();

        Object[] actors = world.getEntities().keySet().toArray(new Object[0]);
        for(Object actor : actors){
            if(actor instanceof Animals){
                animals.add((Animals) actor);
            }
        }


        return animals;
    }

}
