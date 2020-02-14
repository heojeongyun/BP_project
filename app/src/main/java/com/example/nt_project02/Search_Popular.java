package com.example.nt_project02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Search_Popular extends Fragment {

    private SearchPopularRecyclerViewAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_people, container,
        adapter=new SearchPopularRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.search_popular_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        recyclerView.setAdapter(adapter);



        return rootView;

    }
}
