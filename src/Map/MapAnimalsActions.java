package Map;

import MapElement.Animal;
import javafx.util.Pair;
import Maths.Vector2d;

import java.util.List;

public interface MapAnimalsActions {
    boolean isOccupiedByAnimal(Vector2d vector);
    void place (Animal animal);
    void run();
    List<Animal> findTheStrongest(List<Animal> animalsOnField);
    List<Integer> feedAll();
    List<Animal> reproduceAll(int day);
    Pair<List<Integer>, Double> findAllDeadAnimals(int day);


}
