package com.example.nt_project02;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Setting_Fragment extends Fragment {

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

        return rootView;

    }

    private void MystartActivity(Class c){
        Intent intent=new Intent(getActivity(),c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
