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

    /** Pareses the give input file
     * @param file The file to be parsed
     * */
    public static Map<String, InputFileStruct> parseInputsFromFile(File file){
        if (file == null) return null;      // TODO make exeption or something

        Map<String, InputFileStruct> map = new HashMap<>();

        int lineNumber = 0;                 // Debug line number
        String filePath = file.getPath();   // Debug file path

        try(Scanner sc = new Scanner(file)){
            // Handles the extraction of the world size
            int worldSize = Integer.parseInt(sc.nextLine());
            map.put(new String("#" + String.valueOf(worldSize) + "#"), null);

            lineNumber++;   // Debug line number

            while(sc.hasNextLine()){
                String line = sc.nextLine();    // saves the input line as a string
                if (line.contains(" ")) {       // Makes sure there is a " " before splitting the line
                    String[] words = line.split(" ");
                    String actorType = words[0];
                    String mapKey = words[0];

                    // Handles the amount of the given actor type
                    int minAmount = 0;
                    int maxAmount = 0;
                    if (line.contains("-")) {
                        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)"); // Regular expression that extracts the minimum and maximum amount from the interval
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
                        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");   // Regular expression that extracts the x and y coordinates from the static location
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
                            if (map.get(key) != null) {     // Ignores the world size entry
                                if (map.get(key).actorType == actorType)
                                    numOfSameActorType++;
                            }
                        }
                        mapKey += String.valueOf(numOfSameActorType); // Updates the mapKey
                    }
                    InputFileStruct iFS = new InputFileStruct(actorType, minAmount, maxAmount, staticSpawnLocation, isDelayedSpawn);

                    map.put(mapKey, iFS);

                    lineNumber++;   // Debug line number
                }
            }

        }
        catch (Exception e) {
            System.out.println("Error in parseInputsFromFile(), message: " + e.getMessage());
            System.out.println(lineNumber);
            System.out.println(filePath);
            System.out.println();
        }

        return map;
    }



    public static Map<String, InputFileStruct> parseInputsFromFile2(File file){
        if (file == null) return null;      // TODO make exeption or something

        Map<String, InputFileStruct> map = new HashMap<>();

        int lineNumber = 0;                 // Debug line number
        String filePath = file.getPath();   // Debug file path

        try(Scanner sc = new Scanner(file)){
            // Handles the extraction of the world size
            int worldSize = Integer.parseInt(sc.nextLine());
            map.put(new String("#" + String.valueOf(worldSize) + "#"), null);

            lineNumber++;   // Debug line number

            while(sc.hasNextLine()){
                String line = sc.nextLine();    // saves the input line as a string
                if (line.contains(" ")) {       // Makes sure there is a " " before splitting the line
                    InputFileStruct inputFile = new InputFileStruct(line);
                    String mapKey = inputFile.actorType;

                    // Handles the case of the return map already contains an entry of the same class.
                    if (map.containsKey(inputFile.actorType)) {

                        // Findes the amount of times the same actory type is present in the map.
                        int numOfSameActorType = 0;
                        for (String key : map.keySet()) {
                            if (map.get(key) != null) {     // Ignores the world size entry
                                if (map.get(key).actorType.equals(inputFile.actorType))
                                    numOfSameActorType++;
                            }
                        }

                        mapKey += String.valueOf(numOfSameActorType); // Updates the mapKey
                    }
                    map.put(mapKey, inputFile);

                    lineNumber++;   // Debug line number
                }
            }

        }
        catch (Exception e) {
            System.out.println("Error in parseInputsFromFile(), message: " + e.getMessage());
            System.out.println(lineNumber);
            System.out.println(filePath);
            System.out.println();
        }

        return map;
    }

    /** Retrieves all the files within the data folder path
     * @param dataFolder The data folder file
     * */
    public static Map<File, List<File>> getAllInputDataFiles(File dataFolder) {
        Map<File, List<File>> map = new HashMap<>();

        //File dataFolder = new File("src/Data");
        File[] listOfFiles = dataFolder.listFiles();

        for  (File file : listOfFiles) {
            List<File> list = new ArrayList<>();
            for (File f : file.listFiles()) {
                list.add(f);
            }
            map.put(file, list);
        }
        return map;
    }

    public static String getInputFileName(File file){
        String fileName = file.getPath();
        if(!fileName.contains("src/Data/")){
            throw new RuntimeException("Tried to get input file name from file: " + fileName + ".\n \t - Which is not in the DataFolder. \n \t -In CapableFunc.getInputFileName().");
        }
        return fileName.replaceAll(".*/([^/]+)\\.[^/]+$", "$1");
    }

    public static Map<String, Map<String, InputFileStruct>> getAllInputs(File dataFolder) {
        if (dataFolder == null) return null;    // TODO make exeption or something

        Map<String, Map<String, InputFileStruct>> allInputs = new HashMap<>();
        boolean printAllInputs = false;  // Debug print all entries

        /* Goes through all input files and extracts the file name, and makes an entry in "allInput" map
         *  Where the file name is the key and the value is a map of all the inputs
         * */
        Map<File, List<File>> map = CapableFunc.getAllInputDataFiles(dataFolder);   // Gets all the data files
        for (File folder : map.keySet()) {
            for (File file : map.get(folder)) {
                String fileName = CapableFunc.getInputFileName(file);                           // Gets the name of the given file
                Map<String, InputFileStruct> inputs = CapableFunc.parseInputsFromFile(file);    // Retrieves all the inputs
                allInputs.put(fileName, inputs); // Inserts the give files inputs, into the "allInputs" map
            }
        }

        // Prints everything like if printAllInputs is set to true:
        // key        value.size entries
        if (printAllInputs) allInputs.forEach((key, value) -> {System.out.printf("%-10s %5d entries%n",  key, value.size());});

        return allInputs;
    }

    public static int getWorldSize(Map<String, InputFileStruct> map) {
        if (map == null || map.isEmpty()) return 0;
        for (String key : map.keySet()) {
            if (key.contains("#")) {
                Pattern pattern = Pattern.compile("\\#(\\d+)\\#");  // Regular expression that accepts Strings of the form "#<integer>#"
                Matcher matcher = pattern.matcher(key);
                if (matcher.matches()) {
                    //System.out.println(matcher.group(1));
                    //worldSizeKey = key;
                    return Integer.parseInt(matcher.group(1));
                }
            }
        }
        return 0;
    }


}
