package CapableSimulator.Utils;

import CapableSimulator.Actors.*;
import CapableSimulator.Actors.Animals.*;
import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Predators.Putin;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Animals.Predators.WolfGang;
import CapableSimulator.Actors.Plants.BerryBush;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.Actors.Shelter.Burrow;
import itumulator.world.Location;
import itumulator.world.World;

public class SpawningAgent {

    /** Instantiates all the actors specified in the input file struct.
     * @param world The world to spawn the actors in.
     * @param fileStruct The data file containing all the relevant data
     * @throws NullPointerException if the world is null.
     * @throws NullPointerException if the file struct is null.
     */
    public static void generateActors(World world, InputFileStruct fileStruct){
        if (world == null)
            throw new NullPointerException("world is null");
        else if (fileStruct == null)
            throw new NullPointerException("fileStruct is null");

        switch (fileStruct.actorType){
            case "wolf":
                WolfGang gang = new WolfGang(world);

                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = TileFinder.getEmptyTile(world, true);

                    if (location != null) {
                        Wolf wolf = new Wolf(world);
                        gang.addNewFlockMember(wolf);
                        wolf.updateOnMap(location, true);

                        if (fileStruct.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                            wolf.becomeInfected();
                        }
                    }
                }
                break;

            case "rabbit":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Rabbit r = new Rabbit(world);
                        r.updateOnMap(location, true);

                        if (fileStruct.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                            r.becomeInfected();
                        }
                    }
                }
                break;

            case "bear":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Bear b = new Bear(world, fileStruct.staticSpawnLocation != null ? fileStruct.staticSpawnLocation : location);
                        b.updateOnMap(location, true);

                        if (fileStruct.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                            b.becomeInfected();
                        }
                    }
                }
                break;

            case "putin":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Putin p = new Putin(world);
                        p.updateOnMap(location, true);

                        if (fileStruct.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                            p.becomeInfected();
                        }
                    }
                }
                break;

            case "grass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, false);
                    if (location != null) {
                        Grass g = new Grass(world);
                        world.setTile(location, g);
                    }
                }
                break;

            case "berry":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        BerryBush b = new BerryBush(world);
                        world.setTile(location, b);
                    }
                }
                break;

            case "burrow":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, false);
                    if (location != null) {
                        Burrow b = new Burrow(world);
                        world.setTile(location, b);
                    }
                }
                break;

            case  "carcass":
                for (int i = 0; i < fileStruct.getSpawnAmount(); i++) {
                    Location location = (fileStruct.staticSpawnLocation != null) ? fileStruct.staticSpawnLocation : TileFinder.getEmptyTile(world, true);
                    if (location != null) {
                        Carcass c = new Carcass(world);
                        world.setTile(location, c);

                        if (fileStruct.fungiState.equals(CapableEnums.FungiState.FUNGI)) {
                            c.becomeInfected();
                        }
                    }
                }
                break;

            default:
                System.out.println("Unknown Spawn Type: " + fileStruct.actorType);
                break;
        }
    }

    /** Handles the instantiation of all the actors in the given input file
     * @param world The world to spawn the actors in.
     * @param filePath The path of the file to spawn from.
     * @throws NullPointerException if the world is null.
     * @throws IllegalArgumentException if the filePath is empty or null.
     */
    public static void spawnActorsFromInputFile(World world, String filePath) {
        if (world == null)
            throw new NullPointerException("world is null");
        else if (filePath == null || filePath.isEmpty())
            throw new IllegalArgumentException("filePath is empty or null");

        Parser parser = new Parser(filePath);
        parser.parseInputsFromFile();
        parser.getWorldSize();

        for (InputFileStruct inputStruct : parser.getInputMap().values()) {
            generateActors(world, inputStruct);
        }
    }

    /** Spawn an actor in the given world at the specified location.
     * @param world The world to spawn the actor in.
     * @param actor Reference to the actor that is to be spawned.
     * @param location The location to spawn the actor
     * @throws NullPointerException Throws exception if world is null
     * @throws NullPointerException Throws exception if actor is null
     * @throws NullPointerException Throws exception if location is null
     */
    public static void spawnActorAtLocation(World world, WorldActor actor, Location location) {
        if (world == null)
            throw new NullPointerException("World is null");
        else if (actor == null)
            throw new NullPointerException("actor is null");
        else if (location == null)
            throw new NullPointerException("location is null");

        if (location == null || actor == null) {
            if (location == null) throw new NullPointerException("In spawnActorAtLocation(): location is null");
            else throw new NullPointerException("In spawnActorAtLocation(): actor is null");
        }

        if (actor instanceof Animal animal) {
            animal.updateOnMap(location, true);
        }
        else {
            world.setTile(location, actor);
        }
    }
}
