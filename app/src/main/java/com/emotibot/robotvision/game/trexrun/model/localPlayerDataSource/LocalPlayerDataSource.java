package com.emotibot.robotvision.game.trexrun.model.localPlayerDataSource;

import android.content.Context;
import android.util.Log;

import com.emotibot.robotvision.game.trexrun.model.Player;
import com.emotibot.robotvision.game.trexrun.model.PlayerDataSource;

import java.util.List;

public class LocalPlayerDataSource implements PlayerDataSource {

    private static final String TAG = LocalPlayerDataSource.class.getSimpleName();
    private static volatile LocalPlayerDataSource mInstance;
    private PlayerDao playerDao;

    private LocalPlayerDataSource(Context context) {
        playerDao = PlayerDataBase.getInstance(context).getPlayerDao();
    }

    public static LocalPlayerDataSource getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LocalPlayerDataSource.class) {
                if (mInstance == null) {
                    mInstance = new LocalPlayerDataSource(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void getAllPlayerList(final PlayerListCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(playerDao.getALLPlayer());
                } catch (Exception e) {
                    callback.onFailure();
                }
            }
        }).start();
    }

    public void getPlayerList(final int limit, final PlayerListCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Player> players = playerDao.getRankPlayer(limit);
                    Log.d(TAG, "player list count:" + players.size());
                    callback.onSuccess(players);
                } catch (Exception e) {
                    callback.onFailure();
                }
            }
        }).start();
    }

    @Override
    public void updatePlayer(final Player player, PlayerCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playerDao.update(player);
            }
        }).start();
    }

    @Override
    public void getPlayer(final int id, final PlayerCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(playerDao.getPlayer(id));
                } catch (Exception e) {
                    callback.onFailure();
                }
            }
        }).start();
    }

    @Override
    public void insertPlayer(final Player player, final PlayerCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long id = playerDao.insert(player);
                    player.setId(id);
                    Player returnPlayer = playerDao.getPlayer(player.getId());
                    Log.d(TAG, "insertPlayer player id :" + (player.getId()) + ",name;" + player.getName()
                            + ",row id:" + id + ", return:" + (returnPlayer.getName()));
                    callback.onSuccess(returnPlayer);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            }
        }).start();
    }

    @Override
    public int getPlayerRank(int score) {
        return playerDao.getPlayerRank(score);
    }

    @Override
    public void uploadPrivateFile(String[] uploadPath, String matchId, uploadPrivateFileCallback
            callback) {

    }

    @Override
    public void uploadPublicFile(String[] uploadPath, String matchId, uploadPublicFileCallback
            callback) {

    }
}
