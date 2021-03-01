package Map;

import MapElement.Animal;
import MapElement.Genotype;
import MapElement.Grass;
import javafx.util.Pair;
import Maths.Vector2d;

import java.util.*;

import static java.lang.Math.round;

public class WorldMap implements MapAnimalsActions,MapAnimalsStatistics{
    public final int width;
    public final int height;
    public final Vector2d jungle_left_down_corner;
    public final Vector2d jungle_upper_right_corner;
    public final int startEnergy;
    public final int moveEnergy;
    public final int plantEnergy;
    public int numberAnimals;
    public final HashMap<Genotype, ArrayList<Integer>> genotypeHashMap = new HashMap<>();
    public final HashMap<Vector2d, List<Animal>> animalHashMap = new HashMap<>();
    public final List<Animal> animalList = new ArrayList<>();
    public final HashMap<Vector2d, Grass> grassHashMap = new HashMap<>();
    public final List<Grass> grassList = new ArrayList<>();
    public final List<Vector2d> freeFieldNotInTheJungle = new ArrayList<>();
    public final List<Vector2d> freeFieldInJungle = new ArrayList<>();
    public final HashMap<Vector2d, Boolean> freeFieldMap = new HashMap<>();
    public final HashMap<Vector2d, Boolean> freeFieldInJungleMap = new HashMap<>();
    public int startNumberChildTrackedAnimal;
    public int actualNumberOffspringTracked;
    public Animal actualTrackedAnimal = null;

    public WorldMap(int width, int height, double jungleRatio, int startEnergy, int numberAnimals, int moveEnergy, int plantEnergy) {
        this.width = width;
        this.height = height;
        int jungle_width = (int) (jungleRatio * (width));
        int jungle_height = (int) (jungleRatio * (height));
        int jungle_left = (width - 1) / 2 + -jungle_width / 2 + 1;
        int jungle_right = (width - 1) / 2 + jungle_width / 2;
        int jungle_upper = (height - 1) / 2 + jungle_height / 2;
        int jungle_down = (height - 1) / 2 - jungle_height / 2 + 1;
        this.jungle_left_down_corner = new Vector2d(jungle_left, jungle_down);
        this.jungle_upper_right_corner = new Vector2d(jungle_right, jungle_upper);
        this.startEnergy = startEnergy;
        this.numberAnimals = numberAnimals;
        this.initFreeFieldNotInJungle();
        this.initFreeFieldInJungle();
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.actualNumberOffspringTracked =0;
    }

    public boolean inJungle(Vector2d vector) {
        return (vector.follows(this.jungle_left_down_corner) && vector.precedes(this.jungle_upper_right_corner));
    }

    private void initFreeFieldInJungle() {
        for (int i = this.jungle_left_down_corner.x; i <= this.jungle_upper_right_corner.x; i++) {
            for (int j = this.jungle_left_down_corner.y; j <= this.jungle_upper_right_corner.y; j++) {
                Vector2d vector = new Vector2d(i, j);
                this.freeFieldInJungle.add(vector);
                this.freeFieldInJungleMap.put(vector, true);
            }
        }
    }

    void initFreeFieldNotInJungle() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                Vector2d vector = new Vector2d(i, j);
                if (!this.inJungle(vector)) {
                    this.freeFieldNotInTheJungle.add(vector);
                    this.freeFieldMap.put(vector, true);
                }
            }
        }
    }
    @Override
    public List<Animal> findTheStrongest(List<Animal> animalsOnField) {

        int maxEnergy = -moveEnergy;
        for (Animal animal : animalsOnField) {
            if (animal.getEnergy() > maxEnergy) maxEnergy = animal.getEnergy();
        }
        List<Animal> strongestAnimals = new ArrayList<>();
        for (Animal animal : animalsOnField) {
            if (animal.getEnergy() == maxEnergy) strongestAnimals.add(animal);
        }

        return strongestAnimals;
    }
    @Override
    public boolean isOccupiedByAnimal(Vector2d position) {
        return animalHashMap.containsKey(position);
    }
    @Override
    public void place(Animal animal) {
        Vector2d vector = animal.getPosition();
        animalList.add(animal);
        if (this.isOccupiedByAnimal(animal.getPosition())) {
            animalHashMap.get(vector).add(animal);
        } else {
            List<Animal> list = new ArrayList<>();
            list.add(animal);
            animalHashMap.put(vector, list);
        }
        if (this.inJungle(animal.getPosition())) {
            freeFieldInJungle.remove(animal.getPosition());
            freeFieldInJungleMap.remove(animal.getPosition());
        } else {
            freeFieldNotInTheJungle.remove(animal.getPosition());
            freeFieldMap.remove(animal.getPosition());
        }
        animal.genotype.addGenotypeToMap();
    }

    @Override
    public void run() {
        for (Animal animal : animalList) {
            animal.move();
        }
    }
    @Override
    public List<Integer> feedAll() {
        List<Integer> indexGrassList = new ArrayList<>();
        int numberEatenGrasses = 0;
        int n = grassList.size();
        for (int i = 0; i < n; i++) {
            Grass grass = grassList.get(i - numberEatenGrasses);
            Vector2d actualPosition = grass.getPosition();
            if (animalHashMap.containsKey(actualPosition) && animalHashMap.get(actualPosition).size()!=0 ) {
                indexGrassList.add(i - numberEatenGrasses);
                List<Animal> animalsToFeed = findTheStrongest(animalHashMap.get(actualPosition));
                int basicEnergyValue = plantEnergy / animalsToFeed.size();
                int restEnergyValue = plantEnergy % animalsToFeed.size();
                for (int j = 0; j < animalsToFeed.size(); j++) {
                    if (j == 0) {
                        if (restEnergyValue != 0) {
                            animalsToFeed.get(j).feed(grass, basicEnergyValue + 1);
                            restEnergyValue -= 1;
                        } else animalsToFeed.get(j).feed(grass, basicEnergyValue);
                    } else {
                        if (restEnergyValue != 0) {
                            animalsToFeed.get(j).modifyEnergy(basicEnergyValue + 1);
                            restEnergyValue -= 1;
                        } else animalsToFeed.get(j).modifyEnergy(basicEnergyValue);
                    }
                }
                numberEatenGrasses += 1;
            }
        }
        return indexGrassList;
    }
    @Override
    public List<Animal> reproduceAll(int day) {
        List<List<Animal>> animalsListParent = new ArrayList<>();
        List<Animal> animalsListChild = new ArrayList<>();
        Animal animalParent1;
        Animal animalParent2;

        for (List<Animal> animalList : this.animalHashMap.values()) {
            if (animalList.size() > 1) {
                animalList.sort(Comparator.comparing(Animal::getEnergy));
                animalParent1 = animalList.get(animalList.size() - 1);
                animalParent2 = animalList.get(animalList.size() - 2);
                animalParent1.addChild();
                animalParent2.addChild();
                ArrayList<Animal> animalParents = new ArrayList<>();
                animalParents.add(animalParent1);
                animalParents.add(animalParent2);
                animalsListParent.add(animalParents);
            }
        }

        for (List<Animal> animals : animalsListParent) {
            Animal animalParenti1 = animals.get(0);
            Animal animalParenti2 = animals.get(1);
            if (animalParenti1.getEnergy() >= (int) round(0.5 * startEnergy) && animalParenti2.getEnergy() >= (int) round(0.5 * startEnergy)) {
                Animal animalChild = new Animal(this, animalParenti1, animalParenti2, day);
                if(animalParenti1.trackedFamily || animalParenti2.trackedFamily) animalChild.trackedFamily=true;
                if(animalChild.trackedFamily) this.actualNumberOffspringTracked +=1;
                animalsListChild.add(animalChild);
                animalParenti1.modifyEnergy(-(int) round(0.25 * animalParenti1.getEnergy()));
                animalParenti2.modifyEnergy(-(int) round(0.25 * animalParenti2.getEnergy()));
            }
        }
        this.numberAnimals += animalsListChild.size();
        return animalsListChild;
    }
    @Override
    public Pair<List<Integer>, Double> findAllDeadAnimals(int day) {
        List<Integer> deadAnimals = new ArrayList<>();
        int sumLifeTime = 0;
        int numberDeadAnimals = 0;
        int earlyNumberAnimals = numberAnimals;
        for (int i = 0; i < earlyNumberAnimals; i++) {
            Animal animal = animalList.get(i - numberDeadAnimals);
            if (animal.getEnergy() < 1) {

                sumLifeTime += (day - animal.birthDay);
                animal.genotype.deleteGenotypeToMap();
                deadAnimals.add(i - numberDeadAnimals);
                animalList.get(i - numberDeadAnimals).dead();
                numberDeadAnimals += 1;
            }
        }
        double averageLife = (double) Math.round(((double)(sumLifeTime)) / earlyNumberAnimals * 100) / (100);
        Pair<List<Integer>,Double> averageLifeAndDeadAnimals = new Pair<>(deadAnimals, averageLife);
        return averageLifeAndDeadAnimals;

    }
    @Override
    public int getNumberAnimals(){
        return numberAnimals;
    }
    @Override
    public int getNumberGrasses(){
        return animalList.size();
    }
    @Override
    public Genotype dominationGenotype() {
        int highestNumber = 0;
        Genotype genotypeDom = null;
        Iterator iterator = this.genotypeHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            int numberGenotype = ((ArrayList<Integer>) pair.getValue()).size();
            if (numberGenotype > highestNumber) {
                highestNumber = numberGenotype;
                genotypeDom = (Genotype) pair.getKey();
            }
        }
        return genotypeDom;
    }
    public List<Integer> findAllDominationGenotype() {
        List<Integer> indexAnimalsWithDominationGenotype = new ArrayList<>();;
        Genotype genotype = this.dominationGenotype();
        for(int i =0;i<animalList.size();i++ ){
            if(genotype.equals(animalList.get(i).genotype)) indexAnimalsWithDominationGenotype.add(i);
        }
        return indexAnimalsWithDominationGenotype;
    }

    @Override
    public double averageEnergy() {
        int sumEnergy = 0;
        for (Animal animal : this.animalList) {
            sumEnergy += animal.getEnergy();
        }
        if (animalList.size() != 0) return  Math.round(100* sumEnergy / animalList.size() )/ 100;
        else return 0;
    }
    @Override
    public double averageNumberChild() {
        int sumNumberChild = 0;
        for (Animal animal : animalList) sumNumberChild += animal.childNumber;
        if (animalList.size() != 0) return  Math.round(sumNumberChild* 100 / animalList.size() ) / 100;
        else return 0;
    }

    public int numberTrackedOffspring(){
        return this.actualNumberOffspringTracked;
    }
}