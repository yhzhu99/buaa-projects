package com.topelec.foodtraceability;

//import android.app.Activity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.topelec.rfidcontrol.ModulesControl;
import com.topelec.zigbeecontrol.Command;

import it.moondroid.coverflowdemo.R;


public class QueryActivity extends Activity {

    private static final String TAG = ".QueryActivity";
    private static final int REQ_SYSTEM_SETTINGS = 1;

    private ImageView backgroundView;
    private ImageView imageView;
    private TextView idView;
    private TextView nameView;
    private TextView timeView;
    private TextView placeView;
    private TextView materialView;
    private Button btnSettings;
    private Button btnBack;

    private String CurrentId = new String();

    private String AuthorID = new String();
    private String AuthorName = new String();
    private String AuthorTime = new String();
    private String AuthorPlace = new String();
    private String AuthorMaterial = new String();

    /**
     *更新UI
     */
    Handler uiHandler = new Handler() {

        public void handleMessage(Message msg) {
            Bundle data;

            switch (msg.what) {

                case Command.HF_TYPE:  //设置类型typyA
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        showMsgPage(R.drawable.foodtraceability_check_wrong,getResources().getString(R.string.foodtraceability_type_a_fail) ,"","","","");
                    }
                    break;
                case Command.HF_FREQ:  //射频控制
                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        showMsgPage(R.drawable.foodtraceability_check_wrong,getResources().getString(R.string.foodtraceability_frequency_fail) ,"","","","");
                    }

                    break;
                case Command.HF_ACTIVE:       //激活卡片,寻卡

                    data = msg.getData();
                    if (data.getBoolean("result") == false) {
                        //showMsgPage(R.drawable.foodtraceability_standby,"" ,"","","","");
                        hideMsgPage();
                        CurrentId = "";
                    }
                    break;
                case Command.HF_ID:      //防冲突，获取卡号

                    data = msg.getData();
                    if (data.getBoolean("result")) {

                        CurrentId = data.getString("cardNo");
                        Log.d(TAG,"CardNo = "+ CurrentId);
                        if (CurrentId.equals(AuthorID)){
                            showMsgPage(R.drawable.foodtraceability_image_peanut_butter,CurrentId ,AuthorName,AuthorTime,AuthorPlace,AuthorMaterial);
                        } else {
                            showMsgPage(R.drawable.foodtraceability_check_wrong, CurrentId,"","","","");
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
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    ModulesControl mModulesControl;

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
        setContentView(R.layout.activity_query);

        backgroundView = (ImageView) findViewById(R.id.backgroundView);
        imageView = (ImageView) findViewById(R.id.imageView);
        idView = (TextView) findViewById(R.id.idView);
        nameView = (TextView)findViewById(R.id.nameView);
        timeView = (TextView) findViewById(R.id.timeView);
        placeView = (TextView) findViewById(R.id.placeView);
        materialView = (TextView) findViewById(R.id.materialView);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QueryActivity.this,SettingsActivity.class);
                intent.putExtra("CurrentId",CurrentId);

                startActivityForResult(intent,REQ_SYSTEM_SETTINGS);
            }
        });
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mVisible = true;
        mControlsView = findViewById(R.id.foodtraceability_settings);
        mContentView = findViewById(R.id.foodtraceability_content);
        mBackView = findViewById(R.id.foodtraceability_back);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mModulesControl = new ModulesControl(uiHandler);
        initSettings();
        mModulesControl.actionControl(true);

    }

    //隐藏数据泡
    private void hideMsgPage(){
        backgroundView.setImageDrawable(null);
        imageView.setImageDrawable(null);
        idView.setText("");
        nameView.setText("");
        timeView.setText("");
        placeView.setText("");
        materialView.setText("");
    }
    private void showMsgPage(int imageId,String cardId,String name,String time,String place,String material) {
        backgroundView.setImageDrawable(getResources().getDrawable(R.drawable.foodtraceability_msg_background));
        imageView.setImageDrawable(getResources().getDrawable(imageId));
        idView.setText(cardId);
        nameView.setText(name);
        timeView.setText(time);
        placeView.setText(place);
        materialView.setText(material);
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


    private void initSettings() {
        PreferenceManager.setDefaultValues(this,R.xml.settings_foodtraceability,false);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        AuthorID = settings.getString("id",null);
        AuthorName = settings.getString("name",null);
        AuthorTime = settings.getString("time",null);
        AuthorPlace = settings.getString("place",null);
        AuthorMaterial = settings.getString("material",null);
//        //隐藏数据泡
//        hideMsgPage();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_SYSTEM_SETTINGS) {

            PreferenceManager.setDefaultValues(this,R.xml.settings_foodtraceability,false);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            AuthorID = settings.getString("id",null);
            AuthorName = settings.getString("name",null);
            AuthorTime = settings.getString("time",null);
            AuthorPlace = settings.getString("place",null);
            AuthorMaterial = settings.getString("material",null);
//            //隐藏数据泡
//            hideMsgPage();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_query, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            intent.putExtra("CurrentId",CurrentId);

            startActivityForResult(intent,REQ_SYSTEM_SETTINGS);

            return true;
        };

        return super.onOptionsItemSelected(item);
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
    protected void onStop() {
        super.onStop();
        //隐藏数据泡
        hideMsgPage();
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
