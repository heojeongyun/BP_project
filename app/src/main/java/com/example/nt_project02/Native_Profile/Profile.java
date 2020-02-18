package com.example.nt_project02.Native_Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Native_Register;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Profile extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private String destinationUid;

    private MyPagerAdapter mPagerAdapter;
    private UserModel userModel;
    private UserModel destination_userModel;
    private TextView nick_text;
    private TextView self_info_text;
    private ImageView profile_image;
    private String user_kind;
    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String uid;
    private CheckBox activity_profile_BookMark;
    private FirebaseFirestore db;
    private DocumentReference destination_Ref;
    private DocumentReference Ref;
    private List<String> bookmarks_array;
    private String TAG="Profile";


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
        destination_userModel = data.getParcelableExtra("destination_UserModels");
        destinationUid=destination_userModel.getUid();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();




        nick_text=(TextView) findViewById(R.id.nickAgeTV);
        self_info_text=(TextView) findViewById(R.id.Self_info_TextView);
        profile_image=(ImageView) findViewById(R.id.profile_Image);

        nick_text.setText(destination_userModel.getName());


        //현재 현지인 정보 출력

        Log.d(TAG,"Current data:"+destination_userModel.toString());

        // 이미지 URL을 통해 이미지를 불러오는 작업
        Glide.with(getApplicationContext())
                .load(destination_userModel.getImageurl())
                .apply(new RequestOptions().circleCrop())
                .into(profile_image);




        // 현지인과 채팅을 하기 위해 매칭요청 하는 버튼
        Button chat_button=(Button) findViewById(R.id.profile_chat_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스 requests 필드에 아이디 등록
                Ref.update("requests",FieldValue.arrayUnion(destinationUid));
                destination_Ref.update("requests",FieldValue.arrayUnion(uid));
                startToast("매칭을 성공적으로 요청했습니다");
            }
        });

        activity_profile_BookMark=(CheckBox) findViewById(R.id.activity_profile_BookMark);

        //문서 위치 선언
        db=FirebaseFirestore.getInstance();
        //현재 여행자 정보 파이어스토어 경로
        Ref=db.collection("users").document(FirebaseAuth.getInstance().getUid());

        //현재 현지인 정보 파이어스토어경로
        destination_Ref=db.collection("users").document(destinationUid);

        //현재 여행자 정보 가져오기
        db.collection("users")
                .whereEqualTo("uid",uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userModel=document.toObject(UserModel.class);
                                bookmarks_array=userModel.getBookmarks();
                                //즐켜찾기 목록이 있을 시
                                if(bookmarks_array !=null) {
                                    //현재 프로필 현지인이 포함되어 있으면
                                    if(bookmarks_array.contains(destinationUid)){
                                        activity_profile_BookMark.setChecked(true);
                                    }else{
                                        activity_profile_BookMark.setChecked(false);
                                    }
                                    //startToast(bookmarks_array.get(0));
                                }
                                Log.d("Profile_Traver_data", document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.d("Profile_Traver_data", "Error getting documents: ", task.getException());
                        }
                    }
                });








        //즐겨찾기 체크박스 클릭 시
        activity_profile_BookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 등록 되어 있을 때
                if(activity_profile_BookMark.isChecked()){
                    //즐겨찾기 리스트 추가 (중복불가)
                    Ref.update("bookmarks",FieldValue.arrayUnion(destinationUid));
                    destination_Ref.update("bookmarks_number", FieldValue.increment(1));

                //즐겨찾기 되어 있지 않을 때
                }else{
                    //즐겨찾기 리스트 항목 제거
                    Ref.update("bookmarks",FieldValue.arrayRemove(destinationUid));
                    destination_Ref.update("bookmarks_number", FieldValue.increment(-1));//현지인 즐겨찾기 수 추가
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

    private void startToast(String msg){

        Toast.makeText(Profile.this, msg,
                Toast.LENGTH_SHORT).show();
    }



}
