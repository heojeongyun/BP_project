package com.example.nt_project02.Native_Profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.nt_project02.R;
import com.google.android.material.tabs.TabLayout;

public class Profile extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;

    private MyPagerAdapter mPagerAdapter;

    private void initializeViewPager() {

        mViewPager = (ViewPager) findViewById
                ( R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(this);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(this);
    }

    private void initializeTabLayout() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // 탭이 선택되면 position 을 가져와 해당 뷰 노출
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // TODO : tab의 상태가 선택되지 않음으로 변경.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // TODO : 이미 선택된 tab이 다시
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViewPager();
        initializeTabLayout();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        //mViewPager.setCurrentItem(position);
    }

    @Override // 탭 슬라이드(분홍색)
    public void onPageSelected(int position) {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout) ;
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        tab.select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
