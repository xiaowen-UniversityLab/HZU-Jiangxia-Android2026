package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DogImageDao {
    @Insert
    void insert(DogImage dogImage);

    @Query("SELECT * FROM dog ORDER BY timestamp DESC LIMIT 20")
    List<DogImage> getLatest20Images();

    @Query("DELETE FROM dog WHERE id NOT IN (SELECT id FROM dog ORDER BY timestamp DESC LIMIT 20)")
    void deleteOldImages();
}