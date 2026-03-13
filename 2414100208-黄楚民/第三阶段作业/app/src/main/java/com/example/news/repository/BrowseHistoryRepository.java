package com.example.news.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.news.database.AppDatabase;
import com.example.news.database.entity.BrowseHistory;
import com.example.news.database.dao.BrowseHistoryDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrowseHistoryRepository {
    
    private final BrowseHistoryDao browseHistoryDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public BrowseHistoryRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        browseHistoryDao = database.browseHistoryDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public LiveData<List<BrowseHistory>> getBrowseHistory(int userId) {
        return browseHistoryDao.getBrowseHistoryByUser(userId);
    }
    
    public LiveData<List<BrowseHistory>> getBrowseHistory(int userId, int limit) {
        return browseHistoryDao.getBrowseHistoryByUser(userId, limit);
    }
    
    public void insert(BrowseHistory history, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                browseHistoryDao.insert(history);
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(false);
                });
            }
        });
    }
    
    public void delete(BrowseHistory history, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                browseHistoryDao.delete(history);
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(false);
                });
            }
        });
    }
    
    public void deleteById(int id, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                browseHistoryDao.deleteById(id);
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(false);
                });
            }
        });
    }
    
    public void deleteAll(int userId, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                browseHistoryDao.deleteAllByUser(userId);
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(true);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onComplete(false);
                });
            }
        });
    }
    
    public void isNewsBrowsed(int userId, String newsUrl, OnCheckCallback callback) {
        executorService.execute(() -> {
            boolean exists = browseHistoryDao.isNewsBrowsed(userId, newsUrl);
            mainHandler.post(() -> callback.onResult(exists));
        });
    }
    
    public LiveData<Integer> getBrowseCount(int userId) {
        return browseHistoryDao.getBrowseCount(userId);
    }
    
    public interface OnCompleteCallback {
        void onComplete(boolean success);
    }
    
    public interface OnCheckCallback {
        void onResult(boolean exists);
    }
}
