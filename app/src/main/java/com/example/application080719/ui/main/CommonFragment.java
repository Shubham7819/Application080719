package com.example.application080719.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.application080719.dto.MeditationExercise;
import com.example.application080719.R;
import com.example.application080719.dto.SoundItem;
import com.example.application080719.ui.adapters.ExerciseListAdapter;
import com.example.application080719.ui.adapters.SoundListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragment extends Fragment {

    private ArrayList<MeditationExercise> briefExercisesList = new ArrayList<>();
    private int tabPosition = 0;
    public static ArrayList<SoundItem> soundItemsList = new ArrayList<>();
    public static SoundListAdapter soundListAdapter;

    public CommonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            tabPosition = getArguments().getInt(null, 0);
        }
        getListData(tabPosition);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.horizontal_list_layout, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.horizontal_recycler_view);
        if (tabPosition == 0) {
            recyclerView.setLayoutManager(new GridLayoutManager(null, 4, RecyclerView.HORIZONTAL, false));
            soundListAdapter = new SoundListAdapter(getActivity(), soundItemsList);
            recyclerView.setAdapter(soundListAdapter);
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
            ExerciseListAdapter adapter = new ExerciseListAdapter(getActivity(), briefExercisesList);
            recyclerView.setAdapter(adapter);
        }

        return root;
    }

    private void getListData(int tabPosition) {
        if (tabPosition == 0) {
            if (soundItemsList.size() <= 0) {
                soundItemsList.add(new SoundItem("bells", R.raw.bells_tibetan));
                soundItemsList.add(new SoundItem("bird", R.raw.bird_song));
                soundItemsList.add(new SoundItem("clock chimes", R.raw.clock_chimes));
                soundItemsList.add(new SoundItem("farm", R.raw.farm));
                soundItemsList.add(new SoundItem("fire", R.raw.fire_burning));
                soundItemsList.add(new SoundItem("flute", R.raw.flute_tone));
                soundItemsList.add(new SoundItem("music box", R.raw.music_box));
                soundItemsList.add(new SoundItem("night", R.raw.nightime));
                soundItemsList.add(new SoundItem("rain", R.raw.rain));
                soundItemsList.add(new SoundItem("rainforest", R.raw.rainforest_ambience));
                soundItemsList.add(new SoundItem("river", R.raw.river));
                soundItemsList.add(new SoundItem("sea", R.raw.sea_waves));
                soundItemsList.add(new SoundItem("thunder", R.raw.thunder_hd));
                soundItemsList.add(new SoundItem("waterfall", R.raw.waterfall_large));
                soundItemsList.add(new SoundItem("wind", R.raw.wind));
            }
        } else if (tabPosition == 1) {
            briefExercisesList.add(new MeditationExercise("Three minute breathing"
                    , "Peter Morgan, Free Mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Five minute breathing"
                    , "Mindful Awareness Research Centre, UCLA", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Five minute breathing"
                    , "Life Happens", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Six minute breath awareness"
                    , "Melbourne Mindfulness Centre & Still Mind", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Ten minute breathing"
                    , "Peter Morgan, Free Mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Ten minute mindfulness of breathing"
                    , "Padraig O'Morain", R.raw.free_mind_fulness_3minute_breathing));
        } else if (tabPosition == 2) {
            briefExercisesList.add(new MeditationExercise("Brief mindfulness practice"
                    , "Padraig O'Morain", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("The breathing space"
                    , "Vidyamala Burch, Breathworks", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("The tension release meditation"
                    , " Vidyamala Burch, Breathworks", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Three minute breathing space"
                    , "Peter Morgan, Free Mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Three minute mindfulness of sounds"
                    , "Peter Morgan, Free Mindfulness", R.raw.free_mind_fulness_3minute_breathing));
        } else if (tabPosition == 3) {
            briefExercisesList.add(new MeditationExercise("Forty five minute body scan"
                    , "UCSD Center for mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Twenty minute body scan"
                    , "UCSD Center for mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Body scan"
                    , "Kieran Fleck, Senior CBT Therapist", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Four minute body scan"
                    , "Melbourne Mindfulness Centre & Still Mind", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Body scan"
                    , "Vidyamala Burch, Breathworks", R.raw.free_mind_fulness_3minute_breathing));
        } else if (tabPosition == 4) {
            briefExercisesList.add(new MeditationExercise("Seated meditation"
                    , "UCSD Center for mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Sitting meditation"
                    , "Kieran Fleck, Senior CBT Therapist", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Breath, sound and body"
                    , "Mindful Awareness Research Centre, UCLA", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Breath, sounds, body, thoughts, emotions"
                    , "Mindful Awareness Research Centre, UCLA", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Ten minute wisdom meditation"
                    , "UCSD Center for mindfulness", R.raw.free_mind_fulness_3minute_breathing));
            briefExercisesList.add(new MeditationExercise("Compassionate Breath"
                    , "Vidyamala Burch, Breathworks", R.raw.free_mind_fulness_3minute_breathing));
        } else
            briefExercisesList = null;
    }

}
