package com.example.moviecollection.bin;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMovieDetail {

    @GET("search/movie")
    Call<MovieData> get(@Query("api_key") String apiKey ,@Query("query") String title,@Query("language")String language);
}
