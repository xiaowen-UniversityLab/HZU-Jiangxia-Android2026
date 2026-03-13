package com.example.news.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.news.database.AppDatabase;
import com.example.news.database.entity.User;
import com.example.news.database.dao.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    
    private final UserDao userDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    
    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void register(User user, OnRegisterCallback callback) {
        executorService.execute(() -> {
            try {
                if (userDao.isUsernameExists(user.getUsername())) {
                    mainHandler.post(() -> callback.onError("用户名已存在"));
                } else {
                    userDao.insert(user);
                    mainHandler.post(() -> callback.onSuccess());
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("注册失败: " + e.getMessage()));
            }
        });
    }
    
    public void login(String username, String password, OnLoginCallback callback) {
        executorService.execute(() -> {
            User user = userDao.login(username, password);
            if (user != null) {
                userDao.updateLastLogin(user.getId(), System.currentTimeMillis());
                mainHandler.post(() -> callback.onSuccess(user));
            } else {
                mainHandler.post(() -> callback.onError("用户名或密码错误"));
            }
        });
    }
    
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);
    }
    
    public interface OnRegisterCallback {
        void onSuccess();
        void onError(String message);
    }
    
    public interface OnLoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }
}
