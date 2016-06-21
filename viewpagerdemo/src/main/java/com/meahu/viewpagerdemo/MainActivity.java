package com.meahu.viewpagerdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;


import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private ArrayList<Fragment> fragments;

    private ViewPager viewPager;

    private TextView tab_game;

    private TextView tab_app;

    private int line_width;

    private View line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab_game = (TextView) findViewById(R.id.tab_game);
        tab_app = (TextView) findViewById(R.id.tab_app);
        line = findViewById(R.id.line);


        // 初始化TextView动画
        ViewPropertyAnimator.animate(tab_app).scaleX(1.2f).setDuration(0);
        ViewPropertyAnimator.animate(tab_app).scaleY(1.2f).setDuration(0);

        fragments = new ArrayList<Fragment>();
        fragments.add(new APPFragment());
        fragments.add(new GameFragment());
        line_width = getWindowManager().getDefaultDisplay().getWidth()
                / fragments.size();
        line.getLayoutParams().width = line_width;
        line.requestLayout();

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(
                getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                changeState(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                float tagerX = arg0 * line_width + arg2 / fragments.size();
                ViewPropertyAnimator.animate(line).translationX(tagerX)
                        .setDuration(0);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        tab_game.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewPager.setCurrentItem(1);

            }
        });

        tab_app.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewPager.setCurrentItem(0);
            }
        });
    }

    /* 根据传入的值来改变状态 */
    private void changeState(int arg0) {
        if (arg0 == 0) {
            tab_app.setTextColor(getResources().getColor(R.color.green));
            tab_game.setTextColor(getResources().getColor(R.color.gray_white));
            ViewPropertyAnimator.animate(tab_app).scaleX(1.2f).setDuration(200);
            ViewPropertyAnimator.animate(tab_app).scaleY(1.2f).setDuration(200);
            ViewPropertyAnimator.animate(tab_game).scaleX(1.0f)
                    .setDuration(200);
            ViewPropertyAnimator.animate(tab_game).scaleY(1.0f)
                    .setDuration(200);

        } else {
            tab_game.setTextColor(getResources().getColor(R.color.green));
            tab_app.setTextColor(getResources().getColor(R.color.gray_white));
            ViewPropertyAnimator.animate(tab_app).scaleX(1.0f).setDuration(200);
            ViewPropertyAnimator.animate(tab_app).scaleY(1.0f).setDuration(200);
            ViewPropertyAnimator.animate(tab_game).scaleX(1.2f)
                    .setDuration(200);
            ViewPropertyAnimator.animate(tab_game).scaleY(1.2f)
                    .setDuration(200);
        }
    }


}
