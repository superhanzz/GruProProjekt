package CapableSimulator;

import CapableSimulator.Actors.*;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawningAgent {

    World world;

    public SpawningAgent(World world) {
        this.world = world;
    }

    public void generateActors(InputFileStruct fileStruct){
        TileFinder tileFinder = new TileFinder();

        switch (fileStruct.actorType){
            case "wolf":
                WolfGang gang = null;
                Wolf alpha = null;
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Wolf o = new Wolf(gang, (gang == null));
                        if (gang == null) alpha = o;

                        if (gang == null) gang = new WolfGang(o);
                        else gang.addWolfToGang(o);
                        o.updateOnMap(world, location, true);
                    }
                    else
                        System.out.println("Failed to create an actor of type " + fileStruct.actorType);
                    try {
                        gang.setNewAlpha(alpha);
                    } catch (NullPointerException e) {
                        System.out.println("Tried to call setNewAlpha on alpha, but 'gang' is null. \t" + e.getMessage());
                    }
                }
                break;

            case "rabbit":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Rabbit r = new Rabbit();
                        r.updateOnMap(world, location, true);
                    }
                }
                break;

            case "bear":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Bear b = new Bear(fileStruct.staticSpawnLocation != null ? fileStruct.staticSpawnLocation : location);
                        world.setTile(location, b);
                    }
                    else
                        System.out.println("Failed to create an actor of type " + fileStruct.actorType);
                }
                break;

            case "putin":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Putin p = new Putin();
                        p.updateOnMap(world, location, true);
                    }
                }
                break;

            case "grass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Grass g = new Grass();
                        world.setTile(location, g);
                    }
                }
                break;

            case "berry":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        BerryBush b = new BerryBush();
                        bushList.add(b);
                        world.setTile(location, b);
                    }
                    else
                        System.out.println("Failed to create an actor of type " + fileStruct.actorType);
                }
                break;

            case "burrow":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Burrow b = new Burrow();
                        world.setTile(location, b);
                    }
                }
                break;

            default:
                break;
        }
    }

    public void delayedSpawns(World world) {
        Pattern pattern = Pattern.compile("([A-Za-z]+"+actorSpawnCycle+")"); // Makes sure that only actor of the given spawn cycle spawns
        for (String key :  inputMap.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                generateActors2(inputMap.get(key), world);
            }
        }
        actorSpawnCycle++;
    }




    /*
    public void generateActors2(InputFileStruct fileStruct, World world){
        System.out.println(fileStruct.actorType);
        Supplier<Actor> actorConstructor = actorConstructorRegistry.get(fileStruct.actorType);
        if (actorConstructor == null) {
            System.out.println("Tried to create an unknown actor: " + fileStruct.actorType);
            return;
        }


        if(fileStruct.actorType.equals("wolf")) {
            WolfGang gang = null;
            Wolf alpha = null;
            for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
                Location location = getEmptyTile(world, true);
                if (location != null) {
                    Wolf o = new Wolf(gang, (gang == null));
                    if (gang == null) alpha = o;

                    if (gang == null) gang = new WolfGang(o);
                    else gang.addWolfToGang(o);
                    o.updateOnMap(world, location, true);
                    //world.setTile(location, o);
                }
                else
                    System.out.println("Failed to create an actor of type " + fileStruct.actorType);
                try {
                    gang.setNewAlpha(alpha);
                } catch (NullPointerException e) {
                    System.out.println("Tried to call setNewAlpha on alpha, but 'gang' is null. \t" + e.getMessage());
                }
            }
            return;
        } else if (fileStruct.actorType.equals("bear")) {
            for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
                Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : getEmptyTile(world, true);
                if (location != null) {
                    Bear b = new Bear(fileStruct.staticSpawnLocation != null ? fileStruct.staticSpawnLocation : location);
                    world.setTile(location, b);
                }
                else
                    System.out.println("Failed to create an actor of type " + fileStruct.actorType);
            }
            return;
        }
        else if (fileStruct.actorType.equals("berry")) {
            for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
                Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : getEmptyTile(world, true);
                if (location != null) {
                    BerryBush b = new BerryBush();
                    bushList.add(b);
                    world.setTile(location, b);
                }
                else
                    System.out.println("Failed to create an actor of type " + fileStruct.actorType);
            }
            return;
        }
        else if (fileStruct.actorType.equals("rabbit")) {
            for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
                Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : getEmptyTile(world, true);
                if (location != null) {
                    Rabbit r = new Rabbit();
                    r.updateOnMap(world, location, true);
                }
            }
        }
        else if (fileStruct.actorType.equals("putin")) {
            for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
                Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : getEmptyTile(world, true);
                if (location != null) {
                    Putin p = new Putin();
                    p.updateOnMap(world, location, true);
                }
            }
        }
        for (int i = 0; i < fileStruct.getSpawnAmount(); i++){
            Location location = getEmptyTile(world, false);
            if (location != null) {
                Object o = actorConstructor.get();
                world.setTile(location, o);
            }
            else
                System.out.println("Failed to create an actor of type " + fileStruct.actorType);
        }


    }
    */


}
