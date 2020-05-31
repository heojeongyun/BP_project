package com.native_code.bp_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.native_code.bp_project02.CustomData.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Traveler_Chat_Management extends AppCompatActivity {
    private TravelerChatManagementRecyclerViewAdapter adapter;
    private String uid;
    private UserModel userModel;
    private List<String> requests_array;
    private FirebaseFirestore db;
    private DocumentReference destination_Ref;
    private DocumentReference Ref;
    private String TAG="Travele_Chat_Management";
    private String destinationUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler__chat__management);

        adapter=new TravelerChatManagementRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_tcm);
        recyclerView.setLayoutManager(new LinearLayoutManager(Traveler_Chat_Management.this));


        recyclerView.setAdapter(adapter);


    }




    class TravelerChatManagementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;


        public TravelerChatManagementRecyclerViewAdapter() {


            // 파이어베이스에서 데이터를 받아오는 작업
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
                                    //현지인에게 요청목록 적재
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_traveler_chat_management, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


            Log.d(TAG,"usermodel:"+userModels.get(position));


            //현재 여행자 정보 파이어스토어 경로

            Ref=db.collection("users").document(FirebaseAuth.getInstance().getUid());

            Log.d(TAG,"def:"+userModels.get(position).getUid());





            if(userModels.get(position).imageurl != null) {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
            }
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);
            ((CustomViewHolder)holder).request_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(Traveler_Chat_Management.this);
                    builder.setTitle("요청취소");
                    builder.setMessage("해당 현지인 매칭요청을 취소 하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //현재 현지인 정보 파이어스토어경로
                                    destination_Ref=db.collection("users").document(userModels.get(position).getUid());
                                    Ref.update("requests", FieldValue.arrayRemove(userModels.get(position).getUid())); // 현지인 관리목록에서 삭제
                                    destination_Ref.update("requests",FieldValue.arrayRemove(uid));// 여행자 관리목록에서 삭제
                                    userModels.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();


                }
            });



            /*holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        *//*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*//*
                        Intent intent = new Intent(getApplicationContext(), Traveler_Chat_Management.class);
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });*/

        }

        @Override
        public int getItemCount() {

            return userModels.size();
        }





    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView Nick_textView;
        public Button request_cancel_btn;

        public CustomViewHolder(View view) {

            super(view);
            imageView = (ImageView) view.findViewById(R.id.activity_tcm_image);
            Nick_textView = (TextView) view.findViewById(R.id.activity_tcm_nickname);
            request_cancel_btn = (Button) view.findViewById(R.id.activity_tcm_btn);

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
