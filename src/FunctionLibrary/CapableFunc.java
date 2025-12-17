package FunctionLibrary;

import CapableSimulator.Actors.Animals.Predators.Bear;
import CapableSimulator.Actors.Animals.Rabbit;
import CapableSimulator.Actors.Animals.Predators.Wolf;
import CapableSimulator.Actors.Plants.Grass;
import CapableSimulator.Actors.Shelter.Burrow;
import CapableSimulator.Utils.InputFileStruct;
import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapableFunc {

    public static List<String> getAllWorldActorTypes() {
        List<String> actorTypes = getAllBlockingActorTypes();
        actorTypes.add("grass");
        actorTypes.add("burrow");
        actorTypes.add("wolfDen");
        actorTypes.add("putinEgg");
        return actorTypes;
    }

    public static List<String> getAllBlockingActorTypes() {
        List<String> actorTypes = getAllAnimalTypes();
        actorTypes.add("berry");
        actorTypes.add("carcass");
        actorTypes.add("fungus");
        actorTypes.add("fertilTile");
        return actorTypes;
    }

    public static List<String> getAllAnimalTypes() {
        List<String> actorTypes = getAllPredatorTypes();
        actorTypes.add("rabbit");
        return actorTypes;
    }

    public static List<String> getAllPredatorTypes() {
        List<String> actorTypes = new ArrayList<>();
        actorTypes.add("bear");
        actorTypes.add("wolf");
        actorTypes.add("putin");
        actorTypes.add("beartin");
        return actorTypes;
    }


}
