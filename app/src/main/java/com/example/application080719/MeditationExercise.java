package com.example.application080719;

public class MeditationExercise {

    String mExercisetitle;

    String mExerciseGuides;

    int mExerciseAudioResourceId;

    public String getmExercisetitle() {
        return mExercisetitle;
    }

    public int getmExerciseAudioResourceId() {
        return mExerciseAudioResourceId;
    }

    public String getmExerciseGuides() {
        return mExerciseGuides;
    }

    public MeditationExercise(String mExercisetitle, String mExerciseGuides, int mExerciseAudioResourceId) {
        this.mExercisetitle = mExercisetitle;
        this.mExerciseGuides = mExerciseGuides;
        this.mExerciseAudioResourceId = mExerciseAudioResourceId;
    }
}
