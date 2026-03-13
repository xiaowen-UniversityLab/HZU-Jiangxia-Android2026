package com.example.news.network;

import com.example.news.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    
    // 聚合数据新闻接口
    @GET("toutiao/index")
    Call<NewsResponse> getNewsList(
            @Query("key") String key,
            @Query("type") String type,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("is_filter") int isFilter
    );
}
