package com.example.nt_project02.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.Native_Profile.Profile;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PeopleFragment extends Fragment {

    @Nullable





 /*   private List<UserModel> userModel;
    private ArrayList<UserModel> userModels;
    private ArrayList<UserModel> arrayList;
    private EditText editText;*/
    private PeopleFragmentRecyclerViewAdapter adapter;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);




 /*       editText=(EditText) view.findViewById(R.id.txt_search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                adapter.filter(text);


            }
        });*/

        adapter=new PeopleFragmentRecyclerViewAdapter();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        recyclerView.setAdapter(adapter);



        return view;
    }




    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {





        List<UserModel> userModels;
        public PeopleFragmentRecyclerViewAdapter() {



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            //arrayList=new ArrayList<>();


            db.collection("users")
                    .whereEqualTo("user_kind", "현지인")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    userModels.add(document.toObject(UserModel.class));
                                    //arrayList.add(document.toObject(UserModel.class));

                                }
                                notifyDataSetChanged();
                            } else {

                            }
                        }
                    });
   /*         FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModels.clear();
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                        userModels.add(snapshot.getValue(UserModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/



        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).imageurl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).nick);
            ((CustomViewHolder) holder).Region_textView.setText(userModels.get(position).region);
            ((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hash);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getContext(), Profile.class);
                        intent.putExtra("destination_UserModels", userModels.get(position));
                        startActivity(intent);


                    }

                }
            });

        }

        @Override
        public int getItemCount() {

            return userModels.size();
        }

   /*     public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            userModels.clear();
            if (charText.length() == 0) {
                userModels.addAll(arrayList);
            } else {
                for (UserModel user : arrayList) {
                    String name =context.getResources().getString(user);
                    if (name.toLowerCase().contains(charText)) {
                        userModels.add(user);
                    }
                }
            }
            notifyDataSetChanged();
        }*/



    }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public  ImageView imageView;
            public TextView Nick_textView;
            public TextView Region_textView;
            public TextView Hash_textView;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
                Region_textView=(TextView) view.findViewById(R.id.frienditem_region);
                Hash_textView=(TextView) view.findViewById(R.id.frienditem_hash);
            }
        }
    }






