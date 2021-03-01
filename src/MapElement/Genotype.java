package MapElement;

import java.util.ArrayList;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class Genotype {
    private Animal animal;
    public final int[] value;
    //DRY czy not final?
    Genotype (Animal animal){
        this.animal = animal;
        this.value = initGenotype();
    }
    Genotype (Animal animal, Animal animalParent1, Animal animalParent2){
        this.animal = animal;
        this.value =initReproduceGenotype(animalParent1,animalParent2);
    }
    private int[] initGenotype(){
        int[] genotype = new int[32];
        for(int i =0; i <32;i++){
            genotype[i]= (int) round(random() * 7);
        }
        return this.improveGenotype(genotype);
    }
    private  int[] initReproduceGenotype(Animal animalParent1, Animal animalParent2){
        int index1 = (int) round(random() * 32);
        int index2;
        do {
            index2 = (int) round(random() * 32);
        } while (index1 == index2);
        if(index1>index2) {
            int a = index1;
            index1= index2;
            index2 = a;
        }
        int[] newGenotype = new int[32];
        for ( int i = 0; i < index1;i++) newGenotype[i]= animalParent1.genotype.value[i];
        for ( int i = index1; i < index2;i++) newGenotype[i]= animalParent2.genotype.value[i];
        for (int i = index2; i < 32;i++) newGenotype[i]= animalParent1.genotype.value[i];
        return this.improveGenotype(newGenotype);
    }

    private int[] improveGenotype(int[] genotype) {
        int[] genCounters = new int[8];
        int[] newGenotype = new int[32];
        for( int i = 0; i <32 ; i++){
            genCounters[genotype[i]]+=1;
        }
        for( int i =0; i < 8; i++) {
            if (genCounters[i] == 0) {
                while (true) {
                    int randomIndex = (int) round(random() * 7);
                    if (genCounters[randomIndex] > 1) {
                        genCounters[randomIndex] -= 1;
                        genCounters[i] += 1;
                        break;
                    }
                }
            }
        }
        int genoptypeIndex = 0;
        for ( int i = 0; i < 8 ; i++){
            while(genCounters[i]!=0){
                newGenotype[genoptypeIndex]=i;
                genoptypeIndex++;
                genCounters[i]-=1;
            }
        }
        return newGenotype;
    }
    public void addGenotypeToMap() {
        ArrayList<Integer> listForGenotype = new ArrayList<>();
        listForGenotype.add(1);
        if(this.animal.map.genotypeHashMap.containsKey(this)) this.animal.map.genotypeHashMap.get(this).add(1);
        else this.animal.map.genotypeHashMap.put(this,listForGenotype);
    }
    public void deleteGenotypeToMap() {
        if(this.animal.map.genotypeHashMap.get(this).size() == 1) this.animal.map.genotypeHashMap.remove(this);
        else this.animal.map.genotypeHashMap.get(this).remove(0);
    }
    public String genotypeToString( ){
        String genotypeString = "";
        for (int j : this.value) genotypeString += j;
        return genotypeString;
    }
}
