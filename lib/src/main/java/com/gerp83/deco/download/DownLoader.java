package com.gerp83.deco.download;

import android.annotation.SuppressLint;
import android.content.Context;

import com.gerp83.deco.DecoOptions;
import com.gerp83.deco.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by GErP83
 * a class for single image download
 */
public class DownLoader implements Runnable ,Comparable<DownLoader> {

    private DownloadListener downloadListener;
    private Context context;
    private String url;
    private String fileName;
    private int tagId;

    private Map<String, String> headers = null;
    private boolean trustAllHttps = false;
    private final long when = System.nanoTime();

    public DownLoader(Context context, int tagId, String url, String fileName, DownloadListener downloadListener, boolean trustAllHttps, Map<String, String> headers) {

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        this.context = context;
        this.tagId = tagId;
        this.url = url;
        this.fileName = fileName;
        this.downloadListener = downloadListener;
        this.trustAllHttps = trustAllHttps;
        this.headers = headers;

    }

    @Override
    public void run() {
        try {
            URL u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();

            // need to trust all https for https download
            if (trustAllHttps && url.startsWith("https://")) {
                setTrustAllHttps();
            }

            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(DecoOptions.getInstance(context).getConnectionTimeout());
            urlConnection.setReadTimeout(DecoOptions.getInstance(context).getReadTimeout());
            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.setRequestProperty(key, headers.get(key));
                }
            }

            int status = urlConnection.getResponseCode();
            boolean redirect = false;

            // normally, 3xx inputStream redirect
            if (status != HttpURLConnection.HTTP_OK) {
                if (
                        status == HttpURLConnection.HTTP_MOVED_TEMP ||
                        status == HttpURLConnection.HTTP_MOVED_PERM ||
                        status == HttpURLConnection.HTTP_SEE_OTHER
                ) {
                    redirect = true;
                }
            }
            if (redirect) {
                String newUrl = urlConnection.getHeaderField("Location");
                urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
            }

            status = urlConnection.getResponseCode();
            int cachePolicy = DecoOptions.getInstance(context).getCachePolicy();
            long maxAge = 0;
            String cacheControl = urlConnection.getHeaderField("Cache-Control");

            //check for Cache-Control header if policy needed it
            if(cachePolicy == DecoOptions.CACHE_POLICY_HEADER && cacheControl != null) {
                String [] cut = cacheControl.split("=");
                if(cut != null && cut.length == 2) {
                    maxAge = Integer.valueOf(cut[1]) * 1000;
                }
            }

            if(cachePolicy == DecoOptions.CACHE_POLICY_HEADER && status == 304) {
                //do nothing

            } else if(status == 200) {
                if(cachePolicy == DecoOptions.CACHE_POLICY_HEADER) {
                    DecoOptions.getInstance(context).setLong(fileName, System.currentTimeMillis());
                    DecoOptions.getInstance(context).setLong(fileName + "_age", maxAge);
                }

                InputStream inputStream = urlConnection.getInputStream();
                try {
                    inputStream.reset();
                } catch (Throwable e) {
                    e.getLocalizedMessage();
                }
                FileOutputStream fos = new FileOutputStream(new File(FileUtils.get().getDirectory(context) + "/" + fileName));
                byte[] buffer = new byte[1024];
                int read = inputStream.read(buffer);
                while (read != -1) {
                    fos.write(buffer, 0, read);
                    read = inputStream.read(buffer);
                }
                fos.close();
                inputStream.close();

                DownLoadManager.getInstance().removeJob(tagId);
                if (downloadListener != null) {
                    downloadListener.onDownloadFinish(url);
                }

            }

        } catch (Throwable e) {
            if(DecoOptions.getInstance(context).getExceptionLogs()) {
                e.printStackTrace();
            }
            DownLoadManager.getInstance().removeJob(tagId);
        }

    }

    /**
     * need to call it before https image downloads
     */
    private void setTrustAllHttps() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return session.isValid();
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                @SuppressLint ("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                @SuppressLint ("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(DownLoader imageDownLoader) {
        return Long.valueOf(imageDownLoader.when).compareTo(when);
    }

}
