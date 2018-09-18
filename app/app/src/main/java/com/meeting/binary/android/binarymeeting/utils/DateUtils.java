package com.meeting.binary.android.binarymeeting.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    public static String getReadableModifyDate(long noteDate){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy -h:mm a", Locale.getDefault());
        simpleDateFormat.setTimeZone(calendar.getTimeZone());
        calendar.setTimeInMillis(noteDate);
        Date modifiedDate = calendar.getTime();
        return simpleDateFormat.format(modifiedDate);
    }
}
