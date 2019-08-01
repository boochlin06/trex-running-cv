package com.emotibot.robotvision.game.trexrun.sound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.emotibot.robotvision.game.trexrun.R;

import java.util.HashMap;

public class GameSoundPool {
    private Activity mainActivity;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> map;

    @SuppressLint("UseSparseArrays")
    public GameSoundPool(Activity mainActivity) {
        this.mainActivity = mainActivity;
        map = new HashMap<Integer, Integer>();
        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
    }

    public void initGameSound() {
        map.put(1, soundPool.load(mainActivity, R.raw.background, 1));

    }

    public void playSound(int sound, int loop) {
        AudioManager am = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
        float stramVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float stramMaxVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volume = stramVolumeCurrent / stramMaxVolumeCurrent;
        soundPool.play(map.get(sound), volume, volume, 1, loop, 1.0f);
    }
}
