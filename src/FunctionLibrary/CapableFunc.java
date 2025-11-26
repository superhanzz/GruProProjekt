package FunctionLibrary;

import CapableSimulator.InputFileStruct;
import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapableFunc {

    public static Location getEmptyTile(World world, int worldSize){
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

    public static Map<String, Integer> parseInputFile(String path){
        Map<String, Integer> map = new HashMap<>();
        File inputFile = new File(path);

        try(Scanner sc = new Scanner(inputFile)){

            int worldSize = Integer.parseInt(sc.nextLine()); // The First line is always the world size

            /** Iterates though each line of the input file and parses the line into the map<String, Integer> **/
            while(sc.hasNextLine()){
                String[] Words = sc.nextLine().split(" ");
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
            System.out.println(e.getMessage());
        }

        return map;
    }

    public static Map<String, List<File>> getAllInputFiles(String dataPath){
        Map<String, List<File>> weekInputDataMap = new HashMap<>();

        File dataFolder = new File(dataPath);
        File[] listOfFiles = dataFolder.listFiles();
        System.out.println("File list length: " + listOfFiles.length);

        for  (File file : listOfFiles) {
          //  System.out.println("Folder: " + file.getName() + ". Has files: ");
            List<File> list = new ArrayList<>();
            for (File f : file.listFiles()) {
                //System.out.println("\t" + f.getName());
                //f.getPath()
                //weekInputDataMap.put(file.getName(), f);
                list.add(f);
            }
            //System.out.println();
        }

        //weekInputDataMap.forEach((key, value) -> {System.out.println(key);});
        return weekInputDataMap;
    }

    public static Map<String, InputFileStruct> parseInputsFromFile(File file){
        Map<String, InputFileStruct> map = new HashMap<>();

        try(Scanner sc = new Scanner(file)){
            int worldSize = Integer.parseInt(sc.nextLine());

            while(sc.hasNextLine()){
                String line = sc.nextLine();    // saves the input line as a string
                if (line.contains(" ")) {       // Makes sure there is a " " before splitting the line
                    String[] words = line.split(" ");
                    String actorType = words[0];
                    String mapKey = words[0];
                    // Handles amount to spawn
                    int minAmount;
                    int maxAmount;
                    if (line.contains("-")) {
                        Integer[] amounts = new Integer[words[1].split("-").length];
                        minAmount = amounts[0];
                        maxAmount = amounts[1];
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
}
