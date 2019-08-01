package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GameObject {
    protected int group = -1; // 0 = left , 1 == right
    protected float object_x;
    protected float object_y;
    protected float object_width;

    protected float screen_width;
    protected float screen_height;
    protected Paint paint;
    protected Resources resources;
    protected Bitmap defaultBitmap;

    protected float object_height;


    public boolean isObjectRecyclable() {
        return isObjectRecyclable;
    }

    public void setObjectRecyclable(boolean objectRecyclable) {
        isObjectRecyclable = objectRecyclable;
    }

    protected boolean isObjectRecyclable = false;


    public float getScreen_width() {
        return screen_width;
    }

    protected abstract void initBitmap();


    public abstract void release();

    public void setScreen_width(float screen_width) {
        this.screen_width = screen_width;
    }

    public float getScreen_height() {
        return screen_height;
    }

    public void setScreen_height(float screen_height) {
        this.screen_height = screen_height;
    }


    public float getObject_x() {
        return object_x;
    }

    public void setObject_x(float object_x) {
        this.object_x = object_x;
    }

    public float getObject_y() {
        return object_y;
    }

    public void setObject_y(float object_y) {
        this.object_y = object_y;
    }

    public float getObject_width() {
        return object_width;
    }

    public void setObject_width(float object_width) {
        this.object_width = object_width;
    }

    public float getObject_height() {
        return object_height;
    }

    public void setObject_height(float object_height) {
        this.object_height = object_height;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public GameObject(Resources resources, int group) {
        this.resources = resources;
        this.paint = new Paint();
        this.group = group;
        initBitmap();
    }

    public void GameObject(float screen_width, float screen_height, int group) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        this.paint = new Paint();
        this.group = group;
    }


    public abstract void drawSelf(Canvas canvas);
}