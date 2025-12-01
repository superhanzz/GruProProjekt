package CapableSimulator;

import itumulator.world.Location;

import java.util.ArrayList;
import java.util.List;

public class WolfGang {

    Wolf Alpha;
    List<Wolf> NPCs;


    WolfGang(Wolf Alpha) {
        this.Alpha = Alpha;
        NPCs = new ArrayList<Wolf>();
    }

    public void addWolfToGang(Wolf Wolf) {
        NPCs.add(Wolf);
    }

    public void alphaMoved(Location location) {
        for (Wolf npc : NPCs) {
            npc.followAlpha(location);
        }
    }

}
