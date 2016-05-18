package com.shixi.gaodun.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ronger
 * @date:2015-10-19 上午10:29:43
 */
public class BaseApplication extends Application {
    public static final String TAG = "BaseApplication";
    public static BaseApplication mApp;
    // 存放所有开启的activity,便于退出时使用
    public List<Activity> mActivities;
    public List<Activity> mCenterTaskActivitys;
    // 异步请求队列
    private RequestQueue mRequestQueue;
    // 本地广播
    public LocalBroadcastManager mLocalBroadcastManager;
    // 用于图片请求
    private ImageLoader myImageLoader;
    /**
     * 主线程ID
     */
    private static int mMainThreadId;
    /**
     * 主线程ID
     */
    private static Thread mMainThread;
    /**
     * 主线程Handler
     */
    private static Handler mMainThreadHandler;
    /**
     * 主线程Looper
     */
    private static Looper mMainLooper;
    //线程池
    public static ExecutorService executor = null;


    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            synchronized (BaseApplication.class) {
                if (mRequestQueue == null) {
                    mRequestQueue = Volley
                            .newRequestQueue(getApplicationContext());
                }
            }
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return myImageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mActivities = new ArrayList<Activity>();
        mCenterTaskActivitys = new ArrayList<Activity>();
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        initImageLoader(this);
        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        initThreadPool();


    }

    /**
     * 初始化线程池
     */
    private void initThreadPool() {
        int cpuCores = getNumberOfCPUCores();
//		executor = Executors.newFixedThreadPool(cpuCores);//设置核心线程数
        executor = new ThreadPoolExecutor(cpuCores, cpuCores * 2 + 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());//设置核心线程数

    }

    /**
     * 获得设备CPU核数
     *
     * @return
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = 1;
        } catch (NullPointerException e) {
            cores = 1;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    // 各个平台的配置，建议放在全局Application或者程序入口
    {
        // //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setWeixin("wx588f6ee07a6e0aba",
                "9cf10df5ad4e6c14223574c88d543d52");
        // //豆瓣RENREN平台目前只能在服务器端配置
        // //新浪微博 测试账号 3921700954 3969044620
        PlatformConfig.setSinaWeibo("3969044620",
                "69c13e4c0cd3633e71ba64a0fe6be407");
        PlatformConfig.setQQZone("1105147455", "j3q5qOFfkRcYugwm");
        // PlatformConfig.setQQZone("100424468",
        // "c7394704798a158208a74ab60104f0ba");
        // PlatformConfig.setAlipay("2015111700822536");

    }

    /**
     * 初始化图片加载工具
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

        // File cacheDir = StorageUtils.getOwnCacheDirectory(context,
        // "shixiAndroid/image/");
        // // 默认配置对象
        // DisplayImageOptions.Builder oBuilder = new
        // DisplayImageOptions.Builder()
        // // 缓存到内存
        // .cacheInMemory()
        // // 缓存到SD卡
        // .cacheOnDisc()
        // // 加载开始前的默认图片
        // .showStubImage(R.drawable.touxiang_listdefault).showImageForEmptyUri(R.drawable.touxiang_listdefault)
        // .showImageOnFail(R.drawable.touxiang_listdefault)
        // // 设置图片显示为圆角
        // .displayer(new RoundedBitmapDisplayer(30))
        // // 图片质量，防止内存溢出
        // .bitmapConfig(Bitmap.Config.RGB_565);
        // // 图片圆角显示，值为整数，不建议使用容易内存溢出
        // // .displayer(new RoundedBitmapDisplayer(15));
        // DisplayImageOptions options = oBuilder.build();
        //
        // ImageLoaderConfiguration.Builder cBuilder = new
        // ImageLoaderConfiguration.Builder(context)
        // .defaultDisplayImageOptions(options);
        //
        // ImageLoaderConfiguration config = cBuilder
        // // 缓存在内存的图片的最大宽和高度，超过了就缩小
        // .memoryCacheExtraOptions(480, 800)
        // // CompressFormat.PNG类型，70质量（0-100）
        // // .discCacheExtraOptions(400, 400, CompressFormat.PNG, 70, null)
        // // 线程池大小
        // .threadPoolSize(5)
        // // 线程优先级
        // .threadPriority(Thread.NORM_PRIORITY - 2)
        // // 弱引用内存
        // .memoryCache(new WeakMemoryCache())
        // // 内存缓存大小
        // // .memoryCacheSize(20 * 1024 * 1024)
        // // 本地缓存大小
        // // .discCacheSize(50 * 1024 * 1024)
        // // 禁止同一张图多尺寸缓存
        // .denyCacheImageMultipleSizesInMemory()
        // // 设置硬盘缓存路径并不限制硬盘缓存大小
        // // .discCache(new
        // UnlimitedDiscCache(cacheDir)).discCacheFileNameGenerator(new
        // Md5FileNameGenerator())
        // .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        // myImageLoader =
        // com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        // // 禁用输出log功能
        // myImageLoader.init(config);
    }

    /**
     * 添加请求队列
     *
     * @param req
     * @param tag代表本activity或者fragment的标签
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // req.setRetryPolicy(new DefaultRetryPolicy(2 * 1000, 1, 1.0f));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        BaseApplication.mApp.getmRequestQueue().add(req);
    }

    /**
     * 取消对应的请求
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (BaseApplication.mApp.getmRequestQueue() != null) {
            BaseApplication.mApp.getmRequestQueue().cancelAll(tag);
        }
    }

    /**
     * 设置圆形显示
     *
     * @param imageView
     * @param url
     * @param defaultDrawable
     */
    public void setCircleImageByUrl(ImageView imageView, String url,
                                    int defaultDrawable) {
        DisplayImageOptions options = DisplayImageOptionsUtils
                .getCircleBitMap(defaultDrawable);
        try {

            ImageLoader.getInstance().displayImage(url, imageView, options);
        } catch (Exception e) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + defaultDrawable, imageView, options);
        }
    }

    /**
     * 设置矩形圆角的显示
     *
     * @param imageView
     * @param url
     * @param defaultDrawable
     */
    public void setRoundedImageByUrl(ImageView imageView, String url,
                                     int defaultDrawable, int roundPixels) {
        DisplayImageOptions options = DisplayImageOptionsUtils.getRoundBitMap(
                roundPixels, defaultDrawable);
        try {
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } catch (Exception e) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + defaultDrawable, imageView, options);
        }
    }

    /**
     * 设置没有圆角显示
     *
     * @param imageView
     * @param url
     * @param defaultDrawable
     * @param roundPixels
     */
    public void setBigNomalImageByUrl(ImageView imageView, String url,
                                      int defaultDrawable) {
        DisplayImageOptions options = DisplayImageOptionsUtils
                .getBigBitMap(defaultDrawable);
        try {
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } catch (Exception e) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + defaultDrawable, imageView, options);
        }
    }

    /**
     * 通过网络url获取bitmap图片
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromURL(String url) {
        return ImageLoader.getInstance().loadImageSync(url);
    }

    /**
     * 获取主线程ID
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取主线程
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 获取主线程的looper
     */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static BaseApplication getApplication() {
        return mApp;
    }
}
