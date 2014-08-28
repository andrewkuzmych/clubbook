package com.nl.clubbook.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.nl.clubbook.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/18/14
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     * @param title   - alert dialog title
     * @param message - alert message
     */
    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.create().show();
    }
}
