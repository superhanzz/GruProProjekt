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
    private int optimalRadiusAroundAlpha;
    private int allowedRadiusAroundAlpha;
    //public WolfDen wolfDen;
    public Location denLocation;


    public WolfGang(CapableWorld world) {
        super(world);

        optimalRadiusAroundAlpha = 2;
        allowedRadiusAroundAlpha = 3;
        denLocation = null;
    }

    public void alphaMoved(Location location) {
        flockMembers.forEach(animal -> {
            if  (animal instanceof Wolf wolf) wolf.followAlpha(location);
        });
        clearDeadFlockMembers();
    }

    public void getNearbyWolfsFromGang(Wolf askingWolf, List<Wolf> nearbyWolfs) {
        PathFinder pathFinder = new PathFinder(world);
        for (Animal animal : flockMembers) {
            if(animal instanceof Wolf wolf) {
                if (wolf.isOnMap()) {
                    double distance = pathFinder.distance(askingWolf.getLocation(), wolf.getLocation());
                    if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(wolf);
                }
            }
        }
        if (flockLeader != askingWolf && flockLeader instanceof Wolf leader && leader.isOnMap()) {
            double distance = pathFinder.distance(askingWolf.getLocation(), leader.getLocation());
            if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(leader);
        }
    }

    /* ----- ----- ----- ----- Getters and Setters ----- ----- ----- -----*/

    public int getOptimalRadiusAroundAlpha() { return optimalRadiusAroundAlpha; }
    public int getAllowedRadiusAroundAlpha() { return allowedRadiusAroundAlpha; }

    /* ----- ----- ----- ----- Flock Animal Related ----- ----- ----- ----- */


}
