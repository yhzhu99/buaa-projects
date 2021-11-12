package com.example.durian_canteen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rfidcontrol.ModulesControl;
import com.example.zigbeecontrol.Command;

import java.lang.ref.WeakReference;

public class RechargeActivity extends Activity {
    String card = null;
    ModulesControl mModulesControl;
    SqlUtil sqlUtil;
    EditText card_account;
    EditText canteen_recharge_edit;
    double account_value;
    private static class RFIDHandler extends Handler {

        public RFIDHandler(RechargeActivity activity) {
            WeakReference<RechargeActivity> mActivity = new WeakReference<RechargeActivity>(activity);
        }
    }
    @SuppressLint("HandlerLeak")
    Handler rfidHandler = new RFIDHandler(this) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle data;
            switch (msg.what) {
                //判断发送的消息
                case Command.HF_TYPE:  //设置卡片类型TypeA返回结果  ,错误类型:1
                    data = msg.getData();
                    if (!data.getBoolean("result")) {
                        System.out.println(0);
                    }
                    break;
                case  Command.HF_FREQ:  //射频控制（打开或者关闭）返回结果   ,错误类型:1
                    data = msg.getData();
                    if (!data.getBoolean("result")) {
                        System.out.println(1);
                    }
                    break;
                case Command.HF_ACTIVE:       //激活卡片，寻卡，返回结果
                    // 没有识别到卡
                    setCardNUll();
                    break;
                case Command.HF_ID:      //防冲突（获取卡号）返回结果
                    data = msg.getData();
                    if (data.getBoolean("result")) {
                        card = data.getString("cardNo");
                        card_account.setText(card);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_recharge);
        Button cancel = findViewById(R.id.back_button);
        Button recharge = findViewById(R.id.recharge_button);
        card_account = findViewById(R.id.card_account);
        canteen_recharge_edit = findViewById(R.id.canteen_recharge_edit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RechargeActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double value = Double.parseDouble(canteen_recharge_edit.getText().toString());
                rechargeCard(value);
                canteen_recharge_edit.setText(Double.toString(account_value));
            }
        });
        sqlUtil = SqlUtil.getInstance(this);
        mModulesControl = new ModulesControl(rfidHandler);
        mModulesControl.actionControl(true);
    }
    protected void setCardNUll(){
        card = null;
        card_account.setText("0.0");
    }
    protected void rechargeCard(double x){
        account_value+=x;
//        sqlUtil.updateaccount(account_value);
    }
}
