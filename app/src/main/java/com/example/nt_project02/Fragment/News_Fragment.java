package com.example.nt_project02.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nt_project02.R;

// 현재 지정해둔 사진을 띄워준다..앞으로 대대적인 보강이 필요한부분
public class News_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView=(ViewGroup) inflater.inflate(R.layout.news,container,false);

        return rootView;
    }
}
