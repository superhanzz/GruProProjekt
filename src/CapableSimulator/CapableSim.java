package CapableSimulator;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.function.Supplier;

public class CapableSim {

    int worldSize;
    World world;
    Program program;

    String inputDataFilePath;
    int simulationSteps;
    int displaySize;
    int simulationDelay;

    public enum ActorTypes {
        GRASS,
        RABBIT,
        BURROW;
    }


    /* A map of all the Actor's constructors, though lambda's */
    private static final Map<String, Supplier<Actor>> actorConstructorRegistry = new HashMap<>();
    /**
     * Run when the CapableSimulator.CapableSim class is loaded
     * The 'Class::new' is a constructor reference or a lambda
     **/
    static {
        actorConstructorRegistry.put("grass", Grass::new);
        actorConstructorRegistry.put("rabbit", Rabbit::new);
        actorConstructorRegistry.put("burrow", Burrow::new);
    }

    /* A map of all the Actor's class references */
    private static final Map<ActorTypes, Class<? extends Actor>> actorClassTypes = new HashMap<>();
    static {
        actorClassTypes.put(ActorTypes.GRASS, Grass.class);
        actorClassTypes.put(ActorTypes.RABBIT, Rabbit.class);
        actorClassTypes.put(ActorTypes.BURROW, Burrow.class);

    }



    public CapableSim(int simulationSteps, int displaySize, int simulationDelay, String inputDataFilePath) {
        this.simulationSteps = simulationSteps;
        this.displaySize = displaySize;
        this.simulationDelay = simulationDelay;
        this.inputDataFilePath = inputDataFilePath;

        world = null;
        program = null;
        worldSize = 0;
    }

    public CapableSim(World world, int worldSize) {
        this.world = world;
        program = null;
        this.worldSize = worldSize;
    }

    void setupSimulation() {
        program = new Program(worldSize, displaySize, simulationDelay);
        world = program.getWorld();

        setUpDisplayInformation();
    }

    public void runSimulation(){
        /* Parses the input file into a map */
        Map<String, Integer> inputMap = parseInputFile(inputDataFilePath);


        if (program == null) {
            setupSimulation();
        }

        for (String key : inputMap.keySet()) {
            generateActors(key, inputMap.get(key), world);
        }

        // System.out.print("The number of grass actor's in the world is: " + getNumOfActors(ActorTypes.GRASS));

        program.show();
        for (int i = 0; i < simulationSteps; i++){
            program.simulate();
        }
    }

    public void generateActors(String actorType, int amount, World world){
        Supplier<Actor> actorConstructor = actorConstructorRegistry.get(actorType);
        if (actorConstructor == null) {
            System.out.println("Tried to create an unknown actor: " + actorType);
            return;
        }
        for (int i = 0; i < amount; i++){
            Location location = getEmptyTile(world);
            if (location != null)
                world.setTile(location, actorConstructor.get());
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

    void setUpDisplayInformation() {
        DisplayInformation diGrass = new DisplayInformation(Color.green, "alpha-wolf");
        program.setDisplayInformation(Grass.class, diGrass);

        DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, diRabbit);
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

}
