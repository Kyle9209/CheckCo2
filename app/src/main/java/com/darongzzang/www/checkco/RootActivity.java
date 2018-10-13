package com.darongzzang.www.checkco;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RootActivity extends AppCompatActivity {
//    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showPD(Context context){
//        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
//        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        sweetAlertDialog.setTitleText("잠시만 기다려주세요...");
//        sweetAlertDialog.setCancelable(false);
//        sweetAlertDialog.show();
    }

    public void stopPD(){
//        if(sweetAlertDialog != null && sweetAlertDialog.isShowing()){
//            sweetAlertDialog.dismissWithAnimation();
//        }
    }
}
