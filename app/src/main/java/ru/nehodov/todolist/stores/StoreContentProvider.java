package ru.nehodov.todolist.stores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ru.nehodov.todolist.models.Task;

public class StoreContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://ru.nehodov.todolist/tasks");

    private final TaskStore store = TaskStore.getInstance();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        if (TextUtils.isEmpty(selection)) {
            cursor = new TaskStoreCursor(store.getTasks());
        } else {
            ArrayList<Task> filterTasks = new ArrayList<>();
            for (Task task : store.getTasks()) {
                if (task.getName().contains(selection)) {
                    filterTasks.add(task);
                }
            }
            cursor = new TaskStoreCursor(filterTasks);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
