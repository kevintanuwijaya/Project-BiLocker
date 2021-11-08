package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bilocker.adapter.HistoryAdapter;
import com.bilocker.adapter.OnProgessTransactionAdapter;
import com.bilocker.adapter.PromoAdapter;
import com.bilocker.model.Location;
import com.bilocker.model.Transaction;
import com.bilocker.model.User;
import com.bilocker.utils.Convert;
import com.bilocker.utils.LoadingDialog;
import com.blikoon.qrcodescanner.QrCodeActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnProgessTransactionAdapter.OnNoteListener {

    private RecyclerView transaction,history,promotion;
    private TextView userName,userBalance,userSectionName, userSectionEmail, userSectionBalance;
    private SlidingUpPanelLayout profileSlider;
    private ImageView logout;
    private LinearLayout historyPanel;
    private ConstraintLayout noTransactionPanel,scanBtn,topupBtn;
    private User currentUser;
    private RequestQueue requestQueue;

    private static final String historyUrl = "https://bilocker.000webhostapp.com/BiLocker/History.php";
    private static final String currentTransaction = "https://bilocker.000webhostapp.com/BiLocker/CurrentTransaction.php";
    private static final String CurrUser = "https://bilocker.000webhostapp.com/BiLocker/GetUser.php";
    private static final String checkLocker = "https://bilocker.000webhostapp.com/BiLocker/CheckLocker.php";

    private static final String LOG = MainActivity.class.getSimpleName();


    private Vector<Transaction> historyTransaction = new Vector<>();
    private Vector<Transaction> onProgressTransaction = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transaction = findViewById(R.id.main_transaction);
        history = findViewById(R.id.main_history);
        promotion = findViewById(R.id.main_promotion);

        profileSlider = findViewById(R.id.main_profile_slider);
        logout = findViewById(R.id.main_logout);
        historyPanel = findViewById(R.id.main_history_panel);
        noTransactionPanel = findViewById(R.id.main_no_transaction);
        scanBtn = findViewById(R.id.main_camera_button);
        topupBtn = findViewById(R.id.main_topup_button);

        userName = findViewById(R.id.main_user_name);
        userBalance = findViewById(R.id.main_user_balance);
        userSectionName = findViewById(R.id.main_section_current_name);
        userSectionEmail = findViewById(R.id.main_section_current_email);
        userSectionBalance = findViewById(R.id.main_section_current_balance);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
        String login = preferences.getString("user","false");

        generatePromoBanner();

        StringRequest currentUserRequest = new StringRequest(Request.Method.POST, CurrUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] data = response.split("#");

                currentUser = new User();
                currentUser.setEmail(data[0]);
                currentUser.setPassword(data[1]);
                currentUser.setName(data[2]);
                currentUser.setMoney(Integer.parseInt(data[3]));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("email",login);
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(currentUserRequest);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getTransactionData();
                getHistoryData();
                userName.setText(currentUser.getName());
                userBalance.setText("Rp. " + currentUser.getMoney());
                userSectionName.setText(currentUser.getName());
                userSectionEmail.setText(currentUser.getEmail());
                userSectionBalance.setText("Rp. " + currentUser.getMoney());

                loadingDialog.dismissDialog();
            }
        },3000);


    }

    @Override
    protected void onResume() {
        super.onResume();

        profileSlider.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                userName.setAlpha(1-slideOffset);
                userBalance.setAlpha(1-slideOffset);
                logout.setAlpha(1-slideOffset);
                historyPanel.setAlpha(1-slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("BiLocker",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user","false");
                editor.apply();

                Intent login = new Intent(MainActivity.this,LoginPage.class);
                startActivity(login);
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i,101);
            }
        });

        topupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTopup = new Intent(MainActivity.this, TopupActivity.class);
                startActivity(toTopup);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        historyTransaction.removeAll(historyTransaction);
        onProgressTransaction.removeAll(onProgressTransaction);
    }

    private void getHistoryData(){
        StringRequest historyRequest = new StringRequest(Request.Method.POST, historyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray transactions = jsonObject.getJSONArray("Transaction");

                        for (int i=0 ; i<transactions.length(); i++){
                            noTransactionPanel.setVisibility(View.GONE);
                            JSONObject object = transactions.getJSONObject(i);

                            Transaction transaction = new Transaction();

                            Location location = new Location();
                            location.setLocker(object.getString("LockerName"));
                            location.setAddress(object.getString("LockerLocation"));

                            transaction.setLocation(location);
                            transaction.setStartTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionStart")));

                            if(object.isNull("TransactionEnd")){
                                transaction.setFinishTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionStart")));
                                transaction.setPrice(0);
                            }else{
                                transaction.setFinishTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionEnd")));
                                transaction.setPrice(Integer.parseInt(object.getString("TransactionPrice")));
                            }

                            historyTransaction.add(transaction);
                        }

                        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(getApplication());
                        historyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        HistoryAdapter historyAdapter = new HistoryAdapter(getApplication(),historyTransaction);
                        history.setAdapter(historyAdapter);
                        history.setLayoutManager(historyLayoutManager);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("email",currentUser.getEmail());
                return params;
            }
        };

        requestQueue.add(historyRequest);
    }

    private void getTransactionData(){
        StringRequest currentTransactionRequest = new StringRequest(Request.Method.POST, currentTransaction, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray transactions = jsonObject.getJSONArray("Transaction");

                        for (int i=0 ; i<transactions.length(); i++){
                            JSONObject object = transactions.getJSONObject(i);

                            Transaction transaction = new Transaction();

                            Location location = new Location();
                            location.setLocker(object.getString("LockerName"));
                            location.setAddress(object.getString("LockerLocation"));

                            transaction.setLocation(location);
                            transaction.setTransactionID(object.getInt("TransactionID"));
                            transaction.setStartTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionStart")));

                            if(object.isNull("TransactionEnd")){
                                transaction.setFinishTime(new Timestamp(new Date().getTime()));
                                transaction.setPrice(0);
                            }else{
                                transaction.setFinishTime(Convert.getInstance().convertStringToTimestamp(object.getString("TransactionEnd")));
                                transaction.setPrice(Integer.parseInt(object.getString("TransactionPrice")));
                            }

                            onProgressTransaction.add(transaction);
                        }

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

                        OnProgessTransactionAdapter onProgessTransactionAdapter = new OnProgessTransactionAdapter(getApplication(),onProgressTransaction,MainActivity.this::onNoteClick);
                        transaction.setAdapter(onProgessTransactionAdapter);
                        transaction.setLayoutManager(linearLayoutManager);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("email",currentUser.getEmail());
                return params;
            }
        };

        requestQueue.add(currentTransactionRequest);
    }

    private void generatePromoBanner(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        PromoAdapter promoAdapter = new PromoAdapter(getApplication());
        promotion.setAdapter(promoAdapter);
        promotion.setLayoutManager(linearLayoutManager);

    }


    @Override
    public void onNoteClick(int position) {
        Intent toDetail = new Intent(MainActivity.this, TransactionDetailPage.class);
        toDetail.putExtra("TransactionDetail",onProgressTransaction.get(position).getTransactionID());
        startActivity(toDetail);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == 101){
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");

            StringRequest checkLockerRequest = new StringRequest(Request.Method.POST, checkLocker, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(response.equals("Empty")){

                    }else {
                        Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
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
                    params.put("lockerID",result);
                    return params;
                }
            };

            requestQueue.add(checkLockerRequest);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}