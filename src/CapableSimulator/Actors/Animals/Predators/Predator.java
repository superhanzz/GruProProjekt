package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public abstract class Predator extends Animal {

    protected Predator(String actorType, World world, int energy, int age, int MAX_ENERGY) {
        super(actorType, world,  energy, age, MAX_ENERGY);
    }

    protected Predator(String actorType, World world, int energy, int age, int MAX_ENERGY, int MATING_AGE, int MATING_COOLDOWN_DURATION) {
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

    protected boolean tryFight() {
        List<Predator> enemies = new ArrayList<>();

        Map<String, List<Predator>> enemiesMap = new HashMap<>();
        for (String key : WorldActor.getAllPredatorTypes())
            enemiesMap.put(key, new ArrayList<>());

        if (!lookForEnemy(enemies, 3)) return false;

        Predator enemy = null;
        for (Predator possibleEnemy : enemies) {
            List<Predator> list = enemiesMap.get(possibleEnemy.actorType);
            list.add(possibleEnemy);
            enemiesMap.put(possibleEnemy.actorType, list);
        }

        for (String key : enemiesMap.keySet()) {
            enemy = ((Predator) getNearestActor(enemiesMap.get(key)));
            if (enemy != null) break;
        }
        if (enemy == null) return false;
        Location enemyLocation = enemy.getLocation();

        if (PathFinder.distance(getLocation(), enemyLocation) > 1) {
            return moveTowards(enemyLocation);
        }

        attackEnemy(enemy);
        return true;
    }

    protected void attackEnemy(Predator enemyActor) {
        double winChance = getWinChance(enemyActor);
        System.out.println(actorType + " Attacking: " + enemyActor.actorType + ", with a win chance of: " + (winChance * 100.0) + "%");
        if (new Random().nextDouble() < winChance) {
            kill(enemyActor);
        }
        else {
            die();
        }
    }

    protected double getWinChance(Predator enemy) {
        double winChance = 0.0;
        winChance = (getStrengthValue()) / (getStrengthValue() + enemy.getStrengthValue());
        return winChance;
    }

    @Override
    protected void prepareToEat(WorldActor eatableActor) {
        if (eatableActor == null) {
            System.out.println("In prepareToEat(): eatableActor is null");
            return;
        }

        if (PathFinder.distance(getLocation(), world.getLocation(eatableActor)) != 1) {
            Location goTo = PathFinder.getClosestTile(world, getLocation(), eatableActor.getLocation());
            world.move(this, goTo);
        }

        if (eatableActor instanceof Animal animal) {
            kill(animal);
        }
        else eat(eatableActor);
    }

    protected void kill(Animal animal) {
        Carcass carcass = null;
        carcass = animal.die();
        if(carcass != null){
            eat(carcass);
        }

    }

    public abstract double getStrengthValue();


    /* ----- ----- ----- ----- Getters ----- ----- ----- -----*/

    protected abstract boolean isAnimalEnemy(Predator possibleEnemy);

    public String getCombatLookupKey() {
        String key = animalSize.label + "-" + fungiState.label;
        return key;
    }

}
