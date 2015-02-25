package com.nl.clubbook.ui.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 05.02.2015.
 */
public class SelectImageDialogFragment extends DialogFragment {

    public static final String TAG = SelectImageDialogFragment.class.getSimpleName();

    private final int INDEX_PICK_FROM_CAMERA = 0;
    private final int INDEX_PICK_FROM_GALLERY = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] items = getResources().getStringArray(R.array.select_image_dialog_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);

        builder.setTitle(getString(R.string.select_image));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                OnPickOptionSelectedListener listener;

                Activity activity = getActivity();
                if(activity != null && activity instanceof OnPickOptionSelectedListener) {
                    listener = (OnPickOptionSelectedListener) activity;
                } else {
                    return;
                }

                switch (item) {
                    case INDEX_PICK_FROM_CAMERA:
                        listener.onPickFromCamera();
                        break;
                    case INDEX_PICK_FROM_GALLERY:
                        listener.onPickFromGallery();
                        break;

                    default:
                        listener.onPickFromCamera();

                }
            }
        });

        return builder.create();
    }

    public interface OnPickOptionSelectedListener {

        public void onPickFromCamera();

        public void onPickFromGallery();
    }
}
