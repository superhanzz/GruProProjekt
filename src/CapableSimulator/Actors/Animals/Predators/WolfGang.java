package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.Animals.AnimalFlock;
import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.CapableWorld;
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

    /*public void wolfDied(Wolf wolf) {
        if (Alpha == wolf) {
            if (NPCs.isEmpty()) {
                System.out.println("No NPCs Wolfs Left");
            }
            else {
                NPCs.getFirst().promoteToAlpha();
                NPCs.remove(Alpha);
            }
        }
        System.out.println("Wolf Dead.");
    }*/

    /*public void removeWolfFromGang(Wolf wolf) {
        NPCs.remove(wolf);
    }*/

    /*public void cleanNPCList(World world) {
        List<Wolf> wolfsToKill = new ArrayList<>();
        for (Wolf wolf : NPCs) {
            if (!world.getEntities().keySet().contains(wolf)) {
                wolfsToKill.add(wolf);
                System.out.println("Wolf to Killed.");
            }
        }
        for (Wolf wolf : wolfsToKill) {
            NPCs.remove(wolf);
            System.out.println(wolf.toString() + " removed.");
        }
    }*/

    /*public void setNewAlpha(Wolf alpha) {
        Alpha = alpha;
        alphaAssertsDominance(alpha);
    }*/

    /*public void alphaAssertsDominance(Wolf alpha) {
        if (NPCs.contains(alpha)) NPCs.remove(alpha);
        Alpha.setAlpha(alpha);

        for (Wolf npc : NPCs) {
            npc.setAlpha(alpha);
        }
    }*/


    /*public void wolfDenCreated(World world, WolfDen wolfDen) {
        if (this.wolfDen != null) return;
        this.wolfDen = wolfDen;
        denLocation = world.getLocation(wolfDen);
        for  (Wolf npc : NPCs) {
            npc.setWolfDen(wolfDen);
        }
    }*/

    public void getNearbyWolfsFromGang(Wolf askingWolf, List<Wolf> nearbyWolfs) {
        for (Animal animal : flockMembers) {
            if(animal instanceof Wolf wolf) {
                double distance = wolf.distance(askingWolf.getLocation(), wolf.getLocation());
                if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(wolf);
            }
        }
        if (flockLeader != askingWolf && flockLeader instanceof Wolf leader) {
            double distance = leader.distance(askingWolf.getLocation(), leader.getLocation());
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
