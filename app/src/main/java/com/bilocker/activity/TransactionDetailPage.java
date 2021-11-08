package com.bilocker.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
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
import com.bilocker.model.Location;
import com.bilocker.model.Transaction;
import com.bilocker.utils.Convert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.blikoon.qrcodescanner.QrCodeActivity;

public class TransactionDetailPage extends AppCompatActivity {

    private TextView loker,address,duration, price;
    private RequestQueue requestQueue;
    private Button checkoutBtn;

    private static final String selectedTransaction = "https://bilocker.000webhostapp.com/BiLocker/TransactionDetail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail_page);

        loker = findViewById(R.id.detail_loker_text);
        address = findViewById(R.id.detail_loker_address_text);
        duration = findViewById(R.id.detail_duration_text);
        price = findViewById(R.id.detail_price_text);
        checkoutBtn = findViewById(R.id.detail_checkout);

        StringRequest getTransaction = new StringRequest(Request.Method.POST, selectedTransaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray transactions = jsonObject.getJSONArray("Transaction");

                    for (int i=0 ; i<transactions.length(); i++) {
                        JSONObject object = transactions.getJSONObject(i);

                        Transaction transaction = new Transaction();

                        Location location = new Location();
                        location.setLocker(object.getString("LockerName"));
                        location.setAddress(object.getString("LockerLocation"));

                        transaction.setLocation(location);
                        transaction.setTransactionID(object.getInt("TransactionID"));
                        transaction.setStartTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionStart")));

                        if(object.isNull("TransactionEnd")){
                            transaction.setFinishTime(new Timestamp(new java.util.Date().getTime()));
                            transaction.setPrice(0);
                        }else{
                            transaction.setFinishTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionEnd")));
                            transaction.setPrice(Integer.parseInt(object.getString("TransactionPrice")));
                        }

                        loker.setText(transaction.getLocation().getLocker());
                        address.setText(transaction.getLocation().getAddress());
                        duration.setText(getHoursDuration(transaction) + " Hours");
                        price.setText(getEstPrice(transaction) + " IDR");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("TransactionID",""+getIntent().getIntExtra("TransactionDetail",0));
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(TransactionDetailPage.this);
        requestQueue.add(getTransaction);
    }

    private int getHoursDuration(Transaction transaction){
        long diff = transaction.getFinishTime().getTime() - transaction.getStartTime().getTime();
        int diffHours = (int) (diff / (60 * 60 * 1000) + 1);
        return  diffHours;
    }

    private int getEstPrice(Transaction transaction){
        long diff = transaction.getFinishTime().getTime() - transaction.getStartTime().getTime();
        int diffHours = (int) (diff / (60 * 60 * 1000) + 1);
        return  diffHours * 1000;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransactionDetailPage.this, QrCodeActivity.class);
                startActivityForResult(i,101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(requestCode == 101){
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Toast.makeText(getApplication(),result,Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
    }
}