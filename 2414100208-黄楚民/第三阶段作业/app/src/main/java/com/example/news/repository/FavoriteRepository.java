package com.example.news.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.news.database.AppDatabase;
import com.example.news.database.entity.Favorite;
import com.example.news.database.dao.FavoriteDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteRepository {
    
    private final FavoriteDao favoriteDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public FavoriteRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        favoriteDao = database.favoriteDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public LiveData<List<Favorite>> getFavorites(int userId) {
        return favoriteDao.getFavoritesByUser(userId);
    }
    
    public LiveData<List<Favorite>> getFavorites(int userId, int limit) {
        return favoriteDao.getFavoritesByUser(userId, limit);
    }
    
    public void insert(Favorite favorite, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                favoriteDao.insert(favorite);
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
    
    public void delete(Favorite favorite, OnCompleteCallback callback) {
        executorService.execute(() -> {
            try {
                favoriteDao.delete(favorite);
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
                favoriteDao.deleteById(id);
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
                favoriteDao.deleteAllByUser(userId);
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
    
    public void isNewsFavorited(int userId, String newsUrl, OnCheckCallback callback) {
        executorService.execute(() -> {
            boolean exists = favoriteDao.isNewsFavorited(userId, newsUrl);
            mainHandler.post(() -> callback.onResult(exists));
        });
    }
    
    public LiveData<Integer> getFavoriteCount(int userId) {
        return favoriteDao.getFavoriteCount(userId);
    }
    
    public interface OnCompleteCallback {
        void onComplete(boolean success);
    }
    
    public interface OnCheckCallback {
        void onResult(boolean exists);
    }
}
