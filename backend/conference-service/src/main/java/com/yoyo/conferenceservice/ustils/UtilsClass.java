package com.yoyo.conferenceservice.ustils;

import java.util.Random;

public class UtilsClass {

    public static  boolean isStringNotNullOrEmpty(String... data){
        for(String s: data){
            if(s==null ||s.isEmpty()){
                return false;
            }
        }
        return  true;
    }

    public static String generateRandomTitle(Random random, int length) {
        return random.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}
