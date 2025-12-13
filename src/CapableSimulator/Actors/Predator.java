package CapableSimulator.Actors;

import CapableSimulator.CapableWorld;
import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public abstract class Predator extends Animals {

    protected Predator(String actorType, CapableWorld world, int energy, int age, int MAX_ENERGY) {
        super(actorType, world,  energy, age, MAX_ENERGY);
    }

    protected Predator(String actorType, CapableWorld world, int energy, int age, int MAX_ENERGY, int MATING_AGE, int MATING_COOLDOWN_DURATION) {
        super(actorType, world,  energy, age, MAX_ENERGY,  MATING_AGE, MATING_COOLDOWN_DURATION);
    }

    @Override
    public void act(World world) {
        super.act(world);
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

    protected abstract void attackEnemy(Predator enemy);

    @Override
    protected void prepareToEat(WorldActor eatableActor) {
        if (eatableActor == null) {
            System.out.println("In prepareToEat(): eatableActor is null");
            return;
        }

        if (Math.ceil(distance(getLocation(), world.getLocation(eatableActor))) != 1) {
            Location goTo = getClosestTile(world, world.getLocation(eatableActor));
            world.move(this, goTo);
        }

        if (eatableActor instanceof Animals animal) {
            kill(animal);
        }
        else eat(eatableActor);
    }

    protected void kill(Animals animal) {
        Carcass carcass = null;
        carcass = animal.becomeCarcass();
        if(carcass != null){
            eat(carcass);
        }

    }

    protected abstract boolean isAnimalEnemy(Predator possibleEnemy);
}
