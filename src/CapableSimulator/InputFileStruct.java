package CapableSimulator;

import itumulator.world.Location;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileStruct {

    // The actorType
    public String actorType;

    // Amount to spawn
    public int minAmount;
    public int maxAmount;

    // If a static spawn i declared
    public Location staticSpawnLocation;

    // If 2 or more entries of the same type is present in the input file
    public boolean isDelayedSpawn;


    /**
     * */
    public InputFileStruct(String actorType, int minAmount, int maxAmount, Location staticSpawnLocation,  boolean isDelayedSpawn) {
        this.actorType = actorType;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.staticSpawnLocation = staticSpawnLocation;
        this.isDelayedSpawn = isDelayedSpawn;
    }

    public InputFileStruct(String inputLine){
        this.actorType = "";
        this.minAmount = 0;
        this.maxAmount = 0;
        this.staticSpawnLocation = null;
        //this.isDelayedSpawn = false;

        buildInputFile(inputLine);
    }

    public int getSpawnAmount() {
        if (maxAmount == 0)
            return minAmount;

        Random rand = new Random();
        return rand.nextInt(minAmount, maxAmount);
    }

    /** Parses the input file line and builds the file structure
     * */
    public void buildInputFile(String input) {
        if (input == null || input.isEmpty()) throw new IllegalArgumentException();
        String[] words = input.split(" ");
        actorType = words[0];

        parseSpawnAmount(words[1]);

        // If a static spawn location is declared at the given line
        if (input.contains("(") && words.length > 2)
            parseStaticSpawnLocation(words[2]);
    }

    /** Parses the spawn amount from the input file line
     * */
    private void parseSpawnAmount(String input) {
        if (input.contains("-")) {
            Pattern pattern = Pattern.compile("(\\d+)-(\\d+)"); // Regular expression that extracts the minimum and maximum amount from the interval
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                minAmount = Integer.parseInt(matcher.group(1));
                maxAmount = Integer.parseInt(matcher.group(2));
            }
        }
        else minAmount = Integer.parseInt(input);
    }

    /** Parses the static spawn location from the input file line if one i present
     * */
    private void parseStaticSpawnLocation(String input) {
        int x = 0;
        int y = 0;
        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");   // Regular expression that extracts the x and y coordinates from the static location
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            x = Integer.parseInt(matcher.group(1));
            y = Integer.parseInt(matcher.group(2));
            staticSpawnLocation = new Location(x, y);
        }
    }

}
