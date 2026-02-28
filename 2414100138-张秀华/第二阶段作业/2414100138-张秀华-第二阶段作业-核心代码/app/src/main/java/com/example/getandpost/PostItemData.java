package com.example.getandpost;

public class PostItemData {
    private int id;
    private int userId;
    private String title;
    private String body;

    public PostItemData() {
    }

    public PostItemData(int id, int userId, String title, String body) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
    }

    @Override
    public String toString() {
        return "ID：" + id + "\n" +
                "userId：" + userId + "\n" +
                "标题：" + title + "\n" +
                "内容：" + body + "\n";
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
