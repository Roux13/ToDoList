package ru.nehodov.todolist.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;

public class TaskListFragment extends Fragment
        implements ConfirmDeleteAllTasksDialog.ConfirmAllDeleteDialogListener {

    private static final int NEW_TASK_INDEX = -1;
    private static final String ARGUMENT_ALL_TASKS = "ARGUMENT_ALL_TASKS";

    private List<Task> tasks;

    private TaskListListener listener;

    private RecyclerView recycler;

    public static Fragment getInstance(ArrayList<Task> allTasks) {
        Fragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_ALL_TASKS, allTasks);
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
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("ToDoList");
        toolbar.inflateMenu(R.menu.task_list_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
        SearchView searchView = view.findViewById(R.id.search_task_list_menu);
        searchView.setAlpha(1.0f);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tasks = listener.searchTasks(query);
                updateUI();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tasks = listener.searchTasks(newText);
                updateUI();
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
            loadTasks();
            updateUI();
            return true;
        });

        recycler = view.findViewById(R.id.item_list);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this::onFabClick);

        loadTasks();
        updateUI();

        return view;
    }

    private void updateUI() {
        recycler.setAdapter(new ItemAdapter(tasks));
    }

    private void loadTasks() {
        tasks = listener.getTasks();
    }

    public void onFabClick(View view) {
        Task task = new Task("", "", "");
        task.setId(NEW_TASK_INDEX);
        callTaskEditor(task);
    }

    private void callTaskEditor(Task task) {
        listener.callTaskEditor(task);
    }

    @Override
    public void confirmDeleteAllTasks() {
        listener.deleteAll();
    }

    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<Task> tasks;

        private Button editBtn;

        private CheckBox isDoneCheckBox;
        private TextView taskNameTv;
        private TextView taskDescTv;
        private TextView taskDateTv;

        public ItemAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.task_list_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final Task task = tasks.get(position);
            holder.itemView.setBackgroundColor(getColor(position));
            holder.itemView.setOnClickListener(view -> listener.callTaskInfo(task));
            isDoneCheckBox = holder.itemView.findViewById(R.id.isDoneCheckBox);
            taskNameTv = holder.itemView.findViewById(R.id.item_task_name_tv);
            taskDescTv = holder.itemView.findViewById(R.id.item_desc);
            taskDateTv = holder.itemView.findViewById(R.id.item_created_tv);
            editBtn = holder.itemView.findViewById(R.id.item_edit_btn);

            editBtn.setOnClickListener(view -> callTaskEditor(task));

            isDoneCheckBox.setClickable(false);
            taskNameTv.setText(task.getName());
            taskDescTv.setText(task.getDesc());
            taskDescTv.setSelected(true);
            taskDateTv.setText(task.getCreated());
            if (!task.getDoneDate().equals("")) {
                markAsCompleted(task);
            }
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        private void markAsCompleted(Task task) {
            isDoneCheckBox.setChecked(true);
            taskNameTv.setTextColor(
                    requireActivity().getResources().getColor(R.color.colorTextDisabled)
            );
            taskNameTv.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
            taskDescTv.setTextColor(
                    requireActivity().getResources().getColor(R.color.colorTextDisabled)
            );
            taskDescTv.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
            taskDateTv.setTextColor(requireActivity()
                    .getResources()
                    .getColor(R.color.colorTextDisabled));
            taskDateTv.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
            editBtn.setEnabled(false);
            editBtn.setVisibility(View.INVISIBLE);
            taskDateTv.setText(task.getDoneDate());

        }

        private int getColor(int position) {
            int color = 0;
            if (position % 2 == 0) {
                color = getResources().getColor(R.color.grey_item);
            }
            if (position % 2 != 0) {
                color = getResources().getColor(R.color.white);
            }
            return color;
        }

    }

    public interface TaskListListener {
        void callTaskInfo(Task task);

        void callTaskEditor(Task task);

        void deleteAll();

        List<Task> getTasks();

        List<Task> searchTasks(String searchQuery);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (TaskListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    String.format("Class %s must implement %s interface",
                            requireActivity().toString(), TaskListListener.class.getSimpleName()
                    )
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_task_menu) {
            DialogFragment dialog = new ConfirmDeleteAllTasksDialog();
            dialog.show(getParentFragmentManager(), "confirm_dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
