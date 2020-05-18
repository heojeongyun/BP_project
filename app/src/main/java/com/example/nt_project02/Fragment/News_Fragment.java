package com.example.nt_project02.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nt_project02.CustomData.ReviewData;
import com.example.nt_project02.Native_Profile.Profile;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// 현재 지정해둔 사진을 띄워준다..앞으로 대대적인 보강이 필요한부분
public class News_Fragment extends Fragment {

    private ViewFlipper v_fllipper;
    private ArrayList<Integer> news_items;
    private NewsRecyclerViewAdapter adapter;

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




        adapter = new NewsRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.news_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerView.setAdapter(adapter);
        news_items=new ArrayList<Integer>();

        news_items.add(R.drawable.hashimage1);



        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //초기화
                news_items.clear();
                switch (view.getId()) {


                    case R.id.date_course :
                        news_items.add(R.drawable.hashimage1);
                        adapter.notifyDataSetChanged();
                        break ;
                    case R.id.alone_travel :
                        news_items.add(R.drawable.hashimage2);
                        adapter.notifyDataSetChanged();
                        break ;
                    case R.id.activity_course :
                        news_items.add(R.drawable.hashimage3);
                        adapter.notifyDataSetChanged();
                        break ;
                    case R.id.gastro_venture :
                        news_items.add(R.drawable.hashimage4);
                        adapter.notifyDataSetChanged();
                        break ;
                    case R.id.shopping :
                        news_items.add(R.drawable.hashimage5);
                        adapter.notifyDataSetChanged();
                        break ;
                    case R.id.cultural_life :
                        news_items.add(R.drawable.hashimage6);
                        adapter.notifyDataSetChanged();
                        break ;
                }
            }
        };

        Button date_course = (Button) rootView.findViewById(R.id.date_course) ;
        date_course.setOnClickListener(onClickListener) ;
        Button alone_travel = (Button) rootView.findViewById(R.id.alone_travel) ;
        alone_travel.setOnClickListener(onClickListener) ;
        Button activity_course = (Button) rootView.findViewById(R.id.activity_course) ;
        activity_course.setOnClickListener(onClickListener) ;
        Button gastro_venture = (Button) rootView.findViewById(R.id.gastro_venture) ;
        gastro_venture.setOnClickListener(onClickListener) ;
        Button shopping = (Button) rootView.findViewById(R.id.shopping) ;
        shopping.setOnClickListener(onClickListener) ;
        Button cultural_life = (Button) rootView.findViewById(R.id.cultural_life) ;
        cultural_life.setOnClickListener(onClickListener) ;


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

    class NewsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public NewsRecyclerViewAdapter() {

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            //Log.e(TAG,reviewDataList.get(position).mContent);
            ((CustomViewHolder) holder).news_image.setImageDrawable(getResources().getDrawable(news_items.get(position)));


        }

        @Override
        public int getItemCount() {

            return news_items.size();
        }


    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView news_image;



        public CustomViewHolder(View view) {
            super(view);

            news_image=(ImageView) view.findViewById(R.id.news_image);



        }
    }







}