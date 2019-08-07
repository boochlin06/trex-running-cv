package com.emotibot.robotvision.game.trexrun.service;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.emotibot.robotvision.game.trexrun.model.MainPlayerDataSource;
import com.emotibot.robotvision.game.trexrun.model.PlayerDataSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class UploadService extends JobService {

    public static final String TAG = UploadService.class.getSimpleName();

    @Override
    public boolean onStartJob(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] files = getCacheDir().list();
                List<String> uploads = new ArrayList<>();
                Log.d(TAG, getCacheDir().getPath() + ",file count: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    if (files[i].endsWith("_0.jpg") || files[i].endsWith("_1.jpg")) {
                        uploads.add(new File(getCacheDir(), files[i]).getPath());
                    } else {
                        File file = new File(getCacheDir(), files[i]);
                        if (file.exists()) {
                            if (file.delete()) {
                                Log.d(TAG, "header image delete :" + file.getPath());
                            } else {
                                Log.d(TAG, "header image not delete :" + file.getPath());
                            }
                        }
                    }
                    Log.d(TAG, "FileName:" + files[i]);
                }
                MainPlayerDataSource.getInstance(UploadService.this).uploadPublicFile(
                        uploads.toArray(new String[uploads.size()])
                        , "", new PlayerDataSource.uploadPublicFileCallback() {
                            @Override
                            public void onSuccess(String fileName) {
                                Log.d(TAG, "upload success:" + fileName);
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "upload fail");
                            }
                        });
                jobFinished(params, true);
            }
        }).start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
