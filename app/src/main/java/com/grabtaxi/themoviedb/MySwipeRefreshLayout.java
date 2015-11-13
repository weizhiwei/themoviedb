package com.grabtaxi.themoviedb;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    private SwipeRefreshLayout.OnRefreshListener listener;


    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        this.listener = listener;
    }

    @Override
    public void setRefreshing(final boolean refreshing) {
        post(new Runnable() {
            @Override
            public void run() {
                MySwipeRefreshLayout.super.setRefreshing(refreshing);
            }
        });
    }

    public void triggerReload() {
        setRefreshing(true);
        listener.onRefresh();
    }
}
