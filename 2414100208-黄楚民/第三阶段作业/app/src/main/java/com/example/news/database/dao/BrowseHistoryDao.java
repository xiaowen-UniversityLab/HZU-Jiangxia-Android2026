package com.example.news.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.news.database.entity.BrowseHistory;

import java.util.List;

@Dao
public interface BrowseHistoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BrowseHistory history);
    
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY browseTime DESC")
    LiveData<List<BrowseHistory>> getBrowseHistoryByUser(int userId);
    
    @Query("SELECT * FROM browse_history WHERE userId = :userId ORDER BY browseTime DESC LIMIT :limit")
    LiveData<List<BrowseHistory>> getBrowseHistoryByUser(int userId, int limit);
    
    @Query("SELECT EXISTS(SELECT 1 FROM browse_history WHERE userId = :userId AND newsUrl = :newsUrl)")
    boolean isNewsBrowsed(int userId, String newsUrl);
    
    @Delete
    void delete(BrowseHistory history);
    
    @Query("DELETE FROM browse_history WHERE userId = :userId")
    void deleteAllByUser(int userId);
    
    @Query("DELETE FROM browse_history WHERE id = :id")
    void deleteById(int id);
    
    @Query("SELECT COUNT(*) FROM browse_history WHERE userId = :userId")
    LiveData<Integer> getBrowseCount(int userId);
}
