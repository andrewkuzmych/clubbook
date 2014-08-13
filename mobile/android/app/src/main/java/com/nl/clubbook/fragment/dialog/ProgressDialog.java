package com.nl.clubbook.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by User on 11.08.2014.
 */
public class ProgressDialog extends DialogFragment {

    public static final String TAG = "ProgressDialog";

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static Fragment newInstance(String title, String message) {
        ProgressDialog dialog = new ProgressDialog();

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
        String title = args.getString(ARG_TITLE, null);
        String message = args.getString(ARG_MESSAGE, null);

        progressDialog.setTitle(title != null ? title : "");
        progressDialog.setMessage(message != null ? message : "");

        return progressDialog;
    }
}
