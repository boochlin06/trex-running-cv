package com.emotibot.robotvision.game.trexrun.model.localPlayerDataSource;

import com.emotibot.robotvision.game.trexrun.model.Player;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlayerDao {

    @Query("SELECT * FROM players ORDER BY score DESC")
    List<Player> getALLPlayer();


    @Query("SELECT * FROM players ORDER BY score DESC LIMIT :limit")
    List<Player> getRankPlayer(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Player player);

    @Update
    void update(Player player);

    @Delete
    void delete(Player player);

    @Query("SELECT * FROM players WHERE id=:id")
    Player getPlayer(long id);

    @Query("SELECT COUNT(*) FROM players WHERE score> :score")
    int getPlayerRank(int score);
}
