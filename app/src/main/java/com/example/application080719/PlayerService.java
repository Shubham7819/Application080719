package com.example.application080719;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service {

    private final IBinder mBinder = new LocalService();
    public static SoundPool soundPool;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("PlayerService.java", "OnStartCommand called.");
        soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        Notification notification = NotificationUtils.createNotification(getApplication());
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("PlayerService.java", "OnBind called.");
        // COMPLETED: Return the communication channel to the service.
        return mBinder;
    }

    class LocalService extends Binder {

        PlayerService getService() {
            Log.v("PlayerService.java", "getService called.");
            return PlayerService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        PreferenceUtilities.setServiceAvailability(this, false);
        Log.v("PlayerService.java", "OnDestroy called.");
    }

    public int loadAudio(Context context, int resId) {
        return soundPool.load(context, resId, 1);
    }

    public void pauseAudio(int streamId) {
        soundPool.pause(streamId);
    }

    public int playAudio(int soundId) {
        return soundPool.play(soundId, 1, 1, 0, -1, 1);
    }

    public void resumeAudio(int streamId) {
        soundPool.resume(streamId);
    }

    public void stopAudio(int streamId) {
        soundPool.stop(streamId);
    }

    public SoundPool getPlayer() {
        return soundPool;
    }

}
