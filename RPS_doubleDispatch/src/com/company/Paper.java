package com.company;

public class Paper extends Attack {
    public void handle(Rock a){
        System.out.println("Paper v Rock; paper wins");
    }
    public void handle(Paper a){
        System.out.println("Paper v paper; draw");
    }
    public void handle(Scissors a){
        System.out.println("Paper v Scciessor; paper loses");
    }
    public void handleAttack(Attack a){
        a.handle(this);
    }
}
