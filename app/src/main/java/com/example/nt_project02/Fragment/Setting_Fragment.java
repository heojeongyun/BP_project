package com.example.nt_project02.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.nt_project02.Native_Register;
import com.example.nt_project02.R;
import com.example.nt_project02.Sign_UpActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Setting_Fragment extends Fragment {

    ImageView ivUser;
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.setting, container, false);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
        }

        Button native_register = (Button) rootView.findViewById(R.id.native_register);
        native_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MystartActivity(Native_Register.class);

            }
        });

        ivUser = (ImageView) rootView.findViewById(R.id.ivUser);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {

                /*Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,PICK_FROM_ALBUM);*/

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        Button LogoutButton=(Button) rootView.findViewById(R.id.LogoutButton);

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MystartActivity(Sign_UpActivity.class);
            }
        });

        return rootView;

    }


    private void MystartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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


            ivUser.setImageURI(data.getData());
            imageUri = data.getData(); //이미지 경로 원본


            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("UserImages").child(user.getUid());
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


                        Map<String, Object> imageurl = new HashMap<>();
                        imageurl.put("imageurl", ImageUrl);

                        if (user != null) {
                            db.collection("users").document(user.getUid()).set(imageurl, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startToast("사진 등록 성공.");


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









    private void startToast(String msg){

        Toast.makeText(getContext(), msg,
                Toast.LENGTH_SHORT).show();
    }


}

