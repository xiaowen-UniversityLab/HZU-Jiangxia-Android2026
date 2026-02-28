package com.example.moviecollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviecollection.bin.MovieViewModel;
import com.example.moviecollection.bin.MyRecyclerAdapter;
import com.example.moviecollection.bin.Results;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyRecyclerAdapter mMyRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private List<Results> mResultsList = new ArrayList<>();
    private Long movieId;

    MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_main);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMyRecyclerAdapter = new MyRecyclerAdapter(mResultsList, MainActivity.this);
        mRecyclerView.setAdapter(mMyRecyclerAdapter);
        registerForContextMenu(mRecyclerView);

        mMovieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        mMovieViewModel.getAllMovieLive().observe(this, new Observer<List<Results>>() {
            @Override
            public void onChanged(List<Results> results) {
                mResultsList=results;
                mMyRecyclerAdapter.setAllResult(results);

            }
        });


        //跳转到详情页面
        mMyRecyclerAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Results movieResult) {

                Intent intent = new Intent(MainActivity.this, DetialActivity.class);
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

        //长按删除
        mMyRecyclerAdapter.setLongClickListener(new MyRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {

                if (mResultsList != null && !mResultsList.isEmpty() && position >= 0 && position < mResultsList.size()) {
                    Results movieResult = mResultsList.get(position);
                    movieId = movieResult.getId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setMessage("是否确认要删除此电影");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mMovieViewModel.deleteMovie(movieId);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "当前没有收藏的电影", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        if (itemId == R.id.exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("是否要退出应用");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


}