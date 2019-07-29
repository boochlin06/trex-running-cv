package com.emotibot.robotvision.game.trexrun.factory;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.emotibot.robotvision.game.trexrun.R;

public class PlayerDragon extends Dragon {
    public PlayerDragon(Resources resources, int group) {
        super(resources, group);
    }

    @Override
    protected void initBitmap() {
        defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_neutral_1);
        default2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_neutral_2);
        fireBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_neutral_save);
        speed = 7;
    }

    @Override
    public void release() {
        if (!defaultBitmap.isRecycled()) {
            defaultBitmap.recycle();
        }
        if (!fireBitmap.isRecycled()) {
            fireBitmap.recycle();
        }
    }


//    public void drawSelf(Canvas canvas) {
//        if (isAlive()) {
//
//            if (System.currentTimeMillis() - getKeepScoreTime() < 100) {
//                canvas.drawBitmap(scoreBitmap, object_x, object_y, paint);
//            } else {
//
//                canvas.drawBitmap(fireBitmap, object_x, object_y, paint);
//            }
//            if (object_y + fireBitmap.getHeight() >= screen_height - 20) {
//                object_y = screen_height - fireBitmap.getHeight();
//                object_x = object_x + 10;
//            } else {
//                object_y = object_y + speed;
//            }
//        } else {
//            canvas.drawBitmap(defaultBitmap, object_x, object_y, paint);
//            object_y = object_y + speed;
//        }
//    }

}
