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

import java.util.Date;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.utils.DateTimeFormatter;

public class TaskEditorFragment extends Fragment {

    public static final int NEW_TASK_INDEX = -1;
    static final String ARGUMENT_TASK = "ARGUMENT_TASK";

    private TaskEditorListener listener;

    private Task task;
    private EditText taskTittleEdit;
    private EditText taskDescriptionEdit;

    public TaskEditorFragment() {
    }

    public static Fragment getInstance(Task task) {
        Fragment fragment = new TaskEditorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_TASK, task);
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

        taskTittleEdit = view.findViewById(R.id.task_title_edit);
        taskDescriptionEdit = view.findViewById(R.id.task_description_edit);

        task = (Task) requireArguments().getSerializable(ARGUMENT_TASK);

//        if (task.getId() > NEW_TASK_INDEX) {
            taskTittleEdit.setText(task.getName());
            taskDescriptionEdit.setText(task.getDesc());
//        }
        return view;
    }

    private void createTask() {
        String taskName = taskTittleEdit.getText().toString();
        taskName = taskName.equals("") ? "New Task" : taskName;
        String taskDescription = taskDescriptionEdit.getText().toString();
        String created = DateTimeFormatter.format(new Date());

        task = new Task(taskName, taskDescription, created, "");
        listener.addTask(task);
    }

    private void editTask() {
        task.setName(taskTittleEdit.getText().toString());
        task.setDesc(taskDescriptionEdit.getText().toString());
        listener.editTask(task, task.getId());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_edit_menu) {
            if (task.getId() > NEW_TASK_INDEX) {
                editTask();
            } else {
                createTask();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onNavigationButtonClick(View view) {
        requireActivity().onBackPressed();
    }

    public interface TaskEditorListener {
        void addTask(Task task);

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
