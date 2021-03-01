package Simulation;

import Map.WorldMap;
import MapElement.Animal;
import MapElement.Grass;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class Simulator {
    Simulator (Parameters config){
        width = config.mapWidth;
        height = config.mapHeight;
        double jungleRatio = config.jungleRatio;
        int startEnergy = config.startEnergy;
        numberAnimals = config.animalNumber;
        int moveEnergy = config.moveEnergy;
        int plantEnergy = config.plantEnergy;
        map = new WorldMap(width,height, jungleRatio, startEnergy,numberAnimals, moveEnergy, plantEnergy);
        initalizationMap();
    }

    private final int width ;
    private final int height ;
    private final int numberAnimals ;
    AnimationTimer timer = new MyTimer();
    private int day =0;
    private final WorldMap map;
    private final GridPane root = new GridPane();
    private final List<ImageView>  animals = new ArrayList<>();
    private final List<ImageView> grasses = new ArrayList<>();
    private Label animalsNumber = new Label();
    private Label grassNumber = new Label();
    private Label dominationGenotype = new Label();
    private Label averageEnergy = new Label();
    private Label averageLifetime = new Label();
    private Label averageChildNumber = new Label();
    private Label numberOffspring = new Label();
    private Label numberChild = new Label();
    private Label numberChildText = new Label();
    private Label numberOffspringText = new Label();



    private Button btnAllDom;
    private List<Object> removeList = new ArrayList<>();

    public GridPane getRoot() {
        return root;
    }
    public int blockSize(){
        if(width>height) return 500/width;
        else return 500/height;
    }
    public ImageView createImage(String image){
        Image img = new Image(image);
        ImageView view = new ImageView(img);
        view.setFitWidth(blockSize());
        view.setFitHeight(blockSize());
        return view;
    }

    public Label createStatistic( boolean textIs, int deep, String text ){
        Label label1 = new Label((text));
        label1.setFont(Font.font("Times", FontWeight.EXTRA_BOLD, 15));
        if(textIs) root.add(label1,0,map.height+deep);
        else root.add(label1,300/blockSize()+1,map.height+deep);
        GridPane.setColumnSpan(label1,300/blockSize());
        return label1;
    }

    public String chooseImage(Animal animal){
        if(animal.getEnergy()<=20) return "Images/1.png";
        else if(animal.getEnergy()<=80) return "Images/2.jpeg";
        else if(animal.getEnergy()<=300) return "Images/3.jpeg";
        else if(animal.getEnergy()<=1000) return "Images/4.jpeg";
        else return "5.jpeg";
    }
    public void startAction(){
        timer.start();
        root.getChildren().remove(btnAllDom);
        while(removeList.size()!=0){
            root.getChildren().remove(removeList.get(removeList.size()-1));
            removeList.remove(removeList.size()-1);
        }
    }
    public void stopAction(){
        timer.stop();
        btnAllDom = new Button("VIEW ALL DOMINATING GENOM");
        root.add(btnAllDom,0,map.height+8);
        GridPane.setColumnSpan(btnAllDom,300/blockSize());
        btnAllDom.setOnMousePressed(actionEvent -> allDominationGenotypesView());
        for (ImageView animal : animals){
            animal.setOnMousePressed(actionEvent -> trackChoice(animal));
        }
    }
    public void allDominationGenotypesView(){
        List<Integer> animalsDom = map.findAllDominationGenotype();
                for (int i =0; i<animalsDom.size();i++){
            ImageView view = createImage("Images/7.jpeg");
            root.add(view,map.animalList.get(i).getPosition().x, map.height -1-map.animalList.get(i).getPosition().y);
            removeList.add(view);
        }
    }
    public void trackChoice(ImageView animal){
        Label label = new Label("Animal tracked, genotype: ");
        label.setFont(Font.font("Times", FontWeight.EXTRA_BOLD, 15));
        root.add(label,0,map.height+9);
        removeList.add(label);
        GridPane.setColumnSpan(label,300/blockSize());
        int index =animals.indexOf(animal);
        Animal animalTracked = map.animalList.get(index);
        Label label1 = new Label(animalTracked.genotype.genotypeToString());
        label1.setFont(Font.font("Times", FontWeight.EXTRA_BOLD, 15));
        root.add(label1,300/blockSize()+1,map.height+9);
        removeList.add(label1);
        GridPane.setColumnSpan(label1,300/blockSize());
        Button btnTrack = new Button("TRACK");
        root.add(btnTrack,0,map.height+10);
        removeList.add(btnTrack);
        GridPane.setColumnSpan(btnTrack,300/blockSize());
        btnTrack.setOnMousePressed(actionEvent-> trackAnimal(animalTracked));
    }
    public void trackAnimal(Animal animalTracked){
        animalTracked.newTrackedFamily(animalTracked);
        numberChildText = new Label("Child number:");
        numberChildText.setFont(Font.font("Times", FontWeight.EXTRA_BOLD, 15));
        root.add(numberChildText,0,map.height+11);
        root.setColumnSpan(numberChildText,300/blockSize());
        numberOffspringText = new Label("Offspring number:");
        numberOffspringText.setFont(Font.font("Times", FontWeight.EXTRA_BOLD, 15));
        root.add(numberOffspringText,0,map.height+12);
        GridPane.setColumnSpan(numberOffspringText,300/blockSize());
    }


    private void initalizationMap() {
        root.setVgap(0);
        root.setHgap(0);
        root.setPadding(new Insets(0));

        Button btnS = new Button("START");
        root.add(btnS,0,map.height+1);
        GridPane.setColumnSpan(btnS,300/blockSize());
        Button btnE = new Button("STOP!");
        root.add(btnE,300/blockSize()+1,map.height+1);
        GridPane.setColumnSpan(btnE,300/blockSize());

        //Tworze sawanne
        for (int i=0;i<map.width;i++){
            for( int j=0;j<map.height;j++) {
                root.add(createImage("Images/11.jpeg"), i, map.height-1 -j);
            }
        }
        //Tworze dżungle
        for (int i=map.jungle_left_down_corner.x;i<=map.jungle_upper_right_corner.x;i++){
            for( int j=map.jungle_left_down_corner.y ;j<=map.jungle_upper_right_corner.y;j++) {
                root.add(createImage("Images/jun.jpeg"), i, map.height-1 -j);
            }
        }
        //Tworze zwierzeta pierwotne
        for(int i =0; i<numberAnimals;i++) {
            Animal animal = new Animal(map);
            ImageView view = createImage(chooseImage(animal));
            root.add(view, animal.getPosition().x, map.height-1 -animal.getPosition().y);
            animals.add(view);
        }
        //wypisuję statystyki
        createStatistic(true,2,"Number animals: ");
        animalsNumber = createStatistic(false,2,String.valueOf(map.animalList.size()));
        createStatistic(true,3,"Number grasses: ");
        grassNumber = createStatistic(false,3,String.valueOf(map.grassList.size()));
        createStatistic(true,4,"Average child number: ");
        averageChildNumber=createStatistic(false,4,String.valueOf(map.averageNumberChild()));
        createStatistic(true,5,"Average lifetime dead animals: ");
        averageLifetime=createStatistic(false,5,String.valueOf(0));
        createStatistic(true,6,"Domination genotype:");
        dominationGenotype=createStatistic(false,6,String.valueOf((map.dominationGenotype().genotypeToString())));
        createStatistic(true,7,"Average energy:");
        averageEnergy=createStatistic(false,7,String.valueOf(map.averageEnergy()));
        //reaguje na przcyisk start/stop

        btnS.setOnMousePressed(actionEvent -> this.startAction());
        btnE.setOnMousePressed(actionEvent -> this.stopAction());


    }

    private class MyTimer extends AnimationTimer {
        private long lastUpdate = 0 ;
        @Override
        public void handle(long now) {
            if (now - lastUpdate >= 300_000_000) {
                dayUpdate();
                lastUpdate = now ;
            }
        }

        private void dayUpdate() {
            day+=1;
            if(map.actualTrackedAnimal!=null){
                root.getChildren().remove(numberChild);
                root.getChildren().remove(numberOffspring);
                if(map.actualTrackedAnimal.getEnergy()<1){
                    map.actualTrackedAnimal=null;
                    Label text = new Label("ZWIERZE UMARŁO! EPOKA ŚMIERCI: "+day);
                    root.add(text,0,map.height+13);
                    removeList.add(text);
                    root.setColumnSpan(text,300/blockSize());
                    root.getChildren().remove(numberOffspringText);
                    root.getChildren().remove(numberChildText);
                }
                else{
                    numberOffspring=createStatistic(false,11,String.valueOf(map.actualTrackedAnimal.NumberTrackedChild()));
                    numberChild=createStatistic(false,12,String.valueOf(map.numberTrackedOffspring()));
                }
            }

            Pair<List<Integer>, Double> deadStatistic= map.findAllDeadAnimals(day);
            List<Integer> indexDeletedAnimalsList = deadStatistic.getKey();

            for ( int k = 0; k< indexDeletedAnimalsList.size();k++){
                int index = indexDeletedAnimalsList.get(k);
                ImageView label0 = animals.get(index);
                root.getChildren().remove(label0);
                animals.remove(label0);
            }
            if(map.animalList.size()==0) timer.stop();

            map.run();
            for (int k = 0; k < map.animalList.size(); k++){
                ImageView animalView = animals.get(k);
                int xPos = map.animalList.get(k).getPosition().x;
                int yPos = map.height - 1 - map.animalList.get(k).getPosition().y;
                GridPane.setConstraints(animalView, xPos, yPos);
                animals.get(k).toFront();
            }

            List<Integer> indexDeletedGrassList = map.feedAll();
            for ( int k = 0; k< indexDeletedGrassList.size();k++){
                int index = indexDeletedGrassList.get(k);
                ImageView label0 = grasses.get(index);
                root.getChildren().remove(label0);
                grasses.remove(label0);
            }

            List<Animal> animalsChild = map.reproduceAll(day);
            for(int k = map.animalList.size()-animalsChild.size(); k<map.animalList.size();k++){
                Animal animalChild = map.animalList.get(k);
                ImageView view = createImage(chooseImage(animalChild));
                root.add(view, animalChild.getPosition().x, map.height-1 -animalChild.getPosition().y);
                animals.add(view);
            }

            if(map.freeFieldInJungle.size() !=0) {
                Grass grass = new Grass(map, true);
                ImageView view = createImage("Images/Unknown-1.jpeg");
                root.add(view, grass.getPosition().x, map.height - 1 - grass.getPosition().y);
                grasses.add(view);
            }
            if(map.freeFieldNotInTheJungle.size() !=0) {
                Grass grass1 = new Grass(map, false);
                ImageView view = createImage("Images/Unknown-1.jpeg");
                root.add(view, grass1.getPosition().x, map.height - 1 - grass1.getPosition().y);
                grasses.add(view);

            }
            root.getChildren().remove(animalsNumber);
            root.getChildren().remove(grassNumber);
            root.getChildren().remove(averageChildNumber);
            root.getChildren().remove(averageLifetime);
            root.getChildren().remove(dominationGenotype);
            root.getChildren().remove(averageEnergy);
            animalsNumber=createStatistic(false,2,String.valueOf(map.animalList.size()));
            grassNumber=createStatistic(false,3,String.valueOf(map.grassList.size()));
            averageChildNumber=createStatistic(false,4,String.valueOf(map.averageNumberChild()));
            averageLifetime=createStatistic(false,5,String.valueOf(deadStatistic.getValue()));
            dominationGenotype=createStatistic(false,6,String.valueOf((map.dominationGenotype().genotypeToString())));
            averageEnergy=createStatistic(false,7,String.valueOf(map.averageEnergy()));

        }
    }


}
