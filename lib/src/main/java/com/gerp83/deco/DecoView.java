package com.gerp83.deco;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.gerp83.deco.cache.Cache;
import com.gerp83.deco.download.DownLoadManager;
import com.gerp83.deco.download.DownLoader;
import com.gerp83.deco.download.DownloadListener;
import com.gerp83.deco.utils.CanvasUtils;
import com.gerp83.deco.utils.CropCircle;
import com.gerp83.deco.utils.FileUtils;
import com.gerp83.deco.utils.ImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by GErP83
 * custom ImageView class which loads an image from net for itself, and store it in private storage and handle image cache
 */
public class DecoView extends ImageView {

    private String fileName;
    private ReadyListener readyListener;

    private boolean tintMode = false;
    private int tintColor = 0x50000000;

    private int imageSizeBound = 0;
    private int cachePolicy = 0;

    private Map<String, String> headers = null;
    private boolean trustAllHttps = false;

    private float strokeWidth = -1;
    private int strokeColor;

    private boolean cropToCircle = false;
    private int circlePosition = DecoOptions.CIRCLE_CENTRE;

    private float topLeftRadius = 0;
    private float topRightRadius = 0;
    private float bottomLeftRadius = 0;
    private float bottomRightRadius = 0;

    /**
     * callback for image download finish
     */
    public interface ReadyListener {
        void onReady();
    }

    public DecoView(Context context) {
        super(context);
    }

    public DecoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DecoView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * load options from xml
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);

        imageSizeBound = DecoOptions.getInstance(context).getImageSizeBound();
        trustAllHttps = DecoOptions.getInstance(context).getAllTrusted();
        cachePolicy = DecoOptions.getInstance(context).getCachePolicy();

        Set<String> pairs = DecoOptions.getInstance(context).getHeadersForAll();
        if(pairs != null) {
            headers = new HashMap<>();
            String last = null;
            for (String str : pairs) {
                if(last == null) {
                    last = str;
                } else {
                    headers.put(last, str);
                    last = null;
                }
            }
        }
        float density = ImageUtils.getDisplayDensity(context);

        strokeColor = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getColor(R.styleable.DecoView_stroke_color, 0);
        strokeWidth = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getDimension(R.styleable.DecoView_stroke_width, 0) * density;

        cropToCircle = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getBoolean(R.styleable.DecoView_cropToCircle, false);
        circlePosition = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getInt(R.styleable.DecoView_circlePosition, DecoOptions.CIRCLE_CENTRE);

        topLeftRadius = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getDimension(R.styleable.DecoView_topLeftCornerRadius, 0) * density;
        topRightRadius = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getDimension(R.styleable.DecoView_topRightCornerRadius, 0) * density;
        bottomLeftRadius = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getDimension(R.styleable.DecoView_bottomLeftCornerRadius, 0) * density;
        bottomRightRadius = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getDimension(R.styleable.DecoView_bottomRightCornerRadius, 0) * density;

        this.tintMode = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getBoolean(R.styleable.DecoView_tintedMode, false);
        this.tintColor = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DecoView, defStyleAttr, 0).getInt(R.styleable.DecoView_tintColor, tintColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && bitmap.isRecycled()) {
                setImageBitmap(null);
                Cache.getInstance(getContext()).removeBitmapFromCache(fileName);
                addToFileCache();
            }
        }

        CropCircle cropCircle = null;
        Path cropPath = null;
        if(cropToCircle) {
            cropCircle = CanvasUtils.cropCircle(canvas, circlePosition);
        } else {
            cropPath = CanvasUtils.cropRoundedRect(canvas, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
        }

        super.onDraw(canvas);

        if(cropToCircle) {
            CanvasUtils.drawCircleStrokeOverCanvas(canvas, cropCircle, strokeWidth, strokeColor);
        } else {
            CanvasUtils.drawRoundStrokeOverCanvas(canvas, cropPath, strokeWidth, strokeColor);
        }

    }

    /**
     * crop to cropToCircle
     */
    public void setCropToCircle() {
        cropToCircle = true;
        circlePosition = DecoOptions.CIRCLE_CENTRE;
        invalidate();
    }

    /**
     * crop to cropToCircle with position
     */
    public void setCropToCircle(int circlePosition) {
        cropToCircle = true;
        this.circlePosition = circlePosition;
        invalidate();
    }

    /**
     * set corners for rectangle crop
     *
     * @param topLeftRadius     top left radius
     * @param topRightRadius    top right radius
     * @param bottomLeftRadius  the bottom left radius
     * @param bottomRightRadius the bottom right radius
     */
    public void setCorners(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius ) {
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
        invalidate();
    }

    /**
     * set stroke
     *
     * @param color stroke width
     * @param width stroke color
     */
    public void setStroke(float width, int color) {
        strokeWidth = width;
        strokeColor = color;
        invalidate();
    }

    /**
     * onTouchEvent MotionEvent.ACTION_DOWN adds a tint for the ImageView, removes otherwise
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(tintMode) {
            int maskedAction = event.getActionMasked();
            if (maskedAction == MotionEvent.ACTION_DOWN) {
                setColorFilter(tintColor);
            } else if (maskedAction == MotionEvent.ACTION_UP || maskedAction == MotionEvent.ACTION_CANCEL) {
                setColorFilter(null);
            }
        }
        return true;
    }

    /**
     * set to tint mode
     */
    public void setTintMode() {
        this.tintMode = true;
    }

    /**
     * set tint color
     *
     * @param tintColor color
     */
    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    /**
     * set cache policy, set CACHE_POLICY_NORMAL, CACHE_POLICY_HEADER or CACHE_POLICY_NONE
     */
    public void setCachePolicy(int cachePolicy) {
        this.cachePolicy = cachePolicy;
    }

    /**
     * add http headers if needed for image download
     *
     * @param headers http headers for download, example authorization headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * set to call all https url
     */
    public void setTrustAllHttps(boolean trustAllHttps) {
        this.trustAllHttps = trustAllHttps;
    }

    /**
     * loads image from resource
     *
     * @param resourceId id
     */
    public void setImageFromResource(int resourceId) {

        String name = "" + resourceId;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resourceId);

        if (bmp != null) {
            Cache.getInstance(getContext()).addBitmapToCache(name, bmp);

        } else {
            setImageBitmap(null);
            throw new NullPointerException("Bitmap decode error");
        }

        setImageBitmap(Cache.getInstance(getContext()).getBitmapFromCache(name));

    }

    /**
     * loads image from file
     *
     * @param file the file
     * @param name name for the cache
     */
    public void loadFromFile(File file, String name) {
        addToFileCache(file, name);
    }

    /**
     * set ReadyListener
     *
     * @param readyListener set ReadyListener, callback fires when image downloaded
     */
    public void setReadyListener(ReadyListener readyListener) {
        this.readyListener = readyListener;
    }

    /**
     * add image maximum size to add to cache
     *
     * @param imageSizeBound the max size for bitmaps, all bitmaps are stored in the cache will be resized to fit this size
     */
    public void setImageSizeBound(int imageSizeBound) {
        this.imageSizeBound = imageSizeBound;
    }

    /**
     * get file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * download image from net
     *
     * @param url image url
     */
    public void get(final String url) {
        get(url, null, 0);
    }

    /**
     * download image from net
     *
     * @param url  image url
     * @param name name for the cache
     */
    public void get(final String url, String name) {
        get(url, name, 0);
    }

    /**
     * download image from net
     *
     * @param url                  image url
     * @param defaultDrawableResId drawable resource id for default image
     */
    public void get(final String url, int defaultDrawableResId) {
        get(url, null, defaultDrawableResId);
    }

    /**
     * download image from net
     *
     * @param url                  image url
     * @param name                 name for the cache
     * @param defaultDrawableResId drawable resource id for default image
     */
    public void get(final String url, String name, int defaultDrawableResId) {
        if (url == null || url.length() == 0) {
            return;
        }
        if (name == null) {
            fileName = ImageUtils.generateNameFromUrl(url);
        } else {
            fileName = name;
        }
        if (defaultDrawableResId != 0) {
            setImageDrawable(getContext().getResources().getDrawable(defaultDrawableResId));
        }
        if (FileUtils.get().hasFile(getContext(), fileName) && cachePolicy != DecoOptions.CACHE_POLICY_NONE) {

            long timeStamp = DecoOptions.getInstance(getContext()).getLong(fileName);
            long maxAge = DecoOptions.getInstance(getContext()).getLong(fileName + "_age");

            // check age passed
            boolean force = false;
            if (cachePolicy == DecoOptions.CACHE_POLICY_HEADER && System.currentTimeMillis() > timeStamp + maxAge) {
                force = true;
            }

            // it is expired so we need to delete and download it again
            if(force) {
                Cache.getInstance(getContext()).removeBitmapFromCache(fileName);
                FileUtils.get().deleteFile(getContext(), fileName);
                downLoad(url);

            } else {
                if (!Cache.getInstance(getContext()).hasBitmapInCache(fileName)) {
                    try {
                        addToFileCache();
                        addBitmap();

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    addBitmap();
                }

            }

        } else {
            if (cachePolicy == DecoOptions.CACHE_POLICY_NONE) {
                Cache.getInstance(getContext()).removeBitmapFromCache(fileName);
                FileUtils.get().deleteFile(getContext(), fileName);
            }
            downLoad(url);

        }

    }

    // private

    private void downLoad(String url) {
        final Integer tagId = (Integer) getTag(R.id.ic_deco_id);
        if (tagId != null) {
            DownLoadManager.getInstance().cancel(tagId);
        }
        final int imageID = DownLoadManager.getInstance().getNewID();
        setTag(R.id.ic_deco_id, imageID);
        DownLoadManager.getInstance().add(imageID, new DownLoader(getContext(), imageID, url, fileName, downloadlistener, trustAllHttps, headers));
    }

    private void addToFileCache() {
        File file = new File(FileUtils.get().getDirectory(getContext()) + "/" + fileName);
        addToFileCache(file, fileName);
    }

    private void addToFileCache(final File file, String name) {
        String path = file.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float scale = 0;

        if (options.outWidth > imageSizeBound || options.outHeight > imageSizeBound) {
            if (options.outWidth > options.outHeight) {
                scale = (float) options.outWidth / imageSizeBound;
            } else {
                scale = (float) options.outHeight / imageSizeBound;
            }
        }
        int inSampleSize = scale < 2 ? 1 : ImageUtils.roundToNextPowerOfTwo((int) scale);
        int rotation = ImageUtils.getImageRotation(path);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;
        opts.inPreferredConfig = Config.RGB_565;

        try {
            if (rotation == 0) {
                Bitmap bmp = BitmapFactory.decodeFile(path, opts);
                if (bmp != null) {
                    Cache.getInstance(getContext()).addBitmapToCache(name, bmp);
                } else {
                    setImageBitmap(null);
                    throw new Exception("Bitmap decode error");
                }

            } else {
                Bitmap bmp = BitmapFactory.decodeFile(path, opts);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Cache.getInstance(getContext()).addBitmapToCache(name, Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true));
            }

            if (name != null) {
                setImageBitmap(Cache.getInstance(getContext()).getBitmapFromCache(name));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addBitmap() {
        setImageBitmap(Cache.getInstance(getContext()).getBitmapFromCache(fileName));
        if (readyListener != null) {
            readyListener.onReady();
        }
    }

    private DownloadListener downloadlistener = new DownloadListener() {
        @Override
        public void onDownloadFinish(String url) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                    try {
                        addToFileCache();
                        addBitmap();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}