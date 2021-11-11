package com.example.durian_canteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class OrderActivity extends Activity {
    private final int DISH_NUMBER = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_order);
        Button checkout = findViewById(R.id.recharge_page);
        System.out.println(checkout);
        ImageView bgImg = new ImageView(this);
        bgImg = (ImageView) findViewById(R.id.dish_bg);
        bgImg.setAlpha(0.5f);
        ImageView[] views = new ImageView[105];
        views[0] = (ImageView) findViewById(R.id.dish1);
        views[1] = (ImageView) findViewById(R.id.dish2);
        RadioButton[] radioButtons = new RadioButton[105];
        radioButtons[0] = (RadioButton) findViewById(R.id.radio_dish1);
        radioButtons[1] = (RadioButton) findViewById(R.id.radio_dish2);
        TextView[] dishTexts = new TextView[105];
        dishTexts[0] = (TextView) findViewById(R.id.text_dish1);
        dishTexts[1] = (TextView) findViewById(R.id.text_dish2);
        int dishMoney[] = new int[105];
        dishMoney[0] = 88;
        dishMoney[1] = 58;

        System.out.println("----------aaa--------"+dishTexts[0].getText().toString()+dishTexts[1].getText().toString());
        dishTexts[0].setText("牛排");
        System.out.println("----------bbb--------"+dishTexts[0].getText().toString()+dishTexts[1].getText().toString());
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click");
                Intent intent = new Intent(OrderActivity.this, RechargeActivity.class);
                startActivity(intent);
            }
        });

    }
}
