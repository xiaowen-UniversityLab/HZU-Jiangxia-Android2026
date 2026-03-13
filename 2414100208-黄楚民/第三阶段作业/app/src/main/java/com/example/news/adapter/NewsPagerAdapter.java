package com.example.news.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.news.model.NewsCategory;
import com.example.news.fragment.NewsListFragment;

/**
 * 新闻分类 ViewPager 适配器
 * 管理不同分类的新闻列表 Fragment
 * 每个分类对应一个 NewsListFragment
 */
public class NewsPagerAdapter extends FragmentStateAdapter {

    /**
     * 构造函数
     * @param fragmentManager FragmentManager 实例
     * @param lifecycle       Lifecycle 实例
     */
    public NewsPagerAdapter(@NonNull FragmentManager fragmentManager, 
                           @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * 根据位置创建对应分类的 Fragment
     * @param position 页面位置（对应分类索引）
     * @return 对应分类的新闻列表 Fragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置获取分类名称，创建对应的新闻列表 Fragment
        return NewsListFragment.newInstance(NewsCategory.CATEGORIES[position]);
    }

    /**
     * 获取页面总数
     * @return 分类数量
     */
    @Override
    public int getItemCount() {
        return NewsCategory.CATEGORIES.length;
    }
}
