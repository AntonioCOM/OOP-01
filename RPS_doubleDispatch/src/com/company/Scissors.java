package com.company;

public class Scissors extends Attack {
    public void handle(Rock a){
        System.out.println("Scissors v Rock; Scissors loses");
    }
    public void handle(Paper a){
        System.out.println("Scissors v paper; Scissors wins");
    }
    public void handle(Scissors a){
        System.out.println("Scissors v Scciessor; draw");
    }
    public void handleAttack(Attack a){
        a.handle(this);
    }
}
