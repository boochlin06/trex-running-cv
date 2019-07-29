package com.emotibot.robotvision.game.trexrun.model.remotePlayerDataSource;

import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.UploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RemotePlayerDataService {

    @POST("/getAllPlayerList")
    Call<List<Player>> getPlayerList();

    @Multipart
    @POST("/public/file")
    Call<UploadResponse> uploadPublic(@Part("match_id") RequestBody requestBody, @Part List<MultipartBody.Part> parts);

    @Multipart
    @POST("/private/file")
    Call<UploadResponse> uploadPrivate(@Part("match_id") RequestBody requestBody, @Part List<MultipartBody.Part> parts);
}
