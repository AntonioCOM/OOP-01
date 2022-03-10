package com.company;

public class Rock extends Attack {
    public void handle(Rock r){
        System.out.println("Rock v Rock; draw");
    }
    public void handle(Paper p){
        System.out.println("Rock v paper; rock loses");
    }
    public void handle(Scissors s){
        System.out.println("Rock v Scciessor; rock wins");
    }
    public void handleAttack(Attack a){
        a.handle(this);
    }
}
