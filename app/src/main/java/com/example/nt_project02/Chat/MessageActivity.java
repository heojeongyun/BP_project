package com.example.nt_project02.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.NotificationModel;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;
    private Button Image_Button;

    private static final int GALLERY_PICK=1;


    private String username;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String uid;
    private String chatRoomUid;
    private String ImageUrl;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int peopleCount=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent data=getIntent(); //인텐트를 받는다

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();//어플 현재 이용자 아이디
        destinationUid=data.getStringExtra("destination_Uid"); // 상대방 아이디





        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);




       // destinationUid=getIntent().getStringExtra("destinationUid");
        button=(Button)findViewById(R.id.messageActivity_Button); //메시지를 전송하는 버튼
        editText=(EditText)findViewById(R.id.messageActivity_editText); //전송할 메시지를 작성하는 EditText
        Image_Button=(Button)findViewById(R.id.messageActivity_picture); //전송할 사진을 불러오는 버튼

        Image_Button.setOnClickListener(new View.OnClickListener() { //사진 전송 버튼 클릭했을 때
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"), GALLERY_PICK);
                /* Intent 를 통해 앨범화면으로 이동한다. StartActivityForResult 에는 GALLERY_PICK 변수 넣어줌. (이 변수는 밑에서 쓰임)
                인텐트를 처리할 수 있는 컴포넌트가 둘 이상이 되어 여러 컴포넌트 중 하나를 선택할 때 나오는
                다이얼로그의 이름을 'SELECT IMAGE'로 지정함. */
            }
        });


        button.setOnClickListener(new View.OnClickListener() { //메시지 전송 버튼 클릭했을 때!!
            @Override
            public void onClick(View v) {
             ChatModel chatModel=new ChatModel();
             chatModel.users.put(uid,true); //ChatModel users에 자신의 uid를 집어넣음
             chatModel.users.put(destinationUid,true); //상대방의 uid를 집어넣음

             if(chatRoomUid==null){ //채팅방이 없을 때
                 button.setEnabled(false); //메시지 보내는 버튼 못 쓰게 한다
                 FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                     //채팅방을 생성. push로 Chatrooms에 데이터가 쌓이게끔 함.

                     @Override
                     public void onSuccess(Void aVoid) {
                         checkChatRoom(); /* 데이터 Push가 완료되었을 때마다 바로 체크(갱신)해주도록
                         if문 안에 콜백으로 checkChatRoom 넣기 */
                     }
                 });


             }else{ //채팅방이 있을 때

                 ChatModel.Comment comment=new ChatModel.Comment();
                 comment.uid=uid;
                 comment.message=editText.getText().toString();
                 comment.IsImage=false;
                 comment.timestamp= ServerValue.TIMESTAMP; //메시지  전송 때의 시간을 찍어 줌
                 FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                     //uid에 맞는 채팅방을 불러옴
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {//메시지 전송이 Complete되면
                         sendGcm();
                         editText.setText(""); //EditText 칸을 비워두게 하는 역할

                     }
                 });
             }



            }
        });

        checkChatRoom();








    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK){
            final Uri imageUri=data.getData();


            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("MessageImages").child(imageUri.toString());
            //MessageImages에서 imgUri를 스트링으로 가져온다
            UploadTask uploadTask = ref.putFile(imageUri); //이미지 업로드 UploadTask




            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        ImageUrl= task.getResult().toString();


                        ChatModel chatModel=new ChatModel();
                        chatModel.users.put(uid,true);
                        chatModel.users.put(destinationUid,true);

                        if(chatRoomUid==null){
                            button.setEnabled(false);
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });


                        }else{

                            ChatModel.Comment comment=new ChatModel.Comment();
                            comment.uid=uid;
                            comment.message=ImageUrl;
                            comment.IsImage=true;
                            comment.timestamp= ServerValue.TIMESTAMP;
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendGcm();
                                    /*editText.setText("");*/
                                    ImageUrl=null; //초기화

                                }
                            });
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }
    }

    void sendGcm(){
        Gson gson=new Gson();
        //보내는 사람 닉네임 가져오기
        FirebaseFirestore.getInstance().collection("users").document(uid).get() //users에서 uid 가져옴
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { //리스너
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username=document.get("name").toString();
                    } else {

                    }
                } else {

                }
            }
        });

        NotificationModel notificationModel=new NotificationModel();
        notificationModel.to=destinationUserModel.getPushToken();//상대방에게 톡 가면 알림 보내주기 위해 토큰을 받아옴
        notificationModel.notification.title=username;//알림 타이틀: username
        notificationModel.notification.text=editText.getText().toString();//알림 텍스트: 받은 메시지 불러옴
        notificationModel.data.title=username;
        notificationModel.data.text=editText.getText().toString();

        RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));


        Request request=new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyBjcVeOPoTAHTd7wCz7xzadu1ofOTGLAL4")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });

    }



    void checkChatRoom(){ //채팅방의 중복을 피하기 위한 코드

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).
                equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    //users에 같은 값의 uid들이 있는지 확인함으로 중복을 체크할 수 있다.
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){//데이터의 값이 없을 때
                    ChatModel newRoom=new ChatModel();//새로운 ChatModel 생성
                    newRoom.users.put(uid,true);
                    newRoom.users.put(destinationUid,true);
                    //사용자들의 uid 넣기
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        //데이터 업데이트
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    return;
                }

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel =item.getValue(ChatModel.class); //ChatModel에서 users의 uid들을 받아와서 이 uid가 있는지 체크함
                    if(chatModel.users.containsKey(destinationUid)&& chatModel.users.size()==2){ /*유저들이 2명 있고
                    내가 요구한 사람의 uid가 있을 경우*/
                        chatRoomUid=item.getKey(); //채팅방의 id를 받아오고
                        button.setEnabled(true); /*메시지 전송 버튼을 활성화
                        (이게 없으면 checkChatRoom이 실행되기 전에 버튼을 따다닥 눌러버렸을 때
                        그냥 채팅방이 여러 개가 무분별하게 생성되어버림... */
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                        /*recyclerView에 채팅 내용들을 바로 리스트로 넣어주기 위해 LinearLayout으로 선언*/
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();


            FirebaseFirestore.getInstance().collection("users").document(destinationUid)//users에서 상대방의 uid 가져오기
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    destinationUserModel=document.toObject(UserModel.class);
                    getMessageList();
                //UserModel을 불러온 후 Message를 불러오는 게 안정적임 (안 그러면 메시지는 다 떴는데 유저들이 늦게 뜬다던가 불안정할 수 있음)
                }
            });

        }
        void getMessageList(){ //메시지를 읽어들이는 메소드
            databaseReference=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            //chatrooms에서 원하는 id를 가진 방의 comments(메시지)에 접근 후 databaseReference에 담음
            valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                //databaseReference에서 comments(메시지)데이터를 읽은 후 변경 사항에 대한 수신대기를 함(리스너)


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //데이터를 읽을 때마다 데이터를 DataSnapshot으로 받는다
                    comments.clear(); // 데이터가 추가될 때마다 모든 채팅에 대한 내용을 다 보내주기 때문에 그것을 삭제해주는 메소드 작성

                    Map<String,Object> readUsersMap=new HashMap<>();

                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        String key=item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        //Comment를 두개로 나눠 버그 발생을 없앰
                        comment_motify.readUsers.put(uid, true);//읽은 것에 태그를 달아줌으로 읽음을 인지할 수 있게 됨

                        readUsersMap.put(key, comment_motify);//읽은 내용을 가지고 있음
                        comments.add(comment_origin);//기존 comments
                    }

                    if(comments.size()>=1) { //메시지가 있을 경우
                        if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                            //마지막 메시지의 Readuser에 내가 없을 경우

                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                    //방 id에 맞는 comments에 접근
                                    .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        //readUsersMap 업데이트(서버에 보고) 그 후 이벤트가 실행되게끔 밑에 콜백 달아줌
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyDataSetChanged(); //메시지 갱신
                                    recyclerView.scrollToPosition(comments.size() - 1); //recyclerView 내에서 맨 마지막 position으로 이동.
                                }
                            });
                        } else { //Readuser에 내가 있을 경우에는 그냥 메시지를 읽게 됨
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size() - 1);
                        }
                        //메세지가 갱신됨!
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public int getItemViewType(int position) {
            if(!comments.get(position).IsImage){ //comments가 사진이 아닐 때 0, 사진일 때 1 반환
                return 0;
            }else{
                return 1;
            }


        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //사진을 보낼때와 그냥 메시지를 보낼 때 각자 다른 Layout을 사용하게 한다.

            if(viewType==0){//사진을 전송하지 않았을 때
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat,parent,false);

                return new MessageViewHolder(view); //뷰를 재사용할 때 쓰는 클래스
            }else{//사진을 전송했을 때
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat2,parent,false);

                return new ImageViewHolder(view);
            }



        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            long unixTime=(long) comments.get(position).timestamp;//타임스탬프를받아옴
            Date date=new Date(unixTime);//받아온 값을 Date에 담아줌
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));//지역을 설정해준다.
            String time=simpleDateFormat.format(date); //값을 알아볼 수 있게 date 형태로 바꿔줌

            if (!comments.get(position).IsImage){ //이 메시지가 사진이 아니라면
                MessageViewHolder messageViewHolder=((MessageViewHolder)holder);

                messageViewHolder.textView_timestamp.setText(time); //현재 날짜가 찍혀 나오게 해준다
                //내가 보낸 메세지
                if(comments.get(position).uid.equals(uid)){ //이 메시지의 uid가 내 사용자uid와 같다면

                    messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE); //상대방에 대한 정보 구성들을 감춰줌
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastrightbubble);//말풍선
                    messageViewHolder.textView_message.setText(comments.get(position).message);//내가 쓴 메시지 넣어주기
                    messageViewHolder.textView_message.setTextSize(25);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);// 내 말풍선을 오른쪽으로 옮겨줌
                    //messageViewHolder.textView_message.setGravity(Gravity.RIGHT);
                    //messageViewHolder.message_image.setVisibility(View.INVISIBLE);
                    setReadCounter(position,messageViewHolder.textView_readCounter_left);//읽은사람을 왼쪽으로 옮겨줌



                    //상대방이 보낸 메세지
                }else
                {

                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.imageurl)
                        .apply(new RequestOptions().circleCrop())//원형 이미지
                        .into(messageViewHolder.imageView_profile); //상대방의 프사를 넣어준다
                    messageViewHolder.textView_name.setText(destinationUserModel.getNick());//상대방의 닉네임을 넣어줌
                    messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);//상대방의 구성요소가 보이게 함
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastleftbubble);//말풍선
                    messageViewHolder.textView_message.setText(comments.get(position).message);//메시지 넣어주기
                    messageViewHolder.textView_message.setTextSize(25);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);//상대의 말풍선을 왼쪽으로 옮겨줌
                    //setReadCounter(position,messageViewHolder.textView_readCounter_right);


                }

            }else if(comments.get(position).IsImage){
                //이 메시지가 사진이라면
                ImageViewHolder imageViewHolder=((ImageViewHolder)holder);
                imageViewHolder.textView_timestamp.setText(time);//현재 날짜가 찍혀 나오게 해준다

                if(comments.get(position).uid.equals(uid)){ //내가 보낼 때

                    //imageViewHolder.textView_message.setText(comments.get(position).message);
                    imageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                    imageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);// 내 사진을 오른쪽으로 옮겨줌
                    //imageViewHolder.textView_message.setVisibility(View.INVISIBLE);
                    Glide.with(holder.itemView.getContext()).load(comments.get(position).message)
                            .into(imageViewHolder.message_image);
                    setReadCounter(position,imageViewHolder.textView_readCounter_left);//읽은사람을 왼쪽으로 옮겨줌



                    //상대방이 보낸 사진
                }else
                {

                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.imageurl)
                        .apply(new RequestOptions().circleCrop())//원형 이미지
                        .into(imageViewHolder.imageView_profile);//상대방의 프사를 넣어준다
                    imageViewHolder.textView_name.setText(destinationUserModel.getNick());//상대방의 닉네임을 넣어줌
                    imageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);//상대방의 구성요소가 보이게 함
                    imageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);//상대의 사진을 왼쪽으로 옮겨줌
                    //imageViewHolder.textView_message.setVisibility(View.INVISIBLE);
                    Glide.with(holder.itemView.getContext()).load(comments.get(position).message)
                            .into(imageViewHolder.message_image);
                    setReadCounter(position,imageViewHolder.textView_readCounter_right);//읽은사람을 오른쪽으로 옮겨줌




                }
            }







        }

        void setReadCounter(final int position, final TextView textView){ //몇명이 읽었는지 확인하는 메소드
            if (peopleCount == 0) {//서버에 무리가 가지 않게 하기 위해 처음에만 확인하게 하는 if문


                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
                        .child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            //chatrooms에서 chatroomid 접근하고 users에서 이벤트리스너 추가
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        //인원수에 각각의 메시지에 담겨있는 읽은 사람의 count를 가져옴
                        peopleCount = users.size();//전체 인원 수를 가져옴
                        int count = peopleCount - comments.get(position).readUsers.size(); //전체인원수에서 읽은인원만큼을 뺌
                        //comment를 읽은 사람에 대한 count를 가져올 수 있음
                        if (count > 0) {//안 읽은 사람이 있을 때
                            textView.setVisibility(View.VISIBLE);//안읽은만큼의 숫자가 보이게 함
                            textView.setText(String.valueOf(count));//몇명이 안읽었는지 보여줌
                        } else {//모두 읽었을 때
                            textView.setVisibility(View.INVISIBLE);//모두 읽었으니 안보이게 함
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{//처음 확인하는 것이 아닐 때
                int count = peopleCount - comments.get(position).readUsers.size();//전체인원수에서 읽은인원만큼을 뺌
                //comment를 읽은 사람에 대한 count를 가져올 수 있음
                if (count > 0) {//안 읽은 사람이 있을 때
                    textView.setVisibility(View.VISIBLE);//안읽은만큼의 숫자가 보이게 함
                    textView.setText(String.valueOf(count));//몇명이 안읽었는지 보여줌
                } else {//모두 읽었을 때
                    textView.setVisibility(View.INVISIBLE);//모두 읽었으니 안보이게 함
                }
            }

        }

        @Override
        public int getItemCount() {
            return comments.size(); //몇 번 돌아가는지 알 수 있다
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder{
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            //public ImageView message_image;

            public MessageViewHolder(View view){

                super(view);
                textView_message=view.findViewById(R.id.TextView_msg);
                textView_name=(TextView) view.findViewById(R.id.TextView_nickname);
                imageView_profile=(ImageView) view.findViewById(R.id.profile_Image);
                linearLayout_destination=(LinearLayout) view.findViewById(R.id.linearlayout_destination);
                linearLayout_main=(LinearLayout)view.findViewById(R.id.linearlayout_main);
                textView_timestamp=(TextView) view.findViewById(R.id.textview_timestamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right=(TextView)view.findViewById(R.id.messageItem_textview_readCounter_right);
                //message_image=(ImageView)view.findViewById(R.id.message_image);
            }

        }
        private class ImageViewHolder extends RecyclerView.ViewHolder{
            //public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public ImageView message_image;

            public ImageViewHolder(View view){

                super(view);
                //textView_message=view.findViewById(R.id.TextView_msg);
                textView_name=(TextView) view.findViewById(R.id.TextView_nickname);
                imageView_profile=(ImageView) view.findViewById(R.id.profile_Image);
                linearLayout_destination=(LinearLayout) view.findViewById(R.id.linearlayout_destination);
                linearLayout_main=(LinearLayout)view.findViewById(R.id.linearlayout_main);
                textView_timestamp=(TextView) view.findViewById(R.id.textview_timestamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right=(TextView)view.findViewById(R.id.messageItem_textview_readCounter_right);
                message_image=(ImageView)view.findViewById(R.id.message_image);
            }

        }
    }

    @Override
    public void onBackPressed(){ //채팅방에서 뒤로가기 눌렀을 때

        if(valueEventListener!=null) {//메시지가 없을 때 뒤로가기하면 꺼지지 않게 해 주는 if문
            databaseReference.removeEventListener(valueEventListener);//채팅방에서 나오면 채팅방을 보고있는 상태가 꺼짐

        }
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);//액티비티 전환 애니메이션

    }
}
