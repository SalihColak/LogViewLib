package com.thk.mylibrary;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MyObserver implements LifecycleObserver {

    private Activity activity;

    protected MyObserver(Activity activity){
        this.activity = activity;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreateListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onCreate() called.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStartListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onStart() called.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResumeListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onResume() called.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPauseListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onPause() called.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStopListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onStop() called.");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroyListener() {
        Log.d("lifecycleEvent",activity.getClass().getSimpleName()+": onDestroy() called.");
    }


}
