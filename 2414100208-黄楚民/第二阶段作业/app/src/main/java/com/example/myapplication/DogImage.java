package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dog")
public class DogImage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String content; // 通用字段：存储GET的图片URL或POST的响应数据
    public long timestamp; // 时间戳：用于排序保留最新20条

    // 构造函数：接收内容+时间戳
    public DogImage(String content, long timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }
}