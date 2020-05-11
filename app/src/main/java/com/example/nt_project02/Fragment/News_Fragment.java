package com.example.nt_project02.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nt_project02.R;

// 현재 지정해둔 사진을 띄워준다..앞으로 대대적인 보강이 필요한부분
public class News_Fragment extends Fragment {

    ViewFlipper v_fllipper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.news, container, false);


        int images[] = {
                R.drawable.bannerimage1,
                R.drawable.bannerimage3,
                R.drawable.bannerimage4
        };

        v_fllipper = rootView.findViewById(R.id.image_slide);

        for (int image : images) {
            fllipperImages(image);
        }

        return rootView;
    }

    // 이미지 슬라이더 구현 메서드
    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);

        v_fllipper.addView(imageView);      // 이미지 추가
        v_fllipper.setFlipInterval(3000);       // 자동 이미지 슬라이드 딜레이시간(1000 당 1초)
        v_fllipper.setAutoStart(true);          // 자동 시작 유무 설정

        // animation
        v_fllipper.setInAnimation(getContext() ,R.anim.slide_in_right);
        v_fllipper.setOutAnimation(getContext(), R.anim.slide_out_left);
    }







}