package com.example.news.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.database.entity.Favorite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 收藏列表适配器
 * 显示用户收藏的新闻列表，支持点击查看和删除操作
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    // 收藏数据列表
    private List<Favorite> list = new ArrayList<>();
    // 点击事件监听器
    private OnItemClickListener itemClickListener;
    private OnDeleteClickListener deleteClickListener;
    // 日期格式化器
    private SimpleDateFormat dateFormat;

    /**
     * 构造函数
     * 初始化日期格式
     */
    public FavoriteAdapter() {
        dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    }

    /**
     * 设置数据
     * @param newList 收藏列表数据
     */
    public void setData(List<Favorite> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * 设置点击监听器
     * @param listener 点击回调接口
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 设置删除监听器
     * @param listener 删除回调接口
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    /**
     * 创建 ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 绑定数据到 ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorite item = list.get(position);
        holder.bind(item);
    }

    /**
     * 获取列表项总数
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ViewHolder 内部类
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        // 视图组件
        private final ImageView ivThumbnail;    // 缩略图
        private final TextView tvTitle;         // 标题
        private final TextView tvSource;        // 来源
        private final TextView tvTime;          // 时间
        private final ImageButton btnDelete;    // 删除按钮

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        /**
         * 绑定数据
         * @param item 收藏数据
         */
        void bind(Favorite item) {
            // 设置标题
            tvTitle.setText(item.getTitle());
            
            // 显示分类和作者
            String sourceText = "";
            if (!TextUtils.isEmpty(item.getCategory())) {
                sourceText += "[" + item.getCategory() + "] ";
            }
            if (!TextUtils.isEmpty(item.getAuthor())) {
                sourceText += item.getAuthor();
            }
            tvSource.setText(sourceText.isEmpty() ? "未知来源" : sourceText);
            
            // 设置收藏时间
            tvTime.setText(dateFormat.format(new Date(item.getFavoriteTime())));

            // 加载图片
            if (!TextUtils.isEmpty(item.getImageUrl())) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // 设置点击监听
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item);
                }
            });

            // 设置删除监听
            btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(item);
                }
            });
        }
    }

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(Favorite item);
    }

    /**
     * 删除回调接口
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Favorite item);
    }
}
