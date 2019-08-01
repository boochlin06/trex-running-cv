package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.emotibot.robotvision.game.trexrun.Constants.GameConstant;
import com.emotibot.robotvision.game.trexrun.R;

public class Obstacle extends GameObject {
    protected int group = -1; // 0 = left , 1 == right
    protected int speed = GameConstant.GAMESPEED;

    protected Paint paint;
    protected Resources resources;
    protected Bitmap defaultBitmap;
    protected Bitmap firedBitmap;
    protected boolean isFired;
    protected int frameCount;
    protected boolean isMoving = true;

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public void setGroup(int group) {
        this.group = group;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public boolean isFired() {
        return isFired;
    }

    public void setFired(boolean fired) {
        isFired = fired;
    }

    protected boolean isObjectRecyclable = false;

    @Override
    protected void initBitmap() {
        int type = (int) (Math.random() * 10);
        switch (type) {
            case 0:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree1);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree1_black);
                break;
            case 1:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree2);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree2_black);
                break;
            case 2:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree3);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree3_black);
                break;
            case 3:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree4);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree4_black);
                break;
            case 4:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree5);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree5_black);
                break;
            case 5:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree6);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.tree6_black);
                break;
            case 6:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.car1);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.car_black);
                break;
            case 7:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.car2);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.car_black);
                break;
            case 8:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.car3);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.car_black);
                break;
            case 9:
                defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.car4);
                firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.car_black);
                break;

        }

    }

    @Override
    public void release() {
        if (!defaultBitmap.isRecycled()) {
            defaultBitmap.recycle();
        }
        if (!firedBitmap.isRecycled()) {
            firedBitmap.recycle();
        }
    }

    public Obstacle(Resources resources, int group) {
        super(resources, group);
        this.resources = resources;
        this.paint = new Paint();
        this.group = group;
        speed = 8;
        initBitmap();
        this.object_width = defaultBitmap.getWidth();
        this.object_height = defaultBitmap.getHeight();
    }

    public void Obstacle(float screen_width, float screen_height, int group) {
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

        if (isFired) {
            if (paint.getAlpha() > 10) {
                paint.setAlpha(paint.getAlpha() - 4);
                canvas.drawBitmap(firedBitmap, object_x, object_y, paint);
            } else {
                isObjectRecyclable = true;
            }
        } else {
            canvas.drawBitmap(defaultBitmap, object_x, object_y, paint);
        }

        if (isMoving) {
            object_x = object_x - speed;
        }

        if (object_x < -100) {
            isObjectRecyclable = true;
        }
    }
}
