package com.emotibot.robotvision.game.trexrun;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.emotibot.intellieyecoreaar.InferResult;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Utility {

    public static ArrayList<SinglePlayerResult> splitInferResultToPlayer(InferResult inferResult) {
        ArrayList<SinglePlayerResult> playerResults = new ArrayList<>();
        SinglePlayerResult player1 = new SinglePlayerResult();
        SinglePlayerResult player2 = new SinglePlayerResult();
        if (inferResult.getFaceBox()[0] != 0) {
            int firstAt = -1;
            float max = 0;
            for (int i = 0; i < inferResult.getEmotionData().length / 2; i++) {

                float emotion = inferResult.getEmotionData()[i];
                if (emotion > max) {
                    max = emotion;
                    firstAt = i;
                }
            }
            if (firstAt != -1) {
                player1.setEmotionIndex(firstAt);
            }
            player1.setPos_x(inferResult.getFaceBox()[0]);
            player1.setPos_y(inferResult.getFaceBox()[1]);
            player1.setWidth(inferResult.getFaceBox()[2]);
            player1.setHeight(inferResult.getFaceBox()[3]);
            player1.setPositionIndex(0);
            playerResults.add(player1);
        }
        if (inferResult.getFaceBox()[inferResult.getFaceBox().length / 2] != 0) {
            int firstAt = -1;
            float max = 0;
            for (int i = inferResult.getEmotionData().length / 2; i < inferResult.getEmotionData().length; i++) {

                float emotion = inferResult.getEmotionData()[i];
                if (emotion > max) {
                    max = emotion;
                    firstAt = i;
                }
            }
            if (firstAt != -1) {
                player2.setEmotionIndex(firstAt);
            }
            player2.setPos_x(inferResult.getFaceBox()[inferResult.getFaceBox().length / 2]);
            player2.setPos_y(inferResult.getFaceBox()[inferResult.getFaceBox().length / 2 + 1]);
            player2.setWidth(inferResult.getFaceBox()[inferResult.getFaceBox().length / 2 + 2]);
            player2.setHeight(inferResult.getFaceBox()[inferResult.getFaceBox().length / 2 + 3]);
            if (player1.getPos_x() > player2.getPos_x()) {
                player1.setPositionIndex(1);
                player2.setPositionIndex(0);
            }
            playerResults.add(player2);
        }
        return playerResults;
    }

    public static void takeScreenshot(String path, Activity activity) {
        try {
            View v1 = activity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(path);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void takeScreenshot(String path, Bitmap bitmap) {
        try {
            File imageFile = new File(path);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
