package com.example.nt_project02;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.nt_project02.Fragment.Chatting_Fragment;
import com.example.nt_project02.Fragment.News_Fragment;
import com.example.nt_project02.Fragment.PeopleFragment;
import com.example.nt_project02.Fragment.Setting_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Member;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    ViewPager viewPager;
    private Integer navigationTabBar_position;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        //파이어 베이스 유저 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //유저가 없다면 회원가입으로 돌아가게 하기
        if (user == null) {
            MystartActivity(Sign_UpActivity.class);
        } else {// 회원가입 로그인 성공

            // 파이어스토어 객체선언
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
            final DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                //정보 가져오는 것이 성공적일 때
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        //DocumentSnapshot에 정보를 담아둠
                        DocumentSnapshot document = task.getResult();
                        //document가 null이 아닐 때
                        if (document != null) {
                            //재차 확인
                            if (document.exists()) {
                                //LogCat에 출력
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                //아니면 No such document라고 출력됨
                            } else {
                                Log.d(TAG, "No such document");
//                                로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                MystartActivity(MemberActivity.class);
                            }
                        }
                        //아예 오류떠서 실패했을 때
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }


        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);

        viewPager.setOffscreenPageLimit(4);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        News_Fragment news_fragment = new News_Fragment();
        adapter.addItem(news_fragment);

        PeopleFragment peopleFragment = new PeopleFragment();
        adapter.addItem(peopleFragment);

        Chatting_Fragment chatting_fragment = new Chatting_Fragment();
        adapter.addItem(chatting_fragment);

        Setting_Fragment setting_fragment = new Setting_Fragment();
        adapter.addItem(setting_fragment);

        viewPager.setAdapter(adapter);
        initUI();






        //해당 단말기 토큰을 가져온다(푸시 메세지 등등 전용)
        passPushTokenToServer();

        //해시키
        getHashKey();

    }
    //해시키 구하는 메서드
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }





    @Override
    //뒤로가기 버튼 비활성화 (로그아웃 해야 처음 로그인 화면으로 가게끔)
    public void onBackPressed() {

        //super.onBackPressed();
    }

    //액티비티 이동 메서드
    private void MystartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void initUI() {

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.home),
                        Color.parseColor(colors[0]))
                        //.selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("소식")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.profile),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("매칭")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.chatting),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("채팅")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.setting),
                        Color.parseColor(colors[4]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("설정")
                        .build()
        );


        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar.getModels().get(position).hideBadge();

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 500);
    }

    //어댑터 설정 클래스
    class PagerAdapter extends FragmentStatePagerAdapter {


        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public PagerAdapter(FragmentManager fm) {

            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }


    //토큰 가져오는 메소드
    void passPushTokenToServer() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", token);

        //파이어베이스의 해당하는 아이디에 토큰을 업데이트
        FirebaseFirestore.getInstance().collection("users").document(uid).update(map);


    }

}




