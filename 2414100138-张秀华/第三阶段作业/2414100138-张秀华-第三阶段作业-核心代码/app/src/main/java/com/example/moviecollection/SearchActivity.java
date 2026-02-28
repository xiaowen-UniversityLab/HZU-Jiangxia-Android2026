package com.example.moviecollection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviecollection.bin.GetMovieDetail;
import com.example.moviecollection.bin.MovieData;
import com.example.moviecollection.bin.MyRecyclerAdapter;
import com.example.moviecollection.bin.Results;
import com.example.moviecollection.bin.SearchBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private Retrofit mRetrofit;
    private SearchBar mSearchBar;
    private GetMovieDetail mGetMovieDetail;
    private List<Results> mResultsList;
    private MyRecyclerAdapter mMyRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private String api = "64dd699d41ef3f0f242e6b2c4ecfa992";
    private String Language = "zh-CN";


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mResultsList = (List<Results>) msg.obj;
                mMyRecyclerAdapter.setAllResult(mResultsList);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchBar = findViewById(R.id.search_bar);
        mRecyclerView = findViewById(R.id.rv_search);
        mProgressBar = findViewById(R.id.pb);

        mRetrofit = new Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mGetMovieDetail = mRetrofit.create(GetMovieDetail.class);

        mProgressBar.setVisibility(View.GONE);

        ActionBar titleBar = getSupportActionBar();
        titleBar.setDisplayHomeAsUpEnabled(true);

        mResultsList = new ArrayList<>();
        mMyRecyclerAdapter = new MyRecyclerAdapter(mResultsList, SearchActivity.this);
        mRecyclerView.setAdapter(mMyRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //点击跳转到详情页面
        mMyRecyclerAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Results movieResult) {

                Intent intent = new Intent(SearchActivity.this, DetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", movieResult.getTitle());
                bundle.putString("overview", movieResult.getOverview());
                bundle.putString("date", movieResult.getRelease_date());
                bundle.putString("poster", movieResult.getPoster_path());
                bundle.putString("background", movieResult.getBackdrop_path());
                bundle.putLong("id", movieResult.getId());
                bundle.putDouble("vote", movieResult.getVote_average());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //网络请求
        mSearchBar.setOnSearchListener(new SearchBar.OnSearchListener() {
            @Override
            public void onSearch() {
                mProgressBar.setVisibility(View.VISIBLE);

                String title = mSearchBar.gettext().toString().trim();
                if (title.isEmpty()) {
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }

                Call<MovieData> call = mGetMovieDetail.get(api, title, Language);

                call.enqueue(new Callback<MovieData>() {
                    @Override
                    public void onResponse(Call<MovieData> call, Response<MovieData> response) {

                        mProgressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {

                            List<Results> results = response.body().getResults();

                            Message message = new Message();
                            message.what = 1;
                            message.obj = results;
                            mHandler.sendMessage(message);


                        }
                    }

                    @Override
                    public void onFailure(Call<MovieData> call, Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, "搜索失败，请检查网络是否正常，以下展示测试数据", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        Log.e("NetworkError", "请求失败: " + t.getMessage());
                        loadMockMovieData();
                    }
                });
            }
        });




    }

    //网络请求失败，模拟数据
    public void loadMockMovieData () {

        List<Results> mockResults = new ArrayList<>();
        Results movie1 = new Results();
        movie1.setId(12345);
        movie1.setTitle("你的名字");
        movie1.setOverview("故事发生在乡下，少女三叶和东京少年泷在梦中交换了身体，两人在彼此的生活中产生了奇妙的羁绊……");
        movie1.setPoster_path("/vRQ7wpxpHt2aV8RnOkwHlOQAMKe.jpg");
        movie1.setBackdrop_path("/vRQ7wpxpHt2aV8RnOkwHlOQAMKe.jpg");
        movie1.setRelease_date("2016-08-26");
        movie1.setVote_average(8.4);
        mockResults.add(movie1);

        Results movie2 = new Results();
        movie2.setId(67890);
        movie2.setTitle("千与千寻");
        movie2.setOverview("千寻在神灵世界中，为了救回变成猪的父母，经历了一段奇幻的冒险，最终找回了自我……");
        movie2.setPoster_path("/9oZmkkNlI4Ktx6NTkdpeU525qSc.jpg");
        movie2.setBackdrop_path("/9oZmkkNlI4Ktx6NTkdpeU525qSc.jpg");
        movie2.setRelease_date("2001-07-20");
        movie2.setVote_average(8.6);
        mockResults.add(movie2);

        Results movie3 = new Results();
        movie3.setId(13579);
        movie3.setTitle("龙猫");
        movie3.setOverview("小月和小梅姐妹俩在乡下的森林里，遇到了神奇的龙猫，开启了一段温暖治愈的冒险……");
        movie3.setPoster_path("/okAv5Q1ad2WMt7hxsXUyolfE6zA.jpg");
        movie3.setBackdrop_path("/okAv5Q1ad2WMt7hxsXUyolfE6zA.jpg");
        movie3.setRelease_date("1988-04-16");
        movie3.setVote_average(8.8);
        mockResults.add(movie3);

        Message message = new Message();
        message.what = 1;
        message.obj = mockResults;
        mHandler.sendMessage(message);
    }


}