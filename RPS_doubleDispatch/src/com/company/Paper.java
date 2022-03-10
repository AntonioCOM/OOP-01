package com.company;

public class Paper extends Attack {
    public void handleRock(Attack a){
        System.out.println("Paper v Rock; paper wins");
    }
    public void handlePaper(Attack a){
        System.out.println("Paper v paper; draw");
    }
    public void handleScissors(Attack a){
        System.out.println("Paper v Scciessor; paper loses");
    }
    public void handleAttack(Attack a){
        a.handlePaper(this);
    }
}
