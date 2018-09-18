package com.binary.profileservice.ustils;

public class UtilsClass {

    public static  boolean isStringNotNullOrEmpty(String... data){
        for(String s: data){
            if(s==null ||s.isEmpty()){
                return false;
            }
        }
        return  true;
    }
}
