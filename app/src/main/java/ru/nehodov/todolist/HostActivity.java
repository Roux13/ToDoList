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
        taskStore = new SqlTaskStore(dbHelper.getWritableDatabase());
        // Раскомментировать, если нужна версия с MemTaskStore
//        taskStore = MemTaskStore.getInstance();

        if (fm.findFragmentById(R.id.activity_host) == null) {
            fm.beginTransaction()
                    .add(R.id.activity_host, TaskListFragment.getInstance(new Bundle()))
                    .commit();
        }
    }

    @Override
    public void callTaskEditor(Bundle args) {
        int taskId = args.getInt(TaskListFragment.TASK_ID_KEY);
        if (taskId > TaskEditorFragment.NEW_TASK_INDEX) {
            args.putSerializable(
                    TaskEditorFragment.TRANSFERRED_TASK_KEY,
                    taskStore.getTask(taskId)
            );
        }
        this.replaceFragment(TaskEditorFragment.getInstance(args));
    }

    @Override
    public void callTaskInfo(Bundle args) {
        this.replaceFragment(TaskInfoFragment.getInstance(args));
    }

    @Override
    public List<Task> searchTasks(String searchQuery) {
        return taskStore.searchTasks(searchQuery);
    }

    @Override
    public void addNewTask(Task task) {
        taskStore.addTask(task);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    @Override
    public void doTask(int taskId) {
        taskStore.doTask(taskId);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_KEY, taskStore.getTask(taskId));
        args.putInt(TaskListFragment.TASK_ID_KEY, taskId);
        getSupportFragmentManager().popBackStack();
        callTaskInfo(args);
    }

    @Override
    public void deleteTask(int taskId) {
        taskStore.deleteTask(taskId);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    @Override
    public void deleteAll() {
        taskStore.deleteAll();
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    @Override
    public void editTask(Task task, int id) {
        taskStore.replaceTask(task, id);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
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
