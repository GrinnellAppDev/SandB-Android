package edu.grinnell.sandb;

import android.app.Application;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ScarletAndBlackApplication extends Application {
	
    @Override
    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        	.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        	.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
	        .denyCacheImageMultipleSizesInMemory()
	        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
	        .memoryCacheSize(6 * 1024 * 1024)
	        .discCacheSize(100 * 1024 * 1024)
	        .discCacheFileCount(100)
	        .enableLogging()
	        .build();
        ImageLoader.getInstance().init(config);
    }
}