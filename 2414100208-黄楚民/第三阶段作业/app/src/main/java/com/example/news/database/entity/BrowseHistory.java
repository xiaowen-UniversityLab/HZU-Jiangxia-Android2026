package com.example.news.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "browse_history",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"userId", "newsUrl"}, unique = true)})
public class BrowseHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private String newsId;
    private String title;
    private String summary;
    private String imageUrl;
    private String newsUrl;
    private String category;
    private String author;
    private long browseTime;

    public BrowseHistory(int userId, String newsId, String title, String summary, String imageUrl, String newsUrl, String category, String author) {
        this.userId = userId;
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.newsUrl = newsUrl;
        this.category = category;
        this.author = author;
        this.browseTime = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(long browseTime) {
        this.browseTime = browseTime;
    }
}
