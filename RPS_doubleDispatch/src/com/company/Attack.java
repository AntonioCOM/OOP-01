package com.company;

public abstract class Attack {
    public abstract void handleAttack(Attack a); //entry point
    public abstract void handle(Rock r);    ///public abstract void handleRock(Attack a);
    public abstract void handle(Paper p);    ///public abstract void handlePaper(Attack a);
    public abstract void handle(Scissors s);    ///public abstract void handleScissors(Attack a);

}
