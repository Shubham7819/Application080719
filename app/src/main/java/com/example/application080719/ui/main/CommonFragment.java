package com.example.application080719.ui.main;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application080719.MeditationExercise;
import com.example.application080719.R;
import com.example.application080719.ui.ExerciseListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonFragment extends Fragment {

    private ArrayList<MeditationExercise> briefExercisesList = new ArrayList<>();

    public CommonFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        int tabPosition = 0;
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
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));

        ExerciseListAdapter adapter = new ExerciseListAdapter(getActivity(), briefExercisesList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void getListData(int tabPosition) {
        if (tabPosition == 0) {
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
        } else if (tabPosition == 1) {
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
        } else if (tabPosition == 2) {
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
        } else {
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
        }
    }

}
