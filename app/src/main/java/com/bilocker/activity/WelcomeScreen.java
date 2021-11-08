package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilocker.R;

public class WelcomeScreen extends AppCompatActivity {

    private ImageView logo;
    private TextView title;
    private Animation logoAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        logo = findViewById(R.id.welcome_logo);
        title = findViewById(R.id.welcome_title);

        logoAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_logo_animation);

        logo.setAnimation(logoAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toLogin = new Intent(WelcomeScreen.this, LoginPage.class);
                startActivity(toLogin);
            }
        },2000);
    }

}