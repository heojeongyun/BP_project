package com.example.nt_project02.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.ChatModel;
import com.example.nt_project02.Chat.MessageActivity;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class Chatting_Fragment extends Fragment {



    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chatting, container, false);


        RecyclerView recyclerView=rootView.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));


        return rootView;

    }


    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private String uid;
        private List<ChatModel> chatModels=new ArrayList<>();
        private List<UserModel> userModels;
        private DatabaseReference databaseReference;
        private String lastMessageKey;
        private ArrayList<String> destinationUsers=new ArrayList<>();
        private String destinationUid=null;


        public ChatRecyclerViewAdapter() {
            //databaseReference=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            userModels=new ArrayList<>();
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    chatModels.clear();
                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));



                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final CustomViewHolder customViewHolder=(CustomViewHolder)holder;



            // 일일이 챗방에 있는 유저를 체크



            FirebaseDatabase.getInstance().getReference().child("chatrooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(String user:chatModels.get(position).users.keySet()){
                        if(!user.equals(uid)) {
                            destinationUid = user;
                            destinationUsers.add(destinationUid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });








            if(destinationUid!=null) {
                FirebaseFirestore.getInstance().collection("users").document(destinationUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            UserModel userModel = document.toObject(UserModel.class);
                            Glide.with(customViewHolder.itemView.getContext())
                                    .load(userModel.getImageurl())
                                    .apply(new RequestOptions().circleCrop())
                                    .into(customViewHolder.imageView);
                            customViewHolder.textView_title.setText(userModel.getName());


                        } else {


                        }
                    }
                });
            }

            //메세지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴

            Map<String,ChatModel.Comment> commentMap=new TreeMap<>(Collections.<String>reverseOrder());
            if(chatModels.get(position).comments.size()>=1) {
                commentMap.putAll(chatModels.get(position).comments);
                lastMessageKey = (String) commentMap.keySet().toArray()[0];

                if (!chatModels.get(position).comments.get(lastMessageKey).IsImage) {
                    customViewHolder.textView_lastMessage.setText(chatModels.get(position).comments.get(lastMessageKey).message);
                } else {
                    customViewHolder.textView_lastMessage.setText("사진");
                }
            }



            customViewHolder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){
                    Intent intent=new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destination_Uid", destinationUsers.get(position));

                    ActivityOptions activityOptions=ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());

                }
            });

            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            if(chatModels.get(position).comments.size()>=1) {
                long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;

                Date date = new Date(unixTime);
                customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
            }
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_lastMessage;
            public TextView textView_timestamp;

            public CustomViewHolder(View view) {
                super(view);



                imageView=(ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title=(TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_lastMessage=(TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp=(TextView)view.findViewById(R.id.chatitem_textview_tiemstamp);
            }
        }
    }



}
