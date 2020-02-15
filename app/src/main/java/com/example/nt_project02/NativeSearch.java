package com.example.nt_project02;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;

public class NativeSearch extends AppCompatActivity {

    private ViewPager mViewPager;

    TextViewPagerAdapter adapter = new TextViewPagerAdapter(getSupportFragmentManager());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativesearch);
        mViewPager = (ViewPager) findViewById(R.id.activity_nativesearch_vp);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_ns_tab);
        tabLayout.setupWithViewPager(mViewPager);
        EditText editText = (EditText) findViewById(R.id.activity_ns_txt);
    }
    public void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new Search_Popular(), "인기");
        adapter.addFragment(new Search_Account(), "계정");
        adapter.addFragment(new Search_Place(), "지역");
        adapter.addFragment(new Search_Hashtag(), "Hashtag");
        viewPager.setAdapter(adapter);
    }
}
