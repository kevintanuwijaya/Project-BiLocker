package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bilocker.R;

public class LoginPage extends AppCompatActivity {

    private TextView toRegisterTxt,recoverTxt;
    private Button loginBtn;
    private TextView emailTxt,passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        toRegisterTxt = findViewById(R.id.login_to_register_txt);
        recoverTxt = findViewById(R.id.login_recover_txt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        toRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toRegister = new Intent(LoginPage.this,RegisterPage.class);
                startActivity(toRegister);

            }
        });

    }
}