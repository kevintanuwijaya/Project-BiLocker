package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import com.bilocker.utils.Convert;
import com.bilocker.utils.LoadingDialog;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    private AppCompatRadioButton biMoneyRadio;
    private RadioGroup paymentRadio;
    private ImageView backBtn;
    private Button payBtn;
    private int money,topay,transactionID;
    private String lockerID,userEmail;
    private RequestQueue requestQueue;
    private LoadingDialog loading;

    private final static String payTransactionURL = "https://bilocker.000webhostapp.com/BiLocker/PayTransaction.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        biMoneyRadio = findViewById(R.id.checkout_bimoney_button);
        paymentRadio = findViewById(R.id.checkout_radio_button);
        payBtn = findViewById(R.id.checkout_pay_btn);
        backBtn = findViewById(R.id.checkout_back_btn);
        requestQueue = Volley.newRequestQueue(this);
        loading = new LoadingDialog(this);

        money = getIntent().getIntExtra("MONEY",0);
        topay = getIntent().getIntExtra("MONEYTOPAY",0);
        transactionID = getIntent().getIntExtra("TRANSACTIONID",0);
        lockerID = getIntent().getStringExtra("LOCKERID");
        SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
        userEmail = preferences.getString("user","false");
    }

    @Override
    protected void onStart() {
        super.onStart();

        String userMoney = Convert.getInstance().convertIntToRupiah(money);

        biMoneyRadio.setText("BiMoney " + userMoney);

    }

    @Override
    protected void onResume() {
        super.onResume();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.startDialog();
                int selectedID = paymentRadio.getCheckedRadioButtonId();
                if(selectedID == R.id.checkout_bimoney_button ){
                    if(money > topay){
                        payTransaction();
                    }else{
                        loading.dismissDialog();
                        Toast.makeText(getApplicationContext(), "Insufficient Money, Please Top Up", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void payTransaction(){
        StringRequest storeTransactionRequest = new StringRequest(Request.Method.POST, payTransactionURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("payStatus",response);
                loading.dismissDialog();
                if(response.equals("Continue")){
                    Intent toConfirm = new Intent(CheckOutActivity.this, CheckOutConfirmActivity.class);
                    toConfirm.putExtra("LOCKERID",lockerID);
                    startActivity(toConfirm);
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
                params.put("endTime",Convert.getInstance().convertDateToString(new Date()));
                params.put("price",""+topay);
                params.put("transactionID",""+transactionID);
                params.put("email",userEmail);
                params.put("lockerID",lockerID);
                return params;
            }
        };

        requestQueue.add(storeTransactionRequest);
    }
}