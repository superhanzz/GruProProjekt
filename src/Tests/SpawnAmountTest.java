package Tests;

import CapableSimulator.CapableSim;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;


import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpawnAmountTest {
    World world;
    int spawns;
    int testSample;
    Map<String, Interval> spawnCalls;

    public record Interval(int min, int max, int worldSize) {}

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

    protected void buildSpawnCalls(String actorType) {
        spawns = 0;
        testSample = 10;

        /* Finds all files where grass is defined and put's them in a map with the amount of instances as an interval, if a interval isn't specified, the max value is set to 0 */
        spawnCalls =  new HashMap<>();

        for (String path : inputFiles){
            String fileName = path;

            String[] splits = fileName.split("/");
            fileName = splits[splits.length-1];

            File file = new File(path);

            try(Scanner sc = new Scanner(file)){
                int worldSize = Integer.parseInt(sc.nextLine()); // The First line is always the world size

                while(sc.hasNextLine()){
                    String line = sc.nextLine();

                    if (line.contains(actorType)){
                        line = line.split(" ")[1];

                        Interval interval = null;
                        if (line.contains("-")) {
                            String[] split = line.split("-");
                            interval = new Interval(Integer.parseInt(split[0]), Integer.parseInt(split[1]), worldSize);
                        }
                        else interval = new Interval(Integer.parseInt(line), 0,  worldSize);

                        spawnCalls.put(fileName, interval);
                    }
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    protected void checkSpawns(String actorType) {
        Map<String, Boolean> assertions = new HashMap<>();

        spawnCalls.forEach((s, interval) -> {
            System.out.println(s);
            world = new World(interval.worldSize);
            CapableSim sim = new CapableSim(world, interval.worldSize);

            String filePath = "";
            for (String path : inputFiles){
                if (path.contains(s)) {
                    filePath = path;
                    break;
                }
            }
            sim.generateActors(actorType, sim.parseInputFile(filePath).get(actorType), world);

            CapableSim.ActorTypes type;
            switch (actorType){
                case "grass":
                     type = CapableSim.ActorTypes.GRASS;
                    break;
                case "rabbit":
                    type = CapableSim.ActorTypes.RABBIT;
                    break;
                case "burrow":
                    type = CapableSim.ActorTypes.BURROW;
                    break;
                default:
                    return;
            }

            int numOfActors = sim.getNumOfActors(type);

            //System.out.print("Number of " + type + " actors: ");
            //System.out.println(sim.getNumOfActors(type));
            //System.out.println();

            if (interval.max != 0) {
                assertions.put(s, ((numOfActors >= interval.min)  && (numOfActors <= interval.max)));
            }
            else assertions.put(s, (numOfActors == interval.min));

        });

        assertions.forEach((key, value) -> {assertTrue(value);});
    }

    @AfterEach
    public void tearDown() {
        world = null;
    }

}
