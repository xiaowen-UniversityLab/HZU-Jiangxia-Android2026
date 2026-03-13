package com.example.news.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.news.R;
import com.example.news.adapter.NewsPagerAdapter;
import com.example.news.model.NewsCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 新闻主页面 Fragment
 * 包含 TabLayout 和 ViewPager2，展示不同分类的新闻
 * 支持左右滑动切换分类
 */
public class NewsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private NewsPagerAdapter pagerAdapter;

    /**
     * 创建 Fragment 视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_tabs, container, false);
    }

    /**
     * 视图创建完成后的初始化
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViewPager();
        setupTabLayout();
    }

    /**
     * 初始化视图组件
     *
     * @param view 根视图
     */
    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
    }

    /**
     * 设置 ViewPager2
     * 配置适配器和页面缓存数量
     */
    private void setupViewPager() {
        // 创建适配器，传入 FragmentManager 和生命周期
        pagerAdapter = new NewsPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);

        // 设置离屏页面缓存数量，提高滑动流畅度
        viewPager.setOffscreenPageLimit(3);

        // 页面切换监听（可根据需要添加业务逻辑）
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 页面选中时的回调，可用于埋点统计等
            }
        });
    }

    /**
     * 设置 TabLayout
     * 将 TabLayout 和 ViewPager2 关联，设置 Tab 标题和样式
     */
    private void setupTabLayout() {
        // 使用 TabLayoutMediator 关联 TabLayout 和 ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // 设置每个 Tab 的标题
            tab.setText(NewsCategory.CATEGORY_NAMES[position]);
        }).attach();

        // 设置 Tab 模式为可滚动（当 Tab 数量较多时）
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        // 设置 Tab 居中显示
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
    }
}
