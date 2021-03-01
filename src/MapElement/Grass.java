package MapElement;

import Maths.Vector2d;
import Map.WorldMap;

import static java.lang.Math.round;

public class Grass {
    private final Vector2d location;

    public Grass(WorldMap map, boolean inTheJungle) {
        this.location = this.initGrass(map,inTheJungle);


    }
    private Vector2d initGrass(WorldMap map, boolean inTheJungle) {
        if(inTheJungle){
            int x = (int) round((map.freeFieldInJungle.size()-1)*Math.random());
            Vector2d vector = new Vector2d(map.freeFieldInJungle.get(x).x, map.freeFieldInJungle.get(x).y);
            map.grassHashMap.put(vector,this);
            map.grassList.add(this);
            map.freeFieldInJungle.remove(vector);
            map.freeFieldInJungleMap.remove(vector);
            return vector;
            }
        else{
            int x = (int) round((map.freeFieldNotInTheJungle.size()-1)*Math.random());
            Vector2d vector = new Vector2d(map.freeFieldNotInTheJungle.get(x).x, map.freeFieldNotInTheJungle.get(x).y);
            map.grassHashMap.put(vector,this);
            map.grassList.add(this);
            map.freeFieldNotInTheJungle.remove(vector);
            map.freeFieldMap.remove(vector);
            return vector;
        }
    }
    public Vector2d getPosition() {
        return location;
    }



}
