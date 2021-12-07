package org.springframework.samples.parchisYOca.util;

import java.util.Random;

public class Dice {

    public Integer rollDice() {
        Integer rnd = new Random().ints(1,1,6).findFirst().getAsInt();
        return rnd;
    }
}
