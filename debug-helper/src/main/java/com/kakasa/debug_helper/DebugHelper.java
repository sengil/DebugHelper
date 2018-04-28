package com.kakasa.debug_helper;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Seng on 2018/4/26.
 */

public class DebugHelper {

    private static final String IS_TEST_BASE = "is_test_baseurl";
    private static final String CURENT_USER = "current_user";
    private static final String DEBUG_USER_INFO = "debug_user_info";
    private static final int NOTIFI_ID = 777;

    private static DebugHelper sInstance;
    private SharedPreferences mDebug_sp;
    private DebugActions mDebugAction;
    private File mDebugUserInfoPath;
    private NotificationManager mNotificationManager;

    private DebugHelper() {

    }

    public void init(Application context, DebugActions debugAction) {
        mDebugAction = debugAction;
        mDebug_sp = context.getSharedPreferences("debug_sp", Context.MODE_PRIVATE);
        mDebugUserInfoPath = new File(getDiskCacheDir(context, null), DEBUG_USER_INFO);
        showNotifi(context);
    }

    private void showNotifi(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFI_ID);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context, DebugActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mBuilder
                .setSmallIcon(R.drawable.ic_pan)
                .setContentTitle("当前域名")
                .setContentText(mDebugAction.getBaseUrl())
                .setContentIntent(pendingIntent);
        Notification mNotification = mBuilder.build();
//        mNotification.icon = R.mipmap.ic_launcher;
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotification.when = System.currentTimeMillis();
        mNotificationManager.notify(NOTIFI_ID, mNotification);
    }


    public static DebugHelper getInstance() {
        if (sInstance == null)
            sInstance = new DebugHelper();
        return sInstance;
    }


    public boolean isTestBase() {
        return mDebug_sp.getBoolean(IS_TEST_BASE, true);
    }


    public void switchServer() {
        boolean flag = isTestBase();
        mDebug_sp.edit().putBoolean(IS_TEST_BASE, !flag).commit();
        if (!flag)
            mDebugAction.onDebugSwitch();
        else
            mDebugAction.onReleaseSwitch();
        mNotificationManager.cancel(NOTIFI_ID);
        mDebugAction.restartApp();
    }

    public void saveUserInfo(String username, String password) {
        HashMap<String, String> map = getSerializable(mDebugUserInfoPath);
        if (map == null)
            map = new HashMap<>();
        map.put(username, password);
        saveSerializable(map, mDebugUserInfoPath);
        saveCurrentUser(username, password);

    }

    public void saveCurrentUser(String username, String password) {
        mDebug_sp.edit().putString(CURENT_USER, username + "#" + password).apply();
    }

    public Pair<String, String> getCurrentUser() {
        String userInfo = mDebug_sp.getString(CURENT_USER, "");
        if (!TextUtils.isEmpty(userInfo)) {
            String[] split = userInfo.split("#");
            return Pair.create(split[0], split[1]);
        }
        return null;
    }

    public List<Pair<String, String>> getAccounts() {
        HashMap<String, String> serializable = getSerializable(mDebugUserInfoPath);
        if (serializable == null)
            return null;
        Iterator<Map.Entry<String, String>> iterator = serializable.entrySet().iterator();
        List<Pair<String, String>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            Pair<String, String> stringStringPair =
                    new Pair<>(next.getKey(), next.getValue());
            list.add(stringStringPair);
        }
        return list;
    }

    public void switchAccount() {
        mDebugAction.onSwitchAccount();

    }

    public String getBaseUrl() {
        return mDebugAction.getBaseUrl();
    }


    public interface DebugActions {

        void onDebugSwitch();

        void onReleaseSwitch();

        void onSwitchAccount();

        void restartApp();

        String getBaseUrl();
    }


    private static File getDiskCacheDir(Context context, String uniqueName) {
        File cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir();
        } else {
            cachePath = context.getCacheDir();
        }

        File cacheDir;
        if (!TextUtils.isEmpty(uniqueName)) {
            cacheDir = new File(cachePath, uniqueName);

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
        } else {
            cacheDir = cachePath;
        }
        return cacheDir;
    }


    private static <T extends Serializable> T getSerializable(File file) {
        T object = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            object = (T) in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    private static void saveSerializable(Serializable serializable, File file) {
        ObjectOutputStream out = null;
        try {
            if (!file.exists())
                file.createNewFile();
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(serializable);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (out != null)
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
    }
}
