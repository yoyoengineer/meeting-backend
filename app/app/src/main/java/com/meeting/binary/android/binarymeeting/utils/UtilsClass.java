package com.meeting.binary.android.binarymeeting.utils;

public class UtilsClass {

    public static boolean isStringNotNullOrEmpty(String eventId, String question){
        if (eventId != null && !eventId.isEmpty() && question != null && !question.isEmpty())
            return true;
        else
            return false;
    }
}
