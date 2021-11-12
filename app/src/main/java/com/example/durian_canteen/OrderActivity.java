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
import android.widget.ImageView;

import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.CompoundButton;
import android.view.View;


import com.example.rfidcontrol.ModulesControl;
import com.example.zigbeecontrol.Command;
import com.example.zigbeecontrol.SensorControl;

import java.lang.ref.WeakReference;

public class OrderActivity extends Activity extends SensorControl.LedListener{

    private boolean isLed1On;
    SensorControl mSensorControl;

    private final int DISH_NUMBER = 2;
    public int sumMoney = 0;
    String card = null; //卡片ID
    Double CardSum = null; //卡片余额
    ModulesControl mModulesControl;
    SqlUtil sqlUtil;
    // EditText card_sum; //卡片余额显示
    TextView showMsg;
    int count = 2; // 控制延时
    private static class RFIDHandler extends Handler {
        public RFIDHandler(OrderActivity activity) {
            WeakReference<OrderActivity> mActivity = new WeakReference<OrderActivity>(activity);
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
                    System.out.println("TYPE");
                    if (!data.getBoolean("result")) {
                        System.out.println(0);
                    }
                    break;
                case  Command.HF_FREQ:  //射频控制（打开或者关闭）返回结果   ,错误类型:1
                    data = msg.getData();
                    System.out.println("FREQ");
                    if (!data.getBoolean("result")) {
                        System.out.println(1);
                    }
                    break;
                case Command.HF_ACTIVE:       //激活卡片，寻卡，返回结果
                    // 没有识别到卡
                    System.out.println(count);
                    count +=1;
                    if(count>2){
                        setCardNUll();
                        showMsg.setText("当前没有识别到卡");
                    }
                    break;
                case Command.HF_ID:      //防冲突（获取卡号）返回结果
                    data = msg.getData();

                    System.out.println("ID");
                    if (data.getBoolean("result")) {
                        String newcard = data.getString("cardNo");
                        count = 0;
                        if(card == null){
                            card = newcard;
                            System.out.println("6666---------"+card);
                            Double sum = sqlUtil.getCardSUM(card);
                            if((Double)sum!=null){
                                CardSum = sum;
                                // card_sum.setText(Double.toString(sum));
                                showMsg.setText("当前余额为："+Double.toString(sum)+"（元）");
                            } else {
                                CardSum = null;
                                // card_sum.setText("0.0");
                                showMsg.setText("卡还没有被注册");
                            }
                        } else if (!card.equals(newcard)){
                            card = newcard;

                            Double sum = sqlUtil.getCardSUM(card);
                            if((Double)sum!=null){
                                CardSum = sum;
                                // card_sum.setText(Double.toString(sum));
                                showMsg.setText("当前余额为："+Double.toString(sum)+"（元）");
                            } else {
                                CardSum = null;
                                // card_sum.setText("0.0");
                                showMsg.setText("卡还没有被注册");
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

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data;
            data = msg.getData();

            System.out.println(data);
            // switch (msg.what) {

            // }
            super.handleMessage(msg);
        }

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqlUtil = SqlUtil.getInstance(this);
        mModulesControl = new ModulesControl(rfidHandler);
        mModulesControl.actionControl(true);

        setContentView(R.layout.activity_canteen_order);
        Button checkout = findViewById(R.id.recharge_page); // 充值
        Button consume = (Button) findViewById(R.id.consume);
        Button show_led = (Button) findViewById(R.id.show_led);
        // System.out.println(checkout);
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

        showMsg = (TextView) findViewById(R.id.display_msg);
        chooseButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    System.out.println("1 Checked");
                    sumMoney+=88;

                } else {
                    System.out.println("1 Un-Checked");
                    sumMoney-=88;
                }
                finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
            }
        });

        chooseButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    System.out.println("2 Checked");
                    sumMoney+=58;
                } else {
                    System.out.println("2 Un-Checked");
                    sumMoney-=58;
                }
                finalTotalMoneyText.setText("总价："+String.valueOf(sumMoney)+"（元）");
            }
        });

        consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("consume");
                 int cardSumTmp = CardSum.intValue();
                if (sumMoney > cardSumTmp){
                    System.out.println("余额不足");
                    showMsg.setText("余额不足");
                }else{
                    consumeCard(sumMoney);
                    Double sum = sqlUtil.getCardSUM(card);
                    showMsg.setText("已成功支付，当前余额为："+String.valueOf(sum)+"（元）");
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
        
        show_led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("led");
                mSensorControl.led1_off(false);
               
            }
        });

        // 一下 zigbee
        mSensorControl = new SensorControl();
        mSensorControl.addLedListener(this);

    }
    protected void setCardNUll(){
        card = null;
        CardSum = null;
        // card_sum.setText("0.0");
    }
    protected void consumeCard(double x){
        sqlUtil.updatesum(card, -x);
    }

}
