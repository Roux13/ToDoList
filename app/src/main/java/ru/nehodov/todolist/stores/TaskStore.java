package ru.nehodov.todolist.stores;

import java.util.ArrayList;
import java.util.Calendar;

import ru.nehodov.todolist.models.Task;

public class TaskStore {

    private static TaskStore INSTANCE;

    private final ArrayList<Task> tasks = new ArrayList<>();

    private TaskStore() {
        for (int i = 0; i < 12; i ++) {
            tasks.add(new Task("New Task " + i, "Description task " + i, Calendar.getInstance()));
            i++;
            Task closedTask = new Task("New Task " + i, "Description task " + i, Calendar.getInstance());
            closedTask.doTask();
            tasks.add(closedTask);
        }
    }

    public static TaskStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TaskStore();
        }
        return INSTANCE;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public void addTask(Task newTask) {
        this.tasks.add(newTask);
    }

    public void replaceTask(Task task, int index) {
        Task taskToReplace = tasks.get(index);
        taskToReplace = task;
    }

    public void deleteTask(int index) {
        tasks.remove(index);
    }

    public void deleteAll() {
        tasks.clear();
    }

    public int getSize() {
        return this.tasks.size();
    }

}
