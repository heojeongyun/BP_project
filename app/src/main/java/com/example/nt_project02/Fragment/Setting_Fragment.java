package com.example.nt_project02.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.BookmarkActivity;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.LoginActivity;
import com.example.nt_project02.Native_Profile.Profile;
import com.example.nt_project02.Native_Register;
import com.example.nt_project02.R;
import com.example.nt_project02.Sign_UpActivity;
import com.facebook.AccessToken;
import com.facebook.login.DeviceLoginManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Setting_Fragment extends Fragment {

    private ImageView ivUser;
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageUri;
    private String register_ImageURL;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private UserModel userModel;
    private TextView nick_textview;
    private String TAG = "Setting_Fragment";
    private String user_kind;
    private Button NativeRegisterButton;
    private TextView profile_textview;
    private ImageView bluepeopleImageView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.setting, container, false);

        // setting.xml의 nick_textview 객체 생성
        nick_textview = (TextView) rootView.findViewById(R.id.nick_TextView);
        profile_textview=(TextView) rootView.findViewById(R.id.fragment_setting_Profile);
        // bluepeopleImageView=(ImageView) rootView.findViewById(R.id.fragment_setting_BluePeopleImageView);


        // Firebase db로 부터 사용자 정보 불러오기
        /*db.collection("users")
                .whereEqualTo("uid", user_uid)
                .get()// 사용자 정보 확인하기
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // 사용자 정보가 일치할 경우에 Firebase db로부터 사용자 사진과 별명을 가져온다
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                userModel=document.toObject(UserModel.class);
                                if(userModel.getImageurl()!=null) { // 이미지의 URL값이 존재할 경우에만 사진을 가져온다
                                    register_ImageURL = userModel.getImageurl();
                                    Glide.with(getContext())
                                            .load(register_ImageURL)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(ivUser);
                                    nick_textview.setText(userModel.getName());

                                }


                            }

                        } else {

                        }
                    }
                });*/

        NativeRegisterButton = (Button) rootView.findViewById(R.id.fragment_setting_native_register_Button);

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


                                
                                userModel = doc.toObject(UserModel.class);
                                user_kind = userModel.getUser_kind();
                                if (user_kind != null) {
                                    if (user_kind.equals("현지인")) {
                                        //현지인이면 등록 버튼 안 보이게
                                        NativeRegisterButton.setVisibility(View.GONE);
                                        //프로필 보기 클릭 시 본인 프로필창으로 이
                                        profile_textview.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Profile.class);
                                                intent.putExtra("destination_UserModels", userModel);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        });

                                    }  else {
                                        profile_textview.setText("여행자");

//                                        //로고 안보이게
//                                        bluepeopleImageView.setVisibility(View.GONE);

                                       NativeRegisterButton.setOnClickListener(new View.OnClickListener() { //현지인 등록 버튼
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Native_Register.class);
                                                //intent.putExtra("user_kind", "여행자");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);


                                            }
                                        });
                                    }
                                }
                                if (userModel.getImageurl() != null) { // 이미지의 URL값이 존재할 경우에만 사진을 가져온다
                                    register_ImageURL = userModel.getImageurl();
                                    Glide.with(getContext())
                                            .load(register_ImageURL)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(ivUser);
                                }
                                    nick_textview.setText(userModel.getName());


                            }
                        }

                        Log.d(TAG, "Current data: " + userModel);
                    }
                });







    /*    Button native_register = (Button) rootView.findViewById(R.id.native_register);
        native_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User_Kind=userModel.getUser_kind();
                if(User_Kind.equals("현지인")) { // 사용자가 현지인인 경우에만 정보를 등록/수정 가능
                    MystartActivity(Native_Register.class);
                }else{
                    startToast("접근권한이 없습니다");
                }

            }
        });*/
        // 등록버튼의 객체를 선언
        ivUser = (ImageView) rootView.findViewById(R.id.ivUser);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM); //갤러리로부터 사진을 가져와 등록하는 부분


            }
        });
        /*Button upload=(Button)rootView.findViewById(R.id.Upload);
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View rootView) {

                *//*Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,PICK_FROM_ALBUM);*//*

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM); //갤러리로부터 사진을 가져와 등록하는 부분

                nick_textview.setText(userModel.getName()); // userModel의 이름 가져오기


            }
        });*/

        Button LogoutButton = (Button) rootView.findViewById(R.id.fragment_setting_LogoutButton);
        //LogoutButton 클릭시 LoginActivity 화면으로 넘어가게 하는 부분
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();

                    MystartActivity(LoginActivity.class);

            }
        });

        Button fragment_setting_BookmarkButton = (Button) rootView.findViewById(R.id.fragment_setting_BookmarkButton);
        fragment_setting_BookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MystartActivity(BookmarkActivity.class);
            }
        });


        return rootView;
        }





    private void MystartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그아웃시 초기화하는 부분
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     /*   super.onActivityResult(requestCode, resultCode, data);

        Uri image = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
            ivUser.setImageBitmap(bitmap);



        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (requestCode == PICK_FROM_ALBUM) {


            if (data != null) {

                //ivUser.setImageURI(data.getData());
                imageUri = data.getData();

                // Firebase storage로부터 이미지 URL을 통해 이미지 파일을 가져온다
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("UserImages").child(user.getUid());
                // 등록한 사진의 URL을 Firebase storage에 업로드 하는 부분
                UploadTask uploadTask = ref.putFile(imageUri);


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
                            String ImageUrl = task.getResult().toString();

                            // 이미지 URL과 실제 이미지를 매칭시켜 객체에 저장하는 곳
                            Map<String, Object> imageurl = new HashMap<>();
                            imageurl.put("imageurl", ImageUrl);

                            if (user != null) {
                                db.collection("users").document(user.getUid()).set(imageurl, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startToast("사진 등록 성공.");
                                                //MystartActivity(LoginActivity.class);


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        startToast("사진 등록 실패.");

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
    }








    // 알림 메세지를 출력하는 함수이다
    private void startToast(String msg){

        Toast.makeText(getContext(), msg,
                Toast.LENGTH_SHORT).show();
    }


}

