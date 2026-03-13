package com.example.news.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.news.database.entity.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite favorite);
    
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY favoriteTime DESC")
    LiveData<List<Favorite>> getFavoritesByUser(int userId);
    
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY favoriteTime DESC LIMIT :limit")
    LiveData<List<Favorite>> getFavoritesByUser(int userId, int limit);
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND newsUrl = :newsUrl)")
    boolean isNewsFavorited(int userId, String newsUrl);
    
    @Delete
    void delete(Favorite favorite);
    
    @Query("DELETE FROM favorites WHERE userId = :userId")
    void deleteAllByUser(int userId);
    
    @Query("DELETE FROM favorites WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    LiveData<Integer> getFavoriteCount(int userId);
}
