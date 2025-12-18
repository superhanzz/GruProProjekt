package CapableSimulator.Actors.Animals.Predators;

import CapableSimulator.Actors.Animals.Animal;
import CapableSimulator.Actors.Carcass;
import CapableSimulator.Actors.WorldActor;

import CapableSimulator.Utils.CapableEnums;
import CapableSimulator.Utils.PathFinder;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public abstract class Predator extends Animal {

    /** Default constructor.
     * @param actorType The actor type.
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal
     * @param age The animals starting age.
     * @param MAX_ENERGY The maximum amount of energy the animal can have.
     */
    protected Predator(String actorType, World world, int energy, int age, int MAX_ENERGY) {
        super(actorType, world, energy, age, MAX_ENERGY);
    }

    /** Constructor for testing.
     * @param actorType The actor type.
     * @param world The world wherein the actor exists.
     * @param energy The starting energy of the animal
     * @param age The animals starting age.
     * @param MAX_ENERGY The maximum amount of energy the animal can have.
     * @param MATING_AGE The required age for mating.
     * @param MATING_COOLDOWN_DURATION The required time (simulation steps) before the animal can reproduce again.
     */
    protected Predator(String actorType, World world, int energy, int age, int MAX_ENERGY, int MATING_AGE, int MATING_COOLDOWN_DURATION) {
        super(actorType, world, energy, age, MAX_ENERGY, MATING_AGE, MATING_COOLDOWN_DURATION);
    }

    /* ----- ----- ----- ----- Behavior ----- ----- ----- -----*/

    @Override
    public void act(World world) {
        super.act(world);
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

    /* ----- ----- ----- ----- Fighting ----- ----- ----- -----*/

    /**
     * @return Returns true if an enemy was found, and a fight occurred otherwise, returns false.
     */
    protected boolean tryFight() {
        List<Predator> enemies = new ArrayList<>();

        Map<String, List<Predator>> enemiesMap = new HashMap<>();
        for (String key : WorldActor.getAllPredatorTypes())
            enemiesMap.put(key, new ArrayList<>());

        if (!lookForEnemy(enemies, 3)) return false;

        Predator enemy = null;
        for (Predator possibleEnemy : enemies) {
            List<Predator> list = enemiesMap.get(possibleEnemy.getActorType());
            list.add(possibleEnemy);
            enemiesMap.put(possibleEnemy.getActorType(), list);
        }

        for (String key : enemiesMap.keySet()) {
            enemy = ((Predator) getNearestActor(enemiesMap.get(key)));
            if (enemy != null) break;
        }
        if (enemy == null) return false;
        Location enemyLocation = enemy.getLocation();

        if (moveNextToTarget(enemyLocation)) {
            attackEnemy(enemy);
            return true;
        }
        else
            return false;

    }

    /**
     * @param enemies The list wherein all the found enemies are added.
     * @param radius The radius of the area wherein to search for enemies.
     * @return Returns true if any enemies were found, if not returns false.
     */
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

    /**
     * @param enemyActor Reference to the actor that is being fought with.
     * @throws NullPointerException Throws exception if enemy actor is null.
     */
    protected void attackEnemy(Predator enemyActor) {
        if (enemyActor == null)
            throw new NullPointerException("enemyActor is null");

        double winChance = getWinChance(enemyActor);
        System.out.println(getActorType() + " Attacking: " + enemyActor.getActorType() + ", with a win chance of: " + (winChance * 100.0) + "%");
        if (new Random().nextDouble() < winChance) {
            kill(enemyActor);
        }
        else {
            die();
        }
    }

    /**
     * @param animal Reference to the animal that is to be killed.
     * @throws NullPointerException Throws exception if animal is null.
     */
    protected void kill(Animal animal) {
        if (animal == null)
            throw new NullPointerException("animal is null");

        Carcass carcass = null;
        carcass = animal.die();
        if(carcass != null){
            eat(carcass);
        }

    }

    /* ----- ----- ----- ----- Getters ----- ----- ----- -----*/

    /**
     * @param enemy Reference to the enemy actor.
     * @return Returns the probability of winning.
     * @throws NullPointerException Throws exception if enemy is null.
     */
    protected double getWinChance(Predator enemy) {
        if (enemy == null)
            throw new NullPointerException("enemy is null");

        double winChance = 0.0;
        winChance = (getStrengthValue()) / (getStrengthValue() + enemy.getStrengthValue());
        return winChance;
    }

    /**
     * @return Returns the strength value of the actor.
     */
    public abstract double getStrengthValue();

    /** Evaluates if a possible enemy actor is actually an enemy.
     * @param possibleEnemy The animal to be evaluated.
     * @return Returns true if the animal is an enemy, if not returns false.
     * @throws NullPointerException Throws exception if possible enemy is null.
     */
    protected abstract boolean isAnimalEnemy(Predator possibleEnemy);
}
