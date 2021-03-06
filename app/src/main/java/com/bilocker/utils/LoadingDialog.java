package com.bilocker.utils;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.bilocker.R;

public class LoadingDialog {

    Context mContext;
    AlertDialog alertDialog;

    public LoadingDialog(Context context) {
        mContext = context;
    }

    public void startDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.loading_dialog,null);
        alertBuilder.setView(view);
        alertBuilder.setCancelable(false);

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
