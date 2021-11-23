package org.springframework.samples.parchisYOca.util;

import java.util.Random;

public class RandomStringGenerator {

    public static String getRandomString(Integer length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        String sol = randomString.toString();
        return sol;
    }
}
