package com.company;

public class IntBox {
    int X;

    IntBox (int a){this.X = a;}

    IntBox add(int v){
        this.X= this.X + v; // X+=v
        return new IntBox(this.X);
    }
    IntBox subtract(int v){
        this.X= this.X - v;
        return new IntBox(this.X);
    }
    IntBox multiply(int v){
        this.X= this.X * v;
        return new IntBox(this.X);
    }
    @Override public String toString(){
        return "IntBox(" + X + ")" + "@" + Integer.toHexString(hashCode());
    }

    IntBox(IntBox that){ // copy constructor
        this.X = that.X;
    }

}
