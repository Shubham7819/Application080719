package com.example.application080719;

public class MeditationExercise {

    private String mExerciseTitle;

    private String mExerciseGuides;

    private int mExerciseAudioResourceId;

    public String getExerciseTitle() {
        return mExerciseTitle;
    }

    public int getExerciseAudioResourceId() {
        return mExerciseAudioResourceId;
    }

    public String getExerciseGuides() {
        return mExerciseGuides;
    }

    public MeditationExercise(String mExerciseTitle, String mExerciseGuides, int mExerciseAudioResourceId) {
        this.mExerciseTitle = mExerciseTitle;
        this.mExerciseGuides = mExerciseGuides;
        this.mExerciseAudioResourceId = mExerciseAudioResourceId;
    }
}
