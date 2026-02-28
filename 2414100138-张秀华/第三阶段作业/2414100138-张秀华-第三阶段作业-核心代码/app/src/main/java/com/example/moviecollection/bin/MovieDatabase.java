package com.example.moviecollection.bin;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Results.class}, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {


    private static MovieDatabase INSTANCE;

    static synchronized MovieDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, "movie_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract MovieDao getResultsDao();
}
