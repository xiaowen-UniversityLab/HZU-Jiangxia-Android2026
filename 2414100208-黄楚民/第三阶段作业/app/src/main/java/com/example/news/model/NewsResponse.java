package com.example.news.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsResponse {
    
    @SerializedName("error_code")
    private int errorCode;
    
    @SerializedName("reason")
    private String reason;
    
    @SerializedName("result")
    private Result result;
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Result getResult() {
        return result;
    }
    
    public void setResult(Result result) {
        this.result = result;
    }
    
    public boolean isSuccess() {
        return errorCode == 0;
    }
    
    public static class Result {
        @SerializedName("stat")
        private String stat;
        
        @SerializedName("data")
        private List<NewsItem> data;
        
        @SerializedName("page")
        private String page;
        
        @SerializedName("pageSize")
        private String pageSize;
        
        public String getStat() {
            return stat;
        }
        
        public void setStat(String stat) {
            this.stat = stat;
        }
        
        public List<NewsItem> getData() {
            return data;
        }
        
        public void setData(List<NewsItem> data) {
            this.data = data;
        }
        
        public String getPage() {
            return page;
        }
        
        public void setPage(String page) {
            this.page = page;
        }
        
        public String getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }
    }
}
