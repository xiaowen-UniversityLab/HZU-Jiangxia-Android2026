package com.example.moviecollection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.moviecollection.bin.MovieViewModel;
import com.example.moviecollection.bin.Results;

public class DetialActivity extends AppCompatActivity {

    private TextView title, overview, relaseDate;
    private ImageView poster, background;
    private Button btnFavorite, btnDelete;
    private String M_title, M_overview, M_data, M_poster, M_background, M_posterPath;
    private Long M_id;
    private Double M_vote;
    private String IMAGE_BASE_PATH = "https://image.tmdb.org/t/p/w500";

    MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        title = findViewById(R.id.tvTitle);
        overview = findViewById(R.id.tvOverview);
        relaseDate = findViewById(R.id.tvReleaseDate);
        poster = findViewById(R.id.ivPoster);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnDelete = findViewById(R.id.btnDelete);
        background = findViewById(R.id.iv_background);

        mMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        ActionBar titleBar = getSupportActionBar();
        titleBar.setDisplayHomeAsUpEnabled(true);

        //接收数据
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        M_title = bundle.getString("title");
        M_overview = bundle.getString("overview");
        M_data = bundle.getString("date");
        M_id = bundle.getLong("id");
        M_vote = bundle.getDouble("vote");
        M_background = bundle.getString("background");
        M_posterPath = bundle.getString("poster");


        //设置数据
        title.setText(M_title);
        overview.setText("简介" + '\n' + M_overview);
        relaseDate.setText("上映时间：" + M_data);

        String fullPosterPath = IMAGE_BASE_PATH + M_posterPath;
        if (fullPosterPath != null && !fullPosterPath.isEmpty()) {
            Glide.with(DetialActivity.this)
                    .load(fullPosterPath)
                    .error(R.drawable.movie_poster)
                    .into(poster);
        }

        String fullBackgroundPath = IMAGE_BASE_PATH + M_background;
        if (fullBackgroundPath != null && !fullBackgroundPath.isEmpty()) {
            Glide.with(DetialActivity.this)
                    .load(fullBackgroundPath)
                    .error(R.drawable.movie_poster)
                    .into(background);
        }


        //按钮操作
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Results results = new Results(M_id, M_overview, M_posterPath, M_background, M_data, M_title, M_vote);
                mMovieViewModel.insertMovie(results);
                Toast.makeText(DetialActivity.this, "收藏成功~", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMovieViewModel.deleteMovie(M_id);
                Toast.makeText(DetialActivity.this, "取消成功~", Toast.LENGTH_SHORT).show();
            }
        });

    }
}