package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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

public class CheckOutConfirmActivity extends AppCompatActivity {

    private LoadingDialog loading;
    private String lockerID;
    private RequestQueue requestQueue;
    private ImageView check;
    private Button closeBtn;

    private final static String closeLockerURL = "https://bilocker.000webhostapp.com/BiLocker/CloseLocker.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_confirm);

        requestQueue = Volley.newRequestQueue(this);
        check = findViewById(R.id.check_out_confirm_check);
        closeBtn = findViewById(R.id.check_out_confirm_button);

        lockerID = getIntent().getStringExtra("lockerID");
        loading = new LoadingDialog(this);
        loading.startDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.check_image_animation);
        check.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeLocker();
                    }
                });
            }
        },2000);

    }

    private void closeLocker(){

        StringRequest closeLockerRequest = new StringRequest(Request.Method.POST, closeLockerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();

                if(response.equals("Locker is Close")){
                    Toast.makeText(getApplication(), "Thank You For using Bilocker", Toast.LENGTH_SHORT).show();
                    Intent toMain = new Intent(CheckOutConfirmActivity.this, MainActivity.class);
                    toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toMain);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("lockerID",lockerID);
                return params;
            }
        };

        requestQueue.add(closeLockerRequest);

    }
}