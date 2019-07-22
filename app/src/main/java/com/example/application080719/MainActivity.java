package com.example.application080719;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.application080719.ui.SelectedAudioAdapter;
import com.example.application080719.ui.SoundListAdapter;
import com.example.application080719.ui.main.CommonFragment;
import com.example.application080719.ui.main.SectionsPagerAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import static com.example.application080719.Sounds.BELLS;
import static com.example.application080719.Sounds.soundIdList;
import static com.example.application080719.Sounds.soundPool;
import static com.example.application080719.Sounds.streamIdList;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    final String TAG = MainActivity.class.getSimpleName();
    public static MenuItem menuItemPlay;
    static BadgeDrawable audioPlayingCountBadge;
    public static SelectedAudioAdapter selectedAudioAdapter;

    //TODO:
    // - - for selection MenuItem show a list of playing sound with pause and remove button in bottom sheet
    // - 2] in timer item use dialog to pick time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(this);

        selectedAudioAdapter = new SelectedAudioAdapter(this, Sounds.selectedSoundsList);

        BottomSheetDialog selectionBottomSheetDialog = new BottomSheetDialog(this);
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
        currentPlayingList.setAdapter(selectedAudioAdapter);

//        LinearLayout timerBottomSheetLayout = findViewById(R.id.timer_bottom_sheet);
//        BottomSheetBehavior timerBottomSheetBehavior = BottomSheetBehavior.from(timerBottomSheetLayout);
        BottomSheetDialog timerBottomSheetDialog = new BottomSheetDialog(this);
        View timerSheetView = getLayoutInflater().inflate(R.layout.timer_bottom_sheet, null);
        timerBottomSheetDialog.setContentView(timerSheetView);
//        timerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        timerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ImageButton timerSheetCloseBtn = timerSheetView.findViewById(R.id.timer_sheet_close_btn);
        timerSheetCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                timerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                timerBottomSheetDialog.dismiss();
            }
        });
        LinearLayout timePickerBtn = timerSheetView.findViewById(R.id.time_picker_btn);
        TextView hourTV = timerSheetView.findViewById(R.id.hours_count);
        TextView minTV = timerSheetView.findViewById(R.id.minutes_count);
        TextView meridianTV = timerSheetView.findViewById(R.id.meridian_tv);

        Calendar calendar = Calendar.getInstance();
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        if (hourOfDay > 12) {
                            hourTV.setText(String.valueOf(hourOfDay - 12));
                            meridianTV.setText("PM");
                        } else {
                            hourTV.setText(String.valueOf(hourOfDay));
                            meridianTV.setText("AM");
                        }
                        minTV.setText(String.valueOf(minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        int selectionMenuItemId = bottomNavigationView.getMenu().getItem(2).getItemId();
        audioPlayingCountBadge = bottomNavigationView.getOrCreateBadge(selectionMenuItemId);
        audioPlayingCountBadge.setBadgeTextColor(getResources().getColor(R.color.colorAccent));
        audioPlayingCountBadge.setBackgroundColor(getResources().getColor(R.color.colorTitle));
        audioPlayingCountBadge.setVisible(false);

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuItemPlay = bottomNavigationView.getMenu().getItem(0);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_play) {
                    String menuTitle = menuItem.getTitle().toString();
                    if (getString(R.string.play).equals(menuTitle)) {
                        if (Sounds.soundsPlayingCounter > 0) {
                            for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
                                if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                                    CommonFragment.soundItemsList.get(i).setItemPlaying(true);
                                    soundPool.resume(streamIdList[i]);
                                }
                            }
//                            soundPool.autoResume();
                            menuItem.setTitle("Pause");
                            menuItem.setIcon(R.drawable.ic_pause);
                            Sounds.allPaused = false;
                            CommonFragment.soundListAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(MainActivity.this, "Select some sounds first.", Toast.LENGTH_SHORT).show();
                    } else {
                        soundPool.autoPause();
                        menuItem.setTitle(R.string.play);
                        menuItem.setIcon(R.drawable.ic_play);
                        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
                            if (CommonFragment.soundItemsList.get(i).isItemPlaying()) {
                                CommonFragment.soundItemsList.get(i).setItemPlaying(false);
                            }
                        }
                        Sounds.allPaused = true;
                        CommonFragment.soundListAdapter.notifyDataSetChanged();
                    }
                } else if (menuItem.getItemId() == R.id.action_selecction) {
                    if (Sounds.soundsPlayingCounter > 0) {
                        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
//                        selectionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        selectionBottomSheetDialog.show();
                    } else
                        Toast.makeText(MainActivity.this, "Select some sounds first.", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.action_timer) {
//                    timerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    timerBottomSheetDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy called...");
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            if (sampleId == soundIdList[Sounds.BELLS]) {
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemLoaded(true);
                Sounds.streamIdList[Sounds.BELLS] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.BELLS).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.BIRD]) {
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemLoaded(true);
                Sounds.streamIdList[Sounds.BIRD] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.BIRD).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.CLOCK_CHIMES]) {
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemLoaded(true);
                Sounds.streamIdList[Sounds.CLOCK_CHIMES] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.CLOCK_CHIMES).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.FARM]) {
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FARM] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FARM).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.FIRE]) {
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FIRE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FIRE).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.FLUTE]) {
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemLoaded(true);
                Sounds.streamIdList[Sounds.FLUTE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.FLUTE).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.MUSIC_BOX]) {
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemLoaded(true);
                Sounds.streamIdList[Sounds.MUSIC_BOX] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.MUSIC_BOX).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.NIGHT]) {
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemLoaded(true);
                Sounds.streamIdList[Sounds.NIGHT] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.NIGHT).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.RAIN]) {
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RAIN] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RAIN).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.RAINFOREST]) {
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RAINFOREST] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RAINFOREST).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.RIVER]) {
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemLoaded(true);
                Sounds.streamIdList[Sounds.RIVER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.RIVER).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.SEA]) {
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemLoaded(true);
                Sounds.streamIdList[Sounds.SEA] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.SEA).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.THUNDER]) {
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemLoaded(true);
                Sounds.streamIdList[Sounds.THUNDER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.THUNDER).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.WATERFALL]) {
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemLoaded(true);
                Sounds.streamIdList[Sounds.WATERFALL] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemPlaying(true);
                CommonFragment.soundItemsList.get(Sounds.WATERFALL).setItemSelected(true);
                supportMethod();
            } else if (sampleId == soundIdList[Sounds.WIND]) {
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
        Sounds.soundsPlayingCounter++;
        menuItemPlay.setTitle("Pause");
        menuItemPlay.setIcon(R.drawable.ic_pause);
        Sounds.allPaused = false;
        updateBadgeNumber(Sounds.soundsPlayingCounter);
        for (int i = Sounds.BELLS; i <= Sounds.WIND; i++) {
            if (CommonFragment.soundItemsList.get(i).isItemSelected()) {
                CommonFragment.soundItemsList.get(i).setItemPlaying(true);
            }
        }
        CommonFragment.soundListAdapter.notifyDataSetChanged();
        MainActivity.selectedAudioAdapter.notifyDataSetChanged();
    }

    public static void updateBadgeNumber(int number) {
        audioPlayingCountBadge.setNumber(number);
        if (number > 0) {
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
                Sounds.soundsPlayingCounter--;
                Sounds.selectedSoundsList.remove(CommonFragment.soundItemsList.get(i));
                CommonFragment.soundItemsList.get(i).setItemSelected(false);
            }
            MainActivity.updateBadgeNumber(Sounds.soundsPlayingCounter);
            MainActivity.menuItemPlay.setTitle(R.string.play);
            MainActivity.menuItemPlay.setIcon(R.drawable.ic_play);
            CommonFragment.soundListAdapter.notifyDataSetChanged();
            MainActivity.selectedAudioAdapter.notifyDataSetChanged();
        }
    }
}