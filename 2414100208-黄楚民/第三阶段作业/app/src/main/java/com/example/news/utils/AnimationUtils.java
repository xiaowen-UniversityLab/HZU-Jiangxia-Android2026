package com.example.news.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * 动画工具类
 * 提供各种常用的动画效果封装
 */
public class AnimationUtils {

    /**
     * 数字滚动动画
     * 让 TextView 中的数字从 start 滚动到 end
     *
     * @param textView 要显示数字的文本控件
     * @param start    起始数字
     * @param end      结束数字
     * @param duration 动画时长（毫秒）
     */
    public static void animateNumberCounter(TextView textView, int start, int end, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);                     // 设置动画时长
        animator.setInterpolator(new DecelerateInterpolator());  // 使用减速插值器，滚动逐渐变慢
        animator.addUpdateListener(animation -> {
            textView.setText(String.valueOf(animation.getAnimatedValue()));
        });
        animator.start();  // 开始动画
    }

    /**
     * 快速数字滚动动画
     * 从 0 开始滚动，默认时长 1 秒
     *
     * @param textView 要显示数字的文本控件
     * @param end      结束数字
     */
    public static void animateNumberCounter(TextView textView, int end) {
        animateNumberCounter(textView, 0, end, 1000);
    }

    /**
     * View 进入动画
     * 从下方滑入并淡入显示
     *
     * @param view  要执行动画的视图
     * @param delay 动画延迟时间（毫秒）
     */
    public static void playEnterAnimation(View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(100f);
        view.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(delay).setInterpolator(new DecelerateInterpolator(1.5f)).start();
    }

    /**
     * View 缩放进入动画
     * 从小到大缩放并淡入显示
     *
     * @param view  要执行动画的视图
     * @param delay 动画延迟时间（毫秒）
     */
    public static void playScaleEnterAnimation(View view, long delay) {
        // 初始状态：完全透明，缩放到 80%
        view.setAlpha(0f);
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);

        // 执行属性动画
        view.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(400).setStartDelay(delay).setInterpolator(new DecelerateInterpolator(1.5f)).start();
    }

    /**
     * 脉冲动画
     * 视图先放大再缩回，用于强调或提示效果
     *
     * @param view 要执行动画的视图
     */
    public static void playPulseAnimation(View view) {
        // 第一阶段：放大到 110%
        view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).withEndAction(() -> {
            view.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
        }).start();
    }

    /**
     * 摇晃动画
     * 视图左右摇晃，用于错误提示或警告
     * 摇晃模式：左 -> 右 -> 左 -> 右 -> 回中
     *
     * @param view 要执行动画的视图
     */
    public static void playShakeAnimation(View view) {
        view.animate().translationX(-20f).setDuration(50).withEndAction(() -> {
            view.animate().translationX(20f).setDuration(50).withEndAction(() -> {
                view.animate().translationX(-15f).setDuration(50).withEndAction(() -> {
                    view.animate().translationX(15f).setDuration(50).withEndAction(() -> {
                        view.animate().translationX(0f).setDuration(50).start();
                    }).start();
                }).start();
            }).start();
        }).start();
    }
}
