package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;

public abstract class Dragon extends GameObject {

    protected int group = -1; // 0 = left , 1 == right
    protected int speed = GameConstant.GAMESPEED;
    protected int frameCount = 0;
    protected Bitmap default2Bitmap;
    protected Bitmap firedBitmap;
    protected Bitmap fired2Bitmap;
    protected boolean isFiring = false;
    protected boolean isMoving = true;

    protected abstract void initBitmap();

    public abstract void release();

    public Dragon(Resources resources, int group) {
        super(resources, group);
        this.resources = resources;
        this.paint = new Paint();
        this.group = group;
        initBitmap();
        this.object_width = defaultBitmap.getWidth();
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

        if (isFiring ) {
            if (frameCount % 10 < 10 / 2) {
                if (!firedBitmap.isRecycled())
                    canvas.drawBitmap(firedBitmap, object_x, object_y, paint);
            } else {
                if (!fired2Bitmap.isRecycled())
                    canvas.drawBitmap(fired2Bitmap, object_x, object_y, paint);
            }
        } else {
            if (frameCount % 20 < 20 / 2 && isMoving) {
                if (!defaultBitmap.isRecycled())
                    canvas.drawBitmap(defaultBitmap, object_x, object_y, paint);
            } else {
                if (!default2Bitmap.isRecycled())
                    canvas.drawBitmap(default2Bitmap, object_x, object_y, paint);
            }
        }
    }

    public boolean isFiring() {
        return isFiring;
    }

    public void setFiring(boolean firing) {
        isFiring = firing;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }
}
