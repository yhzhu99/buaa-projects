package com.topelec.smarthomewall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import it.moondroid.coverflowdemo.R;

public class SmarthomeWallActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageView kichenWindow;
    private ImageView kichenCurtain;
    private ImageView fireWarning;
    private TextView  eMeter;
    private ImageView bedroomWindow;
    private ImageView bedroomCurtain;
    private ImageView bedroomLight;
    private ImageView airconStatus;
    private ImageView dopplerStatus;
    private TextView tempView;
    private TextView humView;
    private TextView pm25View;

    private Button btnLedControl;
    private Button btnAirconControl;
    private Button btnCurtainsControl;
    private Button btnRemoteControl;
    private Button btnSystemSettings;

    private Button btnCamera;

    private View ledControlView;
    private SeekBar ledControlBar;
    private ImageButton btnLenControlCancle;

    private View airconControlView;
    private Button btnAircon;
    private ImageButton btnAirconControlCancle;

    private View curtainControlView;
    private Button btnCurtainClose;
    private Button btnCurtainOpen;
    private Button btnCurtainStop;
    private ImageButton btnCurtainControlCancle;

    //lock page
    private View lockPageView;
    private ImageView lockPage;
    private boolean isLocked = true;

    private static final boolean AUTO_HIDE = true;
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private final Handler mHideHandler = new Handler();

    //UI全屏显示
    private View mContentView;

    //显示程序中的底部控制按钮
    private View mControlsView;

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

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

    private boolean isViewVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smarthome_wall);

        mVisible = true;
        mControlsView = findViewById(R.id.smarthomewall_controls);
        mContentView = findViewById(R.id.smarthomewall_content);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewVisible)
                    return;
                toggle();
            }
        });
        kichenWindow = (ImageView) findViewById(R.id.kichen_window);
        kichenCurtain = (ImageView) findViewById(R.id.kichen_curtain);
        fireWarning = (ImageView) findViewById(R.id.fire_warning);
        eMeter = (TextView) findViewById(R.id.e_meter);
        bedroomWindow = (ImageView) findViewById(R.id.bedroom_window);
        bedroomCurtain = (ImageView) findViewById(R.id.bedroom_curtain);
        bedroomLight = (ImageView) findViewById(R.id.bedroom_light);
        bedroomLight.getDrawable().setAlpha(0);
        airconStatus = (ImageView) findViewById(R.id.air_con_status);
        dopplerStatus = (ImageView) findViewById(R.id.doppler_status);
        tempView = (TextView) findViewById(R.id.tempView);
        humView = (TextView) findViewById(R.id.humView);
        pm25View = (TextView) findViewById(R.id.pm25View);

        btnLedControl = (Button) findViewById(R.id.led_control);
        btnLedControl.setOnClickListener(this);
        btnAirconControl = (Button) findViewById(R.id.air_con_control);
        btnAirconControl.setOnClickListener(this);
        btnCurtainsControl = (Button) findViewById(R.id.curtains_control);
        btnCurtainsControl.setOnClickListener(this);
        btnRemoteControl = (Button) findViewById(R.id.remote_control);
        btnRemoteControl.setOnClickListener(this);
        btnSystemSettings = (Button) findViewById(R.id.system_settings);
        btnSystemSettings.setOnClickListener(this);

        btnCamera = (Button) findViewById(R.id.camera_button);
        btnCamera.setOnClickListener(this);

        ledControlView = findViewById(R.id.led_control_view);
        ledControlBar = (SeekBar) findViewById(R.id.led_control_bar);
        ledControlBar.setOnSeekBarChangeListener(this);
        btnLenControlCancle = (ImageButton) findViewById(R.id.led_view_cancle);
        btnLenControlCancle.setOnClickListener(this);

        airconControlView = findViewById(R.id.air_con_view);
        btnAircon = (Button) findViewById(R.id.air_con_button);
        btnAircon.setOnClickListener(this);
        btnAirconControlCancle = (ImageButton) findViewById(R.id.air_con_view_cancle);
        btnAirconControlCancle.setOnClickListener(this);

        curtainControlView = findViewById(R.id.curtains_control_view);
        btnCurtainClose = (Button)findViewById(R.id.curtain_close_button);
        btnCurtainClose.setOnClickListener(this);
        btnCurtainOpen = (Button)findViewById(R.id.curtain_open_button);
        btnCurtainOpen.setOnClickListener(this);
        btnCurtainStop = (Button)findViewById(R.id.curtain_stop_button);
        btnCurtainStop.setOnClickListener(this);
        btnCurtainControlCancle = (ImageButton) findViewById(R.id.curtains_view_cancle);
        btnCurtainControlCancle.setOnClickListener(this);

        lockPageView = findViewById(R.id.smarthomewall_lockpage);
        lockPage = (ImageView) findViewById(R.id.locked_page);
        lockPage.setOnClickListener(this);
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
        mVisible = false;
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

    private final Runnable mLockHintRunnable = new Runnable() {
        @Override
        public void run() {

            //lockPage.setVisibility(View.GONE);
            lockPage.setImageDrawable(null);
        }
    };
    private void showLockHintMessage() {
        if (isLocked = true) {
            lockPage.setImageDrawable(getResources().getDrawable(R.drawable.smarthomewall_hint_message));
            mHideHandler.postDelayed(mLockHintRunnable,AUTO_HIDE_DELAY_MILLIS);
        }
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.led_control:
                ledControlView.setVisibility(View.VISIBLE);
                isViewVisible = true;
                hide();
                break;
            case R.id.air_con_control:
                airconControlView.setVisibility(View.VISIBLE);
                isViewVisible = true;
                hide();
                break;
            case R.id.curtains_control:
                curtainControlView.setVisibility(View.VISIBLE);
                isViewVisible = true;
                hide();
                break;
            case R.id.remote_control:
                hide();
                break;
            case R.id.system_settings:
                Intent intent = new Intent(SmarthomeWallActivity.this,SettingsSmarthomewall.class);
                startActivityForResult(intent,1);
                hide();
                break;
            case R.id.camera_button:
                break;
            case R.id.led_view_cancle:
                ledControlView.setVisibility(View.GONE);
                isViewVisible = false;
                break;
            case R.id.air_con_button:

                break;
            case R.id.air_con_view_cancle:
                airconControlView.setVisibility(View.GONE);
                isViewVisible = false;
                break;
            case R.id.curtain_close_button:
                break;
            case R.id.curtain_open_button:
                break;
            case R.id.curtain_stop_button:
                break;
            case R.id.curtains_view_cancle:
                curtainControlView.setVisibility(View.GONE);
                isViewVisible = false;
                break;
            case R.id.locked_page:
                showLockHintMessage();
                break;
            default:
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        bedroomLight.getDrawable().setAlpha(progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
