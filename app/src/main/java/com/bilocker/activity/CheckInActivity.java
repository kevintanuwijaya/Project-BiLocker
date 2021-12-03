package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.bilocker.adapter.ItemCategoryAdapter;
import com.bilocker.model.ItemCategory;
import com.bilocker.model.Location;
import com.bilocker.model.Transaction;
import com.bilocker.utils.Convert;
import com.bilocker.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CheckInActivity extends AppCompatActivity {

    private AutoCompleteTextView categoryTextView;
    private Vector<ItemCategory> categories;
    private ImageView backBtn;
    private Button confirmButton;
    private String lockerID;

    private static final String getCategoriesURL = "https://bilocker.000webhostapp.com/BiLocker/GetItemCategories.php";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        categories = new Vector<ItemCategory>();

        categoryTextView = findViewById(R.id.category_autocompleteTextView);
        requestQueue = Volley.newRequestQueue(CheckInActivity.this);
        backBtn = findViewById(R.id.checkin_back_btn);
        confirmButton = findViewById(R.id.checkin_confirm_btn);

        lockerID = getIntent().getStringExtra("LOCKERID");
    }

    @Override
    protected void onStart() {
        super.onStart();

        initCategories();
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

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toConfirm = new Intent(CheckInActivity.this, CheckInConfirmActivity.class);
                toConfirm.putExtra("LOCKERID",lockerID);
                startActivity(toConfirm);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        categories.removeAll(categories);
    }

    private void initCategories(){


        StringRequest checkLockerRequest = new StringRequest(Request.Method.POST, getCategoriesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int[] images = {R.drawable.shield, R.drawable.folder, R.drawable.helmet, R.drawable.food};
                        JSONArray datas = jsonObject.getJSONArray("Data");

                        for (int i=0 ; i<datas.length(); i++){

                            JSONObject object = datas.getJSONObject(i);

                            ItemCategory itemCategory = new ItemCategory(images[i],object.getString("ItemTypeName"));

                            categories.add(itemCategory);
                        }


                        ItemCategoryAdapter adapter = new ItemCategoryAdapter(getApplicationContext(),R.layout.item_type_items,categories);
                        categoryTextView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(checkLockerRequest);

    }


}