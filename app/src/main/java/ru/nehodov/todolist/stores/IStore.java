package ru.nehodov.todolist.stores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public interface IStore extends Serializable {


    ArrayList<Task> getTasks();

    Task getTask(int taskId);

    void addTask(Task newTask);

    void replaceTask(Task task, int taskId);

    void deleteTask(int taskId);

    void deleteAll();

    void doTask(int taskId);

    List<Task> searchTasks(String query);

}
