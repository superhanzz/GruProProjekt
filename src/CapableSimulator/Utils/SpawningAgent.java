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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpawningAgent {

    World world;

    public SpawningAgent(World world) {
        this.world = world;
    }

    /**
     * @param fileStruct*/
    public void generateActors(InputFileStruct fileStruct){

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

    /**
     * @param filePath The path of the file to spawn from.
     */
    public void spawnActorsFromInputFile(String filePath) {
        Parser parser = new Parser(filePath);
        parser.parseInputsFromFile();
        parser.getWorldSize();


        for (InputFileStruct inputStruct : parser.getInputMap().values()) {
            generateActors(inputStruct);
        }
    }

    /**
     * @param actor
     * @param location */
    public void spawnActorAtLocation(WorldActor actor, Location location) {
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
