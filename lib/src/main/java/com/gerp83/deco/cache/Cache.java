package com.gerp83.deco.cache;

import android.content.Context;

import com.gerp83.deco.DecoOptions;

/**
 * Created by GErP83
 * a class for init an LruCache
 */
public class Cache extends LRU<String> {

    private static Cache instance = null;

    private Cache(Context context) {
        super((int) ((Runtime.getRuntime().maxMemory() / DecoOptions.getInstance(context).getCacheSize())));
    }

    public static Cache getInstance(Context context) {
        if (instance == null) {
            instance = new Cache(context);
        }
        return (instance);
    }

}