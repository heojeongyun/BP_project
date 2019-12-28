package com.example.nt_project02.Native_Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;


public class Profile extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private String destinationUid;

    private MyPagerAdapter mPagerAdapter;
    private UserModel userModel;
    private TextView nick_text;
    private TextView self_info_text;
    private ImageView profile_image;
    private String uid;

    private void initializeViewPager() {
        // 각 review 의 내용들을 표시관련
        mViewPager = (ViewPager) findViewById
                ( R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(this);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(this);
    }

    private void initializeTabLayout() {
        // 각 review tab 관련 tab이 선택되었을 때 선택된  tab의 내용으로 변경
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        initializeViewPager();
        initializeTabLayout();

        Intent data=getIntent();
        // Firebase db로 부터 저장된 현지인의 정보들을 불러오는 작업
        userModel = data.getParcelableExtra("destination_UserModels");
        destinationUid=userModel.getUid();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        /*destinationUid=userModel.getUid();*/



        nick_text=(TextView) findViewById(R.id.nickAgeTV);
        self_info_text=(TextView) findViewById(R.id.Self_info_TextView);
        profile_image=(ImageView) findViewById(R.id.profile_Image);

        nick_text.setText(userModel.getNick());
        self_info_text.setText(userModel.getSelf_info());
        // 이미지 URL을 통해 이미지를 불러오는 작업
        Glide.with(getApplicationContext())
                .load(userModel.getImageurl())
                .apply(new RequestOptions().circleCrop())
                .into(profile_image);




        // 현지인과 채팅을 하기 위해 채팅버튼 클릭시 현지인과의 1대1 대화창으로 연결해주는 부분
        Button chat_button=(Button) findViewById(R.id.profile_chat_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 사용자가 현지인이 아닌 경우에만 현지인과의 1대1 대화창으로 연결해주는 부분
                if(destinationUid != uid) {
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    intent.putExtra("destination_Uid", destinationUid);
                    startActivity(intent);
                }

            }
        });


    }
    // Profile Scroll 부분을 관리하는 부분
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
