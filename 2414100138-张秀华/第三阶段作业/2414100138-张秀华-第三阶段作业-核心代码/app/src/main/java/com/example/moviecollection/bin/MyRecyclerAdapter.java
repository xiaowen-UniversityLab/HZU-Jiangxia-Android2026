package com.example.moviecollection.bin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviecollection.DetialActivity;
import com.example.moviecollection.R;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private List<Results> mResultsList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;


    //点击响应接口

    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongClickListener;


    public interface OnItemClickListener {
        void onItemClick(Results movieResult);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setLongClickListener(OnItemLongClickListener listener){
        this.mLongClickListener=listener;
    }


    //利用函数来给列表设置值
    public void setAllResult(List<Results> allResult) {
        mResultsList = allResult;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mResultsList.size();
    }

    public MyRecyclerAdapter(List<Results> resultsList, Context context) {
        mResultsList = resultsList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.main_recycler_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Results movieDataResult = mResultsList.get(position);

        holder.MovieTitle.setText(movieDataResult.getTitle());
        holder.MovieContent.setText(movieDataResult.getOverview());

        // 加载海报图片
        String posterPath = movieDataResult.getPoster_path();
        String fullPosterPath = " ";
        if (posterPath != null && !posterPath.isEmpty()) {

            if (posterPath.startsWith("http")) {
                fullPosterPath = posterPath;
            } else {
                String IMAGE_BASE_PATH = "https://image.tmdb.org/t/p/w500";
                fullPosterPath = IMAGE_BASE_PATH + posterPath;
            }

            Glide.with(mContext)
                    .load(fullPosterPath)
                    .error(R.drawable.movie_poster)
                    .into(holder.MoviePoster);
        } else {
            // 如果没有海报，直接显示占位图
            holder.MoviePoster.setImageResource(R.drawable.movie_poster);
        }


        //点击响应
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null) {
                    mListener.onItemClick((movieDataResult));
                }

            }
        });

        holder.mRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(mLongClickListener!=null){
                    int currentPosition=holder.getAdapterPosition();
                    mLongClickListener.onItemLongClick(currentPosition);
                }
                return true;
            }
        });

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView MovieTitle;
        TextView MovieContent;
        ImageView MoviePoster;
        RelativeLayout mRelativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.MovieTitle = itemView.findViewById(R.id.tv_title);
            this.MovieContent = itemView.findViewById(R.id.tv_content);
            this.MoviePoster = itemView.findViewById(R.id.iv_poster);
            this.mRelativeLayout = itemView.findViewById(R.id.rl_movie);
        }
    }

}
