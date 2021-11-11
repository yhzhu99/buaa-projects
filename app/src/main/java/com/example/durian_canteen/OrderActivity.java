package com.example.durian_canteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
