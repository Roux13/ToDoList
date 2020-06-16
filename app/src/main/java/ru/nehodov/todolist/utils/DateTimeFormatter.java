package ru.nehodov.todolist.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "HH:mm dd-MM-yyyy", Locale.US
    );

    public static String format(Date date) {
        return DATE_FORMAT.format(date);
    }

}
