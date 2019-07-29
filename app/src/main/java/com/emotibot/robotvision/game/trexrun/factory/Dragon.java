package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;

public abstract class Dragon {

    protected int group = -1; // 0 = left , 1 == right
    protected int speed = GameConstant.GAMESPEED;
    protected float object_x;
    protected float object_y;
    protected float object_width;

    protected float screen_width;
    protected float screen_height;
    protected boolean isAlive;
    protected Paint paint;
    protected Resources resources;
    protected Bitmap defaultBitmap;
    protected Bitmap fireBitmap;
    protected int frameCount;
    protected boolean isOnGround;
    protected Bitmap default2Bitmap;

    protected long keepAliveTime;

    protected float object_height;

    protected long keepScoreTime;

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

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public long getKeepScoreTime() {
        return keepScoreTime;
    }

    public void setKeepScoreTime(long keepScoreTime) {
        this.keepScoreTime = keepScoreTime;
    }

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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
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

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Dragon(Resources resources, int group) {
        this.resources = resources;
        this.paint = new Paint();
        this.group = group;
        initBitmap();
        this.object_width = default2Bitmap.getWidth();
    }

    public void Dragon(float screen_width, float screen_height, int group) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        this.paint = new Paint();
        this.group = group;
    }


    public void drawSelf(Canvas canvas) {
        frameCount++;
        if (isObjectRecyclable()) {
            return;
        }
        if (isAlive()) {

            if (object_y + fireBitmap.getHeight() >= screen_height - 50) {
                object_y = screen_height - fireBitmap.getHeight() - 50;

                if (!isOnGround) {
                    this.setKeepAliveTime(System.currentTimeMillis());
                    this.isOnGround = true;
                }
                if (isOnGround && paint.getAlpha() > 0) {
                    if (paint.getAlpha() > 10) {
                        paint.setAlpha(paint.getAlpha() - 4);
                        canvas.drawBitmap(fireBitmap, object_x, object_y, paint);
                    } else {
                        isObjectRecyclable = true;
                    }

                }

            } else {
                object_y = object_y + speed;
                if (object_y >= getScreen_height()) {
                    isObjectRecyclable = true;
                }
            }


        } else {
            if (frameCount % 20 < 20 / 2) {
                if (!defaultBitmap.isRecycled())
                    canvas.drawBitmap(defaultBitmap, object_x, object_y, paint);
            } else {
                if (!default2Bitmap.isRecycled())
                    canvas.drawBitmap(default2Bitmap, object_x, object_y, paint);
            }
            object_y = object_y + speed;
            if (object_y >= getScreen_height()) {
                isObjectRecyclable = true;
            }
        }
    }
}
