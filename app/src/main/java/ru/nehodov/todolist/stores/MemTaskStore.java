package ru.nehodov.todolist.stores;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.utils.DateTimeFormatter;

public class MemTaskStore implements IStore {

    private static IStore instance;
    private static int id = 0;

    private final Map<Integer, Task> tasks = new LinkedHashMap<>();

    private MemTaskStore() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("New Task " + i, "Description task " + i,
                    DateTimeFormatter.format(new Date()));
            task.setId(id++);
            tasks.put(task.getId(), task);
            i++;
            Task closedTask = new Task("New Task " + i, "Description task " + i,
                    DateTimeFormatter.format(new Date()));
            closedTask.setId(id++);
            closedTask.doTask();
            tasks.put(closedTask.getId(), closedTask);
        }
    }

    public static IStore getInstance() {
        if (instance == null) {
            instance = new MemTaskStore();
        }
        return instance;
    }

    @Override
    public void addTask(Task newTask) {
        newTask.setId(id++);
        this.tasks.put(newTask.getId(), newTask);
    }

    @Override
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void replaceTask(Task task, int taskId) {
        Task taskToReplace = tasks.get(taskId);
        taskToReplace = task;
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

    @Override
    public List<Task> searchTasks(String query) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getName().contains(query) || task.getCreated().contains(query)) {
                result.add(task);
            }
        }
        return result;
    }
}
