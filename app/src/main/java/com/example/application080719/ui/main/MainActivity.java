package com.example.application080719.ui.main;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.application080719.MediaBrowserHelper;
import com.example.application080719.PlayerService;
import com.example.application080719.PreferenceUtilities;
import com.example.application080719.R;
import com.example.application080719.dto.Sounds;
import com.example.application080719.ui.adapters.SectionsPagerAdapter;
import com.example.application080719.ui.adapters.SelectedAudioAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    final String TAG = MainActivity.class.getSimpleName();
    BottomNavigationView bottomNavigationView;
    public MenuItem menuItemPlay;
    static BadgeDrawable audioSelectedCountBadge;
    public static SelectedAudioAdapter selectedAudioAdapter;

    BottomSheetDialog timerBottomSheetDialog;
    View selectTimeLayout, countDownLayout;
    public static BottomSheetDialog selectionBottomSheetDialog;

    TextView countDownTV;
    Button stopCountDownBtn;
    CountDownTimer countDownTimer;
    boolean mTimerRunning;
    long timeLeftInMillis;
    public static PlayerService playerService;

    private MediaBrowserHelper mMediaBrowserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate called...");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this
                , getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        setUpBadge();
        updateSelectionBadgeNumber();

        // Setting label visibility type for BottomNavigationView
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        menuItemPlay = bottomNavigationView.getMenu().getItem(0);
        updatePlaybackBtn();

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart called...");
        mMediaBrowserHelper.onStart();

        // ***** Selection MenuItem of BottomNavigationView *****
        selectionBottomSheetDialog = new BottomSheetDialog(this);
        View selectionSheetView = getLayoutInflater().inflate(R.layout.selection_bottom_sheet
                , null);

        selectionBottomSheetDialog.setContentView(selectionSheetView);
        selectionBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CommonFragment.soundListAdapter.notifyDataSetChanged();
            }

        });

        ImageButton selectionSheetCloseBtn = selectionSheetView
                .findViewById(R.id.selection_sheet_close_btn);
        selectionSheetCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFragment.soundListAdapter.notifyDataSetChanged();
                selectionBottomSheetDialog.dismiss();
            }
        });

        Button clearAllBtn = selectionSheetView.findViewById(R.id.clear_all_btn);
        clearAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAll();
                selectionBottomSheetDialog.dismiss();
            }
        });
        //TODO 9) create method to update selectedSoundsList when activity starts by checking sharedPref
        ListView currentPlayingList = selectionSheetView.findViewById(R.id.current_playing_list);
        selectedAudioAdapter = new SelectedAudioAdapter(this, Sounds.selectedSoundsList);
        currentPlayingList.setAdapter(selectedAudioAdapter);

        //TODO on returning back to activity after activity change timer UI disappears but countdown continues.
        // ***** Timer MenuItem of BottomNavigationView *****
        timerBottomSheetDialog = new BottomSheetDialog(this);
        View timerSheetView = getLayoutInflater().inflate(R.layout.timer_bottom_sheet, null);
        timerBottomSheetDialog.setContentView(timerSheetView);

        ImageButton timerSheetCloseBtn = timerSheetView.findViewById(R.id.timer_sheet_close_btn);
        timerSheetCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerBottomSheetDialog.dismiss();
            }
        });

        selectTimeLayout = timerSheetView.findViewById(R.id.select_time_layout);
        countDownLayout = timerSheetView.findViewById(R.id.count_down_layout);
        countDownTV = timerSheetView.findViewById(R.id.count_down_tv);
        stopCountDownBtn = timerSheetView.findViewById(R.id.stop_timer_btn);

        Spinner spinner = timerSheetView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position
                    , long id) {
                if (position == 1) {
                    timeLeftInMillis = 15 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                } else if (position == 2) {
                    timeLeftInMillis = 30 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                } else if (position == 3) {
                    timeLeftInMillis = 45 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                } else if (position == 4) {
                    timeLeftInMillis = 60 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                } else if (position == 5) {
                    timeLeftInMillis = 90 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                } else if (position == 6) {
                    timeLeftInMillis = 2 * 60 * 60 * 1000;
                    startTimer();
                    showCountDownLayout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stopCountDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                showSetTimerLayout();
            }
        });

        LinearLayout timePickerBtn = timerSheetView.findViewById(R.id.time_picker_btn);
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour
                            , int selectedMinute) {
                        if (selectedHour >= currentHour) {
                            int timerHour;
                            int timerMinutes;
                            if (selectedMinute > currentMinutes) {
                                timerHour = selectedHour - currentHour;
                                timerMinutes = selectedMinute - currentMinutes;
                                timerMinutes += (timerHour * 60);
                            } else {
                                if (selectedHour - currentHour > 1) {
                                    timerHour = (selectedHour - currentHour) - 1;
                                } else if (selectedHour - currentHour < 1) {
                                    Toast.makeText(MainActivity.this
                                            , "Please select valid time."
                                            , Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    timerHour = (selectedHour - currentHour) - 1;
                                }
                                timerMinutes = 60 - currentMinutes + selectedMinute;
                                timerMinutes += (timerHour * 60);
                                if (selectedMinute == currentMinutes) {
                                    timerMinutes = 60;
                                    if (selectedHour - currentHour == 0) {
                                        Toast.makeText(MainActivity.this
                                                , "Please select valid time."
                                                , Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                            timeLeftInMillis = timerMinutes * 60 * 1000;
                            startTimer();
                            showCountDownLayout();
                        } else {
                            Toast.makeText(MainActivity.this
                                    , "Please select valid time.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_play) {
                            String menuTitle = menuItem.getTitle().toString();
                            if (getString(R.string.play).equals(menuTitle)) {
                                if (PreferenceUtilities.getSoundsSelectedCount(
                                        MainActivity.this) > 0) {
                                    mMediaBrowserHelper.resumeAll();
                                } else
                                    Toast.makeText(MainActivity.this
                                            , "Select some sounds first.", Toast.LENGTH_SHORT).show();
                            } else {
                                mMediaBrowserHelper.pauseAll();
                            }
                            CommonFragment.soundListAdapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.action_selecction) {
                            if (PreferenceUtilities.getSoundsSelectedCount(MainActivity.this) > 0) {
                                MainActivity.selectedAudioAdapter.notifyDataSetChanged();
                                selectionBottomSheetDialog.show();
                            } else
                                Toast.makeText(MainActivity.this, "Select some sounds first."
                                        , Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.action_timer) {
                            timerBottomSheetDialog.show();
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop called...");
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy called...");

        super.onDestroy();
        mMediaBrowserHelper.onStop();

        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_SOUNDS_SELECTED_COUNTER.equals(key)) {
            updateSelectionBadgeNumber();
        } else if (PreferenceUtilities.KEY_SOUNDS_PLAYING_COUNTER.equals(key)) {
            updatePlaybackBtn();
        }
    }

    // updates playback button of bottom nav by checking sharedPref
    public void updatePlaybackBtn() {
        if (PreferenceUtilities.getSoundsPlayingCount(this) > 0) {
            menuItemPlay.setTitle(R.string.pause);
            menuItemPlay.setIcon(R.drawable.ic_pause);
        } else {
            menuItemPlay.setTitle(R.string.play);
            menuItemPlay.setIcon(R.drawable.ic_play);
        }
    }

    // Setting Badge for BottomNavigationView's Selection MenuItem
    void setUpBadge() {
        int selectionMenuItemId = bottomNavigationView.getMenu().getItem(2).getItemId();
        audioSelectedCountBadge = bottomNavigationView.getOrCreateBadge(selectionMenuItemId);
        audioSelectedCountBadge.setBadgeTextColor(getResources().getColor(R.color.colorAccent));
        audioSelectedCountBadge.setBackgroundColor(getResources().getColor(R.color.colorTitle));
        audioSelectedCountBadge.setVisible(false);
    }

    public void updateSelectionBadgeNumber() {
        int selectedSoundsCount = PreferenceUtilities.getSoundsSelectedCount(MainActivity.this);
        audioSelectedCountBadge.setNumber(selectedSoundsCount);
        if (selectedSoundsCount > 0) {
            audioSelectedCountBadge.setVisible(true);
        } else {
            audioSelectedCountBadge.setVisible(false);
        }
    }

    /**
     * Customize the connection to our {@link androidx.media.MediaBrowserServiceCompat}
     * and implement our app specific desires.
     */
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

//        @Override
//        protected void onChildrenLoaded(@NonNull String parentId,
//                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
//            Log.d(TAG, " MediaBrowserConnection: onChildrenLoaded called ");
//            super.onChildrenLoaded(parentId, children);
//
//            final MediaControllerCompat mediaController = getMediaController();
//
//            // Queue up all media items for this simple sample.
//            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
//                mediaController.addQueueItem(mediaItem.getDescription());
//            }
//
//            // Call prepare now so pressing play just works.
//            mediaController.getTransportControls().prepare();
//        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
//            mIsPlaying = playbackState != null &&
//                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
//            mMediaControlsImage.setPressed(mIsPlaying);
            CommonFragment.soundListAdapter.notifyDataSetChanged();
        }

//        @Override
//        public void onSessionDestroyed() {
//            super.onSessionDestroyed();
//        }

    }

    void stopAll() {
        mMediaBrowserHelper.getTransportControls().stop();
        CommonFragment.soundListAdapter.notifyDataSetChanged();
        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
    }

    void showCountDownLayout() {
        selectTimeLayout.setVisibility(View.GONE);
        countDownLayout.setVisibility(View.VISIBLE);
    }

    void showSetTimerLayout() {
        selectTimeLayout.setVisibility(View.VISIBLE);
        countDownLayout.setVisibility(View.GONE);
    }

    void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d"
                        , minutes, seconds);
                countDownTV.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Timer End !", Toast.LENGTH_SHORT)
                        .show();
                stopAll();
                showSetTimerLayout();
                timerBottomSheetDialog.dismiss();
            }
        }.start();
        mTimerRunning = true;
    }

}