package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView ivContent;
    private Button btnGetImage;
    private Button btnPostTest;
    private AppDatabase appDatabase;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        ivContent = findViewById(R.id.dog);
        btnGetImage = findViewById(R.id.button);
        btnPostTest = findViewById(R.id.button2);
        if (ivContent == null || btnGetImage == null || btnPostTest == null) {
            Toast.makeText(this, "控件初始化失败", Toast.LENGTH_SHORT).show();
            finish(); // 防止后续崩溃
            return;
        }

        // 初始化工具类
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS) // 超时设置
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        appDatabase = AppDatabase.getInstance(this);

        // GET按钮：有网请求，无网提示
        btnGetImage.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(MainActivity.this, "网络异常，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            // 防止重复点击
            btnGetImage.setEnabled(false);
            fetchDogImage();
        });

        // POST按钮：有网请求存数据，无网加载缓存
        btnPostTest.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                btnPostTest.setEnabled(false);
                performPostRequest();
            } else {
                loadCachedDataFromDB();
            }
        });
    }

    // 检查网络
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
        } catch (Exception e) {
            Log.e(TAG, "检查网络异常", e);
            return false;
        }
    }

    // GET请求
    private void fetchDogImage() {
        Request request = new Request.Builder()
                .url("https://dog.ceo/api/breeds/image/random")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "GET请求失败", e);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "GET请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnGetImage.setEnabled(true); // 恢复按钮
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 最终要在主线程恢复按钮
                runOnUiThread(() -> btnGetImage.setEnabled(true));

                if (!response.isSuccessful()) {
                    Log.e(TAG, "GET响应失败：" + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "响应失败：" + response.code(), Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    // 读取响应并解析
                    String responseBody = response.body().string();
                    if (responseBody == null || responseBody.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "响应数据为空", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responseBody);
                    String imageUrl = jsonObject.optString("message", ""); 
                    if (imageUrl.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "解析图片URL失败", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // 保存到数据库（子线程，即使允许主线程也保持规范）
                    new Thread(() -> {
                        try {
                            appDatabase.dogImageDao().insert(new DogImage(imageUrl, System.currentTimeMillis()));
                            appDatabase.dogImageDao().deleteOldImages();
                            Log.d(TAG, "图片URL已保存到数据库：" + imageUrl);
                        } catch (Exception e) {
                            Log.e(TAG, "保存数据库失败", e);
                        }
                    }).start();

                    // 主线程加载图片
                    runOnUiThread(() -> {
                        try {
                            Glide.with(MainActivity.this)
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存图片
                                    .error(android.R.drawable.ic_delete) // 加载失败显示默认图
                                    .into(ivContent);
                            Toast.makeText(MainActivity.this, "GET请求成功，图片已展示", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "加载图片失败", e);
                            Toast.makeText(MainActivity.this, "加载图片失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON解析失败", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e(TAG, "处理响应异常", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "处理数据异常", Toast.LENGTH_SHORT).show());
                } finally {
                    response.body().close(); // 必须关闭响应体
                }
            }
        });
    }

    // POST请求
    private void performPostRequest() {
        RequestBody body = new FormBody.Builder()
                .add("test_key", "test_value")
                .build();

        Request request = new Request.Builder()
                .url("https://httpbin.org/post")
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "POST请求失败", e);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "POST请求失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPostTest.setEnabled(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> btnPostTest.setEnabled(true));

                if (!response.isSuccessful()) {
                    Log.e(TAG, "POST响应失败：" + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "响应失败：" + response.code(), Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String postResponse = response.body().string();
                    if (postResponse == null || postResponse.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "POST响应为空", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // 保存POST响应到数据库
                    new Thread(() -> {
                        try {
                            appDatabase.dogImageDao().insert(new DogImage(postResponse, System.currentTimeMillis()));
                            appDatabase.dogImageDao().deleteOldImages();
                            Log.d(TAG, "POST响应已保存到数据库");
                        } catch (Exception e) {
                            Log.e(TAG, "保存POST数据失败", e);
                        }
                    }).start();

                    // 提示并加载缓存展示
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "POST请求成功，数据已保存", Toast.LENGTH_SHORT).show();
                        loadCachedDataFromDB();
                    });

                } catch (Exception e) {
                    Log.e(TAG, "处理POST响应异常", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "处理POST数据异常", Toast.LENGTH_SHORT).show());
                } finally {
                    response.body().close();
                }
            }
        });
    }

    // 加载数据库缓存
    private void loadCachedDataFromDB() {
        new Thread(() -> {
            try {
                List<DogImage> cachedData = appDatabase.dogImageDao().getLatest20Images();
                runOnUiThread(() -> {
                    if (cachedData.isEmpty()) {
                        Toast.makeText(MainActivity.this, "数据库无缓存数据", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 随机选一条
                    DogImage randomData = cachedData.get(new Random().nextInt(cachedData.size()));
                    String content = randomData.content;
                    if (content == null || content.isEmpty()) {
                        Toast.makeText(MainActivity.this, "缓存数据为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 区分图片URL和POST响应
                    if (content.startsWith("http")) {
                        Glide.with(MainActivity.this)
                                .load(content)
                                .error(android.R.drawable.ic_delete)
                                .into(ivContent);
                        Toast.makeText(MainActivity.this, "展示缓存图片", Toast.LENGTH_SHORT).show();
                    } else {
                        // POST响应太长，只显示前50个字符
                        String showText = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                        Toast.makeText(MainActivity.this, "缓存的POST响应：" + showText, Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "加载缓存数据失败", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "加载缓存失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // 页面销毁时释放资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll(); // 取消所有请求
        }
    }
}
