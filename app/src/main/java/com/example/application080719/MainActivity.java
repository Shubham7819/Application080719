package com.example.application080719;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    public static MenuItem menuItemPlay;
    static BadgeDrawable audioPlayingCountBadge;
    public static SelectedAudioAdapter selectedAudioAdapter;

    BottomSheetDialog timerBottomSheetDialog;
    View selectTimeLayout;
    View countDownLayout;
    public static BottomSheetDialog selectionBottomSheetDialog;

    TextView countDownTV;
    Button stopCountDownBtn;
    CountDownTimer countDownTimer;
    boolean mTimerRunning;
    long timeLeftInMillis;

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel playingAudioChannel = new NotificationChannel(getString(R.string.app_name)
                    , "Current Playing", NotificationManager.IMPORTANCE_DEFAULT);
            playingAudioChannel.setLightColor(Color.GREEN);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(playingAudioChannel);
        }

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Initialize SoundPool Object.
        Sounds.soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        Sounds.soundPool.setOnLoadCompleteListener(this);

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
        TextView hourTV = timerSheetView.findViewById(R.id.hours_count);
        TextView minTV = timerSheetView.findViewById(R.id.minutes_count);
        TextView meridianTV = timerSheetView.findViewById(R.id.meridian_tv);

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
                                timerHour = selectedHour - currentHour;//(selectedHour - currentHour == 0) ? 1 : (selectedHour - currentHour);
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
                                    Sounds.soundPool.resume(Sounds.streamIdList[i]);
                                    PreferenceUtilities.incrementSoundsPlayingCount(MainActivity.this);
                                }
                            }
                            menuItem.setTitle(R.string.pause);
                            menuItem.setIcon(R.drawable.ic_pause);
                            Sounds.allPaused = false;
                        } else
                            Toast.makeText(MainActivity.this, "Select some sounds first.", Toast.LENGTH_SHORT).show();
                    } else {
                        Sounds.soundPool.autoPause();
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

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy called...");

        if (Sounds.soundPool != null) {
            Sounds.soundPool.release();
            Sounds.soundPool = null;
        }
        super.onDestroy();

        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            if (sampleId == Sounds.soundIdList[Sounds.BELLS]) {
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemLoaded(true);
                Sounds.streamIdList[Sounds.BELLS] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.BIRD]) {
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemLoaded(true);
                Sounds.streamIdList[Sounds.BIRD] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.CLOCK_CHIMES]) {
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemLoaded(true);
                Sounds.streamIdList[Sounds.CLOCK_CHIMES] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.FARM]) {
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FARM] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.FIRE]) {
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FIRE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.FLUTE]) {
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FLUTE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.MUSIC_BOX]) {
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemLoaded(true);
                Sounds.streamIdList[Sounds.MUSIC_BOX] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.NIGHT]) {
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemLoaded(true);
                Sounds.streamIdList[Sounds.NIGHT] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.RAIN]) {
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RAIN] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.RAINFOREST]) {
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RAINFOREST] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.RIVER]) {
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RIVER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.SEA]) {
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemLoaded(true);
                Sounds.streamIdList[Sounds.SEA] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.THUNDER]) {
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemLoaded(true);
                Sounds.streamIdList[Sounds.THUNDER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.WATERFALL]) {
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemLoaded(true);
                Sounds.streamIdList[Sounds.WATERFALL] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemSelected(true);
                supportMethod();
            } else if (sampleId == Sounds.soundIdList[Sounds.WIND]) {
                CommonFragment.soundItemsList.get(Sounds.WIND).setItemLoaded(true);
                CommonFragment.soundItemsList.get(Sounds.WIND).setItemSelected(true);
                Sounds.streamIdList[Sounds.WIND] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.WIND).setItemPlaying(true);
                supportMethod();
            } else {
                Log.v(TAG, "sound id did not match");
            }
        }
    }

    void supportMethod() {
        PreferenceUtilities.incrementSoundsSelectedCount(this);
        PreferenceUtilities.incrementSoundsPlayingCount(this);
        menuItemPlay.setTitle("Pause");
        menuItemPlay.setIcon(R.drawable.ic_pause);
        Sounds.allPaused = false;
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                Sounds.soundPool.resume(Sounds.streamIdList[i]);
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                PreferenceUtilities.incrementSoundsPlayingCount(this);
            }
        }
        CommonFragment.soundListAdapter.notifyDataSetChanged();
        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
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

    void stopAll() {
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                Sounds.soundPool.stop(Sounds.streamIdList[i]);
                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                CommonFragment.soundItemsList.get(i).setItemSelected(false);
                PreferenceUtilities.decrementSoundsPlayingCount(this);
                PreferenceUtilities.decrementSoundsSelectedCount(this);
                Sounds.selectedSoundsList.remove(CommonFragment.soundItemsList.get(i));
            }
        }
        MainActivity.menuItemPlay.setTitle(R.string.play);
        MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
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

    void setUpBadge() {
        // Setting Badge for BottomNavigationView's Selection MenuItem
        int selectionMenuItemId = bottomNavigationView.getMenu().getItem(2).getItemId();
        audioPlayingCountBadge = bottomNavigationView.getOrCreateBadge(selectionMenuItemId);
        audioPlayingCountBadge.setBadgeTextColor(getResources().getColor(R.color.colorAccent));
        audioPlayingCountBadge.setBackgroundColor(getResources().getColor(R.color.colorTitle));
        audioPlayingCountBadge.setVisible(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtilities.KEY_SOUNDS_SELECTED_COUNTER.equals(key)) {
            updateBadgeNumber();
        }
    }
}