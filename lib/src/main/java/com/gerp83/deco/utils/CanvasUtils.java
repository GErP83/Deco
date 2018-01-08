package com.gerp83.deco.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.gerp83.deco.DecoOptions;

/**
 * Created by GErP83
 */

public class CanvasUtils {

    /**
     * crop the canvas with a circle
     *
     * @param canvas         Canvas to crop
     * @param circlePosition relative position depends on canvas width/height
     */
    public static CropCircle cropCircle(Canvas canvas, int circlePosition) {

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Path path = new Path();

        CropCircle cropCircle = new CropCircle();

        if(width >= height) {
            if(circlePosition == DecoOptions.CIRCLE_TOP || circlePosition == DecoOptions.CIRCLE_BOTTOM) {
                circlePosition = DecoOptions.CIRCLE_CENTRE;
            }
            switch (circlePosition) {
                case DecoOptions.CIRCLE_LEFT :
                    cropCircle.set(height / 2, height / 2, height / 2);
                    break;
                case DecoOptions.CIRCLE_RIGHT :
                    cropCircle.set(width - (height / 2), height / 2, height / 2);
                    break;
                default:
                    cropCircle.set(width / 2, height / 2, height / 2);
                    break;
            }

        } else {
            if(circlePosition == DecoOptions.CIRCLE_LEFT || circlePosition == DecoOptions.CIRCLE_RIGHT) {
                circlePosition = DecoOptions.CIRCLE_CENTRE;
            }
            switch (circlePosition) {
                case DecoOptions.CIRCLE_TOP :
                    cropCircle.set(width / 2, width / 2, width / 2);
                    break;
                case DecoOptions.CIRCLE_BOTTOM :
                    cropCircle.set(width / 2, height - (width / 2), width / 2);
                    break;
                default:
                    cropCircle.set(width / 2, height / 2, width / 2);
                    break;
            }

        }

        path.addCircle(cropCircle.getX(), cropCircle.getY(), cropCircle.getRadius(), Path.Direction.CW);
        canvas.clipPath(path);
        return cropCircle;
    }

    /**
     * draws a circle over the canvas, when the view is cropped with a circle
     *
     * @param canvas        Canvas to draw
     * @param cropCircle    circle data
     * @param strokeWidth   stroke width
     * @param strokeColor   stroke color
     */
    public static void drawCircleStrokeOverCanvas(Canvas canvas, CropCircle cropCircle, float strokeWidth, int strokeColor) {
        if (strokeWidth > 0 && cropCircle != null) {
            Paint strokePaint = new Paint();
            strokePaint.setColor(strokeColor);
            strokePaint.setAntiAlias(true);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(strokeWidth);
            canvas.drawCircle(cropCircle.getX(), cropCircle.getY(), cropCircle.getRadius() - (strokeWidth / 2), strokePaint);
        }
    }

    /**
     * compute the path for crop the canvas, the path will have rounded corners with the given radius
     *
     * @param canvas            Canvas to crop
     * @param topLeftRadius     top left radius
     * @param topRightRadius    top right radius
     * @param bottomLeftRadius  the bottom left radius
     * @param bottomRightRadius the bottom right radius
     */
    public static Path cropRoundedRect(Canvas canvas, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {

        topLeftRadius = topLeftRadius < 0 ? 0 : topLeftRadius;
        topRightRadius = topRightRadius < 0 ? 0 : topRightRadius;
        bottomLeftRadius = bottomLeftRadius < 0 ? 0 : bottomLeftRadius;
        bottomRightRadius = bottomRightRadius < 0 ? 0 : bottomRightRadius;

        RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        Path path = new Path();

        path.moveTo(rect.left + topLeftRadius / 2, rect.top);
        path.lineTo(rect.right - topRightRadius / 2, rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightRadius / 2);
        path.lineTo(rect.right, rect.bottom - bottomRightRadius / 2);
        path.quadTo(rect.right, rect.bottom, rect.right - bottomRightRadius / 2, rect.bottom);
        path.lineTo(rect.left + bottomLeftRadius / 2, rect.bottom);
        path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - bottomLeftRadius / 2);
        path.lineTo(rect.left, rect.top + topLeftRadius / 2);
        path.quadTo(rect.left, rect.top, rect.left + topLeftRadius / 2, rect.top);
        path.close();

        canvas.clipPath(path);
        return path;
    }

    /**
     * draws a round path over the canvas, when the view is cropped with a rounded path
     *
     * @param canvas        Canvas to draw
     * @param path          stroke path
     * @param strokeWidth   stroke width
     * @param strokeColor   stroke color
     */
    public static void drawRoundStrokeOverCanvas(Canvas canvas, Path path, float strokeWidth, int strokeColor) {
        if (strokeWidth > 0 && path != null) {
            Paint strokePaint = new Paint();
            strokePaint.setColor(strokeColor);
            strokePaint.setAntiAlias(true);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(strokeWidth);
            canvas.drawPath(path, strokePaint);
        }
    }

}
