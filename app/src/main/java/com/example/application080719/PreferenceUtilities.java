package com.example.application080719;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtilities {

    public static final String KEY_SOUNDS_PLAYING_COUNTER = "sounds-playing-count";
    public static final String KEY_SOUNDS_SELECTED_COUNTER = "sounds-selected-count";
    private static final String KEY_SERVICE_EXISTS = "service-exists";

    private static final int DEFAULT_COUNT = 0;
    //TODO 2) Create SharedPreference for soundIdList
    //TODO 3) Create SharedPreference for streamIdList

    //TODO 5) create sharedPref for loadedItems list
    //TODO 6) create sharedPref for selectedItems list
    //TODO 7) create sharedPref for playingItems list

    synchronized public static void decrementSoundsPlayingCount(Context context) {
        int soundsPlayingCount = PreferenceUtilities.getSoundsPlayingCount(context);
        PreferenceUtilities.updatePreference(context, KEY_SOUNDS_PLAYING_COUNTER, --soundsPlayingCount);
    }

    public static int getSoundsPlayingCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_SOUNDS_PLAYING_COUNTER, DEFAULT_COUNT);
    }

    synchronized public static void incrementSoundsPlayingCount(Context context) {
        int soundsPlayingCount = PreferenceUtilities.getSoundsPlayingCount(context);
        PreferenceUtilities.updatePreference(context, KEY_SOUNDS_PLAYING_COUNTER, ++soundsPlayingCount);
    }

    synchronized public static void incrementSoundsSelectedCount(Context context) {
        int soundsSelectedCount = PreferenceUtilities.getSoundsSelectedCount(context);
        PreferenceUtilities.updatePreference(context, KEY_SOUNDS_SELECTED_COUNTER, ++soundsSelectedCount);
    }

    synchronized public static void decrementSoundsSelectedCount(Context context) {
        int soundsSelectedCount = PreferenceUtilities.getSoundsSelectedCount(context);
        PreferenceUtilities.updatePreference(context, KEY_SOUNDS_SELECTED_COUNTER, --soundsSelectedCount);
    }

    public static int getSoundsSelectedCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_SOUNDS_SELECTED_COUNTER, DEFAULT_COUNT);
    }

    private static void updatePreference(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static boolean isServiceAvailable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_SERVICE_EXISTS, false);
    }

    public static void setServiceAvailability(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SERVICE_EXISTS, value);
        editor.apply();
    }
}
