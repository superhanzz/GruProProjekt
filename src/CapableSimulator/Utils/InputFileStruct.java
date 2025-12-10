package CapableSimulator.Utils;

import itumulator.world.Location;

import java.util.Arrays;
import java.util.List;
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
        List<String> words = Arrays.asList(input.split(" "));
        parseActorType(words);

        parseSpawnAmount(words);
        // If a static spawn location is declared at the given line
        parseStaticSpawnLocation(words);

    }

    private void parseActorType(List<String> input) {
        Pattern pattern = Pattern.compile("[a-z]+");
        String index1 = input.get(0);
        String index2 = input.get(1);
        Matcher matcher = pattern.matcher(index1);
        if (matcher.matches()) {
            actorType = index1;
        }
        matcher = pattern.matcher(index2);
        if (matcher.matches()) {
            actorType += " " + index2;
        }
    }

    /** Parses the spawn amount from the input file line
     * */
    private void parseSpawnAmount(List<String> input) {
        String amount = "";
        Pattern amountPattern = Pattern.compile("[\\d-]+");
        for (int i = 0; i < input.size(); i++) {
            Matcher matcher = amountPattern.matcher(input.get(i));
            if (matcher.matches()) {
                amount = input.get(i);
                break;
            }
        }
        Pattern intervalPattern = Pattern.compile("(\\d+)-(\\d+)"); // Regular expression that extracts the minimum and maximum amount from the interval
        Matcher matcher = intervalPattern.matcher(amount);
        if (matcher.matches()) {
            minAmount = Integer.parseInt(matcher.group(1));
            maxAmount = Integer.parseInt(matcher.group(2));
        }
        else minAmount = Integer.parseInt(amount);
    }

    /** Parses the static spawn location from the input file line if one i present
     * */
    private void parseStaticSpawnLocation(List<String> input) {
        Pattern staticSpawnPattern = Pattern.compile("\\((\\d+),(\\d+)\\)");    // Regular expression that extracts the x and y coordinates from the static location
        Matcher matcher = null;
        String staticSpawnLocationString = "";
        for (int i = 0; i < input.size(); i++) {
            matcher = staticSpawnPattern.matcher(input.get(i));
            if (matcher.matches()) {
                staticSpawnLocationString = input.get(i);
                break;
            }
        }
        if (staticSpawnLocationString.isEmpty()) return;
        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));
        staticSpawnLocation = new Location(x, y);

        //else System.out.println("No static spawn for: " + actorType);
    }

}
