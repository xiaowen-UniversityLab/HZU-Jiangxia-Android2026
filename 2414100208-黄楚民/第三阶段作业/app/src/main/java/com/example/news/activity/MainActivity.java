package com.example.news.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news.R;
import com.example.news.adapter.MainPagerAdapter;
import com.example.news.databinding.ActivityMainBinding;

/**
 * 主页面 Activity
 * 应用的主界面，包含新闻列表和个人中心两个页面
 * 使用 ViewPager2 + BottomNavigationView 实现页面切换
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainPagerAdapter pagerAdapter;

    /**
     * 页面创建时初始化
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupViewPager();
        setupBottomNavigation();
    }

    /**
     * 设置 ViewPager2
     * 配置页面适配器，禁用用户滑动以防止误触切换页面
     */
    private void setupViewPager() {
        pagerAdapter = new MainPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setUserInputEnabled(false);
    }

    /**
     * 设置底部导航栏
     * 监听导航项选择，切换对应的页面
     */
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int currentItem = binding.viewPager.getCurrentItem();
            int newItem = -1;
            if (itemId == R.id.nav_news) {
                newItem = 0;
            } else if (itemId == R.id.nav_profile) {
                newItem = 1;
            }
            if (newItem != -1 && currentItem != newItem) {
                binding.viewPager.setCurrentItem(newItem, true);
                return true;
            }
            return false;
        });
    }

    /**
     * 页面销毁时释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
