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
import com.example.application080719.ui.adapters.ExerciseListAdapter;

public class MeditationActivity extends AppCompatActivity {

    private static final String TAG = MeditationActivity.class.getSimpleName();
    private boolean mIsPlaying;
    private ImageView mMediaControlsImage;
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

        audioResourceId = getIntent().getIntExtra(ExerciseListAdapter.EXERCISE_RESOURCE_ID, 0);

        AppCompatSeekBar seekBar = findViewById(R.id.media_seekbar);
        mMediaControlsImage = findViewById(R.id.media_control_iv);
        mMediaControlsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaBrowserHelper.getTransportControls().playFromMediaId(String.valueOf(audioResourceId), null);
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
//        mMediaBrowserHelper.getTransportControls().prepareFromMediaId(String.valueOf(audioResourceId), null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop called...");
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
            Log.d(TAG, " MediaBrowserConnection: onConnected called "
                    + MainActivity.playerService);
        }

        @Override
        protected void onServiceReceived(@NonNull PlayerService playerService) {
            MainActivity.playerService = playerService;
            Log.d(TAG, " MediaBrowserConnection: onServiceReceived called "
                    + MainActivity.playerService);
        }
    }

    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            mMediaControlsImage.setPressed(mIsPlaying);
            CommonFragment.soundListAdapter.notifyDataSetChanged();
        }
    }

}
