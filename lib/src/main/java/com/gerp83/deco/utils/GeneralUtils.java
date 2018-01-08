package com.gerp83.deco.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by gerp83
 */

public class GeneralUtils {

    /**
     * @return an array with screen width and height in pixels
     */
    public static int[] getScreenSize(Context context) {
        if (context == null) {
            return null;
        }
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x, size.y};
    }

    /**
     * Convert pixel to dp
     */
    public static float convertPixelsToDp(float px, Context context) {
        if (context == null) {
            return -1;
        }
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Convert dp to pixel
     */
    public static float convertDpToPixel(float dp, Context context) {
        if (context == null) {
            return -1;
        }
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Convert pixel to sp
     */
    public static float convertPixelsToSp(float px, Context context) {
        if (context == null) {
            return -1;
        }
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    /**
     * Convert sp to pixel
     */
    public static float convertSpToPixel(float sp, Context context) {
        if (context == null) {
            return -1;
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    /**
     * Returns the screen's density.
     *
     * @param context Context for getting screen metrics.
     * @return The screen's density.
     */
    public static float getDisplayDensity(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi <= DisplayMetrics.DENSITY_DEFAULT ? metrics.density : (metrics.density / 2);
    }

}