package com.example.nt_project02;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Setting_Fragment extends Fragment {

    ImageView ivUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.setting,container,false);



        Button native_register=(Button) rootView.findViewById(R.id.native_register);
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
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

        return rootView;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri image = data.getData();
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
            ivUser.setImageBitmap (bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void MystartActivity(Class c){
        Intent intent=new Intent(getActivity(),c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);



        }

    }

