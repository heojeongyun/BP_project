package com.native_code.bp_project02.Native_Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.native_code.bp_project02.R;

import java.util.ArrayList;
// reviewList라는 custom View를 생성
public class MyAdapter extends BaseAdapter {

    private ArrayList<ReviewClass> reviewList = new ArrayList<> ();
    public void addReview(ReviewClass review) {
        reviewList.add(review);
    }
    // reviewList를 초기화하는 함수인데 아마 사용되지 않는듯하다..
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
        // 사용자 입장에서 현지인에게 리뷰를 새로 생성하기 위해 배경화면이 될 Layout인 list_item을 불러오며 원하는 사진과 후기를 첨부
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
