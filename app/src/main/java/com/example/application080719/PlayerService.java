package com.example.application080719;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

//TODO modify to support api version 25 and lower
public class PlayerService extends MediaBrowserServiceCompat {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";

    private static final String LOG_TAG = PlayerService.class.getSimpleName();

    private final IBinder mBinder = new LocalService();

    private MediaSessionCompat mediaSession;
    private PlayerAdapter mPlayback;
    private NotificationUtils mNotificationUtils;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "onCreate called");

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(this, LOG_TAG);

        // MediaSessionCallback() has methods that handle callbacks from a media controller
        mCallback = new MediaSessionCallback();
        mediaSession.setCallback(mCallback);

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setActive(true);

        mNotificationUtils = new NotificationUtils(this);

        mPlayback = new SoundPlayerAdapter(this, new MediaPlayerListener());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.v(LOG_TAG, "onGetRoot called");
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentMediaId
            , @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.v(LOG_TAG, "onLoadChildren called");
        result.sendResult(null);
    }

    public class LocalService extends Binder {

        public PlayerService getService() {
            Log.v("PlayerService.java", "getService called.");
            return PlayerService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayback.stop();
        mediaSession.release();
        Log.v("PlayerService.java", "OnDestroy called.");
    }

    public int loadAudio(Context context, int resId) {
        Log.v(LOG_TAG, "loadAudio called");
        return mPlayback.loadAudio(context, resId);
    }

    public void pauseAudio(int streamId) {
        Log.v(LOG_TAG, "pauseAudio called");
        mPlayback.pauseAudio(streamId);
    }

    public int playAudio(int soundId) {
        Log.v(LOG_TAG, "playAudio called");
        return mPlayback.playAudio(soundId);
    }

    public void resumeAudio(int streamId) {
        Log.v(LOG_TAG, "resumeAudio called");
        mPlayback.resumeAudio(streamId);
    }

    public void stopAudio(int streamId) {
        Log.v(LOG_TAG, "stopAudio called");
        mPlayback.stopAudio(streamId);
    }

    public SoundPool getPlayer() {
        Log.v(LOG_TAG, "getPlayer called");
        return mPlayback.getPlayer();
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            Log.v(LOG_TAG, " MediaSessionCallback: onCommand called");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Bundle result = new Bundle();
                result.putBinder("key", mBinder);
                cb.send(0, result);
            }
        }

        @Override
        public void onPlay() {
            mPlayback.resume();
            Log.d(LOG_TAG, "MediaSessionCallback: onPlay called");
        }

        @Override
        public void onPause() {
            Log.d(LOG_TAG, "MediaSessionCallback: onPause called");
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            Log.d(LOG_TAG, "MediaSessionCallback: onStop called");
            mPlayback.stop();
            mediaSession.setActive(false);
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.
    public class MediaPlayerListener extends PlaybackInfoListener {

        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            Log.d(LOG_TAG, "MediaPlayerListener: onPlaybackStateChange called");
            // Report the state to the MediaSession.
            mediaSession.setPlaybackState(state);

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Log.d(LOG_TAG, "MediaPlayerListener: moveServiceToStartedState called");
                Notification notification =
                        mNotificationUtils.getNotification(state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            PlayerService.this,
                            new Intent(PlayerService.this, PlayerService.class));
                    mServiceInStartedState = true;
                }

                startForeground(NotificationUtils.PLAYER_NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                Log.d(LOG_TAG, "MediaPlayerListener: updateNotificationForPause called");
                stopForeground(false);
                Notification notification =
                        mNotificationUtils.getNotification(state, getSessionToken());
                mNotificationUtils.getNotificationManager()
                        .notify(NotificationUtils.PLAYER_NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                Log.d(LOG_TAG, "MediaPlayerListener: moveServiceOutOfStartedState called");
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }

    }

}
