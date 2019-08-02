package com.example.application080719;

import android.content.Context;

public class PlayerTasks {

    public static final String ACTION_START_PLAYER = "start-player";
    public static final String ACTION_LOAD_AUDIO = "load-audio";
    static final String ACTION_PAUSE_AUDIO = "pause-audio";
    public static final String ACTION_PLAY_AUDIO = "play-audio";
    static final String ACTION_STOP_PLAYER = "stop-player";

    public static void executeTask(Context context, String action) {
        if (ACTION_START_PLAYER.equals(action)) {
            startPlayer(context);
        } else if (ACTION_LOAD_AUDIO.equals(action)) {
            loadAudio(context);
        } else if (ACTION_PAUSE_AUDIO.equals(action)) {
            pauseAudio(context);
        } else if (ACTION_PLAY_AUDIO.equals(action)) {
            playAudio(context);
        } else if (ACTION_STOP_PLAYER.equals(action)) {
            stopPlayer(context);
        }
    }

    private static void startPlayer(Context context) {

    }

    private static void loadAudio(Context context) {

    }

    private static void pauseAudio(Context context) {

    }

    private static void playAudio(Context context) {

    }

    private static void stopPlayer(Context context) {

    }
}
