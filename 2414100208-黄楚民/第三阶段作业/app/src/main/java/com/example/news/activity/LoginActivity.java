package com.example.news.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.news.R;
import com.example.news.database.entity.User;
import com.example.news.databinding.ActivityLoginBinding;
import com.example.news.repository.UserRepository;
import com.example.news.utils.SessionManager;

/**
 * 登录页面
 * 用户输入用户名和密码进行登录
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userRepository = new UserRepository(this);
        sessionManager = new SessionManager(this);
        playCardEnterAnimation();
        setupListeners();
    }

    /**
     * 播放卡片进入动画
     * 卡片从下方滑入并逐渐显示，带有缩放效果
     */
    private void playCardEnterAnimation() {
        // 获取 CardView（根布局的第一个子View）
        CardView cardView = (CardView) binding.getRoot().getChildAt(0);
        if (cardView == null) return;
        // 初始状态：透明、向下偏移、缩小到 90%
        cardView.setAlpha(0f);
        cardView.setTranslationY(100f);
        cardView.setScaleX(0.9f);
        cardView.setScaleY(0.9f);
        // 创建动画集合
        AnimatorSet animatorSet = new AnimatorSet();
        // 透明度动画：从 0 到 1
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(cardView, "alpha", 0f, 1f);
        // 位移动画：从 100 像素到 0
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(cardView, "translationY", 100f, 0f);
        // X轴缩放动画：从 0.9 到 1
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(cardView, "scaleX", 0.9f, 1f);
        // Y轴缩放动画：从 0.9 到 1
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(cardView, "scaleY", 0.9f, 1f);
        // 同时播放四个动画
        animatorSet.playTogether(alphaAnim, translateAnim, scaleXAnim, scaleYAnim);
        animatorSet.setDuration(500);                           // 动画时长 500ms
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));  // 减速插值器
        animatorSet.start();
    }

    /**
     * 设置各种监听器
     */
    private void setupListeners() {
        // 登录按钮点击事件
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        // 注册文字点击事件
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            // 添加 Activity 转场动画：从右向左滑入
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    /**
     * 尝试登录
     * 验证输入并调用登录接口
     */
    private void attemptLogin() {
        // 获取输入的用户名和密码，去除首尾空格
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // 验证用户名是否为空
        if (username.isEmpty()) {
            binding.tilUsername.setError(getString(R.string.fill_all_fields));
            return;
        }
        // 验证密码是否为空
        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.fill_all_fields));
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 调用登录接口
        userRepository.login(username, password, new UserRepository.OnLoginCallback() {
            @Override
            public void onSuccess(User user) {
                // 登录成功
                showLoading(false);
                // 保存登录会话
                sessionManager.createLoginSession(user.getId(), user.getUsername());
                // 显示成功提示
                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                // 跳转到主页面
                startMainActivity();
            }

            @Override
            public void onError(String message) {
                // 登录失败
                showLoading(false);
                // 显示错误信息
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 显示或隐藏加载状态
     *
     * @param show true 显示加载，false 隐藏加载
     */
    private void showLoading(boolean show) {
        // 显示或隐藏进度条
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        // 禁用或启用登录按钮
        binding.btnLogin.setEnabled(!show);
    }

    /**
     * 跳转到主页面
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        // 设置启动模式：新建任务栈并清空之前的 Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // 添加 Activity 转场动画：从右向左滑入
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放绑定，避免内存泄漏
        binding = null;
    }
}
