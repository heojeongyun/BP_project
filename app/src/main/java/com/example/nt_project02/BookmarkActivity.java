package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Native_Profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
    private ActivityBookmarkRecyclerViewAdapter adapter;
    private String uid;
    private UserModel userModel;
    private List<String> bookmarks_array;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        adapter=new ActivityBookmarkRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_bookmark_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookmarkActivity.this));


        recyclerView.setAdapter(adapter);


    }




    class ActivityBookmarkRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;


        public ActivityBookmarkRecyclerViewAdapter() {



            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

            db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();

            userModel=null;




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
                                    Log.d("Bookmark_Traver_data", document.getId() + " => " + document.getData());

                                    if(bookmarks_array !=null) {
                                        for (int i = 0; i < bookmarks_array.size(); i++) {
                                            db.collection("users")
                                                    .whereEqualTo("uid", bookmarks_array.get(i))
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    userModels.add(document.toObject(UserModel.class));
                                                                }
                                                                notifyDataSetChanged();
                                                            } else {

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            } else {
                                Log.d("Bookmark_data", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            //startToast(userModel.getBookmarks().get(0));











        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


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





    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView Nick_textView;
        public TextView Region_textView;
        public TextView Hash_textView;

        public CustomViewHolder(View view) {

            super(view);
            imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
            Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
            Region_textView=(TextView) view.findViewById(R.id.frienditem_region);
            Hash_textView=(TextView) view.findViewById(R.id.frienditem_hash);

        }
    }

    private void MystartActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그아웃시 초기화하는 부분
        startActivity(intent);

    }

    private void startToast(String msg){

        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

}