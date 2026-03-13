package com.example.news.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.news.R;
import com.example.news.database.entity.BrowseHistory;
import com.example.news.database.entity.Favorite;
import com.example.news.repository.BrowseHistoryRepository;
import com.example.news.repository.FavoriteRepository;
import com.example.news.utils.SessionManager;

/**
 * 新闻详情页面 Activity
 * 使用 WebView 加载新闻网页，支持收藏、分享等功能
 */
public class NewsDetailActivity extends AppCompatActivity {

    // Intent 参数 key
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_AUTHOR = "author";
    public static final String EXTRA_CATEGORY = "category";

    // 视图组件
    private WebView webView;            // 网页浏览器
    private ProgressBar progressBar;    // 加载进度条
    private Toolbar toolbar;            // 顶部工具栏
    
    // 数据仓库
    private SessionManager sessionManager;
    private BrowseHistoryRepository historyRepository;
    private FavoriteRepository favoriteRepository;
    
    // 新闻数据
    private String newsTitle;
    private String newsUrl;
    private String imageUrl;
    private String author;
    private String category;
    private boolean isFavorited = false;    // 是否已收藏
    private MenuItem favoriteMenuItem;      // 收藏菜单项

    /**
     * 页面创建时初始化
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        
        // 初始化仓库
        sessionManager = new SessionManager(this);
        historyRepository = new BrowseHistoryRepository(this);
        favoriteRepository = new FavoriteRepository(this);
        
        // 获取 Intent 传递的数据
        getIntentData();
        
        // 初始化视图
        initViews();
        
        // 设置工具栏
        setupToolbar();
        
        // 设置 WebView
        setupWebView();
        
        // 加载网页
        loadUrl();
        
        // 保存浏览记录
        saveBrowseHistory();
        
        // 检查收藏状态
        checkFavoriteStatus();
    }

    /**
     * 获取 Intent 传递的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        newsTitle = intent.getStringExtra(EXTRA_TITLE);
        newsUrl = intent.getStringExtra(EXTRA_URL);
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        author = intent.getStringExtra(EXTRA_AUTHOR);
        category = intent.getStringExtra(EXTRA_CATEGORY);
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
    }

    /**
     * 设置工具栏
     * 显示返回按钮和新闻标题
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // 显示返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            
            // 设置标题
            if (newsTitle != null && !newsTitle.isEmpty()) {
                getSupportActionBar().setTitle(newsTitle);
            } else {
                getSupportActionBar().setTitle("新闻详情");
            }
        }
    }

    /**
     * 设置 WebView
     * 配置 WebView 的各项参数，包括 JavaScript、缩放、缓存等
     */
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        
        // 启用 JavaScript 支持
        settings.setJavaScriptEnabled(true);
        
        // 支持网页缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);  // 不显示缩放按钮
        
        // 自适应屏幕大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        
        // 缓存设置（默认使用标准缓存策略）
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // 允许加载混合内容（HTTP 和 HTTPS）
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 设置 WebViewClient 处理页面加载事件
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在当前 WebView 中加载链接
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 页面开始加载时显示进度条
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面加载完成时隐藏进度条
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // 加载出错时隐藏进度条
                progressBar.setVisibility(View.GONE);
            }
        });

        // 设置 WebChromeClient 以获取加载进度
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 更新进度条进度
                progressBar.setProgress(newProgress);
            }
        });
    }

    /**
     * 加载网页 URL
     */
    private void loadUrl() {
        if (newsUrl != null && !newsUrl.isEmpty()) {
            webView.loadUrl(newsUrl);
        }
    }
    
    /**
     * 保存浏览记录到数据库
     */
    private void saveBrowseHistory() {
        int userId = sessionManager.getUserId();
        if (userId != -1 && newsUrl != null) {
            // 创建浏览历史记录对象
            BrowseHistory history = new BrowseHistory(
                    userId,
                    newsUrl,  // 使用 URL 作为 ID
                    newsTitle != null ? newsTitle : "",
                    "",
                    imageUrl != null ? imageUrl : "",
                    newsUrl,
                    category != null ? category : "",
                    author != null ? author : ""
            );
            // 插入到数据库
            historyRepository.insert(history, null);
        }
    }
    
    /**
     * 检查当前新闻是否已被收藏
     */
    private void checkFavoriteStatus() {
        int userId = sessionManager.getUserId();
        if (userId != -1 && newsUrl != null) {
            favoriteRepository.isNewsFavorited(userId, newsUrl, exists -> {
                isFavorited = exists;
                updateFavoriteIcon();  // 更新收藏图标状态
            });
        }
    }
    
    /**
     * 切换收藏状态
     * 如果已收藏则取消收藏，未收藏则添加收藏
     */
    private void toggleFavorite() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isFavorited) {
            // 取消收藏
            favoriteRepository.isNewsFavorited(userId, newsUrl, exists -> {
                if (exists) {
                    Favorite favorite = new Favorite(userId, newsUrl, newsTitle, "", 
                            imageUrl, newsUrl, category, author);
                    favoriteRepository.delete(favorite, success -> {
                        runOnUiThread(() -> {
                            if (success) {
                                isFavorited = false;
                                updateFavoriteIcon();
                                Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            });
        } else {
            // 添加收藏
            Favorite favorite = new Favorite(userId, newsUrl, newsTitle, "",
                    imageUrl, newsUrl, category, author);
            favoriteRepository.insert(favorite, success -> {
                runOnUiThread(() -> {
                    if (success) {
                        isFavorited = true;
                        updateFavoriteIcon();
                        Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
    
    /**
     * 更新收藏图标显示状态
     */
    private void updateFavoriteIcon() {
        if (favoriteMenuItem != null) {
            favoriteMenuItem.setIcon(isFavorited ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        }
    }

    /**
     * 创建选项菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_detail, menu);
        favoriteMenuItem = menu.findItem(R.id.menu_favorite);
        updateFavoriteIcon();
        return true;
    }

    /**
     * 菜单项选中处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 返回按钮
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_favorite) {
            // 收藏按钮
            toggleFavorite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回键处理
     * 如果 WebView 可以返回上一页，则返回上一页；否则结束当前 Activity
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 页面销毁时的清理工作
     * 释放 WebView 资源，防止内存泄漏
     */
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    /**
     * 启动新闻详情页面的便捷方法
     * @param context   上下文
     * @param title     新闻标题
     * @param url       新闻链接
     * @param imageUrl  新闻图片
     * @param author    作者
     * @param category  分类
     */
    public static void start(Context context, String title, String url, String imageUrl, 
                            String author, String category) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(EXTRA_AUTHOR, author);
        intent.putExtra(EXTRA_CATEGORY, category);
        context.startActivity(intent);
        // 添加 Activity 转场动画
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    
    /**
     * 启动新闻详情页面的简化方法（只传标题和链接）
     * @param context 上下文
     * @param title   新闻标题
     * @param url     新闻链接
     */
    public static void start(Context context, String title, String url) {
        start(context, title, url, null, null, null);
    }
}
