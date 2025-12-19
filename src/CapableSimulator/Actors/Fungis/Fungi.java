package CapableSimulator.Actors.Fungis;

import CapableSimulator.Utils.CapableEnums;
import itumulator.world.World;

public interface Fungi {


    void spreadSpores(World world);

    void becomeInfected();

    boolean isInfected();

    boolean isCarrierOfType(CapableEnums.FungiType fungiType);

    FungiSpore getFungiSpore();

}
