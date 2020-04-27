package ru.nehodov.todolist.stores;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public interface IStore extends Serializable {


    ArrayList<Task> getTasks();

    Task getTask(int id);

    void addTask(Task newTask);

    void replaceTask(Task task, int id);

    void deleteTask(int id);

    void deleteAll();

    List<Task> searchTasks(String query);

}
