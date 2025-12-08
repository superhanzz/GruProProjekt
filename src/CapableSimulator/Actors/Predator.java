package CapableSimulator.Actors;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Predator extends Animals {

    protected Predator(String actorType) {
        super(actorType);
    }

    protected boolean lookForEnemy(List<Predator> enemies, int radius) {
        List<Predator> possibleEnemies = new ArrayList<>();
        Set<Location> neighbors = world.getSurroundingTiles(getLocation(), radius);

        for (Location l : neighbors) {
            Object o = world.getTile(l);
            if (o instanceof Predator predator) {
                possibleEnemies.add(predator);
            }
        }

        if (possibleEnemies.isEmpty()) return false;

        for (Predator predator : possibleEnemies) {
            if (isAnimalEnemy(predator)) enemies.add(predator);
        }

        return !enemies.isEmpty();
    }

    protected abstract boolean isAnimalEnemy(Predator possibleEnemy);
}
