package com.example.application080719.ui.main;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application080719.GetDataService;
import com.example.application080719.MediaBrowserHelper;
import com.example.application080719.PlayerService;
import com.example.application080719.R;
import com.example.application080719.RetrofitClientInstance;
import com.example.application080719.ui.MediaSeekBar;
import com.example.application080719.ui.adapters.ExerciseListAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeditationActivity extends AppCompatActivity {

    private static final String TAG = MeditationActivity.class.getSimpleName();
    private boolean mIsPlaying;
    private ImageView mMediaControlsImage;
    MediaSeekBar seekBar;
    TextView timeCompleted, timeLeft;
    int audioResourceId, max;
    private MediaBrowserHelper mMediaBrowserHelper;
    String exerciseName, exerciseGuides;

    // TODO: use secondary indicator of SeekBar for download progress
    // TODO: add logic to check already downloaded file
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        Log.v(TAG, "onCreate called...");

        TextView exerciseNameTV = findViewById(R.id.exercise_title_tv);
        exerciseName = getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_NAME);
        exerciseNameTV.setText(exerciseName);

        TextView exerciseGuidesTV = findViewById(R.id.exercise_guides_tv);
        exerciseGuides = getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_GUIDES);
        exerciseGuidesTV.setText(exerciseGuides);

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

        GetDataService mService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = mService.getAudioById("download", "1DLEC3blXfd3HKxuLtjmWgWJAfkrdah7X");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
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
        mMediaControlsImage.setPressed(mIsPlaying);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop called...");
        seekBar.disconnectController();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy called...");
        mMediaBrowserHelper.onStop();
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

    private ValueAnimator mProgressAnimator;

    private class MediaBrowserListener extends MediaControllerCompat.Callback
            implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            mMediaControlsImage.setPressed(mIsPlaying);

            if (mProgressAnimator != null) {
                mProgressAnimator.cancel();
                mProgressAnimator = null;
            }

            final int progress = playbackState != null
                    ? (int) playbackState.getPosition()
                    : 0;

            timeCompleted.setText(formatTime(progress));

            if (playbackState != null && playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                final int timeToEnd = (int) ((max - progress) / playbackState.getPlaybackSpeed());

                mProgressAnimator = ValueAnimator.ofInt(progress, max)
                        .setDuration(timeToEnd);
                mProgressAnimator.setInterpolator(new LinearInterpolator());
                mProgressAnimator.addUpdateListener(this);
                mProgressAnimator.start();
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {

            max = metadata != null
                    ? (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                    : 0;

            timeLeft.setText(formatTime(max));
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (!mIsPlaying) {
                valueAnimator.cancel();
                return;
            }

            final int animatedIntValue = (int) valueAnimator.getAnimatedValue();
            timeCompleted.setText(formatTime(animatedIntValue));
        }
    }

    private String formatTime(int timeInMS) {
        int minutes = (timeInMS / 1000) / 60;
        int seconds = (timeInMS / 1000) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d"
                , minutes, seconds);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(getExternalFilesDir(null)
                    + File.separator + exerciseName + " - " + exerciseGuides + ".png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
