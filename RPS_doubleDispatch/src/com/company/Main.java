package com.company;

public class Main {

    public static void main(String[] args) {
        Attack a1 = new Rock();
        Attack a2 = new Paper();
        Attack a3 = new Scissors();

        a2.handleAttack(a3);
    }
}
