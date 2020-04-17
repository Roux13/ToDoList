package ru.nehodov.todolist.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.nehodov.todolist.R;

public class ConfirmDeleteAllTasksDialog extends DialogFragment {

    private ConfirmAllDeleteDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_all_are_you_sure)
                .setNegativeButton(R.string.cancel, this::onNegativeClick)
                .setPositiveButton(R.string.ok, this::OnPositiveClick)
                .create();
    }

    public void onNegativeClick(DialogInterface dialog, int i) {
        this.dismiss();
    }

    public void OnPositiveClick(DialogInterface dialog, int i) {
           listener.confirmDeleteAllTasks();
    }

    public interface ConfirmAllDeleteDialogListener {
        void confirmDeleteAllTasks();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.listener = (ConfirmAllDeleteDialogListener) getFragmentManager()
                    .findFragmentById(R.id.activity_host);
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("Class %s must implement %s interface",
                    context.toString(), ConfirmAllDeleteDialogListener.class.getSimpleName()));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}
