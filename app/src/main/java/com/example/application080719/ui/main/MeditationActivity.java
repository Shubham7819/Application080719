package com.example.application080719.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.application080719.R;
import com.example.application080719.ui.adapters.ExerciseListAdapter;

public class MeditationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        TextView exerciseNameTV = findViewById(R.id.exercise_title_tv);
        exerciseNameTV.setText(getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_NAME));

        TextView exerciseGuidesTV = findViewById(R.id.exercise_guides_tv);
        exerciseGuidesTV.setText(getIntent().getStringExtra(ExerciseListAdapter.EXERCISE_GUIDES));

        AppCompatSeekBar seekBar = findViewById(R.id.media_seekbar);

        int audioResourceId = getIntent().getIntExtra(ExerciseListAdapter.EXERCISE_RESOURCE_ID, 0);
    }

}
