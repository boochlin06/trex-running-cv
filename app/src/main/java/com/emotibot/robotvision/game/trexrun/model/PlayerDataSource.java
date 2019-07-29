package com.emotibot.robotvision.game.trexrun.model;

import java.util.List;

public interface PlayerDataSource {

    interface PlayerListCallback {
        void onSuccess(List<Player> playerList);

        void onFailure();

    }

    interface PlayerCallback {
        void onSuccess(Player player);

        void onFailure();

    }

    void getAllPlayerList(PlayerListCallback callback);


    void getPlayerList(int limit, PlayerListCallback callback);

    void updatePlayer(Player player, PlayerCallback callback);

    void getPlayer(int id, PlayerCallback callback);

    void insertPlayer(Player player, PlayerCallback callback);

    int getPlayerRank(int score);


    interface uploadPublicFileCallback {
        void onSuccess(String qrCodeB64String);

        void onFailure();
    }

    interface uploadPrivateFileCallback {
        void onSuccess();

        void onFailure();
    }

    void uploadPrivateFile(String[] uploadPath, String matchId, uploadPrivateFileCallback callback);

    void uploadPublicFile(String[] uploadPath, String matchId, uploadPublicFileCallback callback);

}
