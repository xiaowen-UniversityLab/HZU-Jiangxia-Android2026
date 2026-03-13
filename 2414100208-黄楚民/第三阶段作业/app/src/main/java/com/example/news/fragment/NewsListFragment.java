package com.example.news.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.news.R;
import com.example.news.activity.NewsDetailActivity;
import com.example.news.adapter.NewsListAdapter;
import com.example.news.model.NewsItem;
import com.example.news.model.NewsResponse;
import com.example.news.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 新闻列表 Fragment
 * 展示特定分类的新闻列表，支持下拉刷新和上拉加载更多
 */
public class NewsListFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private static final int PAGE_SIZE = 20;
    private String category;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsListAdapter adapter;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    /**
     * 创建 Fragment 实例的工厂方法
     *
     * @param category 新闻分类
     * @return NewsListFragment 实例
     */
    public static NewsListFragment newInstance(String category) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment 创建时获取参数
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }
    }

    /**
     * 创建 Fragment 视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    /**
     * 视图创建完成后的初始化
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadData(true);
    }

    /**
     * 初始化视图组件
     *
     * @param view 根视图
     */
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    /**
     * 设置 RecyclerView
     * 配置适配器、布局管理器和滚动监听（用于加载更多）
     */
    private void setupRecyclerView() {
        // 创建适配器
        adapter = new NewsListAdapter();

        // 设置点击监听
        adapter.setOnItemClickListener(item -> {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                // 获取附加信息
                String imageUrl = item.getThumbnailPicS() != null ? item.getThumbnailPicS() : "";
                String author = item.getAuthorName() != null ? item.getAuthorName() : "";
                String newsCategory = item.getCategory() != null ? item.getCategory() : "";

                // 跳转到新闻详情页
                NewsDetailActivity.start(requireContext(), item.getTitle(), item.getUrl(),
                        imageUrl, author, newsCategory);
            } else {
                Toast.makeText(getContext(), "暂无详情链接", Toast.LENGTH_SHORT).show();
            }
        });

        // 创建布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // 添加滚动监听，实现加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 获取当前可见的 item 数量
                int visibleItemCount = layoutManager.getChildCount();
                // 获取总 item 数量
                int totalItemCount = layoutManager.getItemCount();
                // 获取第一个可见 item 的位置
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // 判断是否满足加载更多条件
                if (!isLoading && hasMoreData) {
                    // 当可见项数 + 第一个可见位置 >= 总数时，表示滚动到底部
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMore();  // 加载更多数据
                    }
                }
            }
        });
    }

    /**
     * 设置下拉刷新
     * 监听刷新事件，重置页码并重新加载数据
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;        // 重置页码
            hasMoreData = true;     // 重置加载状态
            loadData(true);         // 重新加载数据
        });
    }

    /**
     * 加载数据
     *
     * @param isRefresh 是否为刷新操作
     */
    private void loadData(boolean isRefresh) {
        // 如果正在加载，则直接返回
        if (isLoading) return;

        // 标记为正在加载
        isLoading = true;

        // 调用 API 获取新闻列表
        RetrofitClient.getInstance().getNewsApiService()
                .getNewsList(RetrofitClient.API_KEY, category, currentPage, PAGE_SIZE, 0)
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<NewsResponse> call,
                                           @NonNull Response<NewsResponse> response) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);  // 停止刷新动画

                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            if (newsResponse.isSuccess() && newsResponse.getResult() != null) {
                                List<NewsItem> data = newsResponse.getResult().getData();
                                if (data != null) {
                                    // 根据是否是刷新操作选择设置数据或添加数据
                                    if (isRefresh) {
                                        adapter.setData(data);
                                    } else {
                                        adapter.addData(data);
                                    }

                                    // 检查是否还有更多数据
                                    hasMoreData = data.size() >= PAGE_SIZE;
                                }
                            } else {
                                // 请求失败，显示错误信息
                                String msg = newsResponse.getReason() != null ?
                                        newsResponse.getReason() : "请求失败";
                                if (isRefresh) {
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // 网络请求失败
                            if (isRefresh) {
                                Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);  // 停止刷新动画
                        if (isRefresh) {
                            Toast.makeText(getContext(), "网络错误: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 加载更多数据
     * 页码加 1 后加载下一页
     */
    private void loadMore() {
        if (!hasMoreData) return;  // 没有更多数据则返回
        currentPage++;             // 页码加 1
        loadData(false);           // 加载数据（非刷新）
    }

    /**
     * 刷新数据（供外部调用）
     */
    public void refresh() {
        if (recyclerView != null) {
            currentPage = 1;
            hasMoreData = true;
            loadData(true);
        }
    }
}
