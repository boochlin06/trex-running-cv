package com.emotibot.robotvision.game.trexrun.model.localPlayerDataSource;

import android.content.Context;

import com.emotibot.robotvision.game.trexrun.model.Player;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Player.class}, version = 2, exportSchema = false)
public abstract class PlayerDataBase extends RoomDatabase {
    private static final String DB_NAME = "PlayerDatabase.db";
    private static volatile PlayerDataBase instance;

    public static synchronized PlayerDataBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static PlayerDataBase create(final Context context) {

        PlayerDataBase dataBase = Room.databaseBuilder(
                context,
                PlayerDataBase.class,
                DB_NAME).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        return dataBase;
    }

    public abstract PlayerDao getPlayerDao();
}
