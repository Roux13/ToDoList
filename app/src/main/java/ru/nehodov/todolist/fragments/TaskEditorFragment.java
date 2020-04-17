package ru.nehodov.todolist.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Calendar;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;

public class TaskEditorFragment extends Fragment {

    public static final int NEW_TASK_INDEX = -1;
    public static final String TRANSFERRED_TASK_KEY = "transferred_task_key";

    private TaskEditorListener listener;

    private int taskIndex;
    private Task task;
    private EditText taskTittleEdit;
    private EditText taskDescriptionEdit;

    public TaskEditorFragment() {
    }

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new TaskEditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_editor_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.task_editor_menu);
        toolbar.setTitle("Task editor");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(this::onNavigationButtonClick);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        taskIndex = getArguments().getInt(TaskListFragment.TASK_INDEX_KEY);

        taskTittleEdit = view.findViewById(R.id.task_title_edit);
        taskDescriptionEdit = view.findViewById(R.id.task_description_edit);

        if (taskIndex > NEW_TASK_INDEX) {
            task = (Task) getArguments().getSerializable(TRANSFERRED_TASK_KEY);
            taskTittleEdit.setText(task.getName());
            taskDescriptionEdit.setText(task.getDesc());
        }
        return view;
    }

    private void createTask() {
        String taskName = taskTittleEdit.getText().toString();
        taskName = taskName.equals("") ? "New Task" : taskName;
        String taskDescription = taskDescriptionEdit.getText().toString();
        Calendar created = Calendar.getInstance();
        task = new Task(taskName, taskDescription, created);
        listener.addNewTask(task);
    }

    private void editTask() {
        task.setName(taskTittleEdit.getText().toString());
        task.setDesc(taskDescriptionEdit.getText().toString());
        listener.editTask(task, taskIndex);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_edit_menu) {
            if (taskIndex > NEW_TASK_INDEX) {
                editTask();
            } else {
                createTask();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNavigationButtonClick(View view) {
        getActivity().onBackPressed();
    }

    public interface TaskEditorListener {
        void addNewTask(Task task);

        void editTask(Task task, int index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (TaskEditorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    String.format(
                            "Class %s must implement %s interface",
                            context.toString(), TaskEditorListener.class.getSimpleName()
                    )
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

}
