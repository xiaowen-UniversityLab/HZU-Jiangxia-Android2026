package com.example.news.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.model.NewsItem;
import com.example.news.view.AnimatedNewsCard;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻列表适配器
 * 使用 AnimatedNewsCard 自定义卡片控件显示新闻列表
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    // 新闻数据列表
    private List<NewsItem> newsList = new ArrayList<>();
    // 点击事件监听器
    private OnItemClickListener listener;

    /**
     * 设置数据（刷新列表）
     * @param list 新闻列表数据
     */
    public void setData(List<NewsItem> list) {
        this.newsList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();  // 通知 RecyclerView 刷新全部数据
    }

    /**
     * 添加数据（加载更多）
     * @param list 要添加的新闻列表
     */
    public void addData(List<NewsItem> list) {
        if (list != null && !list.isEmpty()) {
            int startPosition = newsList.size();  // 记录开始位置
            newsList.addAll(list);                // 添加新数据
            // 使用局部刷新，只刷新新增的部分，性能更好
            notifyItemRangeInserted(startPosition, list.size());
        }
    }

    /**
     * 设置点击监听器
     * @param listener 点击回调接口
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 创建 ViewHolder
     * 这里创建 AnimatedNewsCard 作为列表项视图
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 实例化自定义的 AnimatedNewsCard 控件
        AnimatedNewsCard card = new AnimatedNewsCard(parent.getContext());
        
        // 设置布局参数：宽度填满父容器，高度自适应
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(params);
        
        return new ViewHolder(card);
    }

    /**
     * 绑定数据到 ViewHolder
     * @param holder   ViewHolder 对象
     * @param position 当前项的位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取当前位置的新闻数据
        NewsItem item = newsList.get(position);
        // 绑定数据和动画
        holder.bind(item, position);
    }

    /**
     * 获取列表项总数
     * @return 新闻数量
     */
    @Override
    public int getItemCount() {
        return newsList.size();
    }

    /**
     * ViewHolder 内部类
     * 持有 AnimatedNewsCard 的引用
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // 自定义新闻卡片控件
        private final AnimatedNewsCard card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView 就是 AnimatedNewsCard
            card = (AnimatedNewsCard) itemView;
        }

        /**
         * 绑定数据并播放动画
         * @param item     新闻数据
         * @param position 列表位置
         */
        public void bind(NewsItem item, int position) {
            // 设置新闻数据到卡片
            card.setNewsItem(item);
            
            // 设置卡片点击监听器
            card.setOnCardClickListener(newsItem -> {
                if (listener != null) {
                    listener.onItemClick(newsItem);
                }
            });
            
            // 播放进入动画，每个卡片依次延迟 60ms
            // 这样会产生卡片依次滑入的波浪效果
            card.playEnterAnimation(position * 60);
        }
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        /**
         * 列表项被点击时回调
         * @param item 被点击的新闻数据
         */
        void onItemClick(NewsItem item);
    }
}
