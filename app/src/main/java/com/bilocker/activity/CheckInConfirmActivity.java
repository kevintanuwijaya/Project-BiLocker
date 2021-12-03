package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bilocker.utils.Convert;
import com.bilocker.utils.LoadingDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckInConfirmActivity extends AppCompatActivity {

    private LoadingDialog loading;
    private String lockerID;
    private RequestQueue requestQueue;
    private ImageView check;
    private Button closeBtn;
    private String userEmail;

    private static final String openLockerURL = "https://bilocker.000webhostapp.com/BiLocker/OpenLocker.php";
    private static final String storeTransaction = "https://bilocker.000webhostapp.com/BiLocker/StoreTransaction.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_confirm);

        loading = new LoadingDialog(this);
        check = findViewById(R.id.check_in_confirm_check);
        closeBtn = findViewById(R.id.check_in_confirm_button);

        lockerID = getIntent().getStringExtra("LOCKERID");
        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
        userEmail = preferences.getString("user","false");
    }

    @Override
    protected void onStart() {
        super.onStart();
        openLocker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.startDialog();
                storeTransaction();
            }
        });
    }

    private void openLocker(){
        StringRequest openLockerRequest = new StringRequest(Request.Method.POST, openLockerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Locker is Open")){
                    Animation animation = AnimationUtils.loadAnimation(getApplication(),R.anim.check_image_animation);
                    check.setAnimation(animation);
                    check.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            closeBtn.setVisibility(View.VISIBLE);
                        }
                    },2000);
                }
                Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
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

        requestQueue.add(openLockerRequest);
    }

    private void storeTransaction(){
        StringRequest storeTransactionURL = new StringRequest(Request.Method.POST, storeTransaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Create Transaction Success")){
                    loading.dismissDialog();
                    Toast.makeText(getApplication(),response,Toast.LENGTH_SHORT).show();
                    Intent toMain = new Intent(CheckInConfirmActivity.this, MainActivity.class);
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
                params.put("email",userEmail);
                params.put("lockerID",lockerID);
                params.put("startTime", Convert.getInstance().convertDateToString(new Date()));
                return params;
            }
        };

        requestQueue.add(storeTransactionURL);
    }
}