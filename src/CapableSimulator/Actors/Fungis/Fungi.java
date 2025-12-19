package CapableSimulator.Actors.Fungis;

import CapableSimulator.Utils.CapableEnums;
import itumulator.world.World;

public interface Fungi {

    /** Initates the spreading of spores */
    void spreadSpores(World world);

    /** Handles when an actor has become infected by fungi */
    void becomeInfected();

    /* Checks if the actor is infected by fungi */
    boolean isInfected();

    /** Checks if the actor can carry the specified fungi type */
    boolean isCarrierOfType(CapableEnums.FungiType fungiType);

    /** Retrieves a reference to the actors fungi spore */
    FungiSpore getFungiSpore();

}
