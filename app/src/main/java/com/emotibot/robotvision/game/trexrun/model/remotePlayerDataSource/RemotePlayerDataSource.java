package com.emotibot.robotvision.game.trexrun.model.remotePlayerDataSource;

import android.util.Log;

import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.PlayerDataSource;
import com.emotibot.robotvision.game.trexrun.model.UploadResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RemotePlayerDataSource implements PlayerDataSource {

    public static final String TAG = RemotePlayerDataSource.class.getSimpleName();
    private static volatile RemotePlayerDataSource mInstance;
    private RemotePlayerDataService remotePlayerDataService;

    public static final String DATA_BASE_URL = "http://poc1.emotibot.com:8115";

    private RemotePlayerDataSource(String ip) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(ip)
                .build();
        remotePlayerDataService = retrofit.create(RemotePlayerDataService.class);
    }

    public static RemotePlayerDataSource getInstance(String ip) {
        if (mInstance == null) {
            synchronized (RemotePlayerDataSource.class) {
                if (mInstance == null) {
                    mInstance = new RemotePlayerDataSource(ip);
                }
            }
        }
        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    @Override
    public void getAllPlayerList(final PlayerDataSource.PlayerListCallback callback) {
        Call<List<Player>> call = remotePlayerDataService.getPlayerList();
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                callback.onFailure();
            }
        });

    }

    public void getPlayerList(int limit, PlayerListCallback callback) {

    }

    @Override
    public void updatePlayer(Player player, PlayerCallback callback) {

    }

    @Override
    public void getPlayer(int id, PlayerCallback callback) {

    }

    @Override
    public void insertPlayer(Player player, PlayerCallback callback) {

    }

    @Override
    public int getPlayerRank(int score) {
        return 0;
    }

    @Override
    public void uploadPrivateFile(String[] uploadPath, String matchId, final uploadPrivateFileCallback callback) {
        List<MultipartBody.Part> uploadPart = new ArrayList<>();
        for (int i = 0; i < uploadPath.length; i++) {
            String fromTag = "file" + (i + 1);
            File file = new File(uploadPath[i]);
            MultipartBody.Part part = MultipartBody.Part.createFormData(fromTag, file.getName()
                    , RequestBody.create(MediaType.parse("image/*"), file));
            uploadPart.add(part);
        }

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), matchId);
        Call<UploadResponse> call = remotePlayerDataService.uploadPrivate(description, uploadPart);
        try {
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    UploadResponse uploadResponse = response.body();
                    String err = uploadResponse.getError();
                    if (err == null) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    callback.onFailure();
                }
            });
        } catch (Exception e) {
            callback.onFailure();
        }

    }

    @Override
    public void uploadPublicFile(String[] uploadPath, String matchId, final uploadPublicFileCallback callback) {

        List<MultipartBody.Part> uploadPart;
        for (int i = 0; i < uploadPath.length; i++) {
            uploadPart = new ArrayList<>();
            String fromTag = "file";
            final File file = new File(uploadPath[i]);
            MultipartBody.Part part = MultipartBody.Part.createFormData(fromTag, file.getName()
                    , RequestBody.create(MediaType.parse("image/*"), file));
            uploadPart.add(part);
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), matchId);
            Call<UploadResponse> call = remotePlayerDataService.uploadPublic(description, uploadPart);
            try {
                call.enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response.isSuccessful()) {
                            UploadResponse uploadResponse = response.body();
                            String request_id = uploadResponse.getRequest_id();
                            if (request_id != null) {
                                if (file.exists()) {
                                    if (file.delete()) {
                                        Log.d(TAG, "file upload and delete :" + file.getPath());
                                    } else {
                                        Log.d(TAG, "file upload but not delete :" + file.getPath());
                                    }
                                }
                                callback.onSuccess(file.getName());
                            } else {
                                callback.onFailure();
                            }

                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        t.printStackTrace();
                        Log.e(TAG, "uploadPublicFile failure" + file.getPath() + t.getMessage());
                        callback.onFailure();
                    }
                });
            } catch (Exception e) {
                callback.onFailure();
            }
        }

    }
}
