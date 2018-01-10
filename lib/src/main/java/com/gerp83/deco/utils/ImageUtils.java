package com.gerp83.deco.utils;

import android.content.Context;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by GErP83
 */

public class ImageUtils {

    public static final int UNKNOWN = 0;
    public static final int PNG = 1;
    public static final int JPG = 2;
    public static final int BMP = 3;
    public static final int GIF = 4;


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

    /**
     * get image type from header
     * */
    public static int getImageType(File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            int header = raf.readUnsignedShort();

            if (header == 0x8950) {                             // PNG
                return PNG;
            } else if (header == 0xffd8) {                      // JPG
                return JPG;
            } else if (header == 0x424D) {                      // BMP
                return BMP;
            } else if (header == (('G' << 8) | ('I'))) {   // GIF
                return GIF;
            } else {
                return UNKNOWN;
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return UNKNOWN;
    }

    /**
     * get image rotation from exif
     * */
    public static int getImageRotation(String path) {
        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotation =  90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotation =  180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotation = 270;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return rotation;
    }

    /**
     * get exif value to rotation
     * */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static int roundToNextPowerOfTwo(int num) {
        if (num < 1) {
            return (1);
        }
        num--;
        num |= num >> 1;
        num |= num >> 2;
        num |= num >> 4;
        num |= num >> 8;
        num |= num >> 16;
        num++;
        return (num);
    }

    /**
     * convert url to UUID
     * */
    public static String generateNameFromUrl(String url) {
        return UUID.nameUUIDFromBytes(url.getBytes()).toString();
    }

}