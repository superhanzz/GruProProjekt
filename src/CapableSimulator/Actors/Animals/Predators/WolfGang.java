package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.Animals.AnimalFlock;
import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;

import java.util.List;

public class WolfGang extends AnimalFlock {

    //public Wolf Alpha;
    //public List<Wolf> NPCs;
    private int allowedRadiusAroundAlpha;
    //public WolfDen wolfDen;
    public Location denLocation;


    public WolfGang(CapableWorld world) {
        super(world);

        allowedRadiusAroundAlpha = 2;
        //wolfDen = null;
        denLocation = null;
    }

    public void alphaMoved(Location location) {
        flockMembers.forEach(animal -> {
            if  (animal instanceof Wolf wolf) wolf.followAlpha(location);
        });
        clearDeadFlockMembers();
        /*for (Animals animal : flockMembers) {
            if (animal instanceof Wolf wolf) {
                try {
                    wolf.followAlpha(location);
                } catch (Exception e) {
                    System.out.println(wolf + " ERROR, message:\t" + e.getMessage());
                }
            }
        }*/
    }

    public void getNearbyWolfsFromGang(Wolf askingWolf, List<Wolf> nearbyWolfs) {
        PathFinder pathFinder = new PathFinder(world);
        for (Animal animal : flockMembers) {
            if(animal instanceof Wolf wolf) {
                double distance = pathFinder.distance(askingWolf.getLocation(), wolf.getLocation());
                if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(wolf);
            }
        }
        if (flockLeader != askingWolf && flockLeader instanceof Wolf leader) {
            double distance = pathFinder.distance(askingWolf.getLocation(), leader.getLocation());
            if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(leader);
        }
    }

    protected int getAllowedRadiusAroundAlpha() { return allowedRadiusAroundAlpha; }

    /* ----- ----- ----- ----- Flock Animal Related ----- ----- ----- ----- */


    /*
    @Override
    protected void communicateNewFlockLeader() {
        for (Animals animal : flockMembers) {
            if (animal instanceof Wolf npc) {

            }
        }
    }
    */

}
