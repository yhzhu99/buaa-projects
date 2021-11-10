package com.topelec.sensortest;

import android.annotation.SuppressLint;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.topelec.zigbeecontrol.Command;
import com.topelec.zigbeecontrol.SensorControl;

import it.moondroid.coverflowdemo.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class zigbeetest extends AppCompatActivity implements
        SensorControl.InfraRedSensorListener,SensorControl.HallSensorListener,SensorControl.SmokeSensorListener,
        SensorControl.DimmableLedListener,SensorControl.ShakeSensorListener,SensorControl.AccelerationSensorListener,SensorControl.DopplerSensorListener,
        SeekBar.OnSeekBarChangeListener,SensorControl.PESensorListener{

    private TextView InfraRedView,HallView,SmokeView,ShakeView,X_View,Y_View,Z_View,DopplerView;
    private SeekBar DimmableLedBar;
    private SensorControl mSensorControl;


    Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data;
            data = msg.getData();
            switch (msg.what) {
                case 0x05: //红外传感器
                    if (data.getByte("sensor_status") == 0x01) {
                        InfraRedView.setText("被阻断");
                    }else {
                        InfraRedView.setText("正常");
                    }
                    break;
                case 0x06: //霍尔传感器
                    if (data.getByte("sensor_status") == 0x01) {
                        HallView.setText("有磁场");
                    } else {
                        HallView.setText("正常");
                    }
                    break;
                case 0x07:  //烟雾传感器
                    if (data.getByte("sensor_status") == 0x01) {
                        SmokeView.setText("检测到有害气体");
                    } else {
                        SmokeView.setText("正常");
                    }
                    break;
                case 0x08:  //LED可调光

                    break;
                case 0x09:  //震动传感器
                    if (data.getByte("sensor_status") == 0x01) {
                        ShakeView.setText("有震动");
                    }else {
                        ShakeView.setText("正常");
                    }
                    break;
                case 0x0A:  //三轴加速度传感器
                    switch (data.getByte("sensor_id")) {
                        case 0x01:
                            X_View.setText(String.valueOf(data.getInt("sensor_data")));
                            break;
                        case 0x02:
                            Y_View.setText(String.valueOf(data.getInt("sensor_data")));
                            break;
                        case 0x03:
                            Z_View.setText(String.valueOf(data.getInt("sensor_data")));
                            break;
                        default:
                            break;
                    }
                    break;
                case 0x0B:  //多普勒传感器
                    if (data.getByte("sensor_status") == 0x01) {
                        DopplerView.setText("有波动");
                    } else {
                        DopplerView.setText("正常");
                    }

                    break;
                case Command.PE_SENSOR:
                    if (data.getByte("sensor_status") == 0x01) {
                        InfraRedView.setText("正常");
                    }else {
                        InfraRedView.setText("有遮挡");
                    }
                default:
                    break;
            }
        }
    };

    //定义发送任务定时器
    Handler timerHandler = new Handler();
    Runnable sendRunnable = new Runnable() {
        int i = 1;
        @Override
        public void run() {
            //TODO:查询传感器状态
            switch (i) {
                case 1:
//                    mSensorControl.checkIR(true);
//                    i++;
//                    break;
//                case 2:
//                    mSensorControl.checkHall(true);
//                    i++;
//                    break;
//                case 3:
//                    mSensorControl.checkSmoke(true);
//                    i++;
//                    break;
//                case 4:
//                    mSensorControl.checkShake(true);
//                    i++;
//                    break;
//                case 5:
//                    mSensorControl.checkX(true);
//                    i++;
//                    break;
//                case 6:
//                    mSensorControl.checkY(true);
//                    i++;
//                    break;
//                case 7:
//                    mSensorControl.checkZ(true);
//                    i++;
//                    break;
//                case 8:
//                    mSensorControl.checkDoppler(true);
//                    i++;
//                    break;
//                case 9:
                    mSensorControl.checkPE(true);
                    i = 1;
                default:
                    break;
            }
            timerHandler.postDelayed(this, Command.CHECK_SENSOR_DELAY);
        }
    };

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            //全屏显示
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_zigbeetest);
        InfraRedView = (TextView) findViewById(R.id.infra_red_status);
        HallView = (TextView) findViewById(R.id.hall_status);
        SmokeView = (TextView) findViewById(R.id.smoke_status);
        ShakeView = (TextView) findViewById(R.id.shake_status);
        X_View = (TextView)findViewById(R.id.x_ad);
        Y_View = (TextView) findViewById(R.id.y_ad);
        Z_View = (TextView) findViewById(R.id.z_ad);
        DopplerView = (TextView) findViewById(R.id.doppler_status);

        DimmableLedBar = (SeekBar) findViewById(R.id.seekBar);
        DimmableLedBar.setOnSeekBarChangeListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        //全屏显示
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.btnBack).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });

        mSensorControl = new SensorControl();
        mSensorControl.addInfraRedSensorLinstener(this);
        mSensorControl.addHallSensorListener(this);
        mSensorControl.addSmokeSensorListener(this);
        mSensorControl.addDimmableLedListener(this);
        mSensorControl.addShakeSensorListener(this);
        mSensorControl.addAccelerationSensorListener(this);
        mSensorControl.addDopplerSensorListener(this);
        mSensorControl.addPESensorListener(this);
    }

    //onPause onStop onDestory onCreate onStart onPostCreate onResume onPostResume
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

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
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
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


    public void onProgressChanged(SeekBar seekBar,int progress, boolean fromTouch) {

    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        mSensorControl.steplessLedControl((byte)seekBar.getProgress(),false);
    }


    @Override
    protected void onStart(){
        super.onStart();

        delayedHide(100);

        mSensorControl.actionControl(true);
        //TODO:每CHECK_SENSOR_DELAYms发送一次数据
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSensorControl.removeInfraRedSensorListener(this);
        mSensorControl.removeHallSensorListener(this);
        mSensorControl.removeSmokeSensorListener(this);
        mSensorControl.removeDimmableLedListener(this);
        mSensorControl.removeShakeSensorListener(this);
        mSensorControl.removeAccelerationSensorListener(this);
        mSensorControl.removeDopplerSensorListener(this);
        mSensorControl.removePESensorListener(this);
        mSensorControl.closeSerialDevice();
    }
    @Override
    public void accelerationSensorReceive(byte sensor_id, int sensor_data) {

        Message msg = new Message();
        msg.what = 0x0A;
        Bundle data = new Bundle();
        data.putByte("sensor_id",sensor_id);
        data.putInt("sensor_data",sensor_data);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void dimmableLedReceive(byte sensor_status) {

        Message msg = new Message();
        msg.what = 0x08;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void dopplerSensorReceive(byte sensor_status) {

        Message msg  = new Message();
        msg.what = 0x0B;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void hallSensorReceive(byte sensor_status) {

        Message msg = new Message();
        msg.what = 0x06;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void infraRedSensorReceive(byte sensor_status) {

        Message msg = new Message();
        msg.what = 0x05;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void shakeSensorReceive(byte sensor_status) {

        Message msg = new Message();
        msg.what = 0x09;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void smokeSensorReceive(byte sensor_status) {

        Message msg = new Message();
        msg.what = 0x07;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    @Override
    public void peSensorReceive(byte sensor_status) {
        Message msg = new Message();
        msg.what = Command.PE_SENSOR;
        Bundle data = new Bundle();
        data.putByte("sensor_status",sensor_status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }
}
