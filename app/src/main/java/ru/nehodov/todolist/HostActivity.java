package ru.nehodov.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import ru.nehodov.todolist.fragments.TaskEditorFragment;
import ru.nehodov.todolist.fragments.TaskInfoFragment;
import ru.nehodov.todolist.fragments.TaskListFragment;
import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.stores.TaskStore;

public class HostActivity extends AppCompatActivity implements TaskListFragment.TaskListListener,
        TaskEditorFragment.TaskEditorListener, TaskInfoFragment.TaskInfoListener {

    private final TaskStore taskStore = TaskStore.getInstance();

    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity);

        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        if (fm.findFragmentById(R.id.activity_host) == null) {
            fm.beginTransaction()
                    .add(R.id.activity_host, TaskListFragment.getInstance(args))
                    .commit();
        }
    }


    @Override
    public void callTaskEditor(Bundle args) {
        int indexOfTask = args.getInt(TaskListFragment.TASK_INDEX_KEY);
        if (indexOfTask > TaskEditorFragment.NEW_TASK_INDEX) {
            args.putSerializable(
                    TaskEditorFragment.TRANSFERRED_TASK_KEY,
                    taskStore.getTask(indexOfTask)
            );
        }
        this.replaceFragment(TaskEditorFragment.getInstance(args));
    }

    @Override
    public void callTaskInfo(Bundle args) {
        this.replaceFragment(TaskInfoFragment.getInstance(args));
    }


    @Override
    public void addNewTask(Task task) {
        taskStore.addTask(task);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    @Override
    public void doTask(int index) {
        Task task = taskStore.getTask(index);
        task.doTask();
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_KEY, task);
        args.putInt(TaskListFragment.TASK_INDEX_KEY, index);
        callTaskInfo(args);
    }

    @Override
    public void deleteTask(int index) {
        taskStore.deleteTask(index);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    @Override
    public void deleteAll() {
        taskStore.deleteAll();
        replaceFragment(TaskListFragment.getInstance(new Bundle()));
    }

    @Override
    public void editTask(Task task, int index) {
        taskStore.replaceTask(task, index);
        Bundle args = new Bundle();
        args.putSerializable(TaskListFragment.TASK_STORE_KEY, taskStore.getTasks());
        replaceFragment(TaskListFragment.getInstance(args));
    }

    private void replaceFragment(Fragment fragment) {
        fm.beginTransaction()
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(R.id.activity_host, fragment)
                .commit();
    }
}
