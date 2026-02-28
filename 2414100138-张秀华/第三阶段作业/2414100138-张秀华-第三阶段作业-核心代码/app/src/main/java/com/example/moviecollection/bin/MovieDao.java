package com.example.moviecollection.bin;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(Results... results);

    @Delete
    void deleteMovie(Results... results);

    @Query("SELECT * FROM RESULTS ORDER BY ID DESC")
    LiveData<List<Results>> getAllResultLive();

    @Query("DELETE FROM RESULTS WHERE id =:movieId")
    void deleteMovieById(Long movieId);
}
