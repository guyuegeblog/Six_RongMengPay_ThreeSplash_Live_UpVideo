package com.app.View;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.third.app.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Author: Jan
 * CreateTime:on 2016/9/30.
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
         常用的图片加载库：

         Universal Image Loader：一个强大的图片加载库，包含各种各样的配置，最老牌，使用也最广泛。
         Picasso: Square出品，必属精品。和OkHttp搭配起来更配呦！
         Volley ImageLoader：Google官方出品，可惜不能加载本地图片~
         Fresco：Facebook出的，天生骄傲！不是一般的强大。
         Glide：Google推荐的图片加载库，专注于流畅的滚动。
         */
        Glide.with(context).load(path).placeholder(R.drawable.allloading).
                error(R.drawable.allloading).into(imageView);
    }
}
