package CapableSimulator.Utils;

import CapableSimulator.Actors.*;
import CapableSimulator.CapableWorld;
import CapableSimulator.EventHandeling.Dispacher;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawningAgent {

    CapableWorld world;

    private final Dispacher<WorldActor> simulateDispacher =  new Dispacher<>();

    public SpawningAgent(CapableWorld world) {
        this.world = world;
    }

    public void generateActors(InputFileStruct fileStruct) {
        List<WorldActor> actors = new ArrayList<>();
        generateActors(fileStruct, actors);
    }

    public void generateActors(InputFileStruct fileStruct, List<WorldActor> listOfActors){
        TileFinder tileFinder = new TileFinder(world);
        //System.out.println(fileStruct.actorType);
        //System.out.print(fileStruct.actorType);

        // TODO eval if list should be used for all types of actors
        switch (fileStruct.actorType){
            case "wolf":
                WolfGang gang = new WolfGang(world);
                world.addAnimalFlock(gang);

                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = tileFinder.getEmptyTile(world, true);

                    if (location != null) {
                        Wolf wolf = new Wolf(world);
                        gang.addNewFlockMember(wolf);
                        wolf.updateOnMap(location, true);

                        listOfActors.add(wolf);
                    }
                }
                break;

            case "rabbit":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Rabbit r = new Rabbit(world);
                        r.updateOnMap(location, true);

                        listOfActors.add(r);
                    }
                }
                break;

            case "bear":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Bear b = new Bear(world, fileStruct.staticSpawnLocation != null ? fileStruct.staticSpawnLocation : location);

                        b.updateOnMap(location, true);
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
                        Putin p = new Putin(world);
                        p.updateOnMap(location, true);

                        listOfActors.add(p);
                    }
                }
                break;

            case "grass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, false);
                    if (location != null) {
                        Grass g = new Grass(world);
                        world.setTile(location, g);

                        listOfActors.add(g);
                    }
                }
                break;

            case "berry":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        BerryBush b = new BerryBush(world);
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
                        Burrow b = new Burrow(world);
                        world.setTile(location, b);

                        listOfActors.add(b);
                    }
                }
                break;

            case  "carcass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : tileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Carcass c = new Carcass(world);
                        world.setTile(location, c);

                        listOfActors.add(c);
                    }
                }
                break;

            case "carcass fungi":
                System.out.println("carcass fungi");
                break;

            default:
                System.out.println("Unknown Spawn Type: " + fileStruct.actorType);
                break;
        }

        //System.out.println(": " + listOfActors.size());
    }

    public Map<String, List<WorldActor>> handleSpawnCycle(Map<String, InputFileStruct> inputMap, boolean isInitSpawns) {
        if (inputMap == null) {
            throw new NullPointerException("In handleSpawnCycle(): inputMap is null");
        }

        // TODO maybe the list could be something like 'Class<? extends Actor>' if at all possible
        Map<String, List<WorldActor>> worldActorsSpawned = new HashMap<>();

        // Sets the pattern filter to filter the entries by
        Pattern pattern;
        if (isInitSpawns) {
            System.out.println("Init Spawns:");
            pattern = Pattern.compile("([A-Za-z\\s]+)");
        }
        else {
            System.out.println("Delayed Spawns:");
            pattern = Pattern.compile("([A-Za-z\\s]+\\d)");
        }

        // Iterates though all the entries of the input map and spawns all the actors of the specified type, and adding the list of actors to the return map
        List<String> keysToRemove = new ArrayList<>();
        for (String key :  inputMap.keySet()) {
            Matcher matcher = pattern.matcher(key); // filters the entries
            if (matcher.matches()) {
                InputFileStruct input = inputMap.get(key);
                List<WorldActor> spawnedActors = new ArrayList<>();

                generateActors(input, spawnedActors);   // spawns the actors
                worldActorsSpawned.put(input.actorType, spawnedActors); // adds the list of spawned actors to the return map
                keysToRemove.add(key);
            }
        }
        for (String key : keysToRemove) {
            inputMap.remove(key);
        }

        return worldActorsSpawned;
    }


    public void spawnActorAtLocation(WorldActor actor, Location location) {
        if (location == null || actor == null) {
            if (location == null) throw new NullPointerException("In spawnActorAtLocation(): location is null");
            else throw new NullPointerException("In spawnActorAtLocation(): actor is null");
        }

        if (actor instanceof Animals animal) {
            animal.updateOnMap(location, true);
        }
        else {
            world.setTile(location, actor);
        }

    }


}
