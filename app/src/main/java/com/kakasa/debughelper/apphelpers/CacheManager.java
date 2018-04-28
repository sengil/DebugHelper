package com.kakasa.debughelper.apphelpers;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Seng on 2018/4/27.
 */

public class CacheManager {

    public static File getDiskCacheDir(Context context, String uniqueName) {
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


    public static <T extends Serializable> T getSerializable(File file) {
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

    public static void saveSerializable(Serializable serializable, File file) {
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
