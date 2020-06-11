package com.native_code.bp_project02;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.native_code.bp_project02.Chat.MessageActivity;
import com.native_code.bp_project02.CustomData.MarkerModel;
import com.native_code.bp_project02.CustomData.UserModel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerList extends AppCompatActivity {

    private static final String TAG = "actvity_MarkeList";
    private List<MarkerModel.MarkerData> markerDataList;
    private MarkerListRecyclerViewAdapter adapter;
    private ItemTouchHelper helper;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String user_uid;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        Intent data = getIntent();

        markerDataList = new ArrayList<>();

        markerDataList = (ArrayList) data.getSerializableExtra("Markermodels");

        user_uid=FirebaseAuth.getInstance().getUid();

        adapter = new MarkerListRecyclerViewAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.markerlist_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(adapter);


        //현지인일 경우만 마커 리스트 순서 변경 가능
        db.collection("users")
                .whereEqualTo("uid", user_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userModel = document.toObject(UserModel.class);
                                if(userModel.user_kind.equals("현지인")){

                                    //ItemTouchHelper 생성
                                    helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));

                                    //RecyclerView에 ItemTouchHelper 붙이기

                                    helper.attachToRecyclerView(recyclerView);

                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    class MarkerListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperListener {



        public MarkerListRecyclerViewAdapter() {

            //Log.e(TAG, "Mar:" + markerDataList.get(0).key);

        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_markerlist, parent, false);

            return new CustomViewHolder(view);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ((CustomViewHolder) holder).textview_title.setText(markerDataList.get(position).markerTitle);
            ((CustomViewHolder) holder).textview_content.setText(markerDataList.get(position).Content);


            if (markerDataList.get(position).ImageUrl != null) {
                Glide.with
                        (getApplicationContext())
                        .load(markerDataList.get(position).ImageUrl)
                        .into(((CustomViewHolder) holder).imageview);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),InfoWindow_Edit.class);
                    intent.putExtra("MarkerAdress",markerDataList.get(position).markerSnippet);
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return markerDataList.size();
        }


        @Override
        public boolean onItemMove(int from_position, int to_position) {

            Log.e(TAG,"markerDataList:"+markerDataList);

            //이동할 객체 저장
            MarkerModel.MarkerData markerData = markerDataList.get(from_position);
            MarkerModel.MarkerData markerData2 = markerDataList.get(to_position);


            MarkerModel.MarkerData NewMarKerData=new MarkerModel.MarkerData();
            MarkerModel.MarkerData NewMarKerData2=new MarkerModel.MarkerData();

            //옮겨지는 마커 정보 설정
            NewMarKerData.Content = markerData.Content;
            NewMarKerData.Latitude = markerData.Latitude;
            NewMarKerData.Longitude = markerData.Longitude;
            NewMarKerData.markerTitle = markerData.markerTitle;
            NewMarKerData.markerSnippet = markerData.markerSnippet;
            NewMarKerData.uid = FirebaseAuth.getInstance().getUid();  //어플 현재 이용자 아이디
            NewMarKerData.timestamp = ServerValue.TIMESTAMP; //시간정보 설정;
            NewMarKerData.ImageUrl=markerData.ImageUrl;
            NewMarKerData.sequence=to_position;

            Map<String,Object > postValues=NewMarKerData.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(markerDataList.get(from_position).key,postValues);

            //옮겨지는 마커 정보 설정
            NewMarKerData2.Content = markerData2.Content;
            NewMarKerData2.Latitude = markerData2.Latitude;
            NewMarKerData2.Longitude = markerData2.Longitude;
            NewMarKerData2.markerTitle = markerData2.markerTitle;
            NewMarKerData2.markerSnippet = markerData2.markerSnippet;
            NewMarKerData2.uid = FirebaseAuth.getInstance().getUid();  //어플 현재 이용자 아이디
            NewMarKerData2.timestamp = ServerValue.TIMESTAMP; //시간정보 설정;
            NewMarKerData2.ImageUrl=markerData2.ImageUrl;
            NewMarKerData2.sequence=from_position;

            Map<String,Object > postValues2=NewMarKerData2.toMap();

            childUpdates.put(markerDataList.get(to_position).key,postValues2);


            mDatabase.child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker").updateChildren(childUpdates);

            //이동할 객체 삭제
            markerDataList.remove(from_position);
            // 이동하고 싶은 position에 추가
            markerDataList.add(to_position,markerData);

            //Adapter에 데이터 이동알림
            notifyItemMoved(from_position,to_position);




            return true;


        }


        @Override
        public void onItemSwipe(int position) {

            //마커 정보 서버에서 삭제
            mDatabase.child("chatrooms").child(MessageActivity.chatRoomUid).child("CustomMarker").child(markerDataList.get(position).key).removeValue();
            markerDataList.remove(position);

            notifyItemRemoved(position);


        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView textview_title;
        public TextView textview_content;
        public ImageView imageview;
        //public TextView sequence;

        public CustomViewHolder(View view) {

            super(view);
            textview_title = (TextView) view.findViewById(R.id.title);
            textview_content = (TextView) view.findViewById(R.id.markeritem_content);
            imageview = (ImageView) view.findViewById(R.id.markeritem_imageview);
            //sequence=(TextView) view.findViewById(R.id.sequence);

        }
    }

    private void startToast(String msg) {

        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }


}


