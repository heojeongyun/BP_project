package com.example.nt_project02.Native_Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.CustomData.ReviewData;
import com.example.nt_project02.R;
import com.example.nt_project02.ReviewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Profile extends AppCompatActivity {

    private ViewPager mViewPager;
    private String destinationUid;
    private MyPagerAdapter mPagerAdapter;
    private UserModel current_userModel;
    private UserModel destination_userModel;
    private TextView nick_text;
    private TextView introduction_Text;
    private ImageView profile_image;
    private Button review_register_button;
    private Button detailbutton;

    private String user_kind;
    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String uid;
    private CheckBox activity_profile_BookMark;
    private FirebaseFirestore db;
    private DocumentReference destination_Ref;
    private DocumentReference Ref;
    private List<String> bookmarks_array;
    private List<String> requests;
    private List<String> matching;
    private String TAG="Profile";

    private ReviewRecyclerViewAdapter adapter;
    private List<ReviewData> reviewDataList;

    private int Show_Size=3;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);







        Intent data=getIntent();

        // Firebase db로 부터 저장된 현지인의 정보들을 불러오는 작업
        destination_userModel = data.getParcelableExtra("destination_UserModels");
        destinationUid=destination_userModel.getUid();
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        adapter = new ReviewRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profile_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        recyclerView.setAdapter(adapter);






        nick_text=(TextView) findViewById(R.id.nickAgeTV);
        introduction_Text=(TextView) findViewById(R.id.introduction_Text);
        profile_image=(ImageView) findViewById(R.id.profile_Image);
        review_register_button=(Button) findViewById(R.id.actvity_profile_review_button);
        detailbutton=(Button) findViewById(R.id.profile_detailbutton);

        nick_text.setText(destination_userModel.getName());
        introduction_Text.setText(destination_userModel.getIntroduction());


        //현재 현지인 정보 출력

        Log.d(TAG,"Current data:"+destination_userModel.toString());

        // 이미지 URL을 통해 이미지를 불러오는 작업

        if(destination_userModel.getImageurl()!=null) {
            Glide.with
                    (getApplicationContext())
                    .load(destination_userModel.getImageurl())
                    .apply(new RequestOptions().circleCrop())
                    .into(profile_image);
        }


        detailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Show_Size==3){
                    Show_Size=10;
                    detailbutton.setText("접기");
                }else if(Show_Size==10){
                    Show_Size=3;
                    detailbutton.setText("더보기");
                }

                adapter.notifyDataSetChanged();
            }
        });




   
            
               
            

        // 현지인과 채팅을 하기 위해 매칭요청 하는 버튼
        Button chat_button=(Button) findViewById(R.id.profile_chat_button);

        if(uid.equals(destinationUid)){
            chat_button.setVisibility(View.GONE);
            chat_button.setClickable(false);
        } 

        //현지인에게 매칭 요청 리스너
        View.OnClickListener request =new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 파이어베이스 requests 필드에 아이디 등록
                Ref.update("requests",FieldValue.arrayUnion(destinationUid));
                destination_Ref.update("requests",FieldValue.arrayUnion(uid));
                startToast("매칭을 성공적으로 요청했습니다");
            }

        };

        //요청 거절 리스너
        View.OnClickListener Reject_Request =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToast("해당 현지인에게 이미 매칭 요청 하였습니다.");
            }
        };



        activity_profile_BookMark=(CheckBox) findViewById(R.id.activity_profile_BookMark);

        //문서 위치 선언
        db=FirebaseFirestore.getInstance();

        //현재 여행자 정보 파이어스토어 경로
        Ref=db.collection("users").document(FirebaseAuth.getInstance().getUid());

        //현재 현지인 정보 파이어스토어경로
        destination_Ref=db.collection("users").document(destinationUid);

        //현재 여행자 정보 가져오기
        db.collection("users")
                .whereEqualTo("uid",uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                current_userModel=document.toObject(UserModel.class);
                                bookmarks_array=current_userModel.getBookmarks();
                                requests=current_userModel.getRequests();
                                matching=current_userModel.getMatching();
                                //즐켜찾기 목록이 있을 시
                                if(bookmarks_array != null) {
                                    //현재 프로필 현지인이 포함되어 있으면
                                    if(bookmarks_array.contains(destinationUid)){
                                        activity_profile_BookMark.setChecked(true);
                                    }else{
                                        activity_profile_BookMark.setChecked(false);
                                    }
                                    //startToast(bookmarks_array.get(0));
                                }
                                Log.d("Profile_Traver_data", document.getId() + " => " + document.getData());


                                if(requests !=null){

                                    if(!requests.contains(destinationUid)){
                                        if(matching!=null){
                                            if(!matching.contains(destinationUid)){
                                                chat_button.setOnClickListener(request);
                                            }else{
                                                chat_button.setOnClickListener(Reject_Request);
                                            }
                                        }else{
                                            chat_button.setOnClickListener(request);
                                        }
                                    }
                                    else{
                                        chat_button.setOnClickListener(Reject_Request);

                                    }
                                }else{
                                    if(matching !=null){
                                        if(!matching.contains(destinationUid)){
                                            chat_button.setOnClickListener(request);
                                        }else{
                                            chat_button.setOnClickListener(Reject_Request);
                                        }
                                    }else {
                                        chat_button.setOnClickListener(request);
                                    }
                                }
                            }

                        } else {
                            Log.d("Profile_Traver_data", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //리뷰 쓰기 버튼 클릭 시
        if(uid.equals(destinationUid)){
            review_register_button.setVisibility(View.GONE);
            review_register_button.setClickable(false);

        } else {
            review_register_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("destination_UserModel", destination_userModel);
                    startActivity(intent);
                }
            });
        }








            //즐겨찾기 체크박스 클릭 시
        activity_profile_BookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 등록 되어 있을 때
                if(activity_profile_BookMark.isChecked()){
                    //즐겨찾기 리스트 추가 (중복불가)
                    Ref.update("bookmarks",FieldValue.arrayUnion(destinationUid));
                    destination_Ref.update("bookmarks_number", FieldValue.increment(1));

                //즐겨찾기 되어 있지 않을 때
                }else{
                    //즐겨찾기 리스트 항목 제거
                    Ref.update("bookmarks",FieldValue.arrayRemove(destinationUid));
                    destination_Ref.update("bookmarks_number", FieldValue.increment(-1));//현지인 즐겨찾기 수 추가
                }
            }
        });



    }

   class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public ReviewRecyclerViewAdapter() {

            reviewDataList=new ArrayList<>();

            Log.e(TAG,"start");





            db=FirebaseFirestore.getInstance();
            db.collection("review")
                    .whereEqualTo("destination_Uid",destinationUid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    reviewDataList.add(document.toObject(ReviewData.class));

                                    Log.d("reviewDataList", document.getId() + " => " + document.getData());

                                }
                            } else {
                                Log.d("reviewDataList", "Error getting documents: ", task.getException());
                            }
                        notifyDataSetChanged();
                        }

                    });






        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            //Log.e(TAG,reviewDataList.get(position).mContent);

                ((CustomViewHolder) holder).reviewText.setText(reviewDataList.get(position).getmContent());
                ((CustomViewHolder) holder).name_tv.setText(reviewDataList.get(position).getName());

                if (reviewDataList.get(position).getImageurl() != null) {
                    Glide.with
                            (getApplicationContext())
                            .load(reviewDataList.get(position).getImageurl())
                            .apply(new RequestOptions().circleCrop())
                            .into(((CustomViewHolder) holder).reviewImage);
                }
                ((CustomViewHolder) holder).ratingBar.setRating(reviewDataList.get(position).getRating());


        }

        @Override
        public int getItemCount() {

            if(reviewDataList.size() > Show_Size){
                return Show_Size;
            }
            else
            {
                return reviewDataList.size();
            }
        }


    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView reviewImage;
        public TextView reviewText;
        public TextView name_tv;
        public RatingBar ratingBar;


        public CustomViewHolder(View view) {
            super(view);

            ratingBar=(RatingBar) view.findViewById(R.id.list_ratingBar);
            name_tv=(TextView) view.findViewById(R.id.list_name_tv);
            reviewImage=(ImageView) view.findViewById(R.id.reviewImage);
            reviewText=(TextView) view.findViewById(R.id.reviewText);


        }
    }








    private void startToast(String msg){

        Toast.makeText(Profile.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
