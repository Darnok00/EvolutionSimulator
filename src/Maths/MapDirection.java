package Maths;

import Maths.Vector2d;

public enum MapDirection {
    zero,one,two,three, four, five,six,seven;

    public Vector2d toUnitVector(){
        switch(this){
            case zero:
                return new Vector2d(0,1);
            case one:
                return new Vector2d(1,1);
            case two:
                return new Vector2d(1,0);
            case three:
                return new Vector2d(1,-1);
            case four:
                return new Vector2d(0,-1);
            case five:
                return new Vector2d(-1,-1);
            case six:
                return new Vector2d(-1,0);
            case seven:
                return new Vector2d(-1,1);
            default:
                return null;
        }
    }
}