package com.thk.mylibrary;

import android.os.Looper;

import android.os.Handler;

public class LogMainThread implements MainThread {

    private final Handler handler;

    public LogMainThread() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        handler.post(runnable);
    }
}
