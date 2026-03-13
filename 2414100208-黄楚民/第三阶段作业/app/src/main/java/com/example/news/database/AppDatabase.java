package com.example.news.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.news.database.dao.BrowseHistoryDao;
import com.example.news.database.dao.FavoriteDao;
import com.example.news.database.dao.UserDao;
import com.example.news.database.entity.BrowseHistory;
import com.example.news.database.entity.Favorite;
import com.example.news.database.entity.User;

@Database(entities = {User.class, BrowseHistory.class, Favorite.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract UserDao userDao();
    public abstract BrowseHistoryDao browseHistoryDao();
    public abstract FavoriteDao favoriteDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "news_database"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
