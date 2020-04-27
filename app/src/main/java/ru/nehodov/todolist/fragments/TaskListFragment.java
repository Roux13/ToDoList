package ru.nehodov.todolist.fragments;

import android.content.Context;
import android.database.Cursor;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import ru.nehodov.todolist.stores.StoreContentProvider;

public class TaskListFragment extends Fragment implements ConfirmDeleteAllTasksDialog.ConfirmAllDeleteDialogListener {

    public static final String TASK_KEY = "current_task";
    public static final String TASK_ID_KEY = "task_index_key";
    public static final String TASK_STORE_KEY = "task_store_key";

    private List<Task> tasks;

    private TaskListListener listener;

    private RecyclerView recycler;

    public TaskListFragment() {
    }

    public static Fragment getInstance(Bundle args) {
        Fragment fragment = new TaskListFragment();
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

        try (Cursor cursor = this.getActivity().getContentResolver()
                .query(StoreContentProvider.CONTENT_URI, new String[]{"NAME"}, "0", null, null, null)) {
            while (cursor.moveToNext()) {
                Log.d("ContentProvider", cursor.getString(1));
            }
        }
        return view;
    }

    private void updateUI() {
        recycler.setAdapter(new ItemAdapter(tasks));
    }

    private void loadTasks() {
        tasks = listener.getTasks();
    }

    public void onFabClick(View view) {
        callTaskEditor(TaskEditorFragment.NEW_TASK_INDEX);
    }

    private void callTaskEditor(int taskId) {
        Bundle args = new Bundle();
        args.putInt(TASK_ID_KEY, taskId);
        listener.callTaskEditor(args);
    }

    @Override
    public void confirmDeleteAllTasks() {
        listener.deleteAll();
    }

    private class ItemAdapter extends RecyclerView.Adapter {


        private List<Task> tasks;

        public ItemAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        private CheckBox isDoneChkbx;
        private TextView taskNameTv;
        private TextView taskDescTv;
        private TextView taskDateTv;
        private Button editBtn;

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
            Bundle args = new Bundle();
            holder.itemView.setBackgroundColor(getColor(position));
            holder.itemView.setOnClickListener(view -> {
                args.putSerializable(TASK_KEY, task);
                args.putInt(TASK_ID_KEY, task.getId());
                listener.callTaskInfo(args);
            });
            isDoneChkbx = holder.itemView.findViewById(R.id.is_done_chkbx);
            taskNameTv = holder.itemView.findViewById(R.id.item_task_name_tv);
            taskDescTv = holder.itemView.findViewById(R.id.item_desc);
            taskDateTv = holder.itemView.findViewById(R.id.item_created_tv);
            editBtn = holder.itemView.findViewById(R.id.item_edit_btn);

            editBtn.setOnClickListener(view -> callTaskEditor(task.getId()));

            isDoneChkbx.setClickable(false);
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
            isDoneChkbx.setChecked(true);
            taskNameTv.setTextColor(
                    getContext().getResources().getColor(R.color.colorTextDisabled)
            );
            taskNameTv.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
            taskDescTv.setTextColor(
                    getContext().getResources().getColor(R.color.colorTextDisabled)
            );
            taskDescTv.setPaintFlags(TextPaint.STRIKE_THRU_TEXT_FLAG);
            taskDateTv.setTextColor(getContext()
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
        void callTaskInfo(Bundle args);

        void callTaskEditor(Bundle args);

        void deleteAll();

        List<Task> getTasks();

        List<Task> searchTasks(String searchQuery);

        void doTask(int index);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (TaskListListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    String.format("Class %s must implement %s interface",
                            getContext().toString(), TaskListListener.class.getSimpleName()
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
        switch (itemId) {
            case R.id.delete_all_task_menu:
                DialogFragment dialog = new ConfirmDeleteAllTasksDialog();
                dialog.show(getFragmentManager(), "confirm_dialog");
                return true;
//            case R.id.search_task_list_menu:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
