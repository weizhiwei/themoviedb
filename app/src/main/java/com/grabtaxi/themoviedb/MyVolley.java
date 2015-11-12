/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grabtaxi.themoviedb;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.cache.SimpleImageLoader;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * Helper class that is used to provide references to initialized RequestQueue(s) and ImageLoader(s)
 * 
 * @author Ognyan Bankov
 * 
 */
public class MyVolley {
    private static RequestQueue mRequestQueue;
    private static SimpleImageLoader mImageLoader;
    private static AbstractHttpClient mHttpClient;

    private MyVolley() {
        // no instances
    }

    public static void init(Context context) {
        mHttpClient = new DefaultHttpClient();
        mRequestQueue = Volley.newRequestQueue(context, new HttpClientStack(mHttpClient));
//        DiskLruBasedCache.ImageCacheParams cacheParams = new DiskLruBasedCache.ImageCacheParams(context, "thumbs");
//        cacheParams.setMemCacheSizePercent(0.25f);
        mImageLoader = new SimpleImageLoader(context);
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static AbstractHttpClient getHttpClient() {
        if (mHttpClient != null) {
            return mHttpClient;
        } else {
            throw new IllegalStateException("HttpClient not initialized");
        }
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache} which effectively means
     * that no memory caching is used. This is useful for images that you know that will be show
     * only once.
     * 
     * @return
     */
    public static SimpleImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }
}