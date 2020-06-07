package ru.nehodov.todolist.stores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public interface IStore extends Serializable {


    void addTask(Task newTask);

    Task getTask(int taskId);

    ArrayList<Task> getTasks();

    void replaceTask(Task task, int taskId);

    void deleteTask(int taskId);

    void deleteAll();

    List<Task> searchTasks(String query);

}
