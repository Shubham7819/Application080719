package com.example.application080719;

import android.app.IntentService;
import android.content.Intent;
import android.media.SoundPool;

public class PlayerIntentService extends IntentService {

    public static SoundPool soundPool;

    public PlayerIntentService() {
        super("PlayerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        PlayerTasks.executeTask(this, action);
    }
}
