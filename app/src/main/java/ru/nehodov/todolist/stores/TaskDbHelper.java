package ru.nehodov.todolist.stores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "to_do_list.db";
    private static final int DB_VERSION = 1;

    public TaskDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TaskDbContract.TasksTable.TABLE_NAME + "("
                + TaskDbContract.TasksTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskDbContract.TasksTable.COLUMN_NAME_NAME + " TEXT, "
                + TaskDbContract.TasksTable.COLUMN_NAME_DESC + " TEXT, "
                + TaskDbContract.TasksTable.COLUMN_NAME_CREATED + " TEXT, "
                + TaskDbContract.TasksTable.COLUMN_NAME_DONE + " TEXT DEFAULT NULL, "
                + TaskDbContract.TasksTable.COLUMN_NAME_PHOTO_PATH + " TEXT DEFAULT NULL"
                + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
