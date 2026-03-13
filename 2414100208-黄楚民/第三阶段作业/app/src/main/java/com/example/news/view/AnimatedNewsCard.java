package com.example.news.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.news.R;
import com.example.news.model.NewsItem;

/**
 * 带动画效果的新闻卡片控件
 * 功能包括：点击缩放动画、Shimmer加载效果、图片淡入、进入动画
 */
public class AnimatedNewsCard extends CardView {

    // 视图组件
    private ImageView ivThumbnail;      // 新闻缩略图
    private TextView tvTitle;           // 新闻标题
    private TextView tvSource;          // 新闻来源
    private TextView tvTime;            // 发布时间
    private TextView tvCategory;        // 新闻分类
    private View shimmerContainer;      // Shimmer骨架屏容器
    // 回调和数据
    private OnCardClickListener clickListener;  // 卡片点击监听器
    private NewsItem currentItem;               // 当前显示的新闻数据

    /**
     * 卡片点击回调接口
     */
    public interface OnCardClickListener {
        void onCardClick(NewsItem item);
    }

    /**
     * 构造函数 - 代码中动态创建时使用
     */
    public AnimatedNewsCard(@NonNull Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造函数 - XML布局中使用
     */
    public AnimatedNewsCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造函数 - 带样式属性的版本
     */
    public AnimatedNewsCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化控件
     * 加载布局、初始化视图、设置样式和监听
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_animated_news_card, this, true);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        tvTitle = findViewById(R.id.tvTitle);
        tvSource = findViewById(R.id.tvSource);
        tvTime = findViewById(R.id.tvTime);
        tvCategory = findViewById(R.id.tvCategory);
        shimmerContainer = findViewById(R.id.shimmerContainer);
        setCardElevation(8f);
        setRadius(16f);
        setCardBackgroundColor(context.getResources().getColor(R.color.white, null));
        setupTouchAnimation();
        setOnClickListener(v -> {
            if (clickListener != null && currentItem != null) {
                clickListener.onCardClick(currentItem);
            }
        });
    }

    /**
     * 设置触摸动画效果
     * 按下时缩小，松开时恢复并带弹性效果
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setupTouchAnimation() {
        setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 手指按下：缩小到 96%
                    animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).setInterpolator(new DecelerateInterpolator()).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 手指松开：恢复原始大小，带弹性效果
                    animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new OvershootInterpolator(1.5f)).start();
                    break;
            }
            return false;  // 返回 false 表示不消费事件，允许点击事件继续传递
        });
    }

    /**
     * 设置新闻数据
     *
     * @param item 新闻数据对象
     */
    public void setNewsItem(NewsItem item) {
        this.currentItem = item;
        if (item == null) return;
        showShimmer();
        tvTitle.setText(item.getTitle());
        tvSource.setText(item.getAuthorName());
        tvTime.setText(item.getDate());
        tvCategory.setText(item.getCategory());
        loadImage(item.getThumbnailPicS());
    }

    /**
     * 加载网络图片
     *
     * @param imageUrl 图片URL地址
     */
    private void loadImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(getContext()).load(imageUrl).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground).transition(DrawableTransitionOptions.withCrossFade(400)).into(ivThumbnail);
        } else {
            ivThumbnail.setImageResource(R.drawable.ic_launcher_foreground);
        }
        ivThumbnail.postDelayed(this::hideShimmer, 500);
    }

    /**
     * 显示 Shimmer 骨架屏效果
     * 用于图片加载时的占位动画
     */
    private void showShimmer() {
        if (shimmerContainer != null) {
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.setAlpha(1f);
            ObjectAnimator shimmerAnim = ObjectAnimator.ofFloat(shimmerContainer, "alpha", 1f, 0.5f, 1f);
            shimmerAnim.setDuration(1500);
            shimmerAnim.setRepeatCount(ValueAnimator.INFINITE);
            shimmerAnim.start();
        }
    }

    /**
     * 隐藏 Shimmer 骨架屏
     * 带淡出动画效果
     */
    private void hideShimmer() {
        if (shimmerContainer != null && shimmerContainer.getVisibility() == View.VISIBLE) {
            shimmerContainer.animate().alpha(0f).setDuration(300).withEndAction(() -> shimmerContainer.setVisibility(View.GONE)).start();
        }
    }

    /**
     * 设置卡片点击监听器
     *
     * @param listener 点击回调接口
     */
    public void setOnCardClickListener(OnCardClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * 播放进入动画
     * 卡片从下方滑入并淡入显示
     *
     * @param delay 动画延迟时间（毫秒）
     */
    public void playEnterAnimation(long delay) {
        setAlpha(0f);
        setTranslationY(100f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(this, "translationY", 100f, 0f);
        animatorSet.playTogether(alphaAnim, translateAnim);
        animatorSet.setDuration(400);
        animatorSet.setStartDelay(delay);
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
        animatorSet.start();
    }

    /**
     * 获取当前新闻数据
     *
     * @return 当前显示的新闻对象
     */
    public NewsItem getNewsItem() {
        return currentItem;
    }
}
