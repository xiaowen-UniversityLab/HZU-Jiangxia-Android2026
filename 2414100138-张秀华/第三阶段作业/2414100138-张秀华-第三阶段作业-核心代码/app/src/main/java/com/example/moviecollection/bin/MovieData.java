package com.example.moviecollection.bin;

import java.util.List;

public class MovieData {

    private int page;
    private List<Results> results;
    private int total_pages;
    private int total_results;

    @Override
    public String toString() {
        return "MovieData{" +
                "page=" + page +
                ", results=" + results +
                ", total_pages=" + total_pages +
                ", total_results=" + total_results +
                '}';
    }

    public void setPage(int page) {
        this.page = page;
    }
    public int getPage() {
        return page;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
    public List<Results> getResults() {
        return results;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }
    public int getTotal_results() {
        return total_results;
    }

}