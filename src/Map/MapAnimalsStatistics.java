package Map;

import MapElement.Genotype;

public interface MapAnimalsStatistics {
    int getNumberAnimals();
    int getNumberGrasses();
    Genotype dominationGenotype();
    double averageEnergy();
    double averageNumberChild();
}
