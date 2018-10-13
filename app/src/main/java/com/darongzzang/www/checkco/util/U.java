package com.darongzzang.www.checkco.util;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class U {
    private static final U ourInstance = new U();
    public static U getInstance() {
        return ourInstance;
    }
    private U() {}

    // 파이어베이스 인증 객체 관리
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public FirebaseAuth getFirebaseAuth() { return firebaseAuth; }

    public void showPopup(Context context, int type, String title, String msg,
                          String cName, SweetAlertDialog.OnSweetClickListener cEvent,
                          String oName, SweetAlertDialog.OnSweetClickListener oEvent) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(msg)
                .setConfirmText(cName)
                .setConfirmClickListener(cEvent)
                .setCancelText(oName)
                .setCancelClickListener(oEvent)
                .show();
    }

    public void showPopup(Context context, int type, String title, String msg,
                          String cName, SweetAlertDialog.OnSweetClickListener cEvent) {
        new SweetAlertDialog(context, type)
                .setTitleText(title)
                .setContentText(msg)
                .setConfirmText(cName)
                .setConfirmClickListener(cEvent)
                .show();
    }
}
