package com.nl.clubbook.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 02.09.2014.
 */
public class MessageDialog extends DialogFragment {

    public static final String TAG = "MessageDialog";

    private static final String ARG_ACTION_ID = "action_id";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MESSAGE = "arg_message";
    private static final String ARG_POSITIVE_BTN_TEXT = "arg_positive_btn_text";
    private static final String ARG_NEGATIVE_BTN_TEXT = "arg_negative_btn_text";

    private final int POSITIVE_LISTENER_TYPE = 1;
    private final int NEGATIVE_LISTENER_TYPE = 2;

    public interface MessageDialogListener {

        public void onPositiveButtonClick(MessageDialog dialogFragment);

        public void onNegativeButtonClick(MessageDialog dialogFragment);
    }

    /*
     * Activity or fragment should implement MessageDialogListener to handle click in dialog's buttons
     */
    public static MessageDialog newInstance(String title, String message, String posBtnText, String negBtnText) {
        MessageDialog fragment = new MessageDialog();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BTN_TEXT, posBtnText);
        args.putString(ARG_NEGATIVE_BTN_TEXT, negBtnText);
        fragment.setArguments(args);

        return fragment;
    }

    /*
     * Activity or fragment should implement MessageDialogListener to handle click in dialog's buttons
     * If you use MessageDialog in Activity, put null as targetFragment parameter
     */
    public static MessageDialog newInstance(Fragment targetFragment, String title, String message, String posBtnText, String negBtnText) {
        MessageDialog fragment = new MessageDialog();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BTN_TEXT, posBtnText);
        args.putString(ARG_NEGATIVE_BTN_TEXT, negBtnText);
        fragment.setArguments(args);

        return fragment;
    }

    /*
     * Activity or fragment should implement MessageDialogListener to handle click in dialog's buttons
     * If you use MessageDialog in Activity, put null as targetFragment parameter
     */
    public static MessageDialog newInstance(Fragment targetFragment, int actionId, String title, String message, String posBtnText, String negBtnText) {
        MessageDialog fragment = new MessageDialog();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_ID, actionId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BTN_TEXT, posBtnText);
        args.putString(ARG_NEGATIVE_BTN_TEXT, negBtnText);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        String posBtnText = args.getString(ARG_POSITIVE_BTN_TEXT);
        String negBtnText = args.getString(ARG_NEGATIVE_BTN_TEXT);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title != null ? title : "");
        dialog.setMessage(message != null ? message : "");
        dialog.setPositiveButton(posBtnText != null ? posBtnText : getString(R.string.ok), positiveListener);

        if(negBtnText != null && !negBtnText.isEmpty()) {
            dialog.setNegativeButton(negBtnText, negativeListener);
        }
        return dialog.create();
    }

    public int getActionId() {
        Bundle args = getArguments();
        return args.getInt(ARG_ACTION_ID);
    }

    private DialogInterface.OnClickListener positiveListener
            = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            handleListener(POSITIVE_LISTENER_TYPE);
        }
    };

    private DialogInterface.OnClickListener negativeListener
            = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            handleListener(NEGATIVE_LISTENER_TYPE);
        }
    };

    private void handleListener(int listenerType) {
        MessageDialogListener listener = null;

        Fragment targetFragment = getTargetFragment();
        if(targetFragment != null && targetFragment instanceof MessageDialogListener) {
            listener = (MessageDialogListener) targetFragment;

        } else {

            Activity activity = getActivity();
            if(activity != null && activity instanceof MessageDialogListener) {
                listener = (MessageDialogListener) activity;
            }
        }

        if(listener == null) {
            return;
        }

        if(listenerType == POSITIVE_LISTENER_TYPE) {
            listener.onPositiveButtonClick(MessageDialog.this);
        } else {
            listener.onNegativeButtonClick(MessageDialog.this);
        }
    }
}
