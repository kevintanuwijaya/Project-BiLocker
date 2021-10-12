package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bilocker.R;

public class RegisterPage extends AppCompatActivity {

    private TextView toLoginTxt;
    private Button registerBtn;
    private EditText usernameTxt,emailTxt,passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        toLoginTxt = findViewById(R.id.register_to_login_txt);
    }

    @Override
    protected void onResume() {
        super.onResume();

        toLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(RegisterPage.this,LoginPage.class);
                startActivity(toLogin);
            }
        });
    }
}