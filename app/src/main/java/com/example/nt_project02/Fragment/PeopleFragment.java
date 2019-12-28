package com.example.nt_project02.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Native_Profile.Profile;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class
PeopleFragment extends Fragment {

    @Nullable





 /*   private List<UserModel> userModel;
    private ArrayList<UserModel> userModels;
    private ArrayList<UserModel> arrayList;
    private EditText editText;*/
    private PeopleFragmentRecyclerViewAdapter adapter;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false); // fragment_people 실제 객체화




 /*       editText=(EditText) view.findViewById(R.id.txt_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                adapter.filter(text);


            }
        });*/

        // 어댑터 객체생성
        adapter=new PeopleFragmentRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        recyclerView.setAdapter(adapter);



        return view;
    }



    // adapter 객체로 생성한 PeopleFragmentRecyclerViewAdapter 클래스
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;
        public PeopleFragmentRecyclerViewAdapter() {



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            //arrayList=new ArrayList<>();


            db.collection("users") // Firebase의 db에서 사용자 정보를 불러온다
                    .whereEqualTo("user_kind", "현지인")
                    .get() // Firebase의 db에서 사용자 정보중 현지인의 정보만 불러온다
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) { // 정보를 성공적으로 불러왔을 경우 현지인 목록에 현지인들의 정보를 반복 추가한다
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    userModels.add(document.toObject(UserModel.class));
                                    //arrayList.add(document.toObject(UserModel.class));

                                }
                                notifyDataSetChanged();
                            } else {

                            }
                        }
                    });



   /*         FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        userModels.add(snapshot.getValue(UserModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/



        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Firebase db에서 불러온 현지인의 정보를 UserModels에 붙여넣는 양식
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            Glide.with // Glide는 이미지의 URL을 이미지로 변환해 빠르게 불러올 수 있는 라이브러리이다
                    // Firebase db에 저장되어있는 이미지 URL로부터 이미지를 불러오는 작업이다
                    (holder.itemView.getContext())
                    .load(userModels.get(position).imageurl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);
            // Item_friends의 별명, 사는 지역, 해쉬태그의 포지션을 카운트한다
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).nick);
            ((CustomViewHolder) holder).Region_textView.setText(userModels.get(position).region);
            ((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hash);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        // 어댑터 내의 POSITION 값이 존재할 경우 그 현지인을 클릭시에 Profile로 갱신한다
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getContext(), Profile.class);
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });

        }

        @Override
        public int getItemCount() { //userModels의 사이즈를 정의하는 함수인데 필요한가 싶다..

            return userModels.size();
        }

   /*     public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            userModels.clear();
            if (charText.length() == 0) {
                userModels.addAll(arrayList);
            } else {
                for (UserModel user : arrayList) {
                    String name =context.getResources().getString(user);
                    if (name.toLowerCase().contains(charText)) {
                        userModels.add(user);
                    }
                }
            }
            notifyDataSetChanged();
        }*/



    }

        private class CustomViewHolder extends RecyclerView.ViewHolder { // 실제 사용자들에게 보여지는 부분이다
            public  ImageView imageView;
            public TextView Nick_textView;
            public TextView Region_textView;
            public TextView Hash_textView;
            // item_friends.xml 내의 여러 탭을 사용하기 위해서 선언하는 부분이다
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
                Region_textView=(TextView) view.findViewById(R.id.frienditem_region);
                Hash_textView=(TextView) view.findViewById(R.id.frienditem_hash);
            }
        }
    }






