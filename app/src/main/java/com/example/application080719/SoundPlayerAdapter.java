package com.example.application080719;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.application080719.dto.SoundItem;
import com.example.application080719.dto.Sounds;
import com.example.application080719.ui.main.CommonFragment;
import com.example.application080719.ui.main.MainActivity;

public final class SoundPlayerAdapter extends PlayerAdapter {

    private final String TAG = SoundPlayerAdapter.class.getSimpleName();
    private final Context mContext;
    private SoundPool mSoundPool;
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private int mState;

    public SoundPlayerAdapter(Context context, PlaybackInfoListener listener) {
        super(context);
        Log.v(TAG, "constructor called");
        mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;
    }

    private void initializeSoundPool() {
        Log.v(TAG, "initializeSoundPool called");
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0) {
                        if (sampleId == Sounds.soundIdList[Sounds.BELLS]) {
                            supportMethod(Sounds.BELLS, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.BIRD]) {
                            supportMethod(Sounds.BIRD, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.CLOCK_CHIMES]) {
                            supportMethod(Sounds.CLOCK_CHIMES, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.FARM]) {
                            supportMethod(Sounds.FARM, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.FIRE]) {
                            supportMethod(Sounds.FIRE, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.FLUTE]) {
                            supportMethod(Sounds.FLUTE, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.MUSIC_BOX]) {
                            supportMethod(Sounds.MUSIC_BOX, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.NIGHT]) {
                            supportMethod(Sounds.NIGHT, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.RAIN]) {
                            supportMethod(Sounds.RAIN, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.RAINFOREST]) {
                            supportMethod(Sounds.RAINFOREST, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.RIVER]) {
                            supportMethod(Sounds.RIVER, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.SEA]) {
                            supportMethod(Sounds.SEA, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.THUNDER]) {
                            supportMethod(Sounds.THUNDER, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.WATERFALL]) {
                            supportMethod(Sounds.WATERFALL, sampleId);
                        } else if (sampleId == Sounds.soundIdList[Sounds.WIND]) {
                            supportMethod(Sounds.WIND, sampleId);
                        } else {
                            Log.v(TAG, "sound id did not match");
                        }
                    }
                }
            });
            setNewState(PlaybackStateCompat.STATE_STOPPED);
        }
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPlaybackInfoListener.onPlaybackCompleted();

//                    setNewState(PlaybackStateCompat.STATE_PAUSED);
                }
            });
        }
    }

    // Implements PlaybackControl.
    @Override
    public int loadAudio(Context context, int audioResourceId) {
        Log.v(TAG, "loadAudio called");
        initializeSoundPool();
        return mSoundPool.load(context, audioResourceId, 1);
    }

    @Override
    public void pauseAudio(int streamId) {
        Log.v(TAG, "pauseAudio called");
        mSoundPool.pause(streamId);
    }

    @Override
    public int playAudio(int soundId) {
        Log.v(TAG, "playAudio called");
        setNewState(PlaybackStateCompat.STATE_PLAYING);
        return mSoundPool.play(soundId, 1, 1, 0, -1, 1);
    }

    @Override
    public void resumeAudio(int streamId) {
        Log.v(TAG, "resumeAudio called");
        mSoundPool.resume(streamId);
    }

    @Override
    public void stopAudio(int streamId) {
        Log.v(TAG, "stopAudio called");
        mSoundPool.stop(streamId);
    }

    @Override
    public SoundPool getPlayer() {
        Log.v(TAG, "getPlayer called");
        return mSoundPool;
    }

    private void release() {
        Log.v(TAG, "release called");
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    @Override
    public boolean isPlaying() {
        Log.v(TAG, "isPlaying called");
        return mSoundPool != null;
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause called");
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemPlaying()) {
                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                pauseAudio(Sounds.streamIdList[i]);
                PreferenceUtilities.decrementSoundsPlayingCount(mContext);
            }
        }
        Sounds.allPaused = true;
        setNewState(PlaybackStateCompat.STATE_PAUSED);
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume called");
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected() &&
                    (!CommonFragment.soundItemsList.get(i).isItemPlaying())) {
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                resumeAudio(Sounds.streamIdList[i]);
                PreferenceUtilities.incrementSoundsPlayingCount(mContext);
            }
        }
        Sounds.allPaused = false;
        setNewState(PlaybackStateCompat.STATE_PLAYING);
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop called");
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                stopAudio(Sounds.streamIdList[i]);
                if (CommonFragment.soundItemsList.get(i).isItemPlaying())
                    PreferenceUtilities.decrementSoundsPlayingCount(mContext);
                PreferenceUtilities.decrementSoundsSelectedCount(mContext);
                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                CommonFragment.soundItemsList.get(i).setItemSelected(false);
                CommonFragment.soundItemsList.get(i).setItemLoaded(false);
                Sounds.selectedSoundsList.remove(CommonFragment.soundItemsList.get(i));
            }
        }
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        Log.v(TAG, "setNewState called");
        mState = newPlayerState;

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                1.0f);
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        Log.v(TAG, "getAvailableActions called");
        long actions = 0L;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= 0L;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    @Override
    public void setVolume(float volume) {
        Log.v(TAG, "setVolume called");
        if (mSoundPool != null) {
//            mSoundPool.setVolume(volume, volume);
        }
    }

    private void supportMethod(int idToSkip, int sampleId) {
        Log.v(TAG, "supportMethod called");
        SoundItem soundItem = CommonFragment.soundItemsList.get(idToSkip);
        soundItem.setItemLoaded(true);
        soundItem.setItemSelected(true);
        Sounds.streamIdList[idToSkip] = playAudio(sampleId);
        soundItem.setItemPlaying(true);

        PreferenceUtilities.incrementSoundsSelectedCount(mContext);
        PreferenceUtilities.incrementSoundsPlayingCount(mContext);
        Sounds.allPaused = false;
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (i == idToSkip) {
                continue;
            }
            if (CommonFragment.soundItemsList.get(i).isItemSelected() &&
                    (!CommonFragment.soundItemsList.get(i).isItemPlaying())) {
                resumeAudio(Sounds.streamIdList[i]);
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                PreferenceUtilities.incrementSoundsPlayingCount(mContext);
            }
        }
        CommonFragment.soundListAdapter.notifyDataSetChanged();
        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
    }

}
