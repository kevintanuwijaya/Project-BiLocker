package com.bilocker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bilocker.R;
import com.bilocker.model.User;
import com.bilocker.utils.Convert;

public class CheckOutActivity extends AppCompatActivity {

    private AppCompatRadioButton biMoneyRadio;
    private ImageView backBtn;
    private Button payBtn;
    private int money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        biMoneyRadio = findViewById(R.id.checkout_bimoney_button);
        payBtn = findViewById(R.id.checkout_pay_btn);
        backBtn = findViewById(R.id.checkout_back_btn);

        money = getIntent().getIntExtra("MONEY",0);
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
                
            }
        });
    }
}