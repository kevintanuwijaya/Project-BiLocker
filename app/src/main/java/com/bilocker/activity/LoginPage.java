package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bilocker.R;

import java.util.HashMap;
import java.util.Map;

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
        loginBtn = findViewById(R.id.login_btn);
        emailTxt = findViewById(R.id.login_email);
        passwordTxt = findViewById(R.id.login_password);


    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
        String login = preferences.getString("user","false");

        if(!login.equals("false")){
            Intent toHome = new Intent(LoginPage.this, MainActivity.class);
            startActivity(toHome);
        }
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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String url = "https://bilocker.000webhostapp.com/BiLocker/Login.php";

                final String email = emailTxt.getText().toString();
                final String password = passwordTxt.getText().toString();

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("Failed")){
                            Toast.makeText(getApplication(),"Invalid Email or Password",Toast.LENGTH_SHORT).show();
                            emailTxt.setText("");
                            passwordTxt.setText("");
                        }else{
                            Toast.makeText(getApplication(),"Login Success",Toast.LENGTH_SHORT).show();

                            String[] data = response.split("#");

                            SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user",data[0]);
                            editor.apply();

                            Intent toHome = new Intent(LoginPage.this, MainActivity.class);
                            startActivity(toHome);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),"Volley Error",Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("email",email);
                        params.put("password",password);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(LoginPage.this);
                requestQueue.add(request);
            }
        });

    }
}