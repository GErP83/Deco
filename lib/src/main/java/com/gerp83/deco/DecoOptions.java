package com.gerp83.deco;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by GErP83
 */

public class DecoOptions {

    private static final String PREFS_IMAGE_SIZE_BOUND = "deco_image_size_bound";
    private static final String PREFS_STORAGE_TYPE = "deco_storage_type";
    private static final String PREFS_CACHE_SIZE = "deco_cache_size";
    private static final String PREFS_TRUSTED = "deco_trusted";
    private static final String PREFS_HEADERS = "deco_headers";
    private static final String PREFS_CACHE_POLICY = "deco_cache_policy";

    private static final int DEFAULT_IMAGE_SIZE_BOUND = 1024;

    /**
     * if an image was downloaded and cached before, no download needed again
     * */
    public static final int CACHE_POLICY_NORMAL = 0;
    /**
     * if an image was downloaded and cached before, and a cache-control header was saved, then we try to downloaded again, if
     * it is expired
     * */
    public static final int CACHE_POLICY_HEADER = 1;
    /**
     * if an image was downloaded and cached before, it will downloaded again every time
     * */
    public static final int CACHE_POLICY_NONE = 2;

    /**
     * the maximum amount of memory that the virtual machine will attempt to use / 8
     * */
    public static final int DEFAULT_CACHE_SIZE = 8;

    // file storage options
    public static final int STORAGE_PRIVATE = 0;
    public static final int STORAGE_EXTERNAL = 1;

    // crop positions when canvas cropped with a circle
    public static final int CIRCLE_CENTRE = 0;
    public static final int CIRCLE_LEFT = 1;
    public static final int CIRCLE_RIGHT = 2;
    public static final int CIRCLE_TOP = 3;
    public static final int CIRCLE_BOTTOM = 4;

    private static DecoOptions instance = null;
    private SharedPreferences sharedPreferences = null;

    /**
     * singleton constructor
     */
    public static DecoOptions getInstance(Context context) {
        if (instance == null) {
            instance = new DecoOptions(context);
        }
        return instance;
    }

    private DecoOptions(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * set value for image size bound, the default is 1024
     *
     * @param context Context for SharedPreferences
     * @param value value for image size bound
     */
    public DecoOptions setImageSizeBound(Context context, int value) {
        if(context == null || value < 0) {
            return this;
        }
        setInt(PREFS_IMAGE_SIZE_BOUND, value);
        return this;
    }

    /**
     * get value for image size bound
     *
     */
    int getImageSizeBound() {
        return sharedPreferences.getInt(PREFS_IMAGE_SIZE_BOUND, DEFAULT_IMAGE_SIZE_BOUND);
    }

    /**
     * set storage type, default is STORAGE_PRIVATE
     *
     * @param context Context for SharedPreferences
     * @param value value for storage type
     */
    public DecoOptions setStorageType(Context context, int value) {
        if(context == null || value < 0) {
            return this;
        }
        if(value > STORAGE_EXTERNAL) {
            value = STORAGE_PRIVATE;
        }
        setInt(PREFS_STORAGE_TYPE, value);
        return this;
    }

    /**
     * get storage type
     *
     */
    public int getStorageType() {
        return sharedPreferences.getInt(PREFS_STORAGE_TYPE, STORAGE_PRIVATE);
    }

    /**
     * set cache size, default is DEFAULT_CACHE_SIZE, the value must be between 3 and 8
     *
     * @param context Context for SharedPreferences
     * @param value value for cache size
     */
    public DecoOptions setCacheSize(Context context, int value) {
        if(context == null || value <= 3) {
            return this;
        }
        if(value > DEFAULT_CACHE_SIZE) {
            value = DEFAULT_CACHE_SIZE;
        }
        setInt(PREFS_CACHE_SIZE, value);
        return this;
    }

    /**
     * get cache size
     *
     */
    public int getCacheSize() {
        return sharedPreferences.getInt(PREFS_CACHE_SIZE, DEFAULT_CACHE_SIZE);
    }

    /**
     * set to call all https trusted
     *
     * @param context Context for SharedPreferences
     */
    public DecoOptions setAllTrusted(Context context) {
        if(context == null) {
            return this;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFS_TRUSTED, true);
        editor.apply();
        return this;
    }

    /**
     * get all https is trusted or not
     *
     */
    public boolean getAllTrusted() {
        return sharedPreferences.getBoolean(PREFS_TRUSTED, false);
    }

    /**
     * add http headers if needed for image download
     *
     * @param context Context for SharedPreferences
     * @param pairs String pairs for headers
     */
    public DecoOptions addHeadersForAll(Context context, Set<String> pairs) {
        if(context == null || pairs == null || pairs.size() == 0 || pairs.size() % 2 != 0) {
            return this;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(PREFS_HEADERS, pairs);
        editor.apply();
        return this;
    }

    /**
     * get http headers for images download
     *
     */
    public Set<String> getHeadersForAll() {
        return sharedPreferences.getStringSet(PREFS_HEADERS, null);
    }

    /**
     * set cache policy, default is CACHE_POLICY_NORMAL
     *
     * @param context Context for SharedPreferences
     * @param value value for cache policy
     */
    public DecoOptions setCachePolicy(Context context, int value) {
        if(context == null || value < 0) {
            return this;
        }
        if(value > CACHE_POLICY_NONE) {
            value = CACHE_POLICY_NORMAL;
        }
        setInt(PREFS_CACHE_POLICY, value);
        return this;
    }

    /**
     * get cache policy
     *
     */
    public int getCachePolicy() {
        return sharedPreferences.getInt(PREFS_CACHE_POLICY, CACHE_POLICY_NORMAL);
    }

    private void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, 0);
    }

}
