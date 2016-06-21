package com.meahu.loopviewpager;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by YangZhan on 2016/6/17.
 */
public class ImageLoader {
    public static void loadImage(Context context, String url, ImageView imageView){
        Picasso.with(context).load(url).into(imageView);
    }
}
