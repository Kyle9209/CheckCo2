package com.darongzzang.www.checkco.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.darongzzang.www.checkco.R;
import com.darongzzang.www.checkco.RootActivity;
import com.darongzzang.www.checkco.model.CompanyModel;
import com.darongzzang.www.checkco.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends RootActivity {
    RadioGroup level;
    EditText name, phone, eid, password1 , password2, company;
    TextInputLayout company_til;
    CardView company_cv;

    String name_str, phone_str, company_str;

    SignInActivity self;

    // 파이어베이스 인증 객체 획득
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // 파이어베이스 데이터베이스 객체 획득
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        self = this;

        level = (RadioGroup) findViewById(R.id.level);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        eid = (EditText) findViewById(R.id.eid);
        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);
        company = (EditText) findViewById(R.id.company);
        company_cv = (CardView) findViewById(R.id.company_cv);
        company_til = (TextInputLayout) findViewById(R.id.company_til);

        // 관리자 체크 시 회사 입력란 보이게 처리
        level.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.master) {
                    company_cv.setVisibility(View.VISIBLE);
                    company_til.requestFocus();
                } else {
                    company_cv.setVisibility(View.GONE);
                    company.setText("");
                }
            }
        });

        // 비밀번호확인 입력창 키패드에서 완료 누르면 회원가입 진행
        password2.setImeOptions(EditorInfo.IME_ACTION_DONE);
        password2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                onSignInBtn(null);
                return false;
            }
        });
    }

    public void onSignInBtn(View view) {
        final String eid_str = eid.getText().toString();
        String pwd1_str = password1.getText().toString();
        String pwd2_str = password2.getText().toString();
        name_str = name.getText().toString();
        phone_str = phone.getText().toString();
        company_str = company.getText().toString();

        // 유효성검사
        View focus = null;
        Boolean state = false;

        if(!pwd1_str.equals(pwd2_str)){
            password2.setError("비밀번호가 다릅니다.");
            focus = password2;
            state = true; // 진행불가 상태
        }
        if(TextUtils.isEmpty(pwd2_str) || pwd2_str.length() < 6){
            password2.setError("비밀번호를 확인해주세요.");
            focus = password2;
            state = true; // 진행불가 상태
        }
        if(TextUtils.isEmpty(pwd1_str) || pwd1_str.length() < 6){
            password1.setError("비밀번호를 확인해주세요.");
            focus = password1;
            state = true; // 진행불가 상태
        }
        if(TextUtils.isEmpty(eid_str)){
            eid.setError("사번 또는 ID를 입력해주세요.");
            focus = eid;
            state = true;
        }
        if(TextUtils.isEmpty(phone_str)){
            phone.setError("휴대폰번호를 입력해주세요.");
            focus = phone;
            state = true;
        }
        if(TextUtils.isEmpty(name_str)){
            name.setError("이름을 입력해주세요.");
            focus = name;
            state = true;
        }
        if(company_cv.getVisibility() == View.VISIBLE) {
            if(TextUtils.isEmpty(company_str)){
                company.setError("회사명을 입력해주세요");
                focus = company;
                state = true;
            }
        } else {
            company_str = "null";
        }

        if(state){
            focus.requestFocus();
            return;
        }else{
            String email_str = eid_str + "@darongzzang.com";

            showPD(self);

            // 파이어베이스 계정생성
            firebaseAuth.createUserWithEmailAndPassword(email_str, pwd1_str)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                uploadUserData(eid_str);
                            } else {
                                Toast.makeText(SignInActivity.this, "계정 생성에 실패하였습니다.",
                                        Toast.LENGTH_SHORT).show();

                                stopPD();
                            }
                        }
                     });
        }
    }

    public void uploadUserData(String id){
        String position = "";
        String dept = "";
        int level_int;

        // 로그인된 유저의 uid 획득
        String uid_str = firebaseAuth.getCurrentUser().getUid();

        // 로그인된 레벨 획득
        if (level.getCheckedRadioButtonId() == R.id.employee ) {
            level_int = 1; // 일반사원
            position = "null";
            dept = "null";
        } else {
            level_int = 0; // 관리자
            position = "관리자";
            dept = "관리";
        }

        // 데이터 셋팅
        UserModel userModel = new UserModel(
                uid_str,
                id,
                name_str,
                phone_str,
                "img",
                FirebaseInstanceId.getInstance().getToken(),
                new UserModel.Company(company_str, dept, position),
                level_int,
                0
        );

        // 사용자 데이터를 해시맵 형태로 변환
        Map<String, Object> data = userModel.toMap();

        // 저장될 위치(키값)과 저장될 데이터(내용)을 셋팅
        Map<String, Object> update = new HashMap<>();
        update.put("/users/"+uid_str, data);

        // 관리자 가입에만 해당
        if(company_cv.getVisibility() == View.VISIBLE) {
            CompanyModel companyModel = new CompanyModel(
                FirebaseInstanceId.getInstance().getToken(),
                new CompanyModel.Beacon("null", "null", 0, 0),
                new CompanyModel.Time(new CompanyModel.Time.InTime(9,0), new CompanyModel.Time.OutTime(18,0)),
                null
            );
            data = companyModel.toMap();
            update.put("/companies/" + company_str, data);
        }

        // 파이어베이스 사용자데이터 저장
        dr.updateChildren(update, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                stopPD();

                if (databaseError != null) {
                    Toast.makeText(SignInActivity.this, databaseError.getMessage().toString(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(self, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
