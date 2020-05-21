package com.example.nt_project02.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.NativeSearch;
import com.example.nt_project02.Native_Profile.Profile;
import com.example.nt_project02.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragment extends Fragment {

    @Nullable

    private List<UserModel> userModels;
    private List<UserModel> saveList;
    private TextView textView;
    private PeopleFragmentRecyclerViewAdapter adapter;
    private Context context;
    private String TAG = "PeopleFragment";
    private String name;


    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_people, container,
                false);
        textView = rootView.findViewById(R.id.txt_search);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NativeSearch.class);
                intent.putParcelableArrayListExtra("UserModels", (ArrayList<? extends Parcelable>) userModels);
                intent.putParcelableArrayListExtra("SaveList", (ArrayList<? extends Parcelable>) saveList);
                startActivity(intent);

            }
        });


        /*editText.addTextChangedListener(new TextWatcher() {
            @Override

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString()
                        .toLowerCase(Locale.getDefault());
                //adapter.filter(text);


            }
        });
*/


        adapter = new PeopleFragmentRecyclerViewAdapter();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        recyclerView.setAdapter(adapter);



        return rootView;

    }


    private void MystartActivity(Class c) {

        Intent intent = new Intent(getActivity(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그아웃시 초기화하는 부분
        startActivity(intent);

    }


    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



/*        RecyclerView.ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        RecyclerItem item = mData.get(pos);
                    }
                }
            });
        }*/
        public PeopleFragmentRecyclerViewAdapter() {


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userModels = new ArrayList<>();
            saveList = new ArrayList<>();


            Query postOrder = FirebaseFirestore.getInstance().collection("users").orderBy("bookmarks_number", Query.Direction.DESCENDING);


           /*db.collection("users")
                    .whereEqualTo("user_kind", "현지인")*/
            postOrder.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    userModels.clear();
                    saveList.clear();  //파이어 스토어 데이터 변경 시 중복으로 쌓이는 데이터 방
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc != null) {


                            userModels.add(doc.toObject(UserModel.class));
                            saveList.add(doc.toObject(UserModel.class));


                        }

                    }
                    notifyDataSetChanged();

                    Log.d(TAG, "Current data: " + userModels);
                }

            });


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            return new CustomViewHolder(view);



        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Integer bookmarks_number;


            //item 클릭시 선택효과
            //int selectedPosition = -1;
            //if(selectedPosition==position)
            // holder.itemView.setBackgroundColor (Color.GRAY);

            //색 가져오기

            //Integer Gold = ContextCompat.getColor(getContext(), R.color.Gold);
            //Integer Silver=ContextCompat.getColor(getContext(), R.color.Silver);
            //Integer White=ContextCompat.getColor(getContext(), R.color.white);

            //초기화
            //((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(White);
            ((CustomViewHolder) holder).item_friend_RankImage.setImageResource(R.drawable.medal3);
            ((CustomViewHolder) holder).imageView.setImageResource(R.drawable.user);

            bookmarks_number = userModels.get(position).bookmarks_number;

            if ((bookmarks_number != null) && (bookmarks_number != 0)) {

                if (bookmarks_number >= 2) {
                    //((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(Gold);
                    ((CustomViewHolder) holder).item_friend_RankImage.setImageResource(R.drawable.medal1);
                } else {
                    //((CustomViewHolder)holder).fragment_people_ItemLayout.setBackgroundColor(Silver);
                    ((CustomViewHolder) holder).item_friend_RankImage.setImageResource(R.drawable.medal2);


                }
                //테스트
            }

            //현지인 등록 이미지가 있을 경우 이미지 넣기
            if (userModels.get(position).imageurl != null) {
                Glide.with
                        (holder.itemView.getContext())
                        .load(userModels.get(position).imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(((CustomViewHolder) holder).imageView);
            }
            ((CustomViewHolder) holder).Nick_textView.setText(userModels.get(position).name);
            ((CustomViewHolder) holder).Region_textView.setText(userModels.get(position).region);
            ((CustomViewHolder) holder).Hash_textView.setText(userModels.get(position).hashtag);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) {
                        /*Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(getContext(), Profile.class);
                        Log.d(TAG, userModels.get(position).toString());
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

        public void searchUser(String search) {

            userModels.clear();

            for (int i = 0; i < saveList.size(); i++) {

                if (saveList.get(i).getName() != null && saveList.get(i).getName().contains(search)) {//contains메소드로 search 값이 있으면 true를 반환함

                    userModels.add(saveList.get(i));

                }

            }

            adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌


        }

    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView Nick_textView;
        public TextView Region_textView;
        public TextView Hash_textView;
        public LinearLayout fragment_people_ItemLayout;
        public ImageView item_friend_RankImage;

        public CustomViewHolder(View view) {
            super(view);
            fragment_people_ItemLayout = (LinearLayout) view.findViewById(R.id.fragment_people_itemLayout);
            imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
            Nick_textView = (TextView) view.findViewById(R.id.frienditem_nick);
            Region_textView = (TextView) view.findViewById(R.id.frienditem_region);
            Hash_textView = (TextView) view.findViewById(R.id.frienditem_hash);
            item_friend_RankImage = (ImageView) view.findViewById(R.id.item_friend_RankImage);
        }
    }

    private void startToast(String msg) {

        Toast.makeText(getContext(), msg,
                Toast.LENGTH_SHORT).show();
    }


}
