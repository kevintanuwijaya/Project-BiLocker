package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.bilocker.model.Location;
import com.bilocker.model.Transaction;
import com.bilocker.model.User;
import com.bilocker.utils.Convert;
import com.bilocker.utils.CurrentUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private RecyclerView transaction,history;
    private TextView userName,userBalance;
    private SlidingUpPanelLayout profileSlider;
    private ImageView logout;
    private LinearLayout historyPanel;
    private ConstraintLayout noTransactionPanel;

    private static final String historyUrl = "https://bilocker.000webhostapp.com/BiLocker/History.php";
    private static final String currentTransaction = "https://bilocker.000webhostapp.com/BiLocker/CurrentTransaction.php";

    private static final String LOG = MainActivity.class.getSimpleName();


    private Vector<Transaction> historyTransaction = new Vector<>();
    private Vector<Transaction> onProgressTransaction = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transaction = findViewById(R.id.main_transaction);
        history = findViewById(R.id.main_history);
        userName = findViewById(R.id.main_user_name);
        userBalance = findViewById(R.id.main_user_balance);
        profileSlider = findViewById(R.id.main_profile_slider);
        logout = findViewById(R.id.main_logout);
        historyPanel = findViewById(R.id.main_history_panel);
        noTransactionPanel = findViewById(R.id.main_no_transaction);


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
                params.put("email","kevin.tanuwijaya@binus.ac.id");
                return params;
            }
        };

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

                        OnProgessTransactionAdapter navigationAdapter = new OnProgessTransactionAdapter(getApplication(),onProgressTransaction);
                        transaction.setAdapter(navigationAdapter);
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
                params.put("email","kevin.tanuwijaya@binus.ac.id");
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(currentTransactionRequest);
        requestQueue.add(historyRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();

//        User currentUser = CurrentUser.getInstance().getCurrUser();
//
//        userName.setText(currentUser.getName());
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




    }
}