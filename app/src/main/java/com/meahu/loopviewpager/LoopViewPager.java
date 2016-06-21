package com.meahu.loopviewpager;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 实现了自动轮播的ViewPager
 * Created by Administrator on 2016/1/22 0022.
 */
public class LoopViewPager extends ViewPager implements Runnable {

    private float mPagerRate = 1.f;
    /**
     * 是否能够
     */
    private boolean mIsLoopAble = false;
    /**
     * 是否触摸在pager控件上
     */
    private boolean mIsTouching = false;
    /**
     * 是否自动循环
     */
    private boolean mIsAutoLoop = false;
    /**
     * 是否初始化指示器
     */
    private boolean mIsInitIndicator = false;
    /**
     * 循环数据的size
     */
    private int mDataSourceSize = 0;
    /**
     * 自动循环时间的间隔（单位：毫秒）
     */
    private int mAutoLoopRate = 5000;

    private long mTouchTime = 0;

    private int mChooseResId = -1;

    private int mUnChooseResId = -1;

    private ViewGroup mDotLayout = null;
    /**
     * 指示器的点集合
     */
    private List<ImageView> mDotImgList = null;

    private List<ImageView> mDataImgList = null;

    private List<PagerFormatData> mSourceList = null;

    private View[] mCollisionViews = null;

    private LoopViewPager mInstance = null;

    private LoopViewAdapter mLoopAdapter = null;

    private int mPagePadding = 0;

    private Context mCtx;

    long startTime = 0;

    private TextView mDescriptionTextView = null;

    public LoopViewPager(Context context) {
        super(context);
        onCreate(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    private void onCreate(Context context) {
        mInstance = this;
        mCtx = context;

    }

    public final void init(@NonNull List<PagerFormatData> sourceList, boolean autoLoop, boolean loopAble) {
        mDataSourceSize = sourceList.size();
        mSourceList = sourceList;

        mIsAutoLoop = autoLoop;

        mIsLoopAble = loopAble;

        mLoopAdapter = new LoopViewAdapter(initPagerData(sourceList));

        mInstance.setAdapter(mLoopAdapter);

        mInstance.addOnPageChangeListener(new OnPageChangeListener() {
            int oldPosition = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (mIsLoopAble) {
                    if (position == mDataSourceSize + 1) {
                        mInstance.setCurrentItem(1, false);
                    }

                    if (position == 0 && positionOffset < 0.00001) {
                        mInstance.setCurrentItem(mDataSourceSize, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (mDotLayout != null && mDotImgList != null) {
                    if (position > 0 && position < mDataSourceSize + 1) {
                        if (mDescriptionTextView != null){
                            mDescriptionTextView.setText(mSourceList.get(position - 1).name);
                        }
                        if (oldPosition < mDotImgList.size()) {
                            mDotImgList.get(oldPosition).setImageResource(mUnChooseResId);
                        }
                        int index = position - 1;
                        oldPosition = index;
                        if (index < mDotImgList.size()) {
                            mDotImgList.get(index).setImageResource(mChooseResId);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mInstance.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mIsTouching = true;
                        if (mCollisionViews != null) {
                            for (View itemView : mCollisionViews) {
                                itemView.setEnabled(false);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
                        mTouchTime = System.currentTimeMillis();
                        postDelayed(new DelayPostTask(),mAutoLoopRate);
                        if (mCollisionViews != null) {
                            for (View itemView : mCollisionViews) {
                                itemView.setEnabled(true);
                            }
                        }
                        break;
                }
                return false;
            }
        });

        if (mIsAutoLoop) {
            postDelayed(this, mAutoLoopRate);
        }

        if (mIsLoopAble) {
            mInstance.setCurrentItem(1, false);
            if (mDescriptionTextView != null && sourceList.size() > 0){
                mDescriptionTextView.setText(sourceList.get(0).name);
            }
        }
    }

    public void setPagePadding(int paddingPixels) {
        this.mPagePadding = paddingPixels;
    }

    public void setPagerWidth(int pagerWidth) {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int mScreenWidth = dm.widthPixels;
        this.mPagerRate = (pagerWidth + 0.0f) / mScreenWidth;
    }

    /**
     * 初始pager指示器
     *
     * @param chosenRes
     * @param unchosenRes
     * @param dotLayout
     */
    public void initIndicator(int chosenRes, int unchosenRes, ViewGroup dotLayout) {
        mChooseResId = chosenRes;
        mUnChooseResId = unchosenRes;
        mDotLayout = dotLayout;
        View descriptionView = null;

        if (mDotLayout != null) {
            if (mDotLayout.getChildCount() > 0){
                descriptionView = mDotLayout.getChildAt(0);
                if (!(descriptionView instanceof TextView)){
                    descriptionView = null;
                }
            }
            mDotLayout.removeAllViews();
            if (descriptionView != null && mSourceList.size() > 0) {
                mDescriptionTextView = (TextView) descriptionView;
                mDescriptionTextView.setText(mSourceList.get(0).name);
                mDotLayout.addView(mDescriptionTextView);
            } else {
                mDescriptionTextView = null;
            }
        } else {
            return;
        }

        if (mDotImgList == null) {
            mDotImgList = new ArrayList<ImageView>();
        } else {
            mDotImgList.clear();
        }

        ViewGroup.LayoutParams itemDotImgLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDataSourceSize; i++) {
            ImageView dotImg = new ImageView(mCtx);
            if (i == 0) {
                dotImg.setImageResource(mChooseResId);
            } else {
                dotImg.setImageResource(mUnChooseResId);
            }

            dotImg.setLayoutParams(itemDotImgLp);
            mDotImgList.add(dotImg);
            mDotLayout.addView(dotImg);
        }

        if (mChooseResId > 0 && mUnChooseResId > 0 && mDotLayout != null) {
            mIsInitIndicator = true;
        }
    }

    /**
     * 处理滑动冲突
     *
     * @param views
     */
    public void handleCollision(final View[] views) {
        mCollisionViews = views;
    }

    /**
     * 设置自动循环的时间间隔
     *
     * @param seconds
     */
    public void setAutoLoopRate(int seconds) {
        mAutoLoopRate = seconds;
    }

    /**
     * 当不在本页面时停止滑动
     */
    public void onPause() {
        mIsAutoLoop = false;
    }

    /**
     * 页面恢复时继续滑动
     */
    public void onResume() {
        mIsAutoLoop = true;
    }

    /**
     * 初始pager数据，并设置点击事件
     *
     * @param sourceList
     */
    private List<ImageView> initPagerData(List<PagerFormatData> sourceList) {
        if (sourceList == null) {
            return null;
        }
        if (mDataImgList == null) {
            mDataImgList = new ArrayList<ImageView>();
        } else {
            mDataImgList.clear();
        }


        for (int i = 0; i < mDataSourceSize; i++) {
            RelativeLayout.LayoutParams itemImgLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final PagerFormatData itemInfo = sourceList.get(i);
            ImageView itemImg = new ImageView(mCtx);
            if (i == 0) {
                if (mIsLoopAble) {
                    ImageView finalImg = new ImageView(mCtx);
                    ImageLoader.loadImage(getContext(), sourceList.get(mDataSourceSize - 1).imgUrl, finalImg);
                    finalImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    finalImg.setBackgroundColor(getResources().getColor(R.color.v1_qys_ffe5e5e5));
                    mDataImgList.add(finalImg);
                }
                itemImg.setPadding(mPagePadding, 0, mPagePadding, 0);
            } else {
                itemImg.setPadding(0, 0, mPagePadding, 0);
            }
            ImageLoader.loadImage(getContext(), itemInfo.imgUrl, itemImg);
            itemImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final int pageIndex = i;
            itemImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //AppTodoMgr.executeTodo((Activity) mCtx, (Integer) itemInfo.todoCode, itemInfo.todoContent);
                    Log.i("LoopViewPager", "onClick: 我被点击了");
                }
            });
            itemImg.setLayoutParams(itemImgLp);
            itemImg.setBackgroundColor(getResources().getColor(R.color.v1_qys_ffe5e5e5));

            mDataImgList.add(itemImg);
            if (i == mDataSourceSize - 1) {
                if (mIsLoopAble) {
                    ImageView firstImg = new ImageView(mCtx);
                    ImageLoader.loadImage(getContext(), sourceList.get(0).imgUrl, firstImg);
                    firstImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    firstImg.setBackgroundColor(getResources().getColor(R.color.v1_qys_ffe5e5e5));
                    mDataImgList.add(firstImg);
                }
            }
        }
        return mDataImgList;
    }

    /**
     * 实现自动循环任务
     */
    @Override
    public void run() {
        if (mDataSourceSize > 1) {
            boolean runAble = false;
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= mAutoLoopRate) {
                runAble = true;
                startTime = currentTime;
            }

            if (mIsAutoLoop && !mIsTouching && runAble) {

                int nextItem = mInstance.getCurrentItem() + 1;
                if (nextItem == mDataSourceSize + 1) {
                    nextItem = 1;
                }
                mInstance.setCurrentItem(nextItem, true);
            }
            postDelayed(this, mAutoLoopRate);
        }
    }

    private class DelayPostTask implements Runnable{

        @Override
        public void run() {
            if (System.currentTimeMillis() - mTouchTime >= mAutoLoopRate){
                mIsTouching = false;
            }else {
                long internalTime = mAutoLoopRate -(System.currentTimeMillis() - mTouchTime);
                if (internalTime < 0) {
                    internalTime = 0;
                }
                postDelayed(this, internalTime);
            }
        }
    }

    public static class PagerFormatData {
        public String imgUrl;
        public Object todoCode;
        public String todoContent;
        public String name;
        public String description;
    }

    /**
     * 无限循环pager adapter
     */
    class LoopViewAdapter extends PagerAdapter {
        private List<ImageView> imgSourceList;

        public LoopViewAdapter(@NonNull List<ImageView> imgSourceList) {
            this.imgSourceList = new ArrayList<ImageView>(imgSourceList);
        }

        public synchronized void setImgSourceList(@NonNull List<ImageView> sourceList) {
            imgSourceList = new ArrayList<ImageView>(sourceList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imgSourceList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = imgSourceList.get(position);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imgSourceList.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return mPagerRate;
        }
    }

    public void notifyDataChanged(List<PagerFormatData> sourceList) {
        if (mLoopAdapter != null) {
            mLoopAdapter.setImgSourceList(initPagerData(sourceList));
        }
    }

    public int getSourceCount() {
        int count = 0;
        if (mLoopAdapter != null) {
            if (mIsLoopAble) {
                count = mLoopAdapter.getCount() - 2;
            } else {
                count = mLoopAdapter.getCount();
            }
        }
        return count > 0 ? count : 0;
    }
}
