package com.example.application080719;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.application080719.ui.main.MainActivity;

public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    public static final int PLAYER_NOTIFICATION_ID = 1138;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int PLAYER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String PLAYER_NOTIFICATION_CHANNEL_ID = "player_notification_channel";

    private final PlayerService mService;

    private final NotificationCompat.Action mPlayAction;
    private final NotificationCompat.Action mPauseAction;
    private final NotificationManager mNotificationManager;

    public NotificationUtils(PlayerService service) {
        mService = service;

        mNotificationManager =
                (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);

        mPlayAction =
                new NotificationCompat.Action(
                        R.drawable.ic_play,
                        mService.getString(R.string.play),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_PLAY));
        mPauseAction =
                new NotificationCompat.Action(
                        R.drawable.ic_pause,
                        mService.getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                mService,
                                PlaybackStateCompat.ACTION_PAUSE));

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll();
    }

    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    // This method will create a notification
    public Notification getNotification(@NonNull PlaybackStateCompat state,
                                        MediaSessionCompat.Token token) {

        boolean isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;

        // Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    PLAYER_NOTIFICATION_CHANNEL_ID,
                    "Primary",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mService
                , PLAYER_NOTIFICATION_CHANNEL_ID)
                .setStyle(
                        new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(token)
                                .setShowActionsInCompactView(0)
                                // For backwards compatibility with Android L and earlier.
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(
                                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                                mService,
                                                PlaybackStateCompat.ACTION_STOP)))
                .setSmallIcon(R.drawable.ic_audio)
                .setLargeIcon(largeIcon())
                .setContentTitle("Meditation Sounds Running")
                .setContentText("Click to open app")
                .setContentIntent(contentIntent())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false);

        // If the build version is greater than or equal to JELLY_BEAN and less than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (state.getActions() != 0) {
            notificationBuilder.addAction(isPlaying ? mPauseAction : mPlayAction);
        }

        // Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        // notificationManager.notify(PLAYER_NOTIFICATION_ID, notificationBuilder.build());
        return notificationBuilder.build();
    }

    // This method will create the pending intent which will trigger when
    // the notification is pressed. This pending intent should open up the MainActivity.
    private PendingIntent contentIntent() {
        Intent startActivityIntent = new Intent(mService, MainActivity.class);
        return PendingIntent.getActivity(
                mService,
                PLAYER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // This method is necessary to decode a bitmap needed for the notification.
    private Bitmap largeIcon() {
        Resources res = mService.getResources();
        return BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground);
    }
}
