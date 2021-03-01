package MapElement;

import Maths.MapDirection;
import Maths.Vector2d;
import Map.WorldMap;

import java.util.*;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class Animal {
    final WorldMap map;
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    public final Genotype genotype;
    public final int birthDay;
    public int childNumber;
    public boolean trackedFamily;

    //Zdaje sobię sprawde z DRY ale zalezalo mi by pewne pola zachowac finalne, min genotyp
    public Animal(WorldMap map, Animal animalParent1, Animal animalParent2, int birthDay) {
        this.map = map;
        this.genotype = new Genotype(this, animalParent1, animalParent2);
        this.orientation = chooseOrientation();
        this.position = reproducePosition(animalParent1);
        this.energy = (int) (round(animalParent1.energy * 0.25) + round(animalParent2.energy * 0.25));
        this.map.place(this);
        this.birthDay = birthDay;
        this.childNumber = 0;
        this.trackedFamily = animalParent1.trackedFamily || animalParent2.trackedFamily;
    }

    public Animal(WorldMap map) {
        this.map = map;
        this.genotype = new Genotype(this);
        this.orientation = chooseOrientation();
        this.position = initPosition();
        this.energy = map.startEnergy;
        this.map.place(this);
        this.genotype.addGenotypeToMap();
        this.birthDay = 0;
        this.childNumber = 0;
        this.trackedFamily = false;
    }

    private Vector2d initPosition() {
        while (true) {
            int x = (int) round(random() * (map.width - 1));
            int y = (int) round(random() * (map.height - 1));
            Vector2d vector = new Vector2d(x, y);
            if (!map.isOccupiedByAnimal(vector)) {
                return vector;
            }
        }
    }

    private Vector2d reproducePosition(Animal animalParent1) {
        List<Vector2d> fildToReproduce = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Vector2d vector = new Vector2d(animalParent1.position.x + i, animalParent1.position.y + j);
                if (vector.x == -1) vector = vector.add(new Vector2d(map.width, 0));
                if (vector.y == -1) vector = vector.add(new Vector2d(0, map.height));
                if (vector.x == map.width) vector = vector.subtract(new Vector2d(map.width, 0));
                if (vector.y == map.height) vector = vector.subtract(new Vector2d(0, map.height));
                if (!map.isOccupiedByAnimal(vector) && !map.grassHashMap.containsKey(vector)) fildToReproduce.add(vector);
            }
        }
        if (fildToReproduce.size() != 0) {
            int x = (int) round(random() * (fildToReproduce.size() - 1));
            return fildToReproduce.get(x);
        } else {
            while (true) {
                int x = (int) round(random() * 2) - 1;
                int y = (int) round(random() * 2) - 1;
                if (x != 0 || y != 0) {

                    Vector2d vector = new Vector2d(animalParent1.position.x + x, animalParent1.position.y + y);
                    if (vector.x == -1) vector = vector.add(new Vector2d(map.width, 0));
                    if (vector.y == -1) vector = vector.add(new Vector2d(0, map.height));
                    if (vector.x == map.width) vector = vector.subtract(new Vector2d(map.width, 0));
                    if (vector.y == map.height) vector = vector.subtract(new Vector2d(0, map.height));
                    if (!map.isOccupiedByAnimal(vector) && !map.grassHashMap.containsKey(vector)) fildToReproduce.add(vector);
                    return vector;
                }
            }
        }

    }

    public Vector2d getPosition() {
        return position;
    }

    private MapDirection chooseOrientation() {
        int randIndex = (int) round(random() * 31);
        int chooseIndex = this.genotype.value[randIndex];
        return MapDirection.values()[chooseIndex];
    }

    public int getEnergy() {
        return this.energy;
    }

    public void modifyEnergy(int value) {
        this.energy += value;
    }

    public void dead() {
        if (map.animalHashMap.get(this.position).size() == 1){
            map.animalHashMap.remove(position);
        }
        else map.animalHashMap.get(position).remove(this);
        map.animalList.remove(this);
        if (map.inJungle(position)) {
            if (!map.animalHashMap.containsKey(position) && !map.grassHashMap.containsKey(position)) {
                map.freeFieldInJungleMap.put(position, true);
                map.freeFieldInJungle.add(position);
            }
        } else {
            if (!map.animalHashMap.containsKey(position) && !map.grassHashMap.containsKey(position)) {
                map.freeFieldMap.put(position, true);
                map.freeFieldNotInTheJungle.add(position);
            }
        }
        map.numberAnimals -= 1;
    }

    public void move() {
        Vector2d oldPosition = this.position;
        this.orientation = this.chooseOrientation();
        this.position = this.position.add(this.orientation.toUnitVector());
        this.modifyEnergy((-1) * map.moveEnergy);
        if (this.position.x == -1) this.position = this.position.add(new Vector2d(map.width, 0));
        if (this.position.y == -1) this.position = this.position.add(new Vector2d(0, map.height));
        if (this.position.x == map.width) this.position = this.position.subtract(new Vector2d(map.width, 0));
        if (this.position.y == map.height) this.position = this.position.subtract(new Vector2d(0, map.height));

        if (map.isOccupiedByAnimal(this.position)) map.animalHashMap.get(this.position).add(this);
        else {
            List<Animal> list = new ArrayList<>();
            list.add(this);
            map.animalHashMap.put(this.position, list);
        }
        if (map.animalHashMap.get(oldPosition).size() == 1) map.animalHashMap.remove(oldPosition);
        else map.animalHashMap.get(oldPosition).remove(this);
        if (map.inJungle(oldPosition)) {
            if (!map.animalHashMap.containsKey(oldPosition) && !map.grassHashMap.containsKey(oldPosition)) {
                map.freeFieldInJungleMap.put(oldPosition, true);
                map.freeFieldInJungle.add(oldPosition);
            }
        } else {
            if (!map.animalHashMap.containsKey(oldPosition) && !map.grassHashMap.containsKey(oldPosition)) {
                map.freeFieldMap.put(oldPosition, true);
                map.freeFieldNotInTheJungle.add(oldPosition);
            }
        }
        if (map.inJungle(this.getPosition())) {
            map.freeFieldInJungle.remove(this.getPosition());
            map.freeFieldInJungleMap.remove(this.getPosition());
        } else {
            map.freeFieldNotInTheJungle.remove(this.getPosition());
            map.freeFieldMap.remove(this.getPosition());
        }
    }

    public void feed(Grass grass, int energyValue) {
        Vector2d vector = grass.getPosition();
        this.modifyEnergy(energyValue);
        map.grassHashMap.remove(vector);
        map.grassList.remove(grass);
    }

    public void addChild() {
        this.childNumber += 1;
    }



    public void newTrackedFamily(Animal animal) {
        for (int i = 0; i < map.animalList.size(); i++) {
             if(map.animalList.get(i) == animal){
                 this.trackedFamily =true;
                 this.map.actualTrackedAnimal = animal;
             }
             else this.trackedFamily=false;
        }
        map.startNumberChildTrackedAnimal = animal.childNumber;
        map.actualTrackedAnimal = animal;
    }

    public int NumberTrackedChild() {
        return this.childNumber - map.startNumberChildTrackedAnimal;
    }


}



