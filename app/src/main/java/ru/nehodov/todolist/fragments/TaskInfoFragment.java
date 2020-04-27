package ru.nehodov.todolist.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;

public class TaskInfoFragment extends Fragment implements ConfirmDeleteTaskDialog.ConfirmDeleteTaskDialogListener {

    private TaskInfoListener listener;

    private int taskId;
    private Task task;

    public TaskInfoFragment() {
    }

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new TaskInfoFragment();
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
        View view = inflater.inflate(R.layout.task_info_fragment, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.task_info_menu);
        toolbar.setTitle("Task info");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(this::onNavigationButtonClick);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        TextView tittleTv = view.findViewById(R.id.task_title_info);
        TextView descriptionTv = view.findViewById(R.id.task_description_info);
        TextView createdTv = view.findViewById(R.id.task_created_tv_info);
        TextView closedTv = view.findViewById(R.id.task_closed_tv_info);

        taskId = getArguments().getInt(TaskListFragment.TASK_ID_KEY);
        task = (Task) getArguments().getSerializable(TaskListFragment.TASK_KEY);

        tittleTv.setText(task.getName());
        descriptionTv.setText(task.getDesc());
        createdTv.setText(task.getCreated());
        if (!task.getDoneDate().equals("")) {
            closedTv.setText(task.getDoneDate());
            toolbar.getMenu().findItem(R.id.done_info_menu).setEnabled(false).setVisible(false);
        } else {
            closedTv.setText("");
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle args = new Bundle();
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.edit_info_menu:
                args.putInt(TaskListFragment.TASK_ID_KEY, taskId);
                listener.callTaskEditor(args);
                return true;
            case R.id.done_info_menu:
                listener.doTask(taskId);
                return true;
            case R.id.delete_info_menu:
                DialogFragment dialog = new ConfirmDeleteTaskDialog();
                dialog.show(getFragmentManager(), "confirm_delete_task_dialog");
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onNavigationButtonClick(View view) {
        getActivity().onBackPressed();
    }

    @Override
    public void confirmDeleteTask() {
        listener.deleteTask(taskId);
    }

    public interface TaskInfoListener {

        void callTaskEditor(Bundle args);

        void doTask(int taskId);

        void deleteTask(int taskId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (TaskInfoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    String.format("Class %s must implement %s interface",
                            context.toString(), TaskInfoListener.class.getSimpleName()
                            )
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
