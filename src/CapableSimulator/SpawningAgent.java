package CapableSimulator;

import CapableSimulator.Actors.*;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawningAgent {

    World world;

    private final Dispacher<WorldActor> simulateDispacher =  new Dispacher<>();

    public SpawningAgent(World world) {
        this.world = world;
    }

    public void generateActors(InputFileStruct fileStruct) {
        List<WorldActor> actors = new ArrayList<>();
        generateActors(fileStruct, actors);
    }

    public void generateActors(InputFileStruct fileStruct, List<WorldActor> listOfActors){
        TileFinder tileFinder = new TileFinder();

        // TODO eval if list should be used for all types of actors
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


                        listOfActors.add(o);
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

                        listOfActors.add(r);
                    }
                }
                break;

            case "bear":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Bear b = new Bear(fileStruct.staticSpawnLocation != null ? fileStruct.staticSpawnLocation : location);
                        world.setTile(location, b);

                        listOfActors.add(b);
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

                        listOfActors.add(p);
                    }
                }
                break;

            case "grass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, false);
                    if (location != null) {
                        Grass g = new Grass();
                        world.setTile(location, g);

                        listOfActors.add(g);
                    }
                }
                break;

            case "berry":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        BerryBush b = new BerryBush();
                        world.setTile(location, b);

                        listOfActors.add(b);
                    }
                    else
                        System.out.println("Failed to create an actor of type " + fileStruct.actorType);
                }
                break;

            case "burrow":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, false);
                    if (location != null) {
                        Burrow b = new Burrow();
                        world.setTile(location, b);

                        listOfActors.add(b);
                    }
                }
                break;

            default:
                break;
        }

    }

    public Map<String, List<WorldActor>> handleSpawnCycle(Map<String, InputFileStruct> inputMap, boolean isInitSpawns) {
        if (inputMap == null || inputMap.isEmpty()) {
            String errorType = "";
            if (inputMap == null) errorType = "null";
            else errorType = "empty";

            throw new NullPointerException("In handleSpawnCycle(): inputMap is " + errorType);
        }

        // TODO maybe the list could be something like 'Class<? extends Actor>' if at all possible
        Map<String, List<WorldActor>> worldActorsSpawned = new HashMap<>();

        // Sets the pattern filter to filter the entries by
        Pattern pattern;
        if (isInitSpawns) {
            System.out.println("Init Spawns:");
            pattern = Pattern.compile("([A-Za-z]+)");
        }
        else {
            System.out.println("Delayed Spawns:");
            pattern = Pattern.compile("([A-Za-z]+\\d)");
        }

        // Iterates though all the entries of the input map and spawns all the actors of the specified type, and adding the list of actors to the return map
        for (String key :  inputMap.keySet()) {
            Matcher matcher = pattern.matcher(key); // filters the entries
            if (matcher.matches()) {
                InputFileStruct input = inputMap.get(key);
                List<WorldActor> spawnedActors = new ArrayList<>();

                generateActors(input, spawnedActors);   // spawns the actors
                worldActorsSpawned.put(input.actorType, spawnedActors); // adds the list of spawned actors to the return map
            }
        }

        return worldActorsSpawned;
    }


    public void spawnActorAtLocation(WorldActor actor, Location location) {
        if (location == null || actor == null) {
            if (location == null) throw new NullPointerException("In spawnActorAtLocation(): location is null");
            else throw new NullPointerException("In spawnActorAtLocation(): actor is null");
        }

        if (actor instanceof Animals) {
            ((Animals) actor).updateOnMap(world, location, true);
        }
        else {
            world.setTile(location, actor);
        }

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
