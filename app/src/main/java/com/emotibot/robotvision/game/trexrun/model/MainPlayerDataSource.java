package com.emotibot.robotvision.game.trexrun.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.emotibot.robotvision.game.trexrun.activity.SettingActivity;
import com.emotibot.robotvision.game.trexrun.model.localPlayerDataSource.LocalPlayerDataSource;
import com.emotibot.robotvision.game.trexrun.model.remotePlayerDataSource.RemotePlayerDataSource;

import java.util.List;

public class MainPlayerDataSource implements PlayerDataSource {
    private static volatile MainPlayerDataSource mInstance;

    private PlayerDataSource remote;
    private PlayerDataSource local;
    private static final String TAG = MainPlayerDataSource.class.getSimpleName();

    private MainPlayerDataSource(Context context) {
        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        String ip = setting.getString(SettingActivity.GAME_IP, RemotePlayerDataSource.DATA_BASE_URL);
        remote = RemotePlayerDataSource.getInstance(ip);
        local = LocalPlayerDataSource.getInstance(context);
    }

    public static MainPlayerDataSource getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MainPlayerDataSource.class) {
                if (mInstance == null) {
                    mInstance = new MainPlayerDataSource(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void getAllPlayerList(final PlayerListCallback callback) {

        local.getAllPlayerList(new PlayerListCallback() {
            @Override
            public void onSuccess(List<Player> playerList) {
                callback.onSuccess(playerList);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void updatePlayer(Player player, PlayerCallback callback) {
        local.updatePlayer(player, callback);
    }

    public void getPlayerList(int limit, final PlayerListCallback callback) {
        local.getPlayerList(limit, new PlayerListCallback() {
            @Override
            public void onSuccess(List<Player> playerList) {
                callback.onSuccess(playerList);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }


    @Override
    public void getPlayer(int id, final PlayerCallback callback) {
        local.getPlayer(id, new PlayerCallback() {
            @Override
            public void onSuccess(Player player) {
                callback.onSuccess(player);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });

    }

    @Override
    public void insertPlayer(final Player player1, final PlayerCallback callback) {

        local.insertPlayer(player1, new PlayerCallback() {
            @Override
            public void onSuccess(Player player) {
                callback.onSuccess(player);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public int getPlayerRank(int score) {
        return local.getPlayerRank(score);
    }

    @Override
    public void uploadPrivateFile(String[] uploadPath, String matchId, final uploadPrivateFileCallback callback) {
        remote.uploadPrivateFile(uploadPath, matchId, new uploadPrivateFileCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    @Override
    public void uploadPublicFile(String[] uploadPath, String matchId, final uploadPublicFileCallback callback) {
        remote.uploadPublicFile(uploadPath, matchId, new uploadPublicFileCallback() {
            @Override
            public void onSuccess(String qrCodeB64String) {
                callback.onSuccess(qrCodeB64String);
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }


}
