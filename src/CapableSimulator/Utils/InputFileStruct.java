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

    // if the actor is infected by fungi
    public CapableEnums.FungiState fungiState;

    // Amount to spawn
    public int minAmount;
    public int maxAmount;

    // If a static spawn i declared
    public Location staticSpawnLocation;

    // If 2 or more entries of the same type is present in the input file
    public boolean isDelayedSpawn;

    /** Default constructor.
     * @param inputLine The input line.
     */
    public InputFileStruct(String inputLine){
        actorType = "";
        minAmount = 0;
        maxAmount = 0;
        staticSpawnLocation = null;
        fungiState = CapableEnums.FungiState.NORMAL;

        buildInputFile(inputLine);
    }

    /** Retrieves the amount to spawn.
     * @return Either returns the static amount, or a random amount within the interval.
     */
    public int getSpawnAmount() {
        if (maxAmount == 0)
            return minAmount;

        Random rand = new Random();
        return rand.nextInt(minAmount, maxAmount);
    }

    /** Parses the input file line and builds the file structure
     * @param input The input line from the input file.
     */
    public void buildInputFile(String input) {
        if (input == null || input.isEmpty()) throw new IllegalArgumentException();
        List<String> words = Arrays.asList(input.split(" "));
        parseActorType(words);

        parseSpawnAmount(words);
        // If a static spawn location is declared at the given line
        parseStaticSpawnLocation(words);

    }

    /** Parses the actor type, as well as if the actor should be infected by fungi.
     * @param input The list of all the sub-elements of the input line.
     */
    private void parseActorType(List<String> input) {
        Pattern pattern = Pattern.compile("[a-z]+");
        String index1 = input.get(0);
        String index2 = input.get(1);

        Matcher matcher1 = pattern.matcher(index1);
        Matcher matcher2 = pattern.matcher(index2);

        if (matcher1.matches() &&  matcher2.matches()) {
            if (matcher1.group(0).equals("cordyceps")) {
                fungiState = CapableEnums.FungiState.FUNGI;
                actorType = matcher2.group(0);
            }
            else if (matcher2.group().equals("fungi")) {
                actorType = matcher1.group(0);
                fungiState = CapableEnums.FungiState.FUNGI;
            }
            else
                return;
        }
        else if (matcher1.matches()) {
            actorType = matcher1.group(0);
        }
        else
            return;
    }

    /** Parses the spawn amount from the input file line
     * @param input The list of all the sub-elements of the input line.
     */
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
     * @param input The list of all the sub-elements of the input line.
     */
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
    }
}
