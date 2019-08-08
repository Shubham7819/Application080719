package com.example.application080719;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
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
import androidx.viewpager.widget.ViewPager;

import com.example.application080719.ui.SelectedAudioAdapter;
import com.example.application080719.ui.main.CommonFragment;
import com.example.application080719.ui.main.SectionsPagerAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener
        , SharedPreferences.OnSharedPreferenceChangeListener {

    final String TAG = MainActivity.class.getSimpleName();
    BottomNavigationView bottomNavigationView;
    public MenuItem menuItemPlay;
    static BadgeDrawable audioPlayingCountBadge;
    public static SelectedAudioAdapter selectedAudioAdapter;

    BottomSheetDialog timerBottomSheetDialog;
    View selectTimeLayout, countDownLayout;
    public static BottomSheetDialog selectionBottomSheetDialog;

    TextView countDownTV;
    Button stopCountDownBtn;
    CountDownTimer countDownTimer;
    boolean mTimerRunning, isBind;
    long timeLeftInMillis;
    public static PlayerService playerService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        setUpBadge();
        updateBadgeNumber();

        // Setting label visibility type for BottomNavigationView
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        menuItemPlay = bottomNavigationView.getMenu().getItem(0);
        updatePlaybackBtn();

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "OnServiceConnected called.");
            // get the local service instance
            PlayerService.LocalService localService = (PlayerService.LocalService) service;
            playerService = localService.getService();
            setListenerForSoundPool();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected called.");
            playerService = null;
            isBind = false;
        }
    };

    //COMPLETED start and bind with service on activity start if service not exists
    //COMPLETED bind with service on activity start if service already exists
    @Override
    protected void onStart() {
        super.onStart();

        // Initialize SoundPool Object.
        startServiceIfNotAlready();
        isBind = bindService(new Intent(this, PlayerService.class), serviceConnection, 0);

        // ***** Selection MenuItem of BottomNavigationView *****
        selectionBottomSheetDialog = new BottomSheetDialog(this);
        View selectionSheetView = getLayoutInflater().inflate(R.layout.selection_bottom_sheet, null);

        selectionBottomSheetDialog.setContentView(selectionSheetView);
        selectionBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                CommonFragment.soundListAdapter.notifyDataSetChanged();
            }

        });

        ImageButton selectionSheetCloseBtn = selectionSheetView.findViewById(R.id.selection_sheet_close_btn);
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
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
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
                                    Toast.makeText(MainActivity.this, "Please select valid time.", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    timerHour = (selectedHour - currentHour) - 1;
                                }
                                timerMinutes = 60 - currentMinutes + selectedMinute;
                                timerMinutes += (timerHour * 60);
                                if (selectedMinute == currentMinutes) {
                                    timerMinutes = 60;
                                    if (selectedHour - currentHour == 0) {
                                        Toast.makeText(MainActivity.this, "Please select valid time.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                            timeLeftInMillis = timerMinutes * 60 * 1000;
                            startTimer();
                            showCountDownLayout();
                        } else {
                            Toast.makeText(MainActivity.this, "Please select valid time.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_play) {
                    String menuTitle = menuItem.getTitle().toString();
                    if (getString(R.string.play).equals(menuTitle)) {
                        if (PreferenceUtilities.getSoundsSelectedCount(MainActivity.this) > 0) {
                            for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
                                if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                                    CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                                    MainActivity.playerService.resumeAudio(Sounds.streamIdList[i]);
                                    PreferenceUtilities.incrementSoundsPlayingCount(MainActivity.this);
                                }
                            }
                            menuItem.setTitle(R.string.pause);
                            menuItem.setIcon(R.drawable.ic_pause);
                            Sounds.allPaused = false;
                        } else
                            Toast.makeText(MainActivity.this, "Select some sounds first.", Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.playerService.getPlayer().autoPause();
                        menuItem.setTitle(R.string.play);
                        menuItem.setIcon(R.drawable.ic_play);
                        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
                            if (CommonFragment.soundItemsList.get(i).isItemPlaying()) {
                                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                                PreferenceUtilities.decrementSoundsPlayingCount(MainActivity.this);
                            }
                        }
                        Sounds.allPaused = true;
                    }
                    CommonFragment.soundListAdapter.notifyDataSetChanged();
                } else if (menuItem.getItemId() == R.id.action_selecction) {
                    if (PreferenceUtilities.getSoundsSelectedCount(MainActivity.this) > 0) {
                        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
                        selectionBottomSheetDialog.show();
                    } else
                        Toast.makeText(MainActivity.this, "Select some sounds first.", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.action_timer) {
                    timerBottomSheetDialog.show();
                }
                return true;
            }
        });


    }

    //COMPLETED stop and unbind service on activity destroy if no sound is selected
    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy called...");

        super.onDestroy();

        if (PreferenceUtilities.getSoundsSelectedCount(this) == 0) {
            if (isBind)
                unbindService(serviceConnection);
            stopService(new Intent(this, PlayerService.class));
        }

        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            if (sampleId == Sounds.soundIdList[Sounds.BELLS]) {
                supportMethod(soundPool, Sounds.BELLS, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.BIRD]) {
                supportMethod(soundPool, Sounds.BIRD, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.CLOCK_CHIMES]) {
                supportMethod(soundPool, Sounds.CLOCK_CHIMES, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.FARM]) {
                supportMethod(soundPool, Sounds.FARM, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.FIRE]) {
                supportMethod(soundPool, Sounds.FIRE, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.FLUTE]) {
                supportMethod(soundPool, Sounds.FLUTE, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.MUSIC_BOX]) {
                supportMethod(soundPool, Sounds.MUSIC_BOX, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.NIGHT]) {
                supportMethod(soundPool, Sounds.NIGHT, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.RAIN]) {
                supportMethod(soundPool, Sounds.RAIN, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.RAINFOREST]) {
                supportMethod(soundPool, Sounds.RAINFOREST, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.RIVER]) {
                supportMethod(soundPool, Sounds.RIVER, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.SEA]) {
                supportMethod(soundPool, Sounds.SEA, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.THUNDER]) {
                supportMethod(soundPool, Sounds.THUNDER, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.WATERFALL]) {
                supportMethod(soundPool, Sounds.WATERFALL, sampleId);
            } else if (sampleId == Sounds.soundIdList[Sounds.WIND]) {
                supportMethod(soundPool, Sounds.WIND, sampleId);
            } else {
                Log.v(TAG, "sound id did not match");
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_SOUNDS_SELECTED_COUNTER.equals(key)) {
            updateBadgeNumber();
        } else if (PreferenceUtilities.KEY_SOUNDS_PLAYING_COUNTER.equals(key)) {
            updatePlaybackBtn();
        }
    }

    void supportMethod(SoundPool soundPool, int idToSkip, int sampleId) {
        SoundItem soundItem = CommonFragment.soundItemsList.get(idToSkip);
        soundItem.setItemLoaded(true);
        soundItem.setItemSelected(true);
        Sounds.streamIdList[Sounds.WIND] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
        soundItem.setItemPlaying(true);

        PreferenceUtilities.incrementSoundsSelectedCount(this);
        PreferenceUtilities.incrementSoundsPlayingCount(this);
        Sounds.allPaused = false;
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (i == idToSkip) {
                continue;
            }
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                MainActivity.playerService.resumeAudio(Sounds.streamIdList[i]);
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                PreferenceUtilities.incrementSoundsPlayingCount(this);
            }
        }
        CommonFragment.soundListAdapter.notifyDataSetChanged();
        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
    }

    //COMPLETED 4) create method to update playback button of bottom nav by checking sharedPref
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
        audioPlayingCountBadge = bottomNavigationView.getOrCreateBadge(selectionMenuItemId);
        audioPlayingCountBadge.setBadgeTextColor(getResources().getColor(R.color.colorAccent));
        audioPlayingCountBadge.setBackgroundColor(getResources().getColor(R.color.colorTitle));
        audioPlayingCountBadge.setVisible(false);
    }

    public void updateBadgeNumber() {
        int selectedSoundsCount = PreferenceUtilities.getSoundsSelectedCount(MainActivity.this);
        audioPlayingCountBadge.setNumber(selectedSoundsCount);
        if (selectedSoundsCount > 0) {
            audioPlayingCountBadge.setVisible(true);
        } else {
            audioPlayingCountBadge.setVisible(false);
        }
    }

    private void setListenerForSoundPool() {
        MainActivity.playerService.getPlayer().setOnLoadCompleteListener(this);
    }

    void stopAll() {
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                MainActivity.playerService.stopAudio(Sounds.streamIdList[i]);
                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                CommonFragment.soundItemsList.get(i).setItemSelected(false);
                PreferenceUtilities.decrementSoundsPlayingCount(this);
                PreferenceUtilities.decrementSoundsSelectedCount(this);
                Sounds.selectedSoundsList.remove(CommonFragment.soundItemsList.get(i));
            }
        }
        if (isBind) {
            unbindService(serviceConnection);
            isBind = false;
        }
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

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                countDownTV.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "Timer End !", Toast.LENGTH_SHORT).show();
                stopAll();
                showSetTimerLayout();
                timerBottomSheetDialog.dismiss();
            }
        }.start();
        mTimerRunning = true;
    }

    void startServiceIfNotAlready() {
        if (!PreferenceUtilities.isServiceAvailable(this)) {
            Intent intent = new Intent(this, PlayerService.class);
            startService(intent);
            PreferenceUtilities.setServiceAvailability(this, true);
        }
    }

}