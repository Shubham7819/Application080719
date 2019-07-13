package com.example.application080719;

import android.media.SoundPool;

public class Sounds {

    public final static int BELLS = 0;
    public final static int BIRD = 1;
    public final static int CLOCK_CHIMES = 2;
    public final static int FARM = 3;
    public final static int FIRE = 4;
    public final static int FLUTE = 5;
    public final static int MUSIC_BOX = 6;
    public final static int NIGHT = 7;
    public final static int RAIN = 8;
    public final static int RAINFOREST = 9;
    public final static int RIVER = 10;
    public final static int SEA = 11;
    public final static int THUNDER = 12;
    public final static int WATERFALL = 13;
    public final static int WIND = 14;

    public static SoundPool soundPool;

    public static boolean isBellsSoundLoaded, isBirdSoundLoaded, isClockSoundLoaded,
            isFarmSoundLoaded, isFireSoundLoaded, isFluteSoundLoaded, isMusicBoxSoundLoaded,
            isNightSoundLoaded, isRainSoundLoaded, isRainForestSoundLoaded, isRiverSoundLoaded,
            isSeaSoundLoaded, isThunderSoundLoaded, isWaterfallSoundLoaded, isWindSoundLoaded = false;

    public static boolean isBellsSoundPlaying, isBirdSoundPlaying, isClockSoundPlaying,
            isFarmSoundPlaying, isFireSoundPlaying, isFluteSoundPlaying, isMusicBoxSoundPlaying,
            isNightSoundPlaying, isRainSoundPlaying, isRainForestSoundPlaying, isRiverSoundPlaying,
            isSeaSoundPlaying, isThunderSoundPlaying, isWaterfallSoundPlaying, isWindSoundPlaying = false;

    public static int[] soundIdList = new int[15];

    public static int[] streamIdList = new int[15];

    static int streamCounter = 0;

}
