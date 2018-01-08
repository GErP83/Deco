package com.gerp83.deco.utils;

import android.content.Context;

import com.gerp83.deco.DecoOptions;

import java.io.File;

/**
 * Created by GErP83
 */
public class FileUtils {

    public static FileUtils get() {
        return new FileUtils();
    }

    /**
     * get storage directory
     */
    public File getDirectory(Context context) {
        if(context == null) {
            return null;
        }
        File dir;
        int type = DecoOptions.getInstance(context).getStorageType();
        if(type == DecoOptions.STORAGE_PRIVATE) {
            dir = context.getDir("cache", Context.MODE_PRIVATE);
        } else {
            dir = context.getExternalCacheDir();
        }
        return dir;
    }

    /**
     * the storage contains a file or not
     */
    public boolean hasFile(Context context, String fileName) {
        boolean hasFile;
        if(context == null || fileName == null) {
            return false;
        }
        File file = new File(getDirectory(context), fileName);
        hasFile = file.exists();
        return hasFile;
    }

    /**
     * get a file from the storage
     */
    public File getFile(Context context, String fileName) {
        File hasFile;
        if(context == null || fileName == null) {
            return null;
        }
        File file = new File(getDirectory(context), fileName);
        hasFile = file.exists() ? file : null;
        return hasFile;
    }

    /**
     * delete a file from the storage
     */
    public boolean deleteFile(Context context, String fileName) {
        if(context == null || fileName == null) {
            return false;
        }
        File file = new File(getDirectory(context), fileName);
        if(file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * delete all file from the storage
     */
    public void deleteAll(Context context) {
        if(context == null) {
            return;
        }
        File storage = getDirectory(context);
        if(storage != null) {
            for(File file : storage.listFiles()) {
                file.delete();
            }
        }
    }

}