package com.kakasa.debughelper.apphelpers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Seng on 2018/4/27.
 */

public class ActivityManager {

    private final ArrayList<Activity> mActivities;
    private static ActivityManager sInstance;

    private ActivityManager(){
        mActivities = new ArrayList<>();
    }

    public static ActivityManager getInstance() {
        if(sInstance == null)
            sInstance = new ActivityManager();
        return sInstance;
    }

    public Application.ActivityLifecycleCallbacks lifeCycleCallbacks() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivities.add(activity);
                System.out.println(activity.getLocalClassName()+" Created");
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivities.remove(activity);
                System.out.println(activity.getLocalClassName()+" Destroyed");
            }
        };
    }

    public void finishAll() {
        Iterator<Activity> iterator = mActivities.iterator();
        while (iterator.hasNext()){
            Activity next = iterator.next();
            next.finish();
            iterator.remove();
        }
        mActivities.clear();
    }
}
