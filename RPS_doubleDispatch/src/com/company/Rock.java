package com.company;

public class Rock extends Attack {
    public void handleRock(Attack a){
        System.out.println("Rock v Rock; draw");
    }
    public void handlePaper(Attack a){
        System.out.println("Rock v paper; rock loses");
    }
    public void handleScissors(Attack a){
        System.out.println("Rock v Scciessor; rock wins");
    }
    public void handleAttack(Attack a){
        a.handleRock(this);
    }
}
