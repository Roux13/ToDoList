package ru.nehodov.todolist.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

import ru.nehodov.todolist.R;
import ru.nehodov.todolist.models.Task;
import ru.nehodov.todolist.utils.DateTimeFormatter;
import ru.nehodov.todolist.utils.TaskUtils;

import static android.app.Activity.RESULT_OK;

public class TaskEditorFragment extends Fragment {

    public static final int NEW_TASK_INDEX = -1;

    private static final String TAG = TaskEditorFragment.class.getSimpleName();
    private static final String EXTRA_CURRENT_PHOTO_PATH = "extra_current_photo_path";
    private static final String ARGUMENT_TASK = "ARGUMENT_TASK";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final int PERMISSION_REQUEST_CODE = 99;
    private static final String FILE_PROVIDER_AUTHORITY = "ru.nehodov.todolist.fileprovider";

    private TaskEditorListener listener;

    private Task task;
    private EditText taskTittleEdit;
    private EditText taskDescriptionEdit;
    private ImageView photo;

    private String currentPhotoPath;

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
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(EXTRA_CURRENT_PHOTO_PATH);
            Log.d(TAG, currentPhotoPath + " currentPhotoPath is loaded");
        }
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
        photo = view.findViewById(R.id.photo);

        task = (Task) requireArguments().getSerializable(ARGUMENT_TASK);

        if (task == null) {
            task = new Task("", "", "");
        }

        if (task.getPhotoPath() != null && !task.getPhotoPath().equals("")) {
            photo.setImageBitmap(BitmapFactory.decodeFile(task.getPhotoPath()));
        } else {
            photo.setOnClickListener(this::onPhotoSpaceClick);
        }

        taskTittleEdit.setText(task.getName());
        taskDescriptionEdit.setText(task.getDesc());
        return view;
    }

    public void onPhotoSpaceClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(requireActivity(), CAMERA_PERMISSION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{CAMERA_PERMISSION}, PERMISSION_REQUEST_CODE);
        } else {
            callCameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            photo.callOnClick();
        }
    }

    private void callCameraIntent() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File file = TaskUtils.createImageFile(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            currentPhotoPath = file.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(requireActivity(), FILE_PROVIDER_AUTHORITY, file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.setTaskPhoto();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENT_PHOTO_PATH, currentPhotoPath);
        Log.d(TAG, currentPhotoPath + " currentPhotoPath is saved");
    }

    private void setTaskPhoto() {
//        Log.d(TAG, "Into getPhotoBitmap()");
        Bitmap image = TaskUtils.getPhotoBitmap(
                requireActivity().getContentResolver(),
                currentPhotoPath);
        if (image != null) {
            photo.setImageBitmap(image);
            task.setPhotoPath(currentPhotoPath);
//            Log.d(TAG, currentPhotoPath
//                    + " set into the task. And now photoPath of the task is "
//                    + task.getPhotoPath());
        }
    }

    private void createTask() {
        String taskName = taskTittleEdit.getText().toString();
        taskName = taskName.equals("") ? "New Task" : taskName;
        String taskDescription = taskDescriptionEdit.getText().toString();
        String created = DateTimeFormatter.format(new Date());

        task = new Task(taskName, taskDescription, created, "");
        task.setPhotoPath(currentPhotoPath);
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
