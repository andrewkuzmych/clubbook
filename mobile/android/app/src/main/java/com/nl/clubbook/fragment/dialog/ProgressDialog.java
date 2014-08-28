package com.nl.clubbook.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.nl.clubbook.utils.L;

/**
 * Created by User on 11.08.2014.
 */
public class ProgressDialog extends DialogFragment {

    public static final String TAG = "ProgressDialog";

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public interface OnDialogCanceledListener {
        public void onDialogCanceled();
    }

    public static Fragment newInstance(String title, String message) {
        ProgressDialog dialog = new ProgressDialog();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);

        return dialog;
    }

    public static Fragment newInstance(Fragment targetFragment, String title, String message) {
        ProgressDialog dialog = new ProgressDialog();
        dialog.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getActivity());

        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);

        progressDialog.setTitle(title != null ? title : "");
        progressDialog.setMessage(message != null ? message : "");

        return progressDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        Fragment targetFragment = getTargetFragment();
        if(targetFragment != null && targetFragment instanceof OnDialogCanceledListener) {
            OnDialogCanceledListener listener = (OnDialogCanceledListener) targetFragment;
            listener.onDialogCanceled();
        }
    }
}
