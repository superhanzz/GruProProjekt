package CapableSimulator.Actors.Fungis;

import CapableSimulator.CapableWorld;

public interface Fungi {

    void spreadSpores(CapableWorld world);

    void becomeInfected();

    boolean isInfected();

    FungiSpore getFungiSpore();

}
