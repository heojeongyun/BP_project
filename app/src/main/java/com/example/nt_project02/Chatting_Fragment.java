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

public class Chatting_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.chatting, container, false);

        Button chat = (Button) rootView.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MystartActivity(MessageActivity.class);
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
