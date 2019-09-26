package com.example.nt_project02.Native_Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nt_project02.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private ArrayList<ReviewClass> reviewList = new ArrayList<> ();

    public void addReview(ReviewClass review) {

        reviewList.add(review);
    }

    public void clearReView() {

        reviewList.clear();
    }

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int i) {
        return reviewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate( R.layout.list_item, viewGroup, false);
        }

        ReviewClass review = reviewList.get(i);

        ImageView imageView = view.findViewById(R.id.reviewImage);
        // imageView.setImageDrawable(review.getImage()); // 원하는 이미지 추가

        TextView textView = view.findViewById(R.id.reviewText);
        // textView.setText(review.getText()); // 원하는 텍스트 추가

        return view;
    }
}
