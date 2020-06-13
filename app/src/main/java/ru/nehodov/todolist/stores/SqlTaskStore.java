package ru.nehodov.todolist.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public class SqlTaskStore implements IStore {

    static final String TAG = SqlTaskStore.class.getSimpleName();

    private SQLiteDatabase db;

    private static final String[] projection = {
            TaskDbContract.TasksTable.COLUMN_NAME_ID,
            TaskDbContract.TasksTable.COLUMN_NAME_NAME,
            TaskDbContract.TasksTable.COLUMN_NAME_DESC,
            TaskDbContract.TasksTable.COLUMN_NAME_CREATED,
            TaskDbContract.TasksTable.COLUMN_NAME_DONE,
            TaskDbContract.TasksTable.COLUMN_NAME_PHOTO_PATH
    };

    public SqlTaskStore(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void addTask(Task newTask) {
        Log.d(TAG, "Into addTask()");
        ContentValues values = new ContentValues();
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_NAME, newTask.getName());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DESC, newTask.getDesc());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_CREATED, newTask.getCreated());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DONE, newTask.getDoneDate());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_PHOTO_PATH, newTask.getPhotoPath());
        db.insert(TaskDbContract.TasksTable.TABLE_NAME, null, values);
        Log.d(TAG, "Task " + newTask.getName()
                + " is added. PhotoPath of the Task is "
                + newTask.getPhotoPath());
    }

    @Override
    public Task getTask(int taskId) {
        Task task = null;
        try (Cursor cursor = db.query(TaskDbContract.TasksTable.TABLE_NAME,
                projection,
                TaskDbContract.TasksTable.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null, null, null)){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                task = makeTaskFromCursor(cursor);
                cursor.moveToNext();
            }
        }
        return task;
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        try (Cursor cursor = db.query(TaskDbContract.TasksTable.TABLE_NAME,
                null, null, null,
                null, null, null)){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(makeTaskFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        return result;
    }

    @Override
    public void replaceTask(Task task, int taskId) {
        Log.d(TAG, "into replaceTask()");
        ContentValues values = new ContentValues();
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_NAME, task.getName());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DESC, task.getDesc());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_CREATED, task.getCreated());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DONE, task.getDoneDate());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_PHOTO_PATH, task.getPhotoPath());
        db.update(TaskDbContract.TasksTable.TABLE_NAME, values,
                TaskDbContract.TasksTable.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(taskId)});
        Log.d(TAG, "photoPath of task " + task.getName() + " is " + task.getPhotoPath());

    }

    @Override
    public void deleteTask(int taskId) {
        db.delete(TaskDbContract.TasksTable.TABLE_NAME,
                TaskDbContract.TasksTable.COLUMN_NAME_ID + " = ?",
                new String[]{ String.valueOf(taskId)
                });
    }

    @Override
    public void deleteAll() {
        db.delete(TaskDbContract.TasksTable.TABLE_NAME,
                null, null);
    }

    @Override
    public List<Task> searchTasks(String query) {
        List<Task> result = new ArrayList<>();
        try (Cursor cursor = db.query(TaskDbContract.TasksTable.TABLE_NAME, projection,
                TaskDbContract.TasksTable.COLUMN_NAME_NAME + " LIKE '%" + query + "%' OR "
                        + TaskDbContract.TasksTable.COLUMN_NAME_CREATED + " LIKE '%" + query + "%'",
                null,
                null, null, null)){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.add(makeTaskFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        return result;
    }

    private Task makeTaskFromCursor(Cursor cursor) {
        int taskId = cursor.getInt(cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_ID));
        String taskName = cursor.getString(
                cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_NAME));
        String taskDesc = cursor.getString(
                cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_DESC));
        String created = cursor.getString(
                cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_CREATED));
        String doneTime = cursor.getString(
                cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_DONE));
        String photoUri = cursor.getString(
                cursor.getColumnIndex(TaskDbContract.TasksTable.COLUMN_NAME_PHOTO_PATH));
        Task task = new Task(taskName, taskDesc, created, doneTime);
        task.setId(taskId);
        task.setPhotoPath(photoUri);
        return task;
    }
}
