package com.example.news.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.news.fragment.NewsFragment;
import com.example.news.fragment.ProfileFragment;

/**
 * 主页 ViewPager 适配器
 * 管理主页的两个 Fragment：新闻页面和个人中心页面
 */
public class MainPagerAdapter extends FragmentStateAdapter {

    /**
     * 构造函数
     * @param fragmentActivity FragmentActivity 实例
     */
    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * 根据位置创建对应的 Fragment
     * @param position 页面位置
     * @return 对应位置的 Fragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NewsFragment();      // 新闻页面
            case 1:
                return new ProfileFragment();   // 个人中心页面
            default:
                return new NewsFragment();
        }
    }

    /**
     * 获取页面总数
     * @return 页面数量（2个：新闻 + 个人中心）
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
