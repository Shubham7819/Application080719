package com.example.application080719;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Abstract player implementation that handles playing music with proper handling of headphones
 * and audio focus.
 */

public abstract class PlayerAdapter {

    private final String TAG = PlayerAdapter.class.getSimpleName();

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;

    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private boolean mAudioNoisyReceiverRegistered = false;
    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (isPlaying()) {
                            pause();
                        }
                    }
                }
            };

    private final Context mApplicationContext;
    private final AudioManager mAudioManager;
    private final AudioFocusHelper mAudioFocusHelper;

    private boolean mPlayOnAudioFocus = false;

    public PlayerAdapter(@NonNull Context context) {
        Log.v(TAG, "constructor called");
        mApplicationContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper();
    }

    public abstract int loadAudio(Context context, int audioResourceId);

    public abstract void pauseAudio(int streamId);

    public abstract int playAudio(int soundId);

    public abstract void resumeAudio(int streamId);

    public abstract void stopAudio(int streamId);

    public abstract SoundPool getPlayer();

    public abstract boolean isPlaying();

    public abstract void initializeMediaPlayer(Context context, int resId);

    public abstract void playMeditationAudio();

//    public abstract void seekMeditationAudioTo();

//    public final void play() {
//        if (mAudioFocusHelper.requestAudioFocus()) {
//            registerAudioNoisyReceiver();
//            onPlay();
//        }
//    }

    /**
     * Called when media is ready to be played and indicates the app has audio focus.
     */
//    protected abstract void onPlay();
    public final void pause() {
        Log.v(TAG, "pause called");
        if (!mPlayOnAudioFocus) {
            mAudioFocusHelper.abandonAudioFocus();
        }

        unregisterAudioNoisyReceiver();
        onPause();
    }

    /**
     * Called when media must be paused.
     */
    protected abstract void onPause();

    public final void resume() {
        Log.v(TAG, "resume called");
        if (mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver();
        }
        onResume();
    }

    protected abstract void onResume();

    public final void stop() {
        Log.v(TAG, "stop called");
        mAudioFocusHelper.abandonAudioFocus();
        unregisterAudioNoisyReceiver();
        onStop();
    }

    /**
     * Called when the media must be stopped. The player should clean up resources at this
     * point.
     */
    protected abstract void onStop();

    public abstract void setVolume(float volume);

    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    /**
     * Helper class for managing audio focus related tasks.
     */
    private final class AudioFocusHelper
            implements AudioManager.OnAudioFocusChangeListener {

        private boolean requestAudioFocus() {
            Log.v(TAG, "AudioFocusHelper: requestAudioFocus called");
            final int result = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private void abandonAudioFocus() {
            Log.v(TAG, "AudioFocusHelper: abandonAudioFocus called");
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.v(TAG, "AudioFocusHelper: onAudioFocusChange called");
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPlayOnAudioFocus && !isPlaying()) {
//                        play();
                    } else if (isPlaying()) {
                        setVolume(MEDIA_VOLUME_DEFAULT);
                    }
                    mPlayOnAudioFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setVolume(MEDIA_VOLUME_DUCK);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        mPlayOnAudioFocus = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioManager.abandonAudioFocus(this);
                    mPlayOnAudioFocus = false;
                    stop();
                    break;
            }
        }
    }
}