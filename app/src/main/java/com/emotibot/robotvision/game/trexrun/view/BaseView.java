package com.emotibot.robotvision.game.trexrun.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.emotibot.robotvision.game.trexrun.sound.GameSoundPool;

@SuppressLint("ViewConstructor")
public class BaseView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected int currentFrame;
    protected float scalex;
    protected float scaley;
    protected float screen_width;
    protected float screen_height;
    protected boolean threadFlag;
    protected Paint paint;
    protected Canvas canvas;
    protected Thread thread;
    protected SurfaceHolder surfaceHolder;
    protected GameSoundPool sounds;
    protected Activity mainActivity;



    public BaseView(Context context, GameSoundPool sounds) {
        super(context);
        this.sounds = sounds;
        this.mainActivity = (Activity) context;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        screen_width = this.getWidth();
        screen_height = this.getHeight();
        threadFlag = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        threadFlag = false;
    }

    public void initBitmap() {
    }

    public void release() {
    }

    public void drawSelf() {
    }

    @Override
    public void run() {

    }

    public void setThreadFlag(boolean threadFlag) {
        this.threadFlag = threadFlag;
    }


}
