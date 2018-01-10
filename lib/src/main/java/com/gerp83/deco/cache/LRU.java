package com.gerp83.deco.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCacheCopy;

/**
 * Created by GErP83
 * a class for store bitmaps in LruCacheCopy
 */
public abstract class LRU<T> {

    private LruCacheCopy<T, Bitmap> bitmaps = null;

    LRU(int maxSize) {
        super();
        System.out.println(maxSize);
        bitmaps = new LruCacheCopy<T, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(T key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, T key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
                System.gc();
            }
        };
    }

    /**
     * add a Bitmap to LruCacheCopy
     *
     * @param key    key to identify
     * @param bitmap bitmap to store
     */
    public void addBitmapToCache(T key, Bitmap bitmap) {
        if (key != null && bitmaps.get(key) == null) {
            bitmaps.put(key, bitmap);
        }
    }

    /**
     * remove a Bitmap from LruCacheCopy
     *
     * @param key key to identify
     */
    public void removeBitmapFromCache(T key) {
        if (key != null && bitmaps.get(key) != null) {
            bitmaps.remove(key);
        }
    }

    /**
     * check a Bitmap in LruCacheCopy
     *
     * @param key key to identify
     */
    public boolean hasBitmapInCache(T key) {
        return key != null && bitmaps.get(key) != null;
    }

    /**
     * get a Bitmap size from LruCacheCopy
     *
     * @param key key to identify
     */
    public int[] getBitmapSizeFromCache(T key) {
        if (key != null && bitmaps.get(key) != null) {
            int[] size = new int[2];
            size[0] = bitmaps.get(key).getWidth();
            size[1] = bitmaps.get(key).getHeight();
            return (size);
        } else {
            return null;
        }
    }

    /**
     * loads a Bitmap from LruCacheCopy
     *
     * @param key key to identify
     */
    public Bitmap getBitmapFromCache(T key) {
        if (key != null && bitmaps.get(key) != null) {
            return (bitmaps.get(key));
        }
        return null;
    }

    /**
     * loads a Bitmap from LruCacheCopy with scale
     *
     * @param key   key to identify
     * @param scale the returned bitmap will scaled with this value
     */
    public Bitmap getBitmapFromCache(T key, float scale) {
        if (key != null && bitmaps.get(key) != null) {
            return (Bitmap.createScaledBitmap(bitmaps.get(key), (int) (bitmaps.get(key).getWidth() * scale), (int) (bitmaps.get(key).getHeight() * scale), false));
        }
        return null;
    }

    /**
     * clear LruCacheCopy
     */
    public void clearCache() {
        bitmaps.evictAll();
    }

}