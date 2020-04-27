package ru.nehodov.todolist.stores;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public class SqlTaskStore implements IStore {

    private SQLiteDatabase db;

    private static final String[] projection = {
            TaskDbContract.TasksTable.COLUMN_NAME_ID,
            TaskDbContract.TasksTable.COLUMN_NAME_NAME,
            TaskDbContract.TasksTable.COLUMN_NAME_DESC,
            TaskDbContract.TasksTable.COLUMN_NAME_CREATED,
            TaskDbContract.TasksTable.COLUMN_NAME_DONE
    };

    public SqlTaskStore(SQLiteDatabase db) {
        this.db = db;
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
    public void addTask(Task newTask) {
        ContentValues values = new ContentValues();
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_NAME, newTask.getName());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DESC, newTask.getDesc());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_CREATED, newTask.getCreated());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DONE, newTask.getDoneDate());
        db.insert(TaskDbContract.TasksTable.TABLE_NAME, null, values);
    }

    @Override
    public void replaceTask(Task task, int taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_NAME, task.getName());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DESC, task.getDesc());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_CREATED, task.getCreated());
        values.put(TaskDbContract.TasksTable.COLUMN_NAME_DONE, task.getDoneDate());
        db.update(TaskDbContract.TasksTable.TABLE_NAME, values,
                TaskDbContract.TasksTable.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(taskId)});
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
    public void doTask(int taskId) {
        Task task = getTask(taskId);
        task.doTask();
        replaceTask(task, task.getId());
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
        Task task = new Task(taskName, taskDesc, created, doneTime);
        task.setId(taskId);
        return task;
    }
}
