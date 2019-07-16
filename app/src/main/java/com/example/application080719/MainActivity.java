package com.example.application080719;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.application080719.ui.main.CommonFragment;
import com.example.application080719.ui.main.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import static com.example.application080719.Sounds.soundIdList;
import static com.example.application080719.Sounds.soundPool;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    final String TAG = MainActivity.class.getSimpleName();
    public static MenuItem menuItemPlay;

    //TODO:
    // - 1] make tab layout scrollable and text in one line
    // - 2] implement bottom sheet for bottomnavigationbar items selection and timer
    // - 3] in timer item user dialog to pick time
    // - 4] use badge for selection item to show number of playing sounds

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
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        menuItemPlay = bottomNavigationView.getMenu().getItem(0);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_play) {
                    String menuTitle = menuItem.getTitle().toString();
                    if (getString(R.string.play).equals(menuTitle)) {
                        if (Sounds.soundsPlayingCounter > 0) {
                            soundPool.autoResume();
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
                        Sounds.isBellsSoundPlaying = false;
                        Sounds.isBirdSoundPlaying = false;
                        Sounds.isClockSoundPlaying = false;
                        Sounds.isFarmSoundPlaying = false;
                        Sounds.isFireSoundPlaying = false;
                        Sounds.isFluteSoundPlaying = false;
                        Sounds.isMusicBoxSoundPlaying = false;
                        Sounds.isNightSoundPlaying = false;
                        Sounds.isRainSoundPlaying = false;
                        Sounds.isRainForestSoundPlaying = false;
                        Sounds.isRiverSoundPlaying = false;
                        Sounds.isSeaSoundPlaying = false;
                        Sounds.isThunderSoundPlaying = false;
                        Sounds.isWaterfallSoundPlaying = false;
                        Sounds.isWindSoundPlaying = false;
                        Sounds.allPaused = true;
                        CommonFragment.soundListAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        soundPool.release();
        soundPool = null;
        super.onStop();
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            Log.v(TAG, "Sound id: " + sampleId);
            if (sampleId == soundIdList[Sounds.BELLS]) {
                Sounds.isBellsSoundLoaded = true;
                Sounds.streamIdList[Sounds.BELLS] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isBellsSoundPlaying = true;
                Sounds.isBellsSoundSelected = true;
                supportMethod();
                Log.v(TAG, "BELLSSound is loaded");
            } else if (sampleId == soundIdList[Sounds.BIRD]) {
                Sounds.isBirdSoundLoaded = true;
                Sounds.streamIdList[Sounds.BIRD] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isBirdSoundPlaying = true;
                Sounds.isBirdSoundSelected = true;
                supportMethod();
                Log.v(TAG, "BIRDSound is loaded");
            } else if (sampleId == soundIdList[Sounds.CLOCK_CHIMES]) {
                Sounds.isClockSoundLoaded = true;
                Sounds.streamIdList[Sounds.CLOCK_CHIMES] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isClockSoundPlaying = true;
                Sounds.isClockSoundSelected = true;
                supportMethod();
                Log.v(TAG, "CLOCK_CHIMESSound is loaded");
            } else if (sampleId == soundIdList[Sounds.FARM]) {
                Sounds.isFarmSoundLoaded = true;
                Sounds.streamIdList[Sounds.FARM] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFarmSoundPlaying = true;
                Sounds.isFarmSoundSelected = true;
                supportMethod();
                Log.v(TAG, "FARMSound is loaded");
            } else if (sampleId == soundIdList[Sounds.FIRE]) {
                Sounds.isFireSoundLoaded = true;
                Sounds.streamIdList[Sounds.FIRE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFireSoundPlaying = true;
                Sounds.isFireSoundSelected = true;
                supportMethod();
                Log.v(TAG, "FIRESound is loaded");
            } else if (sampleId == soundIdList[Sounds.FLUTE]) {
                Sounds.isFluteSoundLoaded = true;
                Sounds.streamIdList[Sounds.FLUTE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFluteSoundPlaying = true;
                Sounds.isFluteSoundSelected = true;
                supportMethod();
                Log.v(TAG, "FLUTESound is loaded");
            } else if (sampleId == soundIdList[Sounds.MUSIC_BOX]) {
                Sounds.isMusicBoxSoundLoaded = true;
                Sounds.streamIdList[Sounds.MUSIC_BOX] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isMusicBoxSoundPlaying = true;
                Sounds.isMusicBoxSoundSelected = true;
                supportMethod();
                Log.v(TAG, "MUSIC_BOXSound is loaded");
            } else if (sampleId == soundIdList[Sounds.NIGHT]) {
                Sounds.isNightSoundLoaded = true;
                Sounds.streamIdList[Sounds.NIGHT] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isNightSoundPlaying = true;
                Sounds.isNightSoundSelected = true;
                supportMethod();
                Log.v(TAG, "NIGHTSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RAIN]) {
                Sounds.isRainSoundLoaded = true;
                Sounds.streamIdList[Sounds.RAIN] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRainSoundPlaying = true;
                Sounds.isRainSoundSelected = true;
                supportMethod();
                Log.v(TAG, "RAINSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RAINFOREST]) {
                Sounds.isRainForestSoundLoaded = true;
                Sounds.streamIdList[Sounds.RAINFOREST] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRainForestSoundPlaying = true;
                Sounds.isRainForestSoundSelected = true;
                supportMethod();
                Log.v(TAG, "RAINFORESTSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RIVER]) {
                Sounds.isRiverSoundLoaded = true;
                Sounds.streamIdList[Sounds.RIVER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRiverSoundPlaying = true;
                Sounds.isRiverSoundSelected = true;
                supportMethod();
                Log.v(TAG, "RIVERSound is loaded");
            } else if (sampleId == soundIdList[Sounds.SEA]) {
                Sounds.isSeaSoundLoaded = true;
                Sounds.streamIdList[Sounds.SEA] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isSeaSoundPlaying = true;
                Sounds.isSeaSoundSelected = true;
                supportMethod();
                Log.v(TAG, "SEASound is loaded");
            } else if (sampleId == soundIdList[Sounds.THUNDER]) {
                Sounds.isThunderSoundLoaded = true;
                Sounds.streamIdList[Sounds.THUNDER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isThunderSoundPlaying = true;
                Sounds.isThunderSoundSelected = true;
                supportMethod();
                Log.v(TAG, "THUNDERSound is loaded");
            } else if (sampleId == soundIdList[Sounds.WATERFALL]) {
                Sounds.isWaterfallSoundLoaded = true;
                Sounds.streamIdList[Sounds.WATERFALL] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isWaterfallSoundPlaying = true;
                Sounds.isWaterfallSoundSelected = true;
                supportMethod();
                Log.v(TAG, "WATERFALLSound is loaded");
            } else if (sampleId == soundIdList[Sounds.WIND]) {
                Sounds.isWindSoundLoaded = true;
                Sounds.streamIdList[Sounds.WIND] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isWindSoundPlaying = true;
                Sounds.isWindSoundSelected = true;
                supportMethod();
                Log.v(TAG, "WINDSound is loaded");
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
        CommonFragment.soundListAdapter.notifyDataSetChanged();
    }
}