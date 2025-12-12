import java.awt.Color;
import java.io.File;
import java.util.*;

import CapableSimulator.Actors.Burrow;
import CapableSimulator.CapableSim;
import CapableSimulator.Actors.Grass;
import CapableSimulator.Actors.Rabbit;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    int worldSize = 0;
    World world;
    Program program;
    int simulationSteps = 200;

    public static void main(String[] args) {

        CapableSim sim = new CapableSim(200, 800, 200, "src/Data/week-2/t2-3a.txt");
        sim.runSimulation();


        /* Parses the input file into a map */

        /*
        Map<String, Integer> inputMap = parseInputFile("src/Data/week-1/t1-1c.txt");

        program = new Program(worldSize, 800, 500);
        world = program.getWorld();
        for (String key : inputMap.keySet()) {
            generateActors(key, inputMap.get(key), world);
        }

        setUpDisplayInformation();


        program.show();
        for (int i = 0; i < simulationSteps; i++){
            program.simulate();
        }
*/

        /*
        int size = 1;
        String line1;
        String line2;
        String line3;
        String[] words = new String[2];
        String[] words1 = new String[2];
        ArrayList<CapableSimulator.Actors.Grass> grasses = new ArrayList<>();

        File input1 = new File("src/Data/week-1/t1-1c.txt");
        System.out.println(System.getProperty("user.dir"));
        try(Scanner sc = new Scanner(input1)){
            line1 = sc.nextLine();
            System.out.println(line1);
            size = Integer.parseInt(line1);
            line2 = sc.nextLine();
            words = line2.split(" ");
            if(words[1].contains("-")){
                String[] interval = words[1].split("-");
                Random rand = new Random();
                words[1] = Integer.toString(rand.nextInt(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
            }
            line3 = sc.nextLine();
            words1 = line3.split(" ");
            if(words1[1].contains("-")){
                String[] interval = words1[1].split("-");
                Random rand = new Random();
                words1[1] = Integer.toString(rand.nextInt(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
            }



        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        Program program = new Program(worldSize, 800, 500);

        World world = program.getWorld();

        DisplayInformation diGrass = new DisplayInformation(Color.green, "grass");
        program.setDisplayInformation(CapableSimulator.Actors.Grass.class, diGrass);
        DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");
        program.setDisplayInformation(CapableSimulator.Actors.Rabbit.class, diRabbit);

        createActor(worldSize, words, world);
        createActor(worldSize, words1, world);

        //world.setTile(new Location(0,1), new CapableSimulator.Actors.Grass());
        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        program.show();

        for (int i = 0; i < 200; i++){
            program.simulate();
            //enveiromentalUpdate(world);
        }
        */
    }

    static void createActor(int size, String[] words, World world){
        String type = words[0];
        int amount = Integer.parseInt(words[1]);
        System.out.println(amount);
        for(int i = 0; i < amount; i++){
            Location loc = findEmpty(size, world);
            switch (type){
                case "grass":
                    world.setTile(loc, new Grass());
                    break;
                case "rabbit":
                    world.setTile(loc, new Rabbit());
                    break;
                default:
                    System.out.println("dø");
                    return;
            }
        }
    }

    void generateActors(String actorType, int amount, World world){

        switch (actorType){
            case "grass":
                for(int i = 0; i < amount; i++) {
                    /*
                    CapableSimulator.Actors.Grass grass = new CapableSimulator.Actors.Grass();
                    Location location = getEmptyTile(world);
                    world.setTile(location, grass);
                    */
                    world.setTile(getEmptyTile(world), new Grass()); };
                break;

            case "rabbit":
                for(int i = 0; i < amount; i++) world.setTile(getEmptyTile(world), new Grass());
                break;

            case "burrow":
                for(int i = 0; i < amount; i++) world.setTile(getEmptyTile(world), new Burrow());
                break;

            default:
                System.out.println("Tried to create an unknown actor: "  + actorType);
                break;
        }
    }

    Location getEmptyTile(World world){
        Random rand = new Random();
        boolean isEmpty = false;
        Location emptyTile = null;
        int loopCounter = 0;

        /* Tries to find an empty tile in the world. If it attempts more than a certain amount of times without success, it breaks and returns null */
        while(!isEmpty){
            emptyTile = new  Location(rand.nextInt(worldSize), rand.nextInt(worldSize));
            isEmpty = world.getTile(emptyTile) != null;
            if(loopCounter >= 100) {
                System.out.println("Couldn't find empty tile");
                break;
            }
            loopCounter++;
        }

        return emptyTile ;
    }

    static Location findEmpty(int size, World world){
        Location location;
        Random r = new Random();
        int x = r.nextInt(size);
        int y = r.nextInt(size);

        location = new Location(x, y);


        while(world.getTile(location) != null){
            x = r.nextInt(size);
            y = r.nextInt(size);

            location = new Location(x, y);
        }
        return location;
    }


    Map<String, Integer> parseInputFile(String path){
        Map<String, Integer> map = new HashMap<>();
        File inputFile = new File(path);

        try(Scanner sc = new Scanner(inputFile)){

            worldSize = Integer.parseInt(sc.nextLine()); // The First line is always the world size

            /** Iterates though each line of the input file and parses the line into the map<String, Integer> **/
            while(sc.hasNextLine()){
                String[] Words = sc.nextLine().split(" ");
                if (map.containsKey(Words[0])) throw new RuntimeException("paraseInputFile() read the same identifier more than once: " + Words[0]);

                /* If the quantity is between 2 values, it generates a random number between the 2 */
                if (Words[1].contains("-")) {
                    Random rand = new Random();
                    String[] interval = Words[1].split("-");
                    Words[1] = Integer.toString(rand.nextInt(Integer.parseInt(interval[1]), Integer.parseInt(interval[1])));
                }

                map.put(Words[0], Integer.parseInt(Words[1]));
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return map;
    }

    /**
     * Set's up display information for all the classes
     * */
    void setUpDisplayInformation() {
        DisplayInformation diGrass = new DisplayInformation(Color.green, "grass");
        program.setDisplayInformation(Grass.class, diGrass);

        DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, diRabbit);
    }



    /*static void enveiromentalUpdate(World world){
        Map<Object, Location> entities = world.getEntities();
        for(Object object : entities.keySet()){
            if(object instanceof CapableSimulator.Actors.Grass){
                ((CapableSimulator.Actors.Grass) object).grow(world);
            }
        }
    }*/
}