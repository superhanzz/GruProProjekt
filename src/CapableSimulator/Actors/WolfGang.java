package CapableSimulator.Actors;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;

public class WolfGang {

    public Wolf Alpha;
    public List<Wolf> NPCs;
    public int radiusAroundAlpha;
    public WolfDen wolfDen;
    public Location denLocation;


    public WolfGang(Wolf Alpha) {
        this.Alpha = Alpha;
        NPCs = new ArrayList<Wolf>();
        Alpha.wolfGang = this;
        radiusAroundAlpha = 2;
        wolfDen = null;
        denLocation = null;
    }

    public void addWolfToGang(Wolf Wolf) {
        NPCs.add(Wolf);
    }

    public void alphaMoved(World world, Location location) {
        for (Wolf npc : NPCs) {
            try {
                //if (!world.getEntities().containsKey(npc)) System.out.println(this);
                npc.followAlpha(world, location);
            } catch (Exception e) {
                System.out.println(npc.toString() + " ERROR, message:\t" + e.getMessage());
            }
        }
    }

    public void wolfDied(Wolf wolf) {
        if (Alpha == wolf) {
            NPCs.getFirst().promoteToAlpha();
            NPCs.remove(Alpha);
        }
        System.out.println("Wolf Dead.");
        //System.out.println("Wolf Dead. Wolf removed from NPC list: " + !NPCs.contains(wolf));
    }

    public void removeWolfFromGang(Wolf wolf) {
        NPCs.remove(wolf);
    }

    public void cleanNPCList(World world) {
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
    }

    public void setNewAlpha(Wolf alpha) {
        Alpha = alpha;
        alphaAssertsDominance(alpha);
    }

    public void alphaAssertsDominance(Wolf alpha) {
        if (NPCs.contains(alpha)) NPCs.remove(alpha);
        Alpha.setAlpha(alpha);

        for (Wolf npc : NPCs) {
            npc.setAlpha(alpha);
        }
    }

    public void wolfDenCreated(World world, WolfDen wolfDen) {
        if (this.wolfDen != null) return;
        this.wolfDen = wolfDen;
        denLocation = world.getLocation(wolfDen);
        for  (Wolf npc : NPCs) {
            npc.setWolfDen(wolfDen);
        }
    }

    public int getRadiusAroundAlpha() {
        return radiusAroundAlpha;
    }

    public void getNearbyWolfsFromGang(Wolf askingWolf, List<Wolf> nearbyWolfs) {
        for (Wolf wolf : NPCs) {
            double distance = wolf.distance(askingWolf.getLocation(), wolf.getLocation());
            if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(wolf);
        }
        double distance = Alpha.distance(askingWolf.getLocation(), Alpha.getLocation());
        if (distance <= 2 && !(distance < 1)) nearbyWolfs.add(Alpha);
    }

}
