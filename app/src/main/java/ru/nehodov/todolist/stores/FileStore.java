package ru.nehodov.todolist.stores;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nehodov.todolist.models.Task;

public class FileStore implements IStore {

    private static final String LOG_TAG_FILE_STORE = FileStore.class.getSimpleName();

    private static FileStore INSTANCE;
    private int counter;
    private Context context;

    private FileStore(Context context) {
        this.context = context;
        counter = setCounterValue(context.getFilesDir().list());
    }

    private int setCounterValue(String[] fileNames) {
        int value = 0;
        for (String name : fileNames) {
            String firstPartOfName = name.split("\\.")[0];
            int tmpValue = Integer.parseInt(firstPartOfName);
            if (tmpValue >= value) {
                value = tmpValue + 1;
            }
        }
        return value;
    }

    public static FileStore getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FileStore(context);
        }
        return INSTANCE;
    }

    @Override
    public void addTask(Task newTask) {
        Log.d(LOG_TAG_FILE_STORE, "Into addTask()");

        int id = counter;
        File file = new File(context.getFilesDir(), (counter) + ".txt");
        counter++;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(id);
            out.println(newTask.getName());
            out.println(newTask.getDesc());
            out.println(newTask.getCreated());
            out.println(newTask.getDoneDate());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        Log.d(LOG_TAG_FILE_STORE, "Into getTasks()");

        ArrayList<Task> tasks = new ArrayList<>();
        String[] allFileNames = context.getFilesDir().list();
        if (allFileNames != null && allFileNames.length > 0) {
            for (String fileName : allFileNames) {
                Log.d(LOG_TAG_FILE_STORE, fileName);
                String[] splitName = fileName.split("\\.");
                Log.d(LOG_TAG_FILE_STORE, Arrays.toString(splitName));
                int numberOfTask = Integer.parseInt(splitName[0]);
                tasks.add(getTask(numberOfTask));
            }
        }
        return tasks;
    }

    @Override
    public Task getTask(int taskId) {

        Task task = null;
        File file = new File(context.getFilesDir(), taskId + ".txt");
        try (BufferedReader in = new BufferedReader(new FileReader(file))){
            int id = Integer.parseInt(in.readLine());
            String name = in.readLine();
            String desc = in.readLine();
            String created = in.readLine();
            String doneDate = in.readLine();
            task = new Task(id, name, desc, created, doneDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public void replaceTask(Task task, int taskId) {
        Log.d(LOG_TAG_FILE_STORE, "Into replaceTask()");

        File file = new File(context.getFilesDir(), taskId + ".txt");
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(taskId);
            out.println(task.getName());
            out.println(task.getDesc());
            out.println(task.getCreated());
            out.println(task.getDoneDate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTask(int taskId) {
        Log.d(LOG_TAG_FILE_STORE, "Into deleteTask()");

        File file = new File(context.getFilesDir(), taskId + ".txt");
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void deleteAll() {
        Log.d(LOG_TAG_FILE_STORE, "Into deleteAll()");

        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        counter = 0;
    }

    @Override
    public List<Task> searchTasks(String query) {
        Log.d(LOG_TAG_FILE_STORE, "Into searchTasks()");

        List<Task> tasks = getTasks();
        List<Task> foundTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getName().contains(query) || task.getCreated().contains(query)) {
                foundTasks.add(task);
            }
        }
        return foundTasks;
    }
}
