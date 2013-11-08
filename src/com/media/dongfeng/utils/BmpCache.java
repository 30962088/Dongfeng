package com.media.dongfeng.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class BmpCache {

    private BmpCache(){}
    
//    private Object lock = new Object();
    
    private static BmpCache mInstance = null;
    
    private static Map<String, SoftReference<Bitmap>> mBmpCacheMap = new HashMap<String, SoftReference<Bitmap>>();
    
//    /**
//     * 被裁剪的图片内存缓存(如微博结构里的方图，圆角头像等)，复用时注意裁剪规则要保持一致
//     */
//    private static Map<String, SoftReference<Bitmap>> mPartialBmpCacheMap = new HashMap<String, SoftReference<Bitmap>>();
    
    private boolean mActive = true;
    
    public synchronized static BmpCache getInstance(){
        if(mInstance == null){
            mInstance = new BmpCache();
        }
        return mInstance;
    }
    
    public synchronized void save(String url, Bitmap bm){
        if(mActive == false){
            return;
        }
        if (bm == null || bm.isRecycled() || url == null
                || "".equals(url.trim())) {
            return;
        }
        mBmpCacheMap.put(url, new SoftReference<Bitmap>(bm));
    }

//    public synchronized void savePartial( String url, Bitmap bm ) {
//        if (mActive == false) {
//            return;
//        }
//        if (bm == null || bm.isRecycled() || url == null || "".equals(url.trim())) {
//            return;
//        }
//        mPartialBmpCacheMap.put(url, new SoftReference<Bitmap>(bm));
//    }

    public synchronized Bitmap get(String url){
        if(mActive == false){
            return null;
        }
        if(url != null && !url.trim().equals("")){
            if(mBmpCacheMap.containsKey(url)){
                Bitmap bmp = mBmpCacheMap.get(url).get();
                if(bmp == null){
                	mBmpCacheMap.remove(url);
                }
                return bmp;
            }
        }
        return null;
    }

//    public synchronized Bitmap getPartial( String url ) {
//        if (mActive == false) {
//            return null;
//        }
//        if (url != null && !url.trim().equals("")) {
//            if (mPartialBmpCacheMap.containsKey(url)) {
//                Bitmap bmp = mPartialBmpCacheMap.get(url).get();
//                if (bmp == null) {
//                    mPartialBmpCacheMap.remove(url);
//                }
//                return bmp;
//            }
//        }
//        return null;
//    }

    public synchronized void clear() {
        clear(mBmpCacheMap);
//        clear(mPartialBmpCacheMap);
    }

    private synchronized void clear( Map<String, SoftReference<Bitmap>> map ) {
        Iterator<Entry<String, SoftReference<Bitmap>>> itr = map.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, SoftReference<Bitmap>> e = itr.next();
            SoftReference<Bitmap> sr = e.getValue();
            if (sr != null) {
                Bitmap bmp = sr.get();
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                }
            }
        }
        map.clear();
    }
}
