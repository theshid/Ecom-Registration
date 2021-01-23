package com.ecomtrading.android.converters;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    @TypeConverter
    public static String fromTimeStamp(Long dateLong) {

        return FORMATTER.format(dateLong);
    }

    @TypeConverter
    public static Long dateToTimeStamp(String date) {
        Long timeStamp = null;
        try {
            timeStamp = FORMATTER.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }
}
