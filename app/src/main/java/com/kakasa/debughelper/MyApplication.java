package com.kakasa.debughelper;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.kakasa.debug_helper.DebugHelper;
import com.kakasa.debughelper.apphelpers.ActivityManager;
import com.kakasa.debughelper.util.UrlManager;

/**
 * Created by Seng on 2018/4/26.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("MyApplication");


        DebugHelper.getInstance().init(this, new DebugHelper.DebugActions() {
            @Override
            public void onDebugSwitch() {
                UrlManager.setBaseUrl(UrlManager.BASE_URL_DEBUG);
            }

            @Override
            public void onReleaseSwitch() {
                UrlManager.setBaseUrl(UrlManager.BASE_URL_RELEASE);
            }

            @Override
            public void onSwitchAccount() {
                //重置登录状态
                //.......
                //
                ActivityManager.getInstance().finishAll();
                Intent intent = new Intent(MyApplication.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void restartApp() {
                //重置网络请求框架
//                HttpUtil.reset();

                MyApplication.this.restartApp();
            }

            @Override
            public String getBaseUrl() {
                if (DebugHelper.getInstance().isTestBase())
                    return UrlManager.BASE_URL_DEBUG;
                else
                    return UrlManager.BASE_URL_RELEASE;
            }
        });
        registerActivityLifecycleCallbacks(ActivityManager.getInstance().lifeCycleCallbacks());
    }

    private void restartApp() {
        /*获取一个可执行的intent（栈最底下的acitivity）*/
//        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);

        /*各种杀死进程，除非acv栈里只有一个activity,否则framwork会自动恢复app(到上一个activity位置)
        并且用这种方法杀死进程，activity的ondestroy回调时不会走的，activity死的冤所以，frameworke帮其复活。。
        https://blog.csdn.net/u011277123/article/details/53579269*/
//        PendingIntent restartIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        if (manager == null)
//            return;
//        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);

//        int mPendingIntentId = 123456;
        ActivityManager.getInstance().finishAll();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
