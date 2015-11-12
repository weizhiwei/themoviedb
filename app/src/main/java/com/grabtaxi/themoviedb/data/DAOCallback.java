package com.grabtaxi.themoviedb.data;

public interface DAOCallback<T> {
    public void onSuccess(T result);
    public void onFailure(int errCode, String errMsg);
}
