# Deco

Deco is tiny library for android image loading and caching. Not use any 3rd party library, it has zero dependencies. It has a 18,6 kB size and has 143 methods only.

## What it can do

- can cache dowloaded images to private storage or to application-specific directory on the primary shared/external storage
- can set cache size if the app needs more
- can set cache policy, there is 3x type
- can set read and connection timeout for all images
- can crop image to circle and can set relative position too
- can crop image to rounded cornered rectangle with different corners
- can add a stroke to the image
- can add custom http headers for downloading
- all the options can be setted for one image or for all images
- tint mode
- almost everything can be set from xml as well
  
## Requirements:
Android api 4.3+ and permissions:
```
<uses-permission android:name="android.permission.INTERNET" />
```
Add to dependencies:
```
compile 'com.github.gerp83:Deco:1.02'
```

## Examples

Simple loading, you also set for every image:
- corners for rectangle crop
- crop to circle with relative position
- stroke width and color
- tint and color
- cache policy
- http headers
- trust all https

```
@Override 
public void onCreate(Bundle savedInstanceState) {
  
  ImageView imageView = (ImageView) findViewById(R.id.imageView);
  imageView.get("http://some_image_url");
}
```

From XML you can set: cropToCircle, circlePosition, topLeftCornerRadius, topRightCornerRadius, bottomRightCornerRadius, bottomLeftCornerRadius, stroke_color, stroke_width tintedMode and tintColor.

```
<com.gerp83.deco.DecoView
  android:id="@+id/decoView"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:adjustViewBounds="true"
  android:scaleType="centerCrop"
  android:layout_margin="30dp"
  app:tintedMode="true"
/>
```

With DecoOptions you can set options for all the images. Options:
- storage type
- cache size
- read and connection timeout
- switch exception logs on/off
- call all https trusted
- add http headers
- cache policy

```
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DecoOptions.getInstance(getApplicationContext())
                .setImageSizeBound(getApplicationContext(), 1024)
                .setConnectionTimeout(getApplicationContext(), 30000)
                .setReadTimeout(getApplicationContext(), 10000)
                .setExceptionLogs(getApplicationContext(), false)
                .setCacheSize(getApplicationContext(), DecoOptions.DEFAULT_CACHE_SIZE)
                .setCachePolicy(getApplicationContext(), DecoOptions.CACHE_POLICY_NORMAL)
                .setStorageType(getApplicationContext(), DecoOptions.STORAGE_EXTERNAL);

    }

}
```

Planned updates:
- gif and/or webP support
- animations

## Lisence
[wtfpl](http://www.wtfpl.net/)

I hope this can make your job easier, happy image loading!

