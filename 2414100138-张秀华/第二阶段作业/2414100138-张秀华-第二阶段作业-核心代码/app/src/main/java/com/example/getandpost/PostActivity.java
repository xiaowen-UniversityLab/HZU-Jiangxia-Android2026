package com.example.getandpost;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private PostItemDbHelper dbHelper;
    private Button btnRefresh;

    //主线程
    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            // 接收消息
            if (msg.what == 1) {
                // 网络加载成功
                List<PostItemData> netItemsData = (List<PostItemData>) msg.obj;
                dataList.clear();
                for (int i = 0; i < netItemsData.size(); i++) {
                    PostItemData item = netItemsData.get(i);
                    dataList.add(item.toString());
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(PostActivity.this, "刷新成功！已从网络加载数据", Toast.LENGTH_SHORT).show();
            }

            else if (msg.what == 2) {
                // 无网络，从数据库加载
                List<PostItemData> dbItemsData = (List<PostItemData>) msg.obj;
                dataList.clear();
                for (int i = 0; i < dbItemsData.size(); i++) {
                    PostItemData item = dbItemsData.get(i);
                    dataList.add(item.toString());
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(PostActivity.this, "刷新成功！已从本地数据库加载数据", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.lv_post);
        btnRefresh = findViewById(R.id.bt_refresh_data);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        dbHelper = new PostItemDbHelper(this);

        //按钮刷新
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 子线程1
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean postSuccess = NetUtil.doPost(NetUtil.POST_ADDRESS);//post请求

                            //有网
                            if (postSuccess) {
                                String json = NetUtil.doGet(NetUtil.POST_ADDRESS);//get获取json
                                if (json != null)
                                {
                                    Type listType = new TypeToken<List<PostItemData>>() {}.getType();
                                    List<PostItemData> items = new Gson().fromJson(json, listType);

                                    if (items.size() > 20) {
                                        items = items.subList(0, 20);
                                    }

                                    // 清空，插入新数据
                                    dbHelper.deleteAll();
                                    dbHelper.insertAll(items);

                                    // 网络加载成功。从网络中加载数据
                                    Message message = new Message();
                                    message.what = 1;
                                    message.obj = items;
                                    mHandler.sendMessage(message);
                                }
                                else {//网络加载的为空时
                                    loadFromDatabase();
                                }
                            }

                            //无网
                            else {
                                loadFromDatabase();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadFromDatabase();
                        }
                    }
                }).start();

                Toast.makeText(PostActivity.this, "开始加载数据啦！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 从数据库加载数据
    private void loadFromDatabase() {

        //子线程2
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PostItemData> items = dbHelper.getAllItems();
                Message message = new Message();
                message.what = 2;
                message.obj = items;
                mHandler.sendMessage(message);
            }
        }).start();
    }
}