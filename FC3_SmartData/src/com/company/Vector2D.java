
package com.company;

import java.lang.Math;

public class Vector2D {
    private double x_1,x_2;
    double X;

    Vector2D (double x, double y){this.x_1 =x; this.x_2 = y;}

    double distance(Vector2D v){
        double d = Math.sqrt(Math.pow(this.x_2 - this.x_1, 2) + Math.pow(v.x_2 - v.x_1, 2));
        return d;
    }

    Vector2D add(Vector2D v){
        double x,y;
        x = this.x_1 + v.x_1;
        y = this.x_2 + v.x_2;
        return new Vector2D(x,y);
    }

    Vector2D scale(double f){
        double x,y;
        x = this.x_1*f;
        y = this.x_2*f;
        return new Vector2D(x,y);
    }

    @Override public String toString(){
        return "Vector2D(" + this.x_1 + ", " + this.x_2 + ")" + "@" + Integer.toHexString(hashCode());
    }
}
