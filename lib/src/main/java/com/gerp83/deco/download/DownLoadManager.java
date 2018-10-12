package com.gerp83.deco.download;

import android.annotation.SuppressLint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by GErP83
 * singleton class for handling image downloads
 */
public class DownLoadManager {

    private static DownLoadManager instance = null;

    private ThreadPoolExecutor threadPoolExecutor;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 64;
    private static final int KEEP_ALIVE_TIME = 1;

    private final Map<Integer, Runnable> jobs;
    private Integer counter = 0;

    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    @SuppressLint("UseSparseArrays")
    private DownLoadManager(){
        threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>());

        jobs = Collections.synchronizedMap(new HashMap<Integer, Runnable>());
    }

    /**
     * add job with id and start downloading
     *
     * @param id unique job ID
     * @param imageDownLoader Runnable to load image background
     */
    public void add(Integer id, DownLoader imageDownLoader){
        if(imageDownLoader == null)
            return;
        jobs.put(id, imageDownLoader);
        threadPoolExecutor.execute(imageDownLoader);
    }

    /**
     * remove job with id
     *
     * @param id unique job ID
     */
    public void removeJob(int id){
        synchronized (jobs) {
            jobs.remove(id);
        }
    }

    /**
     * cancel job with id
     *
     * @param id unique job ID
     */
    public void cancel(int id){
        synchronized (jobs) {
            final Runnable job = jobs.get(id);
            if (job != null) {
                jobs.remove(id);
                threadPoolExecutor.remove(job);
            }
        }

    }

    /**
     * If loading a number of images where you don't have a unique ID to represent the individual
     * load, this can be used to generate a sequential ID.
     *
     * @return a new unique ID
     */
    public int getNewID() {
        return counter++;
    }

}