package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.bilocker.utils.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {

    private TextView toLoginTxt;
    private Button registerBtn;
    private EditText usernameTxt,emailTxt,passwordTxt;
    private Intent toLogin;
    private LoadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        toLoginTxt = findViewById(R.id.register_to_login_txt);
        registerBtn = findViewById(R.id.register_btn);
        usernameTxt = findViewById(R.id.register_username);
        emailTxt = findViewById(R.id.register_email);
        passwordTxt = findViewById(R.id.register_password);
        loading = new LoadingDialog(this);

        toLogin = new Intent(RegisterPage.this,LoginPage.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        toLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(toLogin);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.startDialog();
                final String url = "https://bilocker.000webhostapp.com/BiLocker/Register.php";

                final String name = usernameTxt.getText().toString();
                final String email = emailTxt.getText().toString();
                final String password = passwordTxt.getText().toString();

                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismissDialog();
                        Toast.makeText(getApplication(),response,Toast.LENGTH_SHORT).show();
                        if (response.equals("Success")){
                            Toast.makeText(getApplication(),"Register Success",Toast.LENGTH_SHORT).show();
                            startActivity(toLogin);

                        }else
                            if(response.equals("Failed")){
                                Toast.makeText(getApplication(),"Email already Taken",Toast.LENGTH_SHORT).show();
                                usernameTxt.setText("");
                                emailTxt.setText("");
                                passwordTxt.setText("");
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
                        params.put("name",name);
                        params.put("password",password);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(RegisterPage.this);
                requestQueue.add(request);
            }
        });
    }
}