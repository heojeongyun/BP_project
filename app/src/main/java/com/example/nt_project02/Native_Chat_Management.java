package com.example.nt_project02;

import androidx.annotation.Nullable;
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
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.Fragment.Chatting_Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class  Native_Chat_Management extends AppCompatActivity {

    private String destinationUid;
    private UserModel destination_userModel;
    private NativeChatManagementRecyclerViewAdapter adapter;
    private String uid;
    private UserModel userModel;
    private List<String> requests_array;
    private FirebaseFirestore db;
    private DocumentReference destination_Ref;
    private DocumentReference Ref;
    private String TAG="Native_Chat_Management";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native__chat__management);


        adapter=new NativeChatManagementRecyclerViewAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_ncm);
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
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
      //                   Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                                for (QueryDocumentSnapshot document : value) {
                                    userModel = document.toObject(UserModel.class);
                                    requests_array = userModel.getRequests();
                                    //여행자가 요청한 요청 목록 적재
                                    Log.d("Requests", document.getId() + " => " + document.getData());
                                    if (requests_array != null) {
                                        for (int i = 0; i < requests_array.size(); i++) {
                                            db.collection("users")
                                                    .whereEqualTo("uid", requests_array.get(i))
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot value,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
//                                                              Log.w(TAG, "Listen failed.", e);
                                                                return;
                                                            }
                                                            userModels.clear();
                                                            for (QueryDocumentSnapshot document : value) {
                                                                userModels.add(document.toObject(UserModel.class));
                                                            }
                                                            notifyDataSetChanged();

                                                        }
                                                    });
                                        }
                                    }
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

            Intent data=getIntent();

            // Firebase db로 부터 저장된 현지인의 정보들을 불러오는 작업

            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

            //문서 위치 선언
            db=FirebaseFirestore.getInstance();

            //현재 여행자 정보 파이어스토어 경로
            Ref=db.collection("users").document(FirebaseAuth.getInstance().getUid());
            //현재 현지인 정보 파이어스토어경로
            destination_Ref=db.collection("users").document(userModels.get(position).getUid());



            if(userModels.get(position).imageurl != null) {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
            }
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);

            

            ((CustomViewHolder) holder).accept_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 수락 버튼 클릭시 1:1 대화창으로 이동
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    intent.putExtra("destination_Uid", requests_array.get(position));//누구랑 대화할지
                    startActivity(intent);
                    Ref.update("requests",FieldValue.arrayRemove(userModels.get(position).getUid())); // 현지인 관리목록에서 삭제
                    destination_Ref.update("requests",FieldValue.arrayRemove(uid)); // 여행자 관리목록에서 삭제

                    Ref.update("matching",FieldValue.arrayUnion(userModels.get(position).getUid())); //현지인 매칭 목록 데이터 추가
                    destination_Ref.update("matching",FieldValue.arrayUnion(uid)); //여행자 매칭 목록 데이터 추가


                }
            });


            ((CustomViewHolder) holder).reject_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(Native_Chat_Management.this);
                    builder.setTitle("거절");
                    builder.setMessage("해당 여행자 매칭요청을 취소 하시겠습니까?");
                    builder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Ref.update("requests",FieldValue.arrayRemove(userModels.get(position).getUid()));
                                    destination_Ref.update("requests",FieldValue.arrayRemove(uid));
                                    adapter=new NativeChatManagementRecyclerViewAdapter();
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Native_Chat_Management.this));
                                    recyclerView.setAdapter(adapter);
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

    public void onResume() {

        //리사이클러뷰 초기화
        adapter=new NativeChatManagementRecyclerViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(Native_Chat_Management.this));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
