package com.topelec.smartagriculture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.topelec.zigbeecontrol.Command;
import com.topelec.zigbeecontrol.SensorControl;

import it.moondroid.coverflowdemo.R;


public class MainActivity extends Activity implements
        View.OnClickListener, SensorControl.MotorListener, SensorControl.TempHumListener {

    private static final String TAG = "MainActivity";
    private static final int REQ_SYSTEM_SETTINGS = 1;

    private int Temp;
    private int Hum;

    private int fanStatus;//0——停止；1——风扇；2——浇水

    private boolean isAuto;
    private String toControl;//1——温度；2——湿度
    private int settingTemperature;
    private int settingHumidity;

    private TextView tempView;
    private TextView humView;
    private ImageView fanView;
    private ImageView waterView;
    private ImageButton btnFanLeft;
    private ImageButton btnFanRight;
    private ImageButton btnWater;

    private Button btnSettings;
    private Button btnBack;

    SensorControl mSensorControl;

    Handler myHandler = new Handler() {
        //2.重写消息处理函数
        public void handleMessage(Message msg) {
            Bundle data;
            data = msg.getData();
            switch (msg.what) {
                //判断发送的消息
                case 0x02:
                    switch (data.getByte("motor_status")) {
                        case 0x01:
                            fanView.setImageDrawable(getResources().getDrawable(R.drawable.smartagriculture_fan_on));
                            waterView.setImageDrawable(null);
                            fanStatus = 1;
                            break;
                        case 0x02:
                            fanView.setImageDrawable(null);
                            waterView.setImageDrawable(getResources().getDrawable(R.drawable.smartagriculture_water_on));
                            fanStatus = 2;
                            break;
                        case 0x00:
                            fanView.setImageDrawable(null);
                            waterView.setImageDrawable(null);
                            fanStatus = 0;
                            break;
                        default:
                            break;
                    }
                    break;
                case 0x03:
                    switch (data.getByte("senser_id")) {
                        case 0x01:
                            Temp = data.getInt("senser_data");
                            tempView.setText(String.valueOf(Temp));

                            //如下温度自动化管理代码
                            if (isAuto && toControl.equals("1")) {
                                if (Temp > settingTemperature) {
                                    //TODO 温度大于设定值，降低温度，执行打开风扇动作
                                    if (fanStatus != 1)
                                        mSensorControl.fanForward(true);
                                } else {
                                    //TODO 实时温度小于设定值，停止降低温度，如果此时风扇是运行状态，则执行停止风扇动作。
                                    if (fanStatus != 0)
                                        mSensorControl.fanStop(true);
                                }
                            }
                            break;
                        case 0x02:
                            Hum = data.getInt("senser_data");
                            humView.setText(String.valueOf(Hum));

                            if (isAuto && toControl.equals("2")) {
                                if (Hum < settingHumidity) {
                                    //TODO 湿度小于设定值，土壤湿度小，执行浇水（风扇倒转）动作
                                    if (fanStatus != 2)
                                        mSensorControl.fanBackward(true);
                                } else {
                                    //TODO 实时湿度大于设定值，土壤湿度足够，停止灌溉。
                                    if (fanStatus != 0)
                                        mSensorControl.fanStop(true);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //定义发送任务定时器
    Handler timerHandler = new Handler();
    Runnable sendRunnable = new Runnable() {
        int i = 1;

        @Override
        public void run() {
            //TODO:查询温度湿度
            switch (i) {
                case 1:
                    mSensorControl.checkTemperature(true);
                    i++;
                    break;
                case 2:
                    mSensorControl.checkHumidity(true);
                    i = 1;
                    break;
                default:
                    break;
            }
            timerHandler.postDelayed(this, Command.CHECK_SENSOR_DELAY);
        }
    };

    private static final boolean AUTO_HIDE = true;
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private final Handler mHideHandler = new Handler();

    //UI全屏显示
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };


    //显示程序中的底部控制按钮
    private View mControlsView;
    private View mBackView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            mControlsView.setVisibility(View.VISIBLE);
            mBackView.setVisibility(View.VISIBLE);
        }
    };


    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartagriculture);

        initialization();
        getSettings();

        mVisible = true;
        mControlsView = findViewById(R.id.smartagriculture_settings);
        mContentView = findViewById(R.id.smartagriculture_content);
        mBackView = findViewById(R.id.smartagriculture_back);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mSensorControl = new SensorControl();
        mSensorControl.addMotorListener(this);
        mSensorControl.addTempHumListener(this);

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
    }

    private void hide() {

        mControlsView.setVisibility(View.GONE);
        mBackView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void initialization() {
        Temp = 101;
        Hum = 101;

        fanStatus = 0;

        tempView = (TextView) findViewById(R.id.tempView);
        humView = (TextView) findViewById(R.id.humView);

        fanView = (ImageView) findViewById(R.id.fanView);
        waterView = (ImageView) findViewById(R.id.waterView);
        btnFanLeft = (ImageButton) findViewById(R.id.btnFanLeft);
        btnFanLeft.setOnClickListener(this);
        btnFanRight = (ImageButton) findViewById(R.id.btnFanRight);
        btnFanRight.setOnClickListener(this);
        btnWater = (ImageButton) findViewById(R.id.btnWater);
        btnWater.setOnClickListener(this);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnFanLeft:
            case R.id.btnFanRight:
                if (fanStatus == 1) {
                    //TODO 发送停止指令
                    mSensorControl.fanStop(false);

                }else {
                    //TODO 发送正传指令
                    mSensorControl.fanForward(false);
                }
                break;
            case R.id.btnWater:
                if (fanStatus == 2) {
                    //TODO 发送停止指令
                    mSensorControl.fanStop(false);
                }else {
                    //TODO 发送反转指令
                    mSensorControl.fanBackward(false);
                }
                break;
            case R.id.btnSettings:
                startActivityForResult(new Intent(this, SmartAgricultureSettings.class), REQ_SYSTEM_SETTINGS);
                break;
            case R.id.btnBack:
                finish();
                break;
            default:
                break;
        }
    }

    private void getSettings() {

        PreferenceManager.setDefaultValues(this, R.xml.settings_smartagriculture, false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        isAuto = settings.getBoolean("auto_switch", false);
        toControl = settings.getString("setting_list", "2");
        settingTemperature = Integer.parseInt(settings.getString("temp_settings", "27"));
        settingHumidity = Integer.parseInt(settings.getString("hum_settings", "40"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_SYSTEM_SETTINGS) {

            getSettings();

        }

    }

    /**
     * 由不可见变为可见时调用
     */
    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mSensorControl.actionControl(true);

        //TODO:每350ms发送一次数据
        timerHandler.postDelayed(sendRunnable, Command.CHECK_SENSOR_DELAY);

    }


    /**
     * 在完全不可见时调用
     */
    @Override
    protected void onStop() {

        super.onStop();

        timerHandler.removeCallbacks(sendRunnable);
        mSensorControl.actionControl(false);
    }

    /**
     * 在活动销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorControl.removeMotorListener(this);
        mSensorControl.removeTempHumListener(this);
        mSensorControl.closeSerialDevice();
    }

    @Override
    public void motorControlResult(byte motor_status) {

        Message msg = new Message();
        msg.what = 0x02;
        Bundle data = new Bundle();
        data.putByte("motor_status", motor_status);
        msg.setData(data);
        myHandler.sendMessage(msg);
    }

    @Override
    public void tempHumReceive(byte senser_id, int senser_data) {

        Message msg = new Message();
        msg.what = 0x03;
        Bundle data = new Bundle();
        data.putByte("senser_id", senser_id);
        data.putInt("senser_data", senser_data);
        Log.d(TAG,"sensor_id = " + senser_id);
        Log.d(TAG,"sensor_data = "+senser_data);
        msg.setData(data);
        myHandler.sendMessage(msg);
    }

}
