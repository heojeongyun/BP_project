package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.ChatModel;
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Fragment.Chatting_Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class Native_Chat_Management extends AppCompatActivity {

    private String destinationUid;
    private UserModel destination_userModel;
    private NativeChatManagementRecyclerViewAdapter adapter;
    private String uid;
    private UserModel userModel;
    private List<String> requests_array;
    private FirebaseFirestore db;
    private DocumentReference destination_Ref;
    private DocumentReference Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native__chat__management);



        adapter=new NativeChatManagementRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_ncm);
        recyclerView.setLayoutManager(new LinearLayoutManager(Native_Chat_Management.this));


        recyclerView.setAdapter(adapter);

    }




    class NativeChatManagementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;


        public NativeChatManagementRecyclerViewAdapter() {


            //파이어베이스에서 데이터를 참조
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
                                    requests_array=userModel.getRequests();
                                    //여행자가 요청한 요청 목록 적재
                                    Log.d("Requests", document.getId() + " => " + document.getData());

                                    if(requests_array !=null) {
                                        for (int i = 0; i < requests_array.size(); i++) {
                                            db.collection("users")
                                                    .whereEqualTo("uid", requests_array.get(i))
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
                                Log.d("Requests", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            //startToast(userModel.getBookmarks().get(0));











        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_chat_management, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(userModels.get(position).imageurl != null) {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
            }
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);



            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getApplicationContext(), Native_Chat_Management.class);
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });

//            ((CustomViewHolder) holder).accept_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MystartActivity(MessageActivity.class);
//                }
//            });
//
//
//            ((CustomViewHolder) holder).reject_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }0
//            });

        }

        @Override
        public int getItemCount() {

            return userModels.size();
        }





    }



    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView Nick_textView;
        public Button accept_btn;
        public Button reject_btn;

        public CustomViewHolder(View view) {

            super(view);
            imageView = (ImageView) view.findViewById(R.id.activity_ncm_image);
            Nick_textView = (TextView) view.findViewById(R.id.activity_ncm_nickname);
            accept_btn = (Button) view.findViewById(R.id.activity_ncm_btn_accept);
            reject_btn = (Button) view.findViewById(R.id.activity_ncm_btn_reject);

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
