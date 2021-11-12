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
import java.security.CryptoPrimitive;

public class RechargeActivity extends Activity {
    String card = null; //卡片ID
    Double CardSum = null; //卡片余额
    ModulesControl mModulesControl;
    SqlUtil sqlUtil;
    EditText card_sum; //卡片余额显示
    EditText canteen_recharge_edit; //充值金额
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
                        String newcard = data.getString("cardNo");
                        if(card ==null || !card.equals(newcard)){
                            card = newcard;
                            double sum = sqlUtil.getCardSUM(card);
                            if((Double)sum!=null){
                                CardSum = sum;
                                card_sum.setText(Double.toString(sum));
                            } else {
                                CardSum = null;
                                card_sum.setText("0.0");
                            }
                        }
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
        sqlUtil = SqlUtil.getInstance(this);
        mModulesControl = new ModulesControl(rfidHandler);
        mModulesControl.actionControl(true);
        Button back = findViewById(R.id.back_button);
        Button recharge = findViewById(R.id.recharge_button);
        Button activate = findViewById(R.id.activate_card);
        Button cancel = findViewById(R.id.cancel_card);
        card_sum = findViewById(R.id.card_sum);
        canteen_recharge_edit = findViewById(R.id.canteen_recharge_edit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RechargeActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card == null){
                    System.out.println("请放卡");
                }else if (CardSum == null){
                    System.out.println("请先开卡");
                } else{
                    double value = Double.parseDouble(canteen_recharge_edit.getText().toString());
                    if (value == 0.0){

                    } else{
                        double newvalue = value+CardSum;
                        rechargeCard(newvalue);
                        canteen_recharge_edit.setText("0.0");
                        card_sum.setText(Double.toString(newvalue));
                    }
                }
            }
        });
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card == null){
                    System.out.println("请放卡");
                }else if (CardSum != null){
                    System.out.println("已开卡，无需再开卡");
                } else{
                    //开卡
                    sqlUtil.insertCard(card);
                    CardSum = sqlUtil.getCardSUM(card);
                    card_sum.setText(Double.toString(CardSum));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card == null){
                    System.out.println("请放卡");
                }else if (CardSum == null){
                    System.out.println("没开卡，无法注销");
                } else{
                    //开卡
                    sqlUtil.deleteCard(card);
                    CardSum = null;
                    card_sum.setText("0.0");
                }
            }
        });

    }
    protected void setCardNUll(){
        card = null;
        CardSum = null;
        card_sum.setText("0.0");
    }
    protected void rechargeCard(double x){
        sqlUtil.updatesum(card, x);
    }
}
