package Tests;

import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;


import java.util.*;

public class SpawnAmountTest {
    /**
     * Decleres the needed class varaibles for the tests
     * */
    World world;

    public record Interval(int min, int max, int worldSize) {}

    /**
     * Declares and defines an arraylist containing all the inputfile paths.
     * */
    protected static final ArrayList<String> inputFiles = new ArrayList<>();
    static{
        inputFiles.add("src/Data/week-1/t1-1a.txt");
        inputFiles.add("src/Data/week-1/t1-1b.txt");
        inputFiles.add("src/Data/week-1/t1-1c.txt");
        inputFiles.add("src/Data/week-1/t1-2a.txt");
        inputFiles.add("src/Data/week-1/t1-2b.txt");
        inputFiles.add("src/Data/week-1/t1-2cde.txt");
        inputFiles.add("src/Data/week-1/t1-2fg.txt");
        inputFiles.add("src/Data/week-1/t1-3a.txt");
        inputFiles.add("src/Data/week-1/t1-3b.txt");
        inputFiles.add("src/Data/week-1/tf1-1.txt");
    }

    /**
     * Builds the spawnCalls map for the given actor type (fx. "grass"), where the key is the name of the file (fx. t1-1a.txt),
     * and the value is the amount of spawns given as an interval (fx. 20-30), where the world size is saved within the Interval record.
     *
     * @param actorType The actor type which is to be tested.
     * */
    /*
    protected Map<String, Interval> buildSpawnCalls(String actorType) {
        *//* Finds all files where the given actor type
        grass is defined and put's them in a map with the amount of instances as an interval, if an interval isn't specified, the max value is set to 0 *//*


        Map<String, Interval> spawnCalls = new HashMap<>(); // The map to be returned.

        // Iterates though all the input files to find the ones wherein the actor type is present
        for (String path : inputFiles){
            String fileName = path;

            String[] splits = fileName.split("/");  // Spilt's the file path info an array.
            fileName = splits[splits.length-1]; // Defines the file name as the last index of the spilt's array.

            // Defines the file that is to be parsed.
            File file = new File(path);

            // Tries to read the input file, if an exception is thrown, the message is printed.
            try(Scanner sc = new Scanner(file)){
                int worldSize = Integer.parseInt(sc.nextLine()); // The First line is always the world size.

                // A loop that continues for as long as there are more lines in the input file.
                while(sc.hasNextLine()){
                    String line = sc.nextLine(); // Defines the line to be parsed.

                    // Checks if the given actor type occurs in the relevant line, if not, nothing happens and the loop continues to the next line.
                    if (line.contains(actorType)){
                        line = line.split(" ")[1]; // Splits the line at the space, and removes to part before the space.

                        // Declares the interval record.
                        Interval interval = null;
                        if (line.contains("-")) { // If the amount of the given actor type is an interval the interval is spilt at the '-' and the Interval record is defined.
                            String[] split = line.split("-");
                            interval = new Interval(Integer.parseInt(split[0]), Integer.parseInt(split[1]), worldSize);
                        }
                        else interval = new Interval(Integer.parseInt(line), 0,  worldSize); // If the amount is not an interval, the max_value of the interval is set to 0.

                        spawnCalls.put(fileName, interval); // Makes an entry in the spawnCalls map.
                    }
                }
            }catch(Exception e){
                System.out.println("Error in buildSpawnCalls: " + e.getMessage());
            }
        }
        return spawnCalls;
    }

    *//**
     * Test each of the input files' where the given actor type is present.
     *
     * @param actorType The actor type which is to be tested.
     * @param spawnCalls A map containing all the files wherein the actor type is present.
     * *//*
    protected void checkSpawns(String actorType, Map<String, Interval> spawnCalls) {
        Map<String, Boolean> assertions = new HashMap<>(); // The map containing the values to be evaluated in the test.

        // Iterates though each of the entries of spawnCalls.
        spawnCalls.forEach((s, interval) -> {
            System.out.println(s);
            // Creates a world for the test to occur within, doing so for each test to keep the test environment clean.
            world = new World(interval.worldSize); // Reads the relevant world size from the relevant spawnCalls value.
            CapableSim sim = new CapableSim(world, interval.worldSize); // Creates a CapableSim instance because the generateActors() method is needed for this test.

            // Findes the filepath to the input file from the filename (key in spawnCalls) because it is needed in the generateActors() method.
            String filePath = "";
            for (String path : inputFiles){
                if (path.contains(s)) {
                    filePath = path;
                    break;
                }
            }
            // Executes generateActor() within the CapableSim instans. The filepath is needed here to use the pareInputFile() method wherein the amount of the actor type class' is decided.
            //sim.generateActors2(sim.parseInputsFromFile());
            //sim.generateActors(actorType, sim.parseInputFile(filePath).get(actorType), world);


            // Retrieves the number of actor in the world by the given actorType.
            int numOfActors = new WorldUtils(world).getNumOfActors(actorType);

            if (interval.max != 0) { // If the input file specified the amount as an interval.
                assertions.put(s, ((numOfActors >= interval.min)  && (numOfActors <= interval.max)));   // Creates an entry in the assertions map where the file name is the key, and the value is a boolean representing whether the amount of actors is within the interval
            }
            else    // If the input file did not specify the amount as an interval but a static amount.
                assertions.put(s, (numOfActors == interval.min));   // Creates an entry in the assertions map where the file name is the key, and the value is a boolean representing whether the amount of actors is equal to the specified amount.

        });

        // Checks if all the files were read correct and the amount of class instances' was created correctly
        assertions.forEach((key, value) -> {assertTrue(value);});
    }



    *//**
     * Grass Test
     * *//*
    @RepeatedTest(10)
    public void grassSpawnAmountTest(){
        Map<String, Interval> spawnCalls = buildSpawnCalls("grass");
        checkSpawns("grass", spawnCalls);
    }

    *//**
     * Rabbit Test
     * *//*
    @RepeatedTest(10)
    public void rabbitSpawnAmountTest(){
        Map<String, Interval> spawnCalls = buildSpawnCalls("rabbit");
        checkSpawns("rabbit", spawnCalls);
    }

    *//**
     * Rabbit Test
     * *//*
    @RepeatedTest(10)
    public void burrowSpawnAmountTest(){
        Map<String, Interval> spawnCalls = buildSpawnCalls("burrow");
        checkSpawns("burrow", spawnCalls);
    }

    @RepeatedTest(1)
    public void bearTerritorySizeTest(){

        Program program = new Program(21, 800, 500);
        world = program.getWorld();
        Location center = new Location(10, 10);
        Set<Location> Radius = world.getSurroundingTiles(center, 4);

        for (Location location : Radius.toArray(new Location[0])) {
            Grass grass = new Grass();
            world.setTile(location, grass);
        }

        DisplayInformation displayInformation = new DisplayInformation(Color.red);
        program.setDisplayInformation(Grass.class, displayInformation);
        program.show();

        System.out.print(world.getEntities().size());
        for(int i = 0; i < 200; i++) {
            world.setNight();
            program.simulate();
        }

    }
*/

    @AfterEach
    public void tearDown() {
        world = null;
    }

}
