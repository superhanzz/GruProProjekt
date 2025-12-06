package CapableSimulator;

import CapableSimulator.Actors.*;
import itumulator.world.World;

public class WorldUtils {

    World world;

    public WorldUtils(World world) {
        this.world = world;
    }

    public int getNumOfActors(String actorType) {
        int numOfActors = 0;
        Object[] actors = world.getEntities().keySet().toArray(new Object[0]);

        switch (actorType){
            case "grass":
                for(Object actor : actors){
                    if(actor instanceof Grass) numOfActors++;
                }
                break;
            case "rabbit":
                for(Object actor : actors){
                    if(actor instanceof Rabbit) numOfActors++;
                }
                break;
            case "burrow":
                for(Object actor : actors){
                    if(actor instanceof Burrow) numOfActors++;
                }
                break;
            case "wolf":
                for(Object actor : actors){
                    if(actor instanceof Wolf) numOfActors++;
                }
                break;
            case "bear":
                for(Object actor : actors){
                    if(actor instanceof Bear) numOfActors++;
                }
            case "berry":
                for(Object actor : actors){
                    if(actor instanceof Bear) numOfActors++;
                }
                break;
        }

        return numOfActors;
    }


}
