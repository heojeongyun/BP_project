package com.example.nt_project02;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.Native_Profile.Profile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class NativeSearch extends AppCompatActivity {



    public EditText editText;
    private  NativeSearchRecyclerViewAdapter adapter;
    private List<UserModel> userModels;
    private List<UserModel> saveList;
    private String TAG="NativeSearch";
    private String S;
    private int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativesearch);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_ns_tab);
        editText = (EditText) findViewById(R.id.activity_ns_txt);






        //검색창 쓴 글자 저장하기
        editText.addTextChangedListener(new TextWatcher() {

            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.searchUser_name(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

                S=s.toString();

                if (editText.getText().toString().equals("")) {
                    adapter.clear();
                    Log.d(TAG, "빈칸:" + userModels);
                    adapter.notifyDataSetChanged();
                }


            }
        });




        //탭 선택했을 때
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pos= tab.getPosition();  // 탭 위치 받아오기
                Log.d(TAG, "pos:" + pos);
                //인기 탭


                editText.addTextChangedListener(new TextWatcher() {
                    @Override

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.searchUser_name(s.toString());

                        Log.d(TAG, "s:" + s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //빈칸일 시 유저 안뜨게
                        if (editText.getText().toString().equals("")) {
                            adapter.clear();
                            Log.d(TAG, "빈칸:" + userModels);
                            adapter.notifyDataSetChanged();
                        }

                    }
                });

                if (S != null) {

                    adapter.searchUser_name(S);
                }

                //빈칸일 시 유저 안뜨게
                if (editText.getText().toString().equals("")) {
                    adapter.clear();
                    Log.d(TAG, "빈칸:" + userModels);
                    adapter.notifyDataSetChanged();
                }

                //계정 탭
                if(pos==1){



                    editText.addTextChangedListener(new TextWatcher() {
                        @Override

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.searchUser_name(s.toString());
                            Log.d(TAG,"s:"+s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {



                        }
                    });

                    if(S!=null){

                        adapter.searchUser_name(S);
                    }

                    //빈칸일 시 유저 안뜨게
                    if (editText.getText().toString().equals("")) {
                        adapter.clear();
                        Log.d(TAG, "빈칸:" + userModels);
                        adapter.notifyDataSetChanged();
                    }

                }
                //지역 탭
                if(pos==2){


                    editText.addTextChangedListener(new TextWatcher() {
                        @Override

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.searchUser_region(s.toString());
                            Log.d(TAG,"s:"+s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                    if(S!=null){
                        adapter.searchUser_region(S);
                    }

                    //빈칸일 시 유저 안뜨게
                    if (editText.getText().toString().equals("")) {
                        adapter.clear();
                        Log.d(TAG, "빈칸:" + userModels);
                        adapter.notifyDataSetChanged();
                    }

                }
                //HashTag 탭
                if(pos==3){



                    editText.addTextChangedListener(new TextWatcher() {
                        @Override

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.searchUser_hashtag(s.toString());
                            Log.d(TAG,"s:"+s);

                        }

                        @Override
                        public void afterTextChanged(Editable s) {


                        }
                    });

                    if(S!=null){
                        adapter.searchUser_hashtag(S);
                    }
                    //빈칸일 시 유저 안뜨게
                    if (editText.getText().toString().equals("")) {
                        adapter.clear();
                        Log.d(TAG, "빈칸:" + userModels);
                        adapter.notifyDataSetChanged();
                    }

                }





            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter=new NativeSearchRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_nativesearch_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(adapter);






    }

    class NativeSearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {








        public NativeSearchRecyclerViewAdapter() {



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            saveList=new ArrayList<>();

            Intent intent=getIntent();


            userModels=intent.getParcelableArrayListExtra("UserModels");
            saveList=intent.getParcelableArrayListExtra("SaveList");

            Log.d(TAG,"userModels:"+userModels.size());
            Log.d(TAG,"saveList:"+saveList.size());



            //초기에 유저 안뜨게
            if(editText.getText().toString().length()==0){
                userModels.clear();
            }



        }




        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);


                return new CustomViewHolder(view);


        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            Integer bookmarks_number;
            //색 가져오기
            Integer Gold = ContextCompat.getColor(getApplicationContext(), R.color.Gold);
            Integer Silver=ContextCompat.getColor(getApplicationContext(), R.color.Silver);
            Integer White=ContextCompat.getColor(getApplicationContext(), R.color.white);

            //초기화
            ((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(White);
            ((CustomViewHolder)holder).item_friend_RankImage.setImageResource(R.drawable.chick);
            ((CustomViewHolder)holder).imageView.setImageResource(R.drawable.user);

            bookmarks_number=userModels.get(position).bookmarks_number;

            if((bookmarks_number !=null)&& (bookmarks_number!=0)){
                if(bookmarks_number>=2){
                    ((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(Gold);
                    ((CustomViewHolder)holder).item_friend_RankImage.setImageResource(R.drawable.peacock);
                }else{
                    ((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(Silver);
                    ((CustomViewHolder)holder).item_friend_RankImage.setImageResource(R.drawable.chicken);
                }


            }
            //테스트

            //현지인 등록 이미지가 있을 경우 이미지 넣기
            if(userModels.get(position).imageurl!=null) {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
            }
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);
            ((CustomViewHolder) holder).Region_textView.setText(userModels.get(position).region);
            ((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hashtag);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getApplicationContext(), Profile.class);
                        Log.d(TAG,userModels.get(position).toString());
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });


        }

        @Override
        public int getItemCount() {

            return userModels.size();
        }


        //이름 검색 필터메서드
        public  void searchUser_name(String search){


            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getName() !=null &&saveList.get(i).getName().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }
        //지역 검색 필터메서드
        public  void searchUser_region(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getRegion() !=null &&saveList.get(i).getRegion().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }

        //해시태그 검색 필터메서드
        public  void searchUser_hashtag(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getHashtag() !=null &&saveList.get(i).getHashtag().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }

        //유저 안뜨게 하는 메서드
        public void clear(){
            userModels.clear();
            adapter.notifyDataSetChanged();
        }
    }





    private static class CustomViewHolder extends RecyclerView.ViewHolder {
        public  ImageView imageView;
        public TextView Nick_textView;
        public TextView Region_textView;
        public TextView Hash_textView;
        public LinearLayout fragment_people_ItemLayout;
        public ImageView item_friend_RankImage;

        public CustomViewHolder(View view) {
            super(view);
            fragment_people_ItemLayout=(LinearLayout) view.findViewById(R.id.fragment_people_itemLayout);
            imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
            Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
            Region_textView=(TextView) view.findViewById(R.id.frienditem_region);
            Hash_textView=(TextView) view.findViewById(R.id.frienditem_hash);
            item_friend_RankImage=(ImageView) view.findViewById(R.id.item_friend_RankImage);
        }



    }




}
