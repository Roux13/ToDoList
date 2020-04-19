package ru.nehodov.todolist.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.stores.StoreContentProvider;
import ru.nehodov.todolist.utils.DateTimeFormatter;

public class TaskListFragment extends Fragment implements ConfirmDeleteAllTasksDialog.ConfirmAllDeleteDialogListener {

    public static final String TASK_KEY = "current_task";
    public static final String TASK_INDEX_KEY = "task_index_key";
    public static final String TASK_STORE_KEY = "task_store_key";

    private ArrayList<Task> tasks;

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

        tasks = (ArrayList<Task>) getArguments().getSerializable(TASK_STORE_KEY);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("ToDoList");
        toolbar.inflateMenu(R.menu.task_list_menu);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        recycler = view.findViewById(R.id.item_list);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this::onFabClick);

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


    public void onFabClick(View view) {
        callTaskEditor(TaskEditorFragment.NEW_TASK_INDEX);
    }

    private void callTaskEditor(int indexOfTask) {
        Bundle args = new Bundle();
        args.putInt(TASK_INDEX_KEY, indexOfTask);
        listener.callTaskEditor(args);
    }

    @Override
    public void confirmDeleteAllTasks() {
        listener.deleteAll();
    }

    private class ItemAdapter extends RecyclerView.Adapter {


        private ArrayList<Task> tasks;

        public ItemAdapter(ArrayList<Task> tasks) {
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
                args.putInt(TASK_INDEX_KEY, position);
                listener.callTaskInfo(args);
            });
            isDoneChkbx = holder.itemView.findViewById(R.id.is_done_chkbx);
            taskNameTv = holder.itemView.findViewById(R.id.item_task_name_tv);
            taskDescTv = holder.itemView.findViewById(R.id.item_desc);
            taskDateTv = holder.itemView.findViewById(R.id.item_created_tv);
            editBtn = holder.itemView.findViewById(R.id.item_edit_btn);

            editBtn.setOnClickListener(view -> callTaskEditor(position));

            isDoneChkbx.setClickable(false);
            taskNameTv.setText(task.getName());
            taskDescTv.setText(task.getDesc());
            taskDescTv.setSelected(true);
            taskDateTv.setText(DateTimeFormatter.format(task.getCreated().getTime()));
            if (task.getDoneDate() != null) {
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
            taskDateTv.setText(DateTimeFormatter.format(task.getDoneDate().getTime()));

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
        if (item.getItemId() == R.id.delete_all_task_menu) {
            DialogFragment dialog = new ConfirmDeleteAllTasksDialog();
            dialog.show(getFragmentManager(), "confirm_dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
