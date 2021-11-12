package com.example.durian_canteen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import androidx.annotation.Nullable;
import android.widget.CompoundButton;

public class OrderActivity extends Activity {
    private final int DISH_NUMBER = 2;
    public int sumMoney = 0;
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
        CheckBox[] chooseButtons = new CheckBox[105];
        chooseButtons[0] = (CheckBox) findViewById(R.id.choose_dish1);
        chooseButtons[1] = (CheckBox) findViewById(R.id.choose_dish2);

        TextView[] dishTexts = new TextView[105];
        dishTexts[0] = (TextView) findViewById(R.id.text_dish1);
        dishTexts[1] = (TextView) findViewById(R.id.text_dish2);
        int dishMoney[] = new int[105];
        dishMoney[0] = 88;
        dishMoney[1] = 58;

        TextView totalMoneyText = new TextView(this);
        totalMoneyText = (TextView) findViewById(R.id.display_total);
        totalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
        System.out.println("----------aaa--------"+dishTexts[0].getText().toString()+dishTexts[1].getText().toString());
        // dishTexts[0].setText("牛排");
        System.out.println("----------bbb--------"+dishTexts[0].getText().toString()+dishTexts[1].getText().toString());

        TextView finalTotalMoneyText = totalMoneyText;
        chooseButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    System.out.println("1 Checked");
                    sumMoney+=88;
                    finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
                } else {
                    System.out.println("1 Un-Checked");
                    sumMoney-=88;
                    finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
                }
            }
        });

        chooseButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    System.out.println("2 Checked");
                    sumMoney+=58;
                    finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
                } else {
                    System.out.println("2 Un-Checked");
                    sumMoney-=58;
                    finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
                }
            }
        });

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
