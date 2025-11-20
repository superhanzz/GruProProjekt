import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        int size = 1;
        String line1;
        String line2;
        String line3;
        String[] words = new String[2];
        String[] words1 = new String[2];
        ArrayList<Grass> grasses = new ArrayList<>();

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
        Program program = new Program(size, 800, 500);

        World world = program.getWorld();

        DisplayInformation diGrass = new DisplayInformation(Color.green, "grass");
        program.setDisplayInformation(Grass.class, diGrass);
        DisplayInformation diRabbit = new DisplayInformation(Color.red, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, diRabbit);

        createActor(size, words, world);
        createActor(size, words1, world);

        //world.setTile(new Location(0,1), new Grass());
        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        program.show();

        for (int i = 0; i < 200; i++){
            program.simulate();
            //enveiromentalUpdate(world);
        }
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

    /*static void enveiromentalUpdate(World world){
        Map<Object, Location> entities = world.getEntities();
        for(Object object : entities.keySet()){
            if(object instanceof Grass){
                ((Grass) object).grow(world);
            }
        }
    }*/
}