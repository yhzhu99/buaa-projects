package com.topelec.buscard;

import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.topelec.rfidcontrol.ModulesControl;
import com.topelec.zigbeecontrol.Command;

import it.moondroid.coverflowdemo.R;
public class CardActivityGroup extends ActivityGroup {

    @SuppressWarnings("unused")
    private static final String TAG = ".CardActivityGroup";
    public static final String resume_action = "com.topelec.buscard.resume_action";
    public static final String recharge_action = "com.topelec.buscard.recharge_action";

    private FrameLayout bodyView;
    private LinearLayout resumeView, rechargeView;
//    private Button btnConsume;
//    private Button btnRecharge;
    private Button btnBack;
    private int flag = 0; // 通过标记跳转不同的页面，显示不同的菜单项

    ModulesControl mModulesControl;
    Intent intent;

    //for fullscreen
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

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

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

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
    /**
     * 用于更新rechargeUI
     */
    Handler uiHandler = new Handler() {
        //2.重写消息处理函数
        public void handleMessage(Message msg) {
            Bundle data;
            if (flag == 0) {//resume
                intent = new Intent(resume_action);
            }else if (flag == 1){
                intent = new Intent(recharge_action);
            }
//            intent = new Intent(recharge_action);
            switch (msg.what) {
                //判断发送的消息
                case Command.HF_TYPE:  //设置卡片类型TypeA返回结果  ,错误类型:1
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        intent.putExtra("what",1);
                        intent.putExtra("Result",getResources().getString(R.string.buscard_type_a_fail));
                        sendBroadcast(intent);
                    }
                    break;
                case  Command.HF_FREQ:  //射频控制（打开或者关闭）返回结果   ,错误类型:1
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        intent.putExtra("what",1);
                        if (data.getBoolean("Result")) {
                            intent.putExtra("Result",getResources().getString(R.string.buscard_frequency_open_fail));
                        }else {
                            intent.putExtra("Result",getResources().getString(R.string.buscard_frequency_close_fail));
                        }
                        sendBroadcast(intent);
                    }

                    break;
                case Command.HF_ACTIVE:       //激活卡片，寻卡，返回结果
                    data = msg.getData();
                    if (data.getBoolean("result")) {
//                        hfView.setText(R.string.active_card_succeed);
                    } else {
                        intent.putExtra("what",2);
                        sendBroadcast(intent);

                    }

                    break;
                case Command.HF_ID:      //防冲突（获取卡号）返回结果

                    data = msg.getData();
                    intent.putExtra("what",3);

                    if (data.getBoolean("result")) {
                        intent.putExtra("Result",data.getString("cardNo"));
                        sendBroadcast(intent);
                    } else {

                    }
//                    Log.v(TAG,"Result = "+ data.getString("cardNo"));

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_activity_group);

        mVisible = true;
        mControlsView = findViewById(R.id.bottomlist);
        mContentView = findViewById(R.id.body);
        mBackView = findViewById(R.id.buscard_back);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mModulesControl = new ModulesControl(uiHandler);
        mModulesControl.actionControl(true);
        initMainView();
        resumeView.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = 0;
                getWindow().getDecorView().setBackgroundResource(R.drawable.buscard_consume_background);
                showView(flag);
                resumeView.setBackgroundResource(R.drawable.frame_button_background);
                rechargeView.setBackgroundResource(R.drawable.frame_button_nopressbg);
                delayedHide(300);

            }
        });
        rechargeView.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = 1;
                getWindow().getDecorView().setBackgroundResource(R.drawable.buscard_recharge_background);
                showView(flag);
                rechargeView.setBackgroundResource(R.drawable.frame_button_background);
                resumeView.setBackgroundResource(R.drawable.frame_button_nopressbg);
                delayedHide(300);
            }
        });


        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }
    /*
	 * 初始化主界面底部的功能菜单;
	 */
    public void initMainView() {

        bodyView=(FrameLayout) findViewById(R.id.body);
        resumeView=(LinearLayout) findViewById(R.id.resume_radio);
        rechargeView=(LinearLayout) findViewById(R.id.recharge_radio);
        resumeView.setBackgroundResource(R.drawable.frame_button_background);
        rechargeView.setBackgroundResource(R.drawable.frame_button_nopressbg);
        initView();

    }


    private void initView() {
        bodyView.removeAllViews();
        this.getWindow().getDecorView().setBackgroundResource(R.drawable.buscard_consume_background);
        View v = getLocalActivityManager().startActivity("resume_radio",new Intent(CardActivityGroup.this,ResumeActivity.class)).getDecorView();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dmheight = dm.heightPixels;
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                dmheight - 55);
        v.setLayoutParams(param);
        bodyView.addView(v);
    }
    // 在主界面中显示其他界面
    public void showView(int flag) {
        switch (flag) {
            case 0:
                bodyView.removeAllViews();

                bodyView.addView(getLocalActivityManager().startActivity("resume_radio",
                        new Intent(CardActivityGroup.this, ResumeActivity.class)).getDecorView());
                break;
            case 1:
                bodyView.removeAllViews();
                bodyView.addView(getLocalActivityManager().startActivity("recharge_radio",
                        new Intent(CardActivityGroup.this, RechargeActivity.class)).getDecorView());
                break;

            default:
                break;
        }
    }

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

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 在活动销毁时调用
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModulesControl.actionControl(false);
        mModulesControl.closeSerialDevice();
    }
}
