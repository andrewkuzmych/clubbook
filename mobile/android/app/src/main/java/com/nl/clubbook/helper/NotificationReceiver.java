package com.nl.clubbook.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.nl.clubbook.R;
import android.app.NotificationManager;
import android.app.PendingIntent;

import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.activity.MainActivity;
import com.nl.clubbook.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew on 6/13/2014.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    public static final String TYPE_CHAT = "chat";
    public static final String TYPE_FRIENDS = "friends";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String type = json.getString("type");
            String header = json.getString("header");
            String uniqueId = json.getString("unique_id");
            String msg = json.getString("msg");

            ClubbookPreferences clubbookPreferences = ClubbookPreferences.getInstance(context);
            String con = clubbookPreferences.getConversationListener();
            if(con == null || !con.equalsIgnoreCase(uniqueId)) {
                int notificationId = (uniqueId).hashCode();
                generateNotification(context, R.drawable.icon_play, header, msg, type, notificationId);
            }

        } catch (JSONException e) {
            L.i("" + e);
        }
    }

    public static void generateNotification(Context context, int icon, String title, String message, String type, int notificationId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if(ClubbookPreferences.getInstance(context).isNotificationVibrationEnabled()) {
            mBuilder.setVibrate(new long[] {1000, 1000, 1000});
        }

        mBuilder.setAutoCancel(true);
        Intent viewIntent = new Intent(context, MainActivity.class);
        viewIntent.putExtra(EXTRA_TYPE, type);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(viewIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}