package com.meahu.loopviewpager;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LoopViewPager mBannerPager;
    private LinearLayout mBannerDotLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBannerPager = (LoopViewPager) findViewById(R.id.vp_banner);
        mBannerDotLayout= (LinearLayout) findViewById(R.id.banner_dot_layout);
        initBannerView();
    }
    /**
     * 初始化BannerView  初始化首页轮播图
     */
    private void initBannerView() {
        List<LoopViewPager.PagerFormatData> newSourceList = getPagerData();
        mBannerPager.init(newSourceList, true, true);
        mBannerPager.initIndicator(R.drawable.v5_xiaoying_materials_point_choose, R.drawable.v5_xiaoying_materials_point_unchoose, mBannerDotLayout);

    }
    /**
     * 获取需要展示的图片url
     * @return
     */
    public List<LoopViewPager.PagerFormatData> getPagerData() {
        List<LoopViewPager.PagerFormatData> imageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LoopViewPager.PagerFormatData imageData = new LoopViewPager.PagerFormatData();
            imageData.imgUrl = "http://app.xiaoying.tv/android/TEMPLATE/2016/0616/1254/rollimage/30/10/icon.jpg";
            imageList.add(imageData);
        }

        return imageList;
    }
}
