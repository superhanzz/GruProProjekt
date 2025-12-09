package CapableSimulator.Actors;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Predator extends Animals {

    protected Predator(String actorType) {
        super(actorType);
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
        carcass = animal.makeCarcass(world);
        if(carcass != null){
            eat(carcass);
        }

    }

    protected abstract boolean isAnimalEnemy(Predator possibleEnemy);
}
