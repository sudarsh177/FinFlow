package com.example.finflow;

import com.example.finflow.Models.NewsHeadlines;

import java.util.List;

public interface OnFetchDataListener<T> {
    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);
}

