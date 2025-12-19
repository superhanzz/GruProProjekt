package CapableSimulator.Utils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private Map<String, InputFileStruct>  inputMap = new  HashMap<>();

    private String inputFilePath;

    /** Default constructor.
     * @param inputFilePath The path to the input file.
     */
    public Parser(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    /** Constructor for testing. */
    public Parser() {}

    /** Overloaded parseInputsFromFile, Should only be used if the default constructor is used. */
    public void parseInputsFromFile() {
        if (inputFilePath == null || inputFilePath.isEmpty()) return;
        parseInputsFromFile(inputFilePath);
    }

    /** Overloaded parseInputsFromFile, that takes the file path insted of a file. The returned value of the pared file is inserts the values into an internal map: inputMap.
     * @param filePath The path to the input file.
     */
    public void parseInputsFromFile(String filePath){
        inputMap = parseInputsFromFile(new File(filePath));
    }

    /** The default parseInputsFromFile. Parses the given inputFile.
     * @param file Reference to the input file.
     * @return Returns a map of all the parsed data.
     */
    public Map<String, InputFileStruct> parseInputsFromFile(File file) {
        Map<String, InputFileStruct>  local_inputMap = new  HashMap<>();

        final String RED = "\u001B[31m";
        final String GREEN = "\u001B[32m";
        final String RESET = "\u001B[0m";

        int lineNumber = 0;                 // Debug line number
        String filePath = file.getPath();   // Debug file path


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
        return  local_inputMap;
    }

    /** Retrieves all the inputfiles within the specified folder file.
     * @param dataFolder Reference to the data folder.
     * @return Returns a map of all the data folder names and files.
     */
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

    /** Findes the name of the given file.
     * @param file Reference to the file.
     * @return Returns the file name.
     * @throws RuntimeException Throws exception if file is not within the folder: "scr/Data/".
     */
    public static String getInputFileName(File file){
        String fileName = file.getPath();
        if(!fileName.contains("src/Data/")){
            throw new RuntimeException("Tried to get input file name from file: " + fileName + ".\n \t - Which is not in the DataFolder. \n \t -In CapableFunc.getInputFileName().");
        }
        return fileName.replaceAll(".*/([^/]+)\\.[^/]+$", "$1");
    }

    /** Retrieves all the inputs within the specified data folder.
     * @param dataFolder Reference to the data folder.
     * @return Returns a map where the key is the file name, and the value is a map of all the inputs in the specifik input file.
     */
    public Map<String, Map<String, InputFileStruct>> getAllInputs(File dataFolder) {
        if (dataFolder == null) return null;

        Map<String, Map<String, InputFileStruct>> allInputs = new HashMap<>();

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

        return allInputs;
    }

    /** Retrieves the world size from within the specified map, and removes that entry.
     * @param map Reference to a input map.
     * @return Returns the world size as an integer.
     */
    public int getWorldSize(Map<String, InputFileStruct> map) {
        if (map.isEmpty()) return 0;
        String worldSizeKey = null;
        int worldSize = 0;

        for (String key : map.keySet()) {
            if (key.contains("#")) {
                Pattern pattern = Pattern.compile("\\#(\\d+)\\#");  // Regular expression that accepts Strings of the form "#<integer>#"
                Matcher matcher = pattern.matcher(key);
                if (matcher.matches()) {
                    worldSizeKey = key;
                    worldSize = Integer.parseInt(matcher.group(1));
                }
            }
        }
        inputMap.remove(worldSizeKey);
        return worldSize;
    }

    /** Overloaded version of getWorldSize, where the metod is executed on the internal input map.
     * @return Returns the world size as an integer.
     */
    public int getWorldSize() {
        if (inputFilePath == null || inputFilePath.isEmpty()) return 0;
        return getWorldSize(inputMap);
    }

    /** Parses the given input file, to retrieve the world size.
     * @param filePath The input file path.
     * @return Returns the world size as an integer.
     */
    public static int parseWorldSizeFromFile(String filePath) {
        File file = new File(filePath);
        int worldSize = 0;
        try (Scanner sc = new Scanner(file)){
            worldSize = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Error in parseWorldSizeFromFile(), message: " + e.getMessage());
        }
        return worldSize;
    }

    /** Retrieves the internal input map-
     * @return Returns a reference to the internal input map.
     */
    public Map<String, InputFileStruct>  getInputMap() {
        return inputMap;
    }
}
