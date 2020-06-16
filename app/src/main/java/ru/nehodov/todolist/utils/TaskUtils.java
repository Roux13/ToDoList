package ru.nehodov.todolist.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskUtils {

    private static final String TIME_STAMP_PATTERN = "yyyyMMdd_HHmmss";

    public static File createImageFile(File directory) {
        String timeStamp = new SimpleDateFormat(TIME_STAMP_PATTERN, Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        return new File(directory + "/" + imageFileName + ".jpg");
    }

    public static Bitmap getPhotoBitmap(ContentResolver contentResolver, String path) {
        Bitmap image = null;
        File file = new File(path);
        try {
            Uri photoUri = Uri.fromFile(file);
            image = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
