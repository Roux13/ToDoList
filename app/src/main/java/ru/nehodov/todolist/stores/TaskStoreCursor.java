package ru.nehodov.todolist.stores;

import android.database.AbstractCursor;

import java.util.ArrayList;

import ru.nehodov.todolist.models.Task;

public class TaskStoreCursor extends AbstractCursor {

    private final ArrayList<Task> store;

    public TaskStoreCursor(ArrayList<Task> store) {
        this.store = store;
    }

    @Override
    public int getCount() {
        return store.size();
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"_ID", "NAME"};
    }

    @Override
    public String getString(int column) {
        Task task = store.get(getPosition());
        String value = null;
        if (column == 1) {
            value = task.getName();
        }
        return value;
    }

    @Override
    public short getShort(int i) {
        return 0;
    }

    @Override
    public int getInt(int i) {
        return 0;
    }

    @Override
    public long getLong(int i) {
        return 0;
    }

    @Override
    public float getFloat(int i) {
        return 0;
    }

    @Override
    public double getDouble(int i) {
        return 0;
    }

    @Override
    public boolean isNull(int i) {
        return false;
    }
}
