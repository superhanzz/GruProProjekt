package CapableSimulator.Actors.Fungis;

import CapableSimulator.CapableWorld;
import CapableSimulator.Utils.CapableEnums;

public interface Fungi {

    void spreadSpores(CapableWorld world);

    void becomeInfected();

    boolean isInfected();

    boolean isCarrierOfType(CapableEnums.FungiType fungiType);

    FungiSpore getFungiSpore();

}
