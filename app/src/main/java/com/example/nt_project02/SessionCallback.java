package com.example.nt_project02;

import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.util.ArrayList;
import java.util.List;

public class SessionCallback implements ISessionCallback {




    // 로그인에 성공한 상태

    @Override

    public void onSessionOpened() {
        requestMe();

    }



    // 로그인에 실패한 상태

    @Override

    public void onSessionOpenFailed(KakaoException exception) {

        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());

    }



    // 사용자 정보 요청

    public void requestMe() {

        // 사용자정보 요청 결과에 대한 Callback
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");
        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {

            // 세션 오픈 실패. 세션이 삭제된 경우,

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());

            }



            // 사용자정보 요청에 성공한 경우,
            @Override
            public void onSuccess(MeV2Response result) {
                Log.e("SessionCallback :: ", "onSuccess");
            }


            // 사용자 정보 요청 실패

            @Override
            public void onFailure(ErrorResult errorResult) {

                String message = "failed to get user info. msg=" + errorResult;
            }



        });

    }


}