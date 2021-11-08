package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.bilocker.model.User;

import java.util.HashMap;
import java.util.Map;

public class TopupActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backBtn;
    private Button topupBtn;
    private EditText topupTxt;

    private static final String TopupURL = "https://bilocker.000webhostapp.com/BiLocker/RedeemVoucher.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        backBtn = findViewById(R.id.topup_back_button);
        topupBtn = findViewById(R.id.topup_ok_button);
        topupTxt = findViewById(R.id.topup_vouvher_text);
    }

    @Override
    protected void onResume() {
        super.onResume();

        backBtn.setOnClickListener(this);
        topupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topup_back_button:
                onBackPressed();
                break;
            case R.id.topup_ok_button:

                String voucherCode = topupTxt.getText().toString();

                StringRequest currentUserRequest = new StringRequest(Request.Method.POST, TopupURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                        SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
                        String email = preferences.getString("user","false");
                        params.put("email", email);
                        params.put("voucherID",voucherCode);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(TopupActivity.this);
                requestQueue.add(currentUserRequest);

                break;
        }
    }
}