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
        if (getGroup() == 0) {
            defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_blue_run_1);
            default2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_blue_run_2);
            firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_blue_fired);
            fired2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_blue_fired_2);
        } else {
            defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_dark_run_1);
            default2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_dark_run_2);
            firedBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_dark_fired);
            fired2Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_dragon_dark_fired_2);
        }
        setObject_height(defaultBitmap.getHeight());
        setObject_width(defaultBitmap.getWidth());
    }

    @Override
    public void release() {
        if (!defaultBitmap.isRecycled()) {
            defaultBitmap.recycle();
        }
        if (!default2Bitmap.isRecycled()) {
            defaultBitmap.recycle();
        }
        if (!firedBitmap.isRecycled()) {
            firedBitmap.recycle();
        }
    }

}
