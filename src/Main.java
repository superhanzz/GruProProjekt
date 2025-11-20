import java.awt.Color;
import java.io.File;
import java.util.Scanner;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        int size = 1;

        File input1 = new File("src/Data/week-1/t1-1a.txt");
        System.out.println(System.getProperty("user.dir"));
        try(Scanner sc = new Scanner(input1)){
            String line1 = sc.nextLine();
            System.out.println(line1);
            size = Integer.parseInt(line1);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        Program p = new Program(size, 800, 75);

        World w = p.getWorld();

        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        p.show();
    }
}