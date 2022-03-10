package com.company;

public class Scissors extends Attack {
    public void handleRock(Attack a){
        System.out.println("Scissors v Rock; Scissors loses");
    }
    public void handlePaper(Attack a){
        System.out.println("Scissors v paper; Scissors wins");
    }
    public void handleScissors(Attack a){
        System.out.println("Scissors v Scciessor; draw");
    }
    public void handleAttack(Attack a){
        a.handleScissors(this);
    }
}
