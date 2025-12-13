package CapableSimulator.Utils;

import FunctionLibrary.CapableFunc;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, InputFileStruct>  inputMap = new  HashMap<>();
    private String inputFilePath;

    public Parser(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public Parser() {

    }

    public void parseInputsFromFile() {
        if (inputFilePath == null || inputFilePath.isEmpty()) return;
        parseInputsFromFile(inputFilePath);
    }

    public void parseInputsFromFile(String filePath){
        inputMap = parseInputsFromFile(new File(filePath));
    }

    public Map<String, InputFileStruct> parseInputsFromFile(File file) {
        Map<String, InputFileStruct>  local_inputMap = new  HashMap<>();

        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String RESET = "\u001B[0m";

        int lineNumber = 0;                 // Debug line number
        String filePath = file.getPath();   // Debug file path

        //System.out.printf("Parsing file: %s%s%s %n", RED, filePath, RESET);

        try(Scanner sc = new Scanner(file)){
            // Handles the extraction of the world size
            int worldSize = Integer.parseInt(sc.nextLine());
            local_inputMap.put(new String("#" + String.valueOf(worldSize) + "#"), null);

            lineNumber++;   // Debug line number
            while(sc.hasNextLine()){
                String line = sc.nextLine();    // saves the input line as a string
                if (line.contains(" ")) {       // Makes sure there is a " " before splitting the line
                    InputFileStruct inputFile = new InputFileStruct(line);
                    String mapKey = inputFile.actorType;

                    // Handles the case of the return map already contains an entry of the same class.
                    if (local_inputMap.containsKey(inputFile.actorType)) {

                        // Findes the amount of times the same actory type is present in the map.
                        int numOfSameActorType = 0;
                        for (String key : local_inputMap.keySet()) {
                            if (local_inputMap.get(key) != null) {     // Ignores the world size entry
                                if (local_inputMap.get(key).actorType.equals(inputFile.actorType))
                                    numOfSameActorType++;
                            }
                        }
                        mapKey += String.valueOf(numOfSameActorType); // Updates the mapKey
                    }
                    local_inputMap.put(mapKey, inputFile);

                    //System.out.printf("%-10s* %-10s%s%n",GREEN, inputFile.actorType,  RESET);
                    lineNumber++;   // Debug line number
                }
            }
            //System.out.println();
        }
        catch (Exception e) {
            System.out.println("Error in parseInputsFromFile(), message: " + e.getMessage());
            System.out.println(lineNumber);
            System.out.println(filePath);
            System.out.println();
        }
        return  local_inputMap;
    }

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

    public Map<String, Map<String, InputFileStruct>> getAllInputs(File dataFolder) {
        if (dataFolder == null) return null;    // TODO make exeption or something

        Map<String, Map<String, InputFileStruct>> allInputs = new HashMap<>();
        boolean printAllInputs = false;  // Debug print all entries

        /* Goes through all input files and extracts the file name, and makes an entry in "allInput" map
         *  Where the file name is the key and the value is a map of all the inputs
         * */
        Map<File, List<File>> map = getAllInputDataFiles(dataFolder);   // Gets all the data files
        for (File folder : map.keySet()) {
            for (File file : map.get(folder)) {
                String fileName = getInputFileName(file);                               // Gets the name of the given file
                Map<String, InputFileStruct> inputs = parseInputsFromFile(file);       // Retrieves all the inputs
                allInputs.put(fileName, inputs); // Inserts the give files inputs, into the "allInputs" map
            }
        }

        // Prints everything like if printAllInputs is set to true:
        // key        value.size entries
        if (printAllInputs) allInputs.forEach((key, value) -> {System.out.printf("%-10s %5d entries%n",  key, value.size());});

        return allInputs;
    }

    public Map<String, InputFileStruct>  getInputMap() {
        return inputMap;
    }

    public int getWorldSize() {
        if (inputFilePath == null || inputFilePath.isEmpty()) return 0;
        return getWorldSize(inputMap);
    }

    public int getWorldSize(Map<String, InputFileStruct> map) {
        if (map.isEmpty()) return 0;
        String worldSizeKey = null;
        int worldSize = 0;

        for (String key : map.keySet()) {
            if (key.contains("#")) {
                Pattern pattern = Pattern.compile("\\#(\\d+)\\#");  // Regular expression that accepts Strings of the form "#<integer>#"
                Matcher matcher = pattern.matcher(key);
                if (matcher.matches()) {
                    //System.out.println("DEBUG in getWorldSize(), in Parser. \tWorld Size: " + matcher.group(1));
                    worldSizeKey = key;
                    worldSize = Integer.parseInt(matcher.group(1));
                }
            }
        }
        inputMap.remove(worldSizeKey);
        return worldSize;
    }


}
