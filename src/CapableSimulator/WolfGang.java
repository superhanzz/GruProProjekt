package CapableSimulator;

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
                npc.followAlpha(world, location);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void wolfDied(Wolf wolf) {
        if (Alpha == wolf) NPCs.getFirst().promoteToAlpha();
        else NPCs.remove(wolf);
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

}
