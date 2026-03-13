package com.example.news.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.R;
import com.example.news.adapter.FavoriteAdapter;
import com.example.news.database.entity.Favorite;
import com.example.news.repository.FavoriteRepository;
import com.example.news.utils.SessionManager;

import java.util.List;

/**
 * 收藏列表页面 Activity
 * 显示用户的所有收藏新闻，支持查看详情、删除单条、清空全部
 */
public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FavoriteAdapter adapter;
    private FavoriteRepository repository;
    private SessionManager sessionManager;

    /**
     * 页面创建时初始化
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadData();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    /**
     * 设置工具栏
     * 显示返回按钮和标题
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("我的收藏");
        }
    }

    /**
     * 设置 RecyclerView
     * 配置适配器、布局管理器和点击事件
     */
    private void setupRecyclerView() {
        adapter = new FavoriteAdapter();
        
        // 设置点击监听器（跳转到详情页）
        adapter.setOnItemClickListener(item -> {
            NewsDetailActivity.start(this, item.getTitle(), item.getNewsUrl());
        });
        
        // 设置删除监听器
        adapter.setOnDeleteClickListener(this::deleteFavorite);
        
        // 设置布局管理器和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载收藏数据
     */
    private void loadData() {
        sessionManager = new SessionManager(this);
        repository = new FavoriteRepository(this);
        int userId = sessionManager.getUserId();
        
        // 使用 LiveData 观察数据变化，自动更新 UI
        repository.getFavorites(userId).observe(this, this::updateUI);
    }

    /**
     * 更新 UI
     * 根据数据是否为空显示列表或空视图
     * @param list 收藏列表数据
     */
    private void updateUI(List<Favorite> list) {
        if (list == null || list.isEmpty()) {
            // 显示空视图
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setText("暂无收藏");
        } else {
            // 显示列表
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(list);
        }
    }

    /**
     * 删除单条收藏
     * @param favorite 要删除的收藏项
     */
    private void deleteFavorite(Favorite favorite) {
        repository.deleteById(favorite.getId(), success -> {
            if (!success) {
                Toast.makeText(this, "取消收藏失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 清空所有收藏
     * 弹出确认对话框，确认后删除所有收藏
     */
    private void clearAllFavorites() {
        new AlertDialog.Builder(this)
                .setTitle("确认清空")
                .setMessage("确定要清空所有收藏吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    repository.deleteAll(sessionManager.getUserId(), success -> {
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "已清空", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "清空失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return true;
    }

    /**
     * 菜单项选中处理
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回按钮
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_clear) {
            // 清空按钮
            clearAllFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
