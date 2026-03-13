package com.example.news.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.example.news.R;
import com.example.news.activity.BrowseHistoryActivity;
import com.example.news.activity.FavoriteActivity;
import com.example.news.activity.LoginActivity;
import com.example.news.database.entity.User;
import com.example.news.repository.BrowseHistoryRepository;
import com.example.news.repository.FavoriteRepository;
import com.example.news.repository.UserRepository;
import com.example.news.utils.AnimationUtils;
import com.example.news.utils.SessionManager;

/**
 * 个人中心 Fragment
 * 显示用户信息、浏览统计、收藏统计，提供功能入口
 */
public class ProfileFragment extends Fragment {

    // 视图组件
    private TextView tvUsername;        // 用户名文本
    private TextView tvWelcome;         // 欢迎文本
    private TextView tvHistoryCount;    // 浏览记录数量
    private TextView tvFavoriteCount;   // 收藏数量
    private LinearLayout cardLogout;        // 退出登录卡片
    private LinearLayout cardHistory;   // 浏览记录入口
    private LinearLayout cardFavorite;  // 收藏入口

    // 数据仓库
    private SessionManager sessionManager;              // 会话管理
    private UserRepository userRepository;              // 用户数据
    private BrowseHistoryRepository historyRepository;  // 浏览历史
    private FavoriteRepository favoriteRepository;      // 收藏数据

    // LiveData 观察对象
    private LiveData<User> userLiveData;

    /**
     * 创建 Fragment 视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * 视图创建完成后的初始化
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化各种管理器和仓库
        sessionManager = new SessionManager(requireContext());
        userRepository = new UserRepository(requireContext());
        historyRepository = new BrowseHistoryRepository(requireContext());
        favoriteRepository = new FavoriteRepository(requireContext());
        initViews(view);
        loadUserInfo();
        loadStats();
        setupListeners();
        playCardsEnterAnimation(view);
    }

    /**
     * 播放各卡片的进入动画
     * 依次从上到下播放，产生依次滑入的效果
     *
     * @param rootView 根视图
     */
    private void playCardsEnterAnimation(View rootView) {
        View cardUser = rootView.findViewById(R.id.cardUserInfo);
        View cardStats = rootView.findViewById(R.id.cardStats);
        View cardFunctions = rootView.findViewById(R.id.cardFunctions);
        long delay = 100;
        if (cardUser != null) {
            AnimationUtils.playEnterAnimation(cardUser, delay);
            delay += 100;
        }
        if (cardStats != null) {
            AnimationUtils.playEnterAnimation(cardStats, delay);
            delay += 100;
        }
        if (cardFunctions != null) {
            AnimationUtils.playEnterAnimation(cardFunctions, delay);
            delay += 100;
        }
        if (cardLogout != null) {
            AnimationUtils.playEnterAnimation(cardLogout, delay);
        }
    }

    /**
     * 初始化视图组件
     *
     * @param view 根视图
     */
    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tvUsername);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvHistoryCount = view.findViewById(R.id.tvHistoryCount);
        tvFavoriteCount = view.findViewById(R.id.tvFavoriteCount);
        cardLogout = view.findViewById(R.id.cardLogout);
        cardHistory = view.findViewById(R.id.cardHistory);
        cardFavorite = view.findViewById(R.id.cardFavorite);
    }

    /**
     * 加载用户信息
     * 从数据库获取用户数据并显示
     */
    private void loadUserInfo() {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            // 使用 LiveData 观察用户数据变化
            userLiveData = userRepository.getUserById(userId);
            userLiveData.observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    // 显示用户名
                    tvUsername.setText(user.getUsername());
                    // 显示欢迎语
                    tvWelcome.setText(getString(R.string.welcome) + "，" + user.getUsername());
                }
            });
        }
    }

    /**
     * 加载统计数据
     * 包括浏览记录数量和收藏数量
     */
    private void loadStats() {
        int userId = sessionManager.getUserId();

        // 加载浏览记录数量（带动画）
        historyRepository.getBrowseCount(userId).observe(getViewLifecycleOwner(), count -> {
            int targetCount = count != null ? count : 0;
            animateNumberChange(tvHistoryCount, targetCount);
        });

        // 加载收藏数量（带动画）
        favoriteRepository.getFavoriteCount(userId).observe(getViewLifecycleOwner(), count -> {
            int targetCount = count != null ? count : 0;
            animateNumberChange(tvFavoriteCount, targetCount);
        });
    }

    /**
     * 数字变化动画
     * 让数字从当前值滚动到目标值
     *
     * @param textView     显示数字的文本控件
     * @param targetNumber 目标数字
     */
    private void animateNumberChange(TextView textView, int targetNumber) {
        // 获取当前显示的数字
        String currentText = textView.getText().toString();
        int startNumber = 0;
        try {
            startNumber = Integer.parseInt(currentText);
        } catch (NumberFormatException e) {
            startNumber = 0;
        }

        // 播放数字滚动动画，时长 800ms
        AnimationUtils.animateNumberCounter(textView, startNumber, targetNumber, 800);
    }

    /**
     * 设置各种点击监听器
     */
    private void setupListeners() {
        // 退出登录点击事件
        cardLogout.setOnClickListener(v -> {
            // 清除登录会话
            sessionManager.clearSession();
            // 显示提示
            Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
            // 跳转到登录页面
            startActivity(new Intent(getContext(), LoginActivity.class));
            // 结束当前 Activity
            requireActivity().finish();
        });

        // 浏览记录入口点击事件
        cardHistory.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), BrowseHistoryActivity.class));
        });

        // 收藏入口点击事件
        cardFavorite.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        });
    }

    /**
     * 视图销毁时的清理工作
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 移除 LiveData 观察者，避免内存泄漏
        if (userLiveData != null) {
            userLiveData.removeObservers(getViewLifecycleOwner());
        }
    }
}
