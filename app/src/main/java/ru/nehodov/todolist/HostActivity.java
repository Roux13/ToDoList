package ru.nehodov.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import java.util.List;

import ru.nehodov.todolist.fragments.TaskEditorFragment;
import ru.nehodov.todolist.fragments.TaskInfoFragment;
import ru.nehodov.todolist.fragments.TaskListFragment;
import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.stores.FileStore;
import ru.nehodov.todolist.stores.IStore;
import ru.nehodov.todolist.stores.MemTaskStore;
import ru.nehodov.todolist.stores.SqlTaskStore;
import ru.nehodov.todolist.stores.TaskDbHelper;

public class HostActivity extends AppCompatActivity implements TaskListFragment.TaskListListener,
        TaskEditorFragment.TaskEditorListener, TaskInfoFragment.TaskInfoListener {


    private TaskDbHelper dbHelper;
    private IStore taskStore;

    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity);
        dbHelper = new TaskDbHelper(this);
        // Раскомментировать, если нужна версия с SQLiteTaskStore
//        taskStore = new SqlTaskStore(dbHelper.getWritableDatabase());
        // Раскомментировать, если нужна версия с MemTaskStore
//        taskStore = MemTaskStore.getInstance();
        // Раскомментировать, если нужна версия с FileStore
        taskStore = FileStore.getInstance(getApplicationContext());

        if (fm.findFragmentById(R.id.activity_host) == null) {
            fm.beginTransaction()
                    .add(R.id.activity_host, TaskListFragment.getInstance(taskStore.getTasks()))
                    .commit();
        }
    }

    @Override
    public void callTaskEditor(Task task) {
        Task editedTask = task;
        if (task.getId() > TaskEditorFragment.NEW_TASK_INDEX) {
            editedTask = taskStore.getTask(task.getId());
        }
        replaceFragment(TaskEditorFragment.getInstance(editedTask));
    }

    @Override
    public void callTaskInfo(Task task) {
        replaceFragment(TaskInfoFragment.getInstance(task));
    }

    public void callTaskList() {
        replaceFragment(TaskListFragment.getInstance(taskStore.getTasks()));
    }

    @Override
    public List<Task> searchTasks(String searchQuery) {
        return taskStore.searchTasks(searchQuery);
    }

    @Override
    public void addTask(Task task) {
        taskStore.addTask(task);
        callTaskList();
    }

    @Override
    public void doTask(Task task) {
        task.doTask();
        taskStore.replaceTask(task, task.getId());
//        getSupportFragmentManager().popBackStack();
        callTaskInfo(task);
    }

    @Override
    public void deleteTask(Task task) {
        taskStore.deleteTask(task.getId());
        callTaskList();
    }

    @Override
    public void deleteAll() {
        taskStore.deleteAll();
        callTaskList();
    }

    @Override
    public void editTask(Task task, int id) {
        taskStore.replaceTask(task, id);
        callTaskList();
    }

    @Override
    public List<Task> getTasks() {
        return taskStore.getTasks();
    }

    private void replaceFragment(Fragment fragment) {
        fm.beginTransaction()
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(R.id.activity_host, fragment)
                .commit();
    }

}
