package com.example.nt_project02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.nt_project02.Chat.UserModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private FirebaseAuth mAuth; //파이어베이스 인증객체
    private GoogleApiClient googleApiClient; //구글 API 클라이언트 객체
    private static final int REO_SIGN_GOOGLE = 100; //구글 로그인 결과 코드
    private SignInButton signInButton; //구글로그인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();



        // 초기화 Firebase Auth

/*        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        mAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화

        findViewById(R.id.CheckButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetbutton).setOnClickListener(onClickListener);
        findViewById(R.id.signUp_Activity_Button).setOnClickListener(onClickListener);
        findViewById(R.id.activity_login_TemporaryNativeButton).setOnClickListener(onClickListener);
        findViewById(R.id.activity_login_TemporaryTravelerButton).setOnClickListener(onClickListener);
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient); //구글에서 제공하는 인증 액티비티 같은 것이 있음
                startActivityForResult(intent, REO_SIGN_GOOGLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //구글로그인인증을요청했을때 결과값을 되돌려받는곳임
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REO_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) { //인증결과가 성공적이면
                GoogleSignInAccount account = result.getSignInAccount();// account라는 데이터는 구글로그인 정보를 담고있다 (닉, 프사Url, 이메일주소등)
                resultLogin(account); //로그인 결과 값 출력 수행하라는 메소드

            }
        }
    }

    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//구글로그인이 성공했으면
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            // 파이어스토어 객체선언
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
                            final DocumentReference docRef = db.collection("users").document(uid);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                //정보 가져오는 것이 성공적일 때
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        //DocumentSnapshot에 정보를 담아둠

                                        DocumentSnapshot document = task.getResult();

                                        //document가 null이 아닐 때

                                        if (document != null) {

                                            //재차 확인
                                            if (document.exists()) {


                                                MystartActivity(MainActivity.class);
                                            } else {

//                                                    //로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                                MystartActivity(MemberActivity.class);
                                            }
                                        }

/*                            if(유저모델에 있는 닉네임이 널이라면 멤버액티비티로 가게) {
                                Intent intent = new Intent(getApplicationContext(), MemberActivity.class);
                                intent.putExtra("email", account.getEmail());
                                intent.putExtra("photourl", String.valueOf(account.getPhotoUrl()));//photoUrl을 String 형태로 바꿈
                                startActivity(intent);
                            } else {
                                닉네임이 있다면 메인액티비티로 가게
                                MystartActivity(MainActivity.class);
                            }*/
                                    } else { //로그인 실패했으면
                                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                }
                );}


                    public void onBackPressed() {
                        super.onBackPressed();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }


                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.CheckButton:
                                    login();
                                    break;

                                case R.id.gotoPasswordResetbutton:
                                    MystartActivity(PasswordResetActivity.class);
                                    break;

                                case R.id.signUp_Activity_Button:
                                    MystartActivity(Sign_UpActivity.class);
                                    break;

                                case R.id.activity_login_TemporaryNativeButton:
                                    Temporary_native_login();
                                    break;

                                case R.id.activity_login_TemporaryTravelerButton:
                                    Temporary_traveler_login();
                                    break;

                            }


                        }
/*        private void signIn() {
            
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);

        }*/


                    };

                        private void login () {

                            String email = ((EditText) findViewById(R.id.NameEditText)).getText().toString();
                            String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();


                            if (email.length() > 0 && password.length() > 0) {

                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    startToast("로그인 성공");
                                                    String uid = user.getUid();
                                                    // 파이어스토어 객체선언
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
                                                    final DocumentReference docRef = db.collection("users").document(uid);
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        //정보 가져오는 것이 성공적일 때
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                //DocumentSnapshot에 정보를 담아둠

                                                                DocumentSnapshot document = task.getResult();






                                                                //document가 null이 아닐 때

                                                                if (document != null) {

                                                                    //재차 확인
                                                                    if (document.exists()) {


                                                                        MystartActivity(MainActivity.class);
                                                                    } else {

//                                                    //로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                                                        MystartActivity(MemberActivity.class);
                                                                    }
                                                                }
                                                                //아예 오류떠서 실패했을 때
                                                            } else {
                                                                Log.d("login", "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });


                                                } else {

                                                    if (task.getException() != null) {
                                                        startToast(task.getException().toString());
                                                    }
                                                }

                                            }
                                        });
                            } else {


                                startToast("이메일 또는 비밀번호를 입력해주세요.");
                            }


                        }

                        private void Temporary_native_login () {

                            String email = "native@naver.com";
                            String password = "123456";


                            if (email.length() > 0 && password.length() > 0) {

                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    startToast("로그인 성공");
                                                    String uid = user.getUid();
                                                    // 파이어스토어 객체선언
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
                                                    final DocumentReference docRef = db.collection("users").document(uid);
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        //정보 가져오는 것이 성공적일 때
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                //DocumentSnapshot에 정보를 담아둠

                                                                DocumentSnapshot document = task.getResult();

                                                                //document가 null이 아닐 때

                                                                if (document != null) {

                                                                    //재차 확인
                                                                    if (document.exists()) {


                                                                        MystartActivity(MainActivity.class);
                                                                    } else {

                                                                        //로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                                                        MystartActivity(MemberTypeActivity.class);
                                                                    }
                                                                }
                                                                //아예 오류떠서 실패했을 때
                                                            } else {
                                                                Log.d("login", "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });


                                                } else {

                                                    if (task.getException() != null) {
                                                        startToast(task.getException().toString());
                                                    }
                                                }

                                                // ...
                                            }
                                        });

                            } else {


                                startToast("이메일 또는 비밀번호를 입력해주세요.");
                            }


                        }

                        private void Temporary_traveler_login () {

                            String email = "traveler@naver.com";
                            String password = "123456";


                            if (email.length() > 0 && password.length() > 0) {

                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    startToast("로그인 성공");
                                                    String uid = user.getUid();
                                                    // 파이어스토어 객체선언
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    //파이어스토어에서 해당 유저의 uid를 이용하여 정보 가져오기
                                                    final DocumentReference docRef = db.collection("users").document(uid);
                                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        //정보 가져오는 것이 성공적일 때
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                //DocumentSnapshot에 정보를 담아둠

                                                                DocumentSnapshot document = task.getResult();

                                                                //document가 null이 아닐 때

                                                                if (document != null) {

                                                                    //재차 확인
                                                                    if (document.exists()) {


                                                                        MystartActivity(MainActivity.class);
                                                                    } else {

                                                                        //로그인은 됐는데, 상세정보가 등록되어 있지 않으면 MemberActivity클래스로 이동
                                                                        MystartActivity(MemberTypeActivity.class);
                                                                    }
                                                                }
                                                                //아예 오류떠서 실패했을 때
                                                            } else {
                                                                Log.d("login", "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });


                                                } else {

                                                    if (task.getException() != null) {
                                                        startToast(task.getException().toString());
                                                    }
                                                }

                                                // ...
                                            }
                                        });

                            } else {


                                startToast("이메일 또는 비밀번호를 입력해주세요.");
                            }


                        }


                        private void startToast (String msg){

                            Toast.makeText(LoginActivity.this, msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                        private void MystartActivity (Class c){
                            Intent intent = new Intent(this, c);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }


                        @Override
                        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){

                        }
                    }