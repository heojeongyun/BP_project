package com.example.nt_project02;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Native_Profile.Profile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class NativeSearch extends AppCompatActivity {



    public EditText editText;
    private  NativeSearchRecyclerViewAdapter adapter;
    private String TAG="NativeSearch";
    private String S;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativesearch);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_ns_tab);
        editText = (EditText) findViewById(R.id.activity_ns_txt);



        editText.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                S=s.toString();

            }
        });




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final int pos=tab.getPosition();  // 탭 위치 받아오기
                Log.d(TAG,"pos:"+pos);
                if(pos==0){
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

                }
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

                }
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

                }
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







        List<UserModel> userModels;
        List<UserModel> saveList;
        public NativeSearchRecyclerViewAdapter() {



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            saveList=new ArrayList<>();

            db.collection("users")
                    .whereEqualTo("user_kind", "현지인")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            userModels.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc != null) {


                                    userModels.add(doc.toObject(UserModel.class));
                                    saveList.add(doc.toObject(UserModel.class));

                                }
                            }
                            notifyDataSetChanged();
                            Log.d(TAG, "Current data: " + userModels);
                        }
                    });

        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            userModels.clear();
            if (charText.length() == 0) {
                userModels.addAll(saveList);
            } else {
                for (UserModel user : saveList) {
                    String name = user.getName();
                    if (name.toLowerCase().contains(charText)) {
                        userModels.add(user);
                    }
                }
            }
            notifyDataSetChanged();
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
            //((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hash);


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


        public  void searchUser_name(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getName() !=null &&saveList.get(i).getName().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }
        public  void searchUser_region(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getRegion() !=null &&saveList.get(i).getRegion().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }

        public  void searchUser_hashtag(String search){

            userModels.clear();

            for(int i = 0; i < saveList.size(); i++){

                if(saveList.get(i).getHashtag() !=null &&saveList.get(i).getHashtag().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


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
