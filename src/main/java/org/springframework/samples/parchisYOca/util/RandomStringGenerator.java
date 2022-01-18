package org.springframework.samples.parchisYOca.util;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class RandomStringGenerator {

    public static String getRandomString(Integer length) {
    	log.info("Generating new random string of length {}", length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        String sol = randomString.toString();
        log.debug("New string generated is '{}'", sol);
        return sol;
    }
}
