package com.example.moviecollection.bin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.moviecollection.R;

public class SearchBar extends LinearLayout {

    private EditText etSearch;
    private ImageView btnSearch;
    private OnSearchListener onSearchListener;


    public SearchBar(Context context) {
        super(context);
        init();
    }

    public SearchBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnSearchListener {
        void onSearch();
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.onSearchListener = listener;
    }


    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.searchbar_layout, this, true);

        etSearch = findViewById(R.id.etsearch);
        btnSearch = findViewById(R.id.ivsearch);
        //点击响应
        btnSearch.setOnClickListener(v -> {
            if (onSearchListener != null) {
                onSearchListener.onSearch();
            }
        });
    }

    public String gettext() {
        return etSearch.getText().toString();
    }

    public void clearKeyword() {
        etSearch.setText("");
    }
}
