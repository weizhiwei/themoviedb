package com.grabtaxi.themoviedb;

import android.app.Application;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Database.getInstance().open(getApplicationContext());
        MyVolley.init(getApplicationContext());
    }

    // utils
    public static void updateImageView(final ImageView imageView, final String url) {
        if (null != imageView && !TextUtils.isEmpty(url)) {
            MyVolley.getImageLoader().get(url,
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            if (null != imageContainer.getBitmap()) {
                                imageView.setImageBitmap(imageContainer.getBitmap());
                                imageView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
        }
    }


}
