package com.example.news.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.news.R;
import com.example.news.utils.SessionManager;

/**
 * 欢迎页面
 * 应用启动时的过渡页面，显示 Logo 动画后跳转到登录或主页
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        playStartupAnimation();
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            findViewById(R.id.main).postDelayed(this::startLoginActivity, 1500);
        } else {
            findViewById(R.id.main).postDelayed(this::startMainActivity, 1500);
        }
    }

    /**
     * 播放启动动画
     * 包含 Logo 弹性缩放、App名称淡入滑动、版本号淡入三个部分
     */
    private void playStartupAnimation() {
        ImageView logo = findViewById(R.id.logo);
        TextView appName = findViewById(R.id.appName);
        TextView version = findViewById(R.id.version);
        if (logo == null || appName == null) return;
        logo.setScaleX(0f);
        logo.setScaleY(0f);
        logo.setAlpha(0f);

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f);  // 从 0 缩放到 1
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0f, 1f);  // 从 0 缩放到 1
        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);    // 从透明到显示

        AnimatorSet logoSet = new AnimatorSet();
        logoSet.playTogether(logoScaleX, logoScaleY, logoAlpha);
        logoSet.setDuration(800);
        logoSet.setInterpolator(new OvershootInterpolator(1.2f));

        appName.setAlpha(0f);
        appName.setTranslationY(30f);

        ObjectAnimator nameAlpha = ObjectAnimator.ofFloat(appName, "alpha", 0f, 1f);
        ObjectAnimator nameTranslate = ObjectAnimator.ofFloat(appName, "translationY", 30f, 0f);

        AnimatorSet nameSet = new AnimatorSet();
        nameSet.playTogether(nameAlpha, nameTranslate);
        nameSet.setDuration(600);
        nameSet.setStartDelay(400);

        if (version != null) {
            version.setAlpha(0f);
            ObjectAnimator versionAlpha = ObjectAnimator.ofFloat(version, "alpha", 0f, 1f);
            versionAlpha.setDuration(400);
            versionAlpha.setStartDelay(800);
            versionAlpha.start();
        }

        AnimatorSet totalSet = new AnimatorSet();
        totalSet.playTogether(logoSet, nameSet);  // Logo 和名称动画同时开始（但名称有延迟）
        totalSet.start();
    }

    /**
     * 跳转到登录页面
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到主页面
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
