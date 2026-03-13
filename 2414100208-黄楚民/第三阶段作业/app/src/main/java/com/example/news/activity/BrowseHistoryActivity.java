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
import com.example.news.adapter.HistoryAdapter;
import com.example.news.database.entity.BrowseHistory;
import com.example.news.repository.BrowseHistoryRepository;
import com.example.news.utils.SessionManager;

import java.util.List;

/**
 * 浏览历史页面 Activity
 * 显示用户的所有浏览历史记录，支持查看详情、删除单条、清空全部
 */
public class BrowseHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private HistoryAdapter adapter;
    private BrowseHistoryRepository repository;
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
            getSupportActionBar().setTitle("浏览记录");
        }
    }

    /**
     * 设置 RecyclerView
     * 配置适配器、布局管理器和点击事件
     */
    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        
        // 设置点击监听器（跳转到详情页）
        adapter.setOnItemClickListener(item -> {
            NewsDetailActivity.start(this, item.getTitle(), item.getNewsUrl());
        });
        
        // 设置删除监听器
        adapter.setOnDeleteClickListener(this::deleteHistory);
        
        // 设置布局管理器和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载浏览历史数据
     */
    private void loadData() {
        sessionManager = new SessionManager(this);
        repository = new BrowseHistoryRepository(this);
        int userId = sessionManager.getUserId();
        
        // 使用 LiveData 观察数据变化，自动更新 UI
        repository.getBrowseHistory(userId).observe(this, this::updateUI);
    }

    /**
     * 更新 UI
     * 根据数据是否为空显示列表或空视图
     * @param list 历史记录列表数据
     */
    private void updateUI(List<BrowseHistory> list) {
        if (list == null || list.isEmpty()) {
            // 显示空视图
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setText("暂无浏览记录");
        } else {
            // 显示列表
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setData(list);
        }
    }

    /**
     * 删除单条历史记录
     * @param history 要删除的历史记录
     */
    private void deleteHistory(BrowseHistory history) {
        repository.deleteById(history.getId(), success -> {
            if (!success) {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 清空所有浏览历史
     * 弹出确认对话框，确认后删除所有记录
     */
    private void clearAllHistory() {
        new AlertDialog.Builder(this)
                .setTitle("确认清空")
                .setMessage("确定要清空所有浏览记录吗？")
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
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_clear) {
            clearAllHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
