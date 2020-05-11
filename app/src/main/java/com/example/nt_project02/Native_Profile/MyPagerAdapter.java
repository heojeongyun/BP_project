package com.example.nt_project02.Native_Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.nt_project02.R;

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;

    private View mCurrentView = null;

    private int mCurrentIndex;

    public MyPagerAdapter(Context context) {

        mContext = context;
    }

    // priceinfo, travelreview관련 view들의 index를 기준으로 지도라던지 식당후기나 여행후기등을 업로드
    private void updateView() {

        if (mCurrentIndex == 0) {

            TextView textView = mCurrentView.findViewById( R.id.travelPreviewText);
            textView.setText("");
        } else if (mCurrentIndex == 1) {

            TextView textView1 = mCurrentView.findViewById(R.id.priceInfoTextView1);
            textView1.setText("부산 2박3일 추천 코스");
            TextView textView2 = mCurrentView.findViewById(R.id.priceInfoTextView2);
            //textView1.setText("텍스트 입력");
        } else if (mCurrentIndex == 2) {

            MyAdapter adapter = new MyAdapter();

            ListView listView = mCurrentView.findViewById(R.id.reviewListView);
            listView.setAdapter(adapter);

            ReviewClass review1 = new ReviewClass();
            adapter.addReview(review1);

            ReviewClass review2 = new ReviewClass();
            adapter.addReview(review2);

            ReviewClass review3 = new ReviewClass();
            adapter.addReview(review3);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // 리뷰 관련 페이지인 travelpreview, priceinfo, review tab을 볼 수 있게 하는 함수
        mCurrentView = null;

        if (mContext != null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
            if (position == 0) { // travel preview tab 가져오기

                mCurrentView = inflater.inflate(R.layout.travelpreview, container, false);
            } else if (position == 1) { // price info tab 가져오기

                mCurrentView = inflater.inflate(R.layout.priceinfo, container, false);
            } else if (position == 2) { // review tab 가져오기

                mCurrentView = inflater.inflate(R.layout.review, container, false);
            }
        }

        mCurrentIndex = position;

        updateView();

        // add to view pager.
        container.addView(mCurrentView);

        return mCurrentView;
    }
    // 작성한 내용이 tab을 넘길시 삭제되지 않도록 보정
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
