package ru.nehodov.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "HH:mm dd-MM-yyyy", Locale.US
    );

    public static String format(Date date) {
        return dateFormat.format(date);
    }

}
