package com.example.application080719.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.application080719.MediaBrowserHelper;
import com.example.application080719.PlayerService;
import com.example.application080719.R;
import com.example.application080719.ui.MediaSeekBar;
import com.example.application080719.ui.adapters.ExerciseListAdapter;

public class MeditationActivity extends AppCompatActivity {

    private static final String TAG = MeditationActivity.class.getSimpleName();
    private boolean mIsPlaying;
    private ImageView mMediaControlsImage;
    MediaSeekBar seekBar;
    TextView timeCompleted;
    TextView timeLeft;
    int soundId;
    int audioResourceId;
    private MediaBrowserHelper mMediaBrowserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        Log.v(TAG, "onCreate called...");

        TextView exerciseNameTV = findViewById(R.id.exercise_title_tv);
        exerciseNameTV.setText(getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_NAME));

        TextView exerciseGuidesTV = findViewById(R.id.exercise_guides_tv);
        exerciseGuidesTV.setText(getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_GUIDES));

        timeCompleted = findViewById(R.id.playing_time_completed_tv);
        timeLeft = findViewById(R.id.playing_time_left_tv);

        audioResourceId = getIntent().getIntExtra(
                ExerciseListAdapter.EXERCISE_RESOURCE_ID, 0);

        seekBar = findViewById(R.id.media_seekbar);

        mMediaControlsImage = findViewById(R.id.media_control_iv);
        mMediaControlsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsPlaying) {
                    mMediaBrowserHelper.getTransportControls().pause();
                } else {
                    mMediaBrowserHelper.getTransportControls().playFromMediaId(
                            String.valueOf(audioResourceId), null);
                }
            }
        });

        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart called...");
        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop called...");
        seekBar.disconnectController();
        mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy called...");
    }

    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, PlayerService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
            Log.d(TAG, " MediaBrowserConnection: onConnected called ");
            mediaController.getTransportControls().prepareFromMediaId(
                    String.valueOf(audioResourceId), null);
            seekBar.setMediaController(mediaController);
        }

    }

    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            mMediaControlsImage.setPressed(mIsPlaying);
        }
    }

}
