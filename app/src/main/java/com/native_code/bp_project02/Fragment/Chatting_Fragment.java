package com.native_code.bp_project02.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.native_code.bp_project02.CustomData.ChatModel;
import com.native_code.bp_project02.Chat.MessageActivity;
import com.native_code.bp_project02.CustomData.UserModel;
import com.native_code.bp_project02.Native_Chat_Management;
import com.native_code.bp_project02.R;
import com.native_code.bp_project02.Traveler_Chat_Management;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class Chatting_Fragment extends Fragment {


    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM.dd HH:mm"); //사람이 알아볼 수 있게 데이터 포맷을 정해 줌
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private UserModel userModel;
    private String TAG = "Chatting_Fragment";
    private String user_kind;
    public ChatRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<String> requests;
    private TextView request_num;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chatting, container, false);

        recyclerView=rootView.findViewById(R.id.recyclerView_chat_Fragment);
        //리사이클러뷰 붙히기
        mSetAdapter();
        adapter.notifyDataSetChanged();

        final ImageButton traveler_management_btn = (ImageButton) rootView.findViewById(R.id.btn_traveler_chat_management);
        request_num=(TextView) rootView.findViewById(R.id.request_num);

        //파이어베이스에서 사용자를 불러온다음 설정버튼을 변경
        db.collection("users")
                .whereEqualTo("uid", user_uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc != null) {
                                //요청 숫자 뷰 초기화
                                request_num.setVisibility(View.INVISIBLE);

                                userModel = doc.toObject(UserModel.class);
                                user_kind = userModel.getUser_kind();

                                requests=userModel.getRequests();
                                int requests_size=0;
                                if(requests!=null){
                                    requests_size=requests.size();
                                    if(requests_size>0) {

                                        request_num.setVisibility(View.VISIBLE);
                                        request_num.setText(String.valueOf(requests_size));
                                    }
                                }

                                if (user_kind != null) {
                                    if (user_kind.equals("현지인")) { //현지인인 경우 현지인 관리창 연결
                                        traveler_management_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Native_Chat_Management.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else { // 여행자인 경우 여행자 관리창 연결
                                        traveler_management_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Traveler_Chat_Management.class);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                                Log.e(TAG,"respone");



                            }
                        }
                    }
                });

        adapter.notifyDataSetChanged();


        return rootView;

    }


    public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private String uid;
        private List<ChatModel> chatModels=new ArrayList<>();
        private List<UserModel> userModels;
        private DatabaseReference databaseReference;
        private String lastMessageKey;
        private ArrayList<String> destinationUsers=new ArrayList<>();
        private String destinationUid=null;
        private String TAG="Chatting_Fragment";
        private int num=0;

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public ChatRecyclerViewAdapter() { //채팅 목록 가져오기


            userModels=new ArrayList<>();


            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();//uid는 파이어베이스에서 가져옴
            FirebaseDatabase.getInstance().getReference().child("chatrooms")
                    .orderByChild("users/"+uid).equalTo(true).addValueEventListener(new ValueEventListener() {

                        //파이어베이스의 chatrooms에 내가 소속된 방에 대해서 이벤트를 받음
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    chatModels.clear();//밑의 for문에서 데이터를 쌓아둘 거기 때문에 Clear해둠
                    num=0;
                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));
                        //Clear된 것들을 다시 Add해줌


                    }
                    notifyDataSetChanged(); //새로고침


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
            Log.d(TAG,"chatModels:"+chatModels);

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);



            return new CustomViewHolder(view); //뷰 재사용
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            /*holder.setIsRecyclable(false); //다른 해결방법 찾으면 삭제해야 함*/


            final CustomViewHolder customViewHolder=(CustomViewHolder)holder;





            // 일일이 챗방에 있는 유저를 체크
            for(String user:chatModels.get(position).users.keySet()){

                if(!user.equals(uid)) {//내가 아닌 사람을 뽑아옴
                    destinationUid = user;
                    if (!destinationUsers.contains(destinationUid)) {

                        destinationUsers.add(destinationUid);

                    }
                }
            }


            num=0;



            Log.d(TAG,"destinationUsers:"+destinationUsers);




            if(destinationUsers.get(position)!=null) {//상대방이 있을 때
                FirebaseFirestore.getInstance().collection("users").document(destinationUsers.get(position)).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {//users의 Uid를 가져와서 거기에 대해 리스너 대기
                            @Override


                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {


                                    DocumentSnapshot document = task.getResult();
                                    UserModel userModel = document.toObject(UserModel.class);

                                    if(userModel.imageurl!=null) {
                                        Glide.with(customViewHolder.itemView.getContext())
                                                .load(userModel.getImageurl())//userModel에서 이미지를 가져온다
                                                .apply(new RequestOptions().circleCrop())
                                                .into(customViewHolder.imageView);
                                    }

                                    customViewHolder.textView_title.setText(userModel.getName());//채팅방 타이틀을 상대방 이름으로


                                    //메세지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
                                    Map<String,ChatModel.Comment> commentMap=new TreeMap<>(Collections. <String>reverseOrder());
                                    if(chatModels.get(position).comments.size()>=1) {//메시지가 있을 때에만 메시지를 읽어오도록 하는 if문
                                        commentMap.putAll(chatModels.get(position).comments);


                                        num=0;
                                        lastMessageKey = (String) commentMap.keySet().toArray()[0];//마지막 메시지를 가져옴


                                        String lastMessage_uid=chatModels.get(position).comments.get(lastMessageKey).uid;

                                        //마지막 메세지가 내가 보낸게 아니라면
                                        if(!lastMessage_uid.equals(uid)) {
                                            for (int i = 0; i < commentMap.keySet().size(); i++) {
                                                String key = (String) commentMap.keySet().toArray()[i];

                                                int readuser_size = chatModels.get(position).comments.get(key).readUsers.size();
                                                if (readuser_size == 2) {
                                                    break;
                                                }else if(readuser_size==1){
                                                    num+=1;
                                                }

                                                }
                                            }

                                        if(num>0){


                                            Log.e(TAG,"Num:"+num);

                                            customViewHolder.unread_num.setVisibility(View.VISIBLE);
                                            customViewHolder.unread_num.setText(String.valueOf(num));


                                        }
                                        if (!chatModels.get(position).comments.get(lastMessageKey).IsImage) {//마지막 메시지가 사진이 아닐 때
                                            //마지막 메시지의 키를 가져와서 보이게 함
                                            customViewHolder.textView_lastMessage.setText(chatModels.get(position).comments.get(lastMessageKey).message);

                                        } else {
                                            customViewHolder.textView_lastMessage.setText("사진");//"사진"으로 표시
                                        }
                                    }


                                    customViewHolder.itemView.setOnClickListener(new View.OnClickListener(){//클릭 이벤트


                                        @Override
                                        public void onClick(View view){

                                            Intent intent=new Intent(view.getContext(), MessageActivity.class); //채팅방 액티비티
                                            intent.putExtra("destination_Uid", destinationUsers.get(position)); //누구랑 대화할지

                                            ActivityOptions activityOptions=ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                                            //방 들어갈 때 애니메이션


                                            startActivity(intent,activityOptions.toBundle()); //채팅방 액티비티 시작

                                        }

                                    });


                                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));//시간 지역 설정

                                    if(chatModels.get(position).comments.size()>=1) {//메시지가 있다면
                                        long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
                                        //마지막 메시지의 시간을 받아온다.

                                        Date date = new Date(unixTime);
                                        customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));//포맷을 알아볼 수 있게 바꿔준다.
                                    }


                                } else {


                                }

                            }

                        });
            }







        }

        @Override
        public int getItemCount() { //총 아이템 갯수가 몇 개인지 판단
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_lastMessage;
            public TextView textView_timestamp;
            public TextView unread_num;


            public CustomViewHolder(View view) {
                super(view);



                imageView=(ImageView) view.findViewById(R.id.chatitem_imageview);
                textView_title=(TextView)view.findViewById(R.id.chatitem_textview_title);
                textView_lastMessage=(TextView)view.findViewById(R.id.chatitem_textview_lastMessage);
                textView_timestamp=(TextView)view.findViewById(R.id.chatitem_textview_tiemstamp);
                unread_num=(TextView) view.findViewById(R.id.chatitem_unread_num);


            }
        }
    }


    private void mSetAdapter(){

        adapter=new ChatRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onResume() {
        mSetAdapter();
        super.onResume();
    }

}
