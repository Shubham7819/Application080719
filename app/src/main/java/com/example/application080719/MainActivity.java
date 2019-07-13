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

import com.example.application080719.ui.main.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import static com.example.application080719.Sounds.soundIdList;
import static com.example.application080719.Sounds.soundPool;

public class MainActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    final String TAG = MainActivity.class.getSimpleName();
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);

        soundPool.setOnLoadCompleteListener(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_play) {
                    String menuTitle = menuItem.getTitle().toString();
                    if (getString(R.string.play).equals(menuTitle)) {
                        if (Sounds.streamCounter > 0) {
                            soundPool.autoResume();
                            menuItem.setTitle("Pause");
                            menuItem.setIcon(R.drawable.ic_pause);
                        } else
                            Toast.makeText(MainActivity.this, "No files to resume", Toast.LENGTH_SHORT).show();
                    } else {
                        soundPool.autoPause();
                        menuItem.setTitle(R.string.play);
                        menuItem.setIcon(R.drawable.ic_play);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        mediaPlayer = null;
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
                Sounds.streamCounter++;
                Log.v(TAG, "BELLSSound is loaded");
            } else if (sampleId == soundIdList[Sounds.BIRD]) {
                Sounds.isBirdSoundLoaded = true;
                Sounds.streamIdList[Sounds.BIRD] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isBirdSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "BIRDSound is loaded");
            } else if (sampleId == soundIdList[Sounds.CLOCK_CHIMES]) {
                Sounds.isClockSoundLoaded = true;
                Sounds.streamIdList[Sounds.CLOCK_CHIMES] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isClockSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "CLOCK_CHIMESSound is loaded");
            } else if (sampleId == soundIdList[Sounds.FARM]) {
                Sounds.isFarmSoundLoaded = true;
                Sounds.streamIdList[Sounds.FARM] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFarmSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "FARMSound is loaded");
            } else if (sampleId == soundIdList[Sounds.FIRE]) {
                Sounds.isFireSoundLoaded = true;
                Sounds.streamIdList[Sounds.FIRE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFireSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "FIRESound is loaded");
            } else if (sampleId == soundIdList[Sounds.FLUTE]) {
                Sounds.isFluteSoundLoaded = true;
                Sounds.streamIdList[Sounds.FLUTE] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isFluteSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "FLUTESound is loaded");
            } else if (sampleId == soundIdList[Sounds.MUSIC_BOX]) {
                Sounds.isMusicBoxSoundLoaded = true;
                Sounds.streamIdList[Sounds.MUSIC_BOX] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isMusicBoxSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "MUSIC_BOXSound is loaded");
            } else if (sampleId == soundIdList[Sounds.NIGHT]) {
                Sounds.isNightSoundLoaded = true;
                Sounds.streamIdList[Sounds.NIGHT] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isNightSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "NIGHTSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RAIN]) {
                Sounds.isRainSoundLoaded = true;
                Sounds.streamIdList[Sounds.RAIN] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRainSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "RAINSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RAINFOREST]) {
                Sounds.isRainForestSoundLoaded = true;
                Sounds.streamIdList[Sounds.RAINFOREST] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRainForestSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "RAINFORESTSound is loaded");
            } else if (sampleId == soundIdList[Sounds.RIVER]) {
                Sounds.isRiverSoundLoaded = true;
                Sounds.streamIdList[Sounds.RIVER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isRiverSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "RIVERSound is loaded");
            } else if (sampleId == soundIdList[Sounds.SEA]) {
                Sounds.isSeaSoundLoaded = true;
                Sounds.streamIdList[Sounds.SEA] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isSeaSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "SEASound is loaded");
            } else if (sampleId == soundIdList[Sounds.THUNDER]) {
                Sounds.isThunderSoundLoaded = true;
                Sounds.streamIdList[Sounds.THUNDER] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isThunderSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "THUNDERSound is loaded");
            } else if (sampleId == soundIdList[Sounds.WATERFALL]) {
                Sounds.isWaterfallSoundLoaded = true;
                Sounds.streamIdList[Sounds.WATERFALL] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isWaterfallSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "WATERFALLSound is loaded");
            } else if (sampleId == soundIdList[Sounds.WIND]) {
                Sounds.isWindSoundLoaded = true;
                Sounds.streamIdList[Sounds.WIND] = soundPool.play(sampleId, 1, 1, 0, -1, 1);
                Sounds.isWindSoundPlaying = true;
                Sounds.streamCounter++;
                Log.v(TAG, "WINDSound is loaded");
            } else {
                Log.v(TAG, "sound id did not match");
            }
        }
    }
}