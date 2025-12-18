package Tests;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Fungis.Fungi;
import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.*;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileReadingTest {



    @BeforeEach
    public void setup() {

    }


    @RepeatedTest(1)
    public void testReadFile() {
        File dataFolder = new File("src/Data");

        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        final String PURPLE = "\u001B[35m";
        final String CYAN = "\u001B[36m";
        final String WHITE = "\u001B[37m";




        for (String actorType : WorldActor.getAllWorldActorTypes()) {
            if (actorType.equals("carcass") || actorType.equals("putin")) continue;

            System.out.printf("Testing for actorType: %5s%s%s.", RED, actorType.toUpperCase(), RESET);
            System.out.println();

            Map<String, Map<String, InputFileStruct>> inputFilesToTest = new HashMap<>();
            Parser parser = new Parser();

            // Retrieves all the files containing the actor type
            Map<String, Map<String, InputFileStruct>> allInputs = parser.getAllInputs(dataFolder);

            for (String fileName : allInputs.keySet()) {

                Map<String, InputFileStruct> input = allInputs.get(fileName);
                for (String key : input.keySet()) {
                    if (input.get(key) == null) continue;
                    if (input.get(key).actorType.equals(actorType)) {
                        //System.out.println(fileName + " contains the actor type: " + actorType);
                        inputFilesToTest.put(fileName, input);
                    }
                }
            }

            // Testing every input file containing the actor type
            for (String fileName : inputFilesToTest.keySet()) {
                Map<String, InputFileStruct> input = inputFilesToTest.get(fileName);

                // Retrieves the world size and removes it from the inputs map
                int worldSize = parser.getWorldSize(input); // Retrieves the world size
                if (worldSize == 0) continue;   // If there was no world size then skip this file

                // Creates the simulation environment
                World world = new World(worldSize);

                // Creates all the actors of the specified type
                for (String key : input.keySet()) {
                    if (input.get(key) == null) continue;

                    InputFileStruct inputFile = input.get(key);
                    if (!inputFile.actorType.equals(actorType)) continue;

                    int preNum = WorldUtils.getNumOfActors(world, actorType, false);

                    SpawningAgent.generateActors(world, inputFile);
                    int postNum = WorldUtils.getNumOfActors(world, actorType, false);

                    int spawnedNum = postNum - preNum;

                    if (inputFile.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                        int numOfInfected = 0;
                        Map<Object, Location> entities = world.getEntities();
                        for (Object o : world.getEntities().keySet()) {
                            if (entities.get(o) == null) continue;
                            else if (o instanceof Animal animal && animal.getActorType().equals(actorType) && animal.isInfected())
                                numOfInfected++;
                        }
                        assertEquals(numOfInfected, spawnedNum);
                    }



                    String interval = inputFile.minAmount + "-" + inputFile.maxAmount;
                    //System.out.println(spawnedNum + ",\t ");
                    System.out.print("\t");
                    System.out.printf("Spawned: %s%-5d%s Interval: %s%-10s%s File: %s%5s%s%n", GREEN,spawnedNum,RESET, PURPLE,interval,RESET, CYAN,fileName,RESET);


                    if (inputFile.maxAmount == 0)
                        assertEquals(inputFile.minAmount, spawnedNum);
                    else
                        assertTrue((inputFile.minAmount <= spawnedNum) && (spawnedNum <= inputFile.maxAmount));
                }
                //System.out.println();   // Separates files
            }
            System.out.println();
        }
    }


    @AfterEach
    public void tearDown() {
    }
}
