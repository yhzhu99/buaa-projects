package com.topelec.buscard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.topelec.database.DatabaseHelper;
import it.moondroid.coverflowdemo.R;

public class RechargeActivity extends Activity {

    private final static String TAG = ".RechargeActivity";
    private ImageView statusView;
    private TextView idView;
    private TextView sumView;
    private EditText rechargeText;
    private ImageButton btnAuthor;
    private ImageButton btnCancelAuthor;
    private ImageButton btnRecharge;

    private String currentId = new String();
    private String oldId = new String();

    /***接收Group发送来的广播数据，同步更新UI***/
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int what = intent.getExtras().getInt("what");
            switch (what) {
                case 1://初始化错误
                    //TODO:
                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
                    idView.setText(intent.getExtras().getString("Result"));
                    currentId = null;
                    oldId = null;
                    break;
                case 2://未检测到卡
                    idView.setText(getResources().getString(R.string.buscard_not_check_card));
                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_recharge_standby));
                    sumView.setText("");
                    rechargeText.setText("");
                    currentId = null;
                    oldId = null;
                    break;
                case 3: //成功获取卡号
                    currentId = intent.getExtras().getString("Result");
                    idView.setText(currentId);
                    if (currentId == null) {
                        idView.setText("");
//                        statusView.setImageDrawable(getResources().getDrawable(R.drawable.standby));
                    }else {
                       // if (!currentId.equals(oldId)) { //检测到不同的卡
                            //TODO:查询数据库，存在：succeed；不存在：未授权

                            updateCardUI(currentId);
                            oldId = currentId;
                            Log.v(TAG,"Result = "+ currentId+"");
                    //    } else {
                            //TODO:相同的卡，不做处理
                    //    }

                    }

                    break;
                default:
                    break;
            }


        }
    };

    /**数据库相关**/
    Context mContext;
    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private final static String TABLE_NAME = "HFCard";
    private final static String ID = "_id";
    private final static String CARD_ID = "card_id";
    private final static String SUM = "sum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        /**数据库相关变量初始化**/
        mContext = this;
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        mDatabase = mDatabaseHelper.getReadableDatabase();



        statusView = (ImageView) findViewById(R.id.resume_statusView);
        idView = (TextView) findViewById(R.id.idView);
        sumView = (TextView) findViewById(R.id.resume_sumView);
        rechargeText = (EditText) findViewById(R.id.rechargeText);

        
        btnAuthor = (ImageButton) findViewById(R.id.btn_author);
        btnAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:判断没有，则创建一条数据：statusView中显示状态；若存在，则直接在statusView中显示状态
                if (currentId == null || currentId.length() == 0) {
//                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.standby));
                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_recharge_standby));
                    idView.setText(getResources().getString(R.string.buscard_not_check_card));
                    sumView.setText("");
                    rechargeText.setText("");
                    return;
                }
                String result = searchHFCard(CARD_ID,currentId);
                if ( result == null ) {
                    //TODO:插入新行
                    if (insertHFCard(CARD_ID,currentId) != -1) {
                        statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_right));
                        idView.setText(getResources().getString(R.string.buscard_author_succeed));
                        updateCardUI(currentId);
                    } else {
                        statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
                        idView.setText(getResources().getString(R.string.buscard_author_fail));
                    };
                } else if ( result.equals("-1")) {
                    //TODO:查询到多行，错误

                } else {
                    //TODO:本卡已授权
//                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.authored_already));
                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
//                    idView.setText(currentId);
                    idView.setText(R.string.buscard_authored_already);
                }

            }
        });

        btnCancelAuthor = (ImageButton)findViewById(R.id.btn_cancel_author);
        btnCancelAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:跳出提示框，同意直接删除table中对应的记录条目
                if (currentId == null || currentId.length() == 0) {
                    return;
                }
                Log.v(TAG,"button cancel clicked");
                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                builder.setTitle(getResources().getString(R.string.buscard_if_cancel_item));
                builder.setPositiveButton(getResources().getString(R.string.buscard_OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //TODO:删除记录
                        if (deleteHFCard(CARD_ID,currentId) != 0) {
                            statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_right));
                            idView.setText(getResources().getString(R.string.buscard_cancel_author_succeed));
                            sumView.setText("");
                            idView.setText("");

                        }else {
                            statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
                            idView.setText(getResources().getString(R.string.buscard_cancel_author_fail));
                            sumView.setText("");
                            idView.setText("");
                        }
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.buscard_CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //TODO:这里添加点击确定后的逻辑

                    }
                });
                builder.create().show();
            }
        });

        btnRecharge = (ImageButton) findViewById(R.id.btnRecharge);
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: 获取text输入值，加入数据库sum中，并更新sumView中的结果。

                CharSequence value = rechargeText.getText();

                if (currentId == null || currentId.length() == 0 || value == null || value.length() == 0) {
                    return;
                }
                String result = searchHFCard(CARD_ID,currentId);
                if ( result == null) {
                    statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
                    idView.setText(getResources().getString(R.string.buscard_please_author_first));
                    return;

                }else {

                    String newSum = updateHFCard(CARD_ID,currentId,SUM,String.valueOf(value));
                    if ( newSum == null) {
                        statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
                        idView.setText(getResources().getString(R.string.buscard_recharge_fail));

                    }else {
                        statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_right));
                        idView.setText(getResources().getString(R.string.buscard_recharge_succeed));
                        sumView.setText(newSum);
                        rechargeText.setText("");

                    }
                }
            }
        });



    }

    /**
     *
     * @param CardId 卡号
     */
    private void updateCardUI(String CardId) {
        String searchResult = searchHFCard(CARD_ID,CardId);
        if (searchResult == null || searchResult.length() <= 0) { //如果数据库中没有记录
            //statusView.setImageDrawable(getResources().getDrawable(R.drawable.no_author));
            statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
            idView.setText(getResources().getString(R.string.buscard_please_author_first));
            sumView.setText("");
//            rechargeText.setText("");
        } else if (searchResult.equals("-1")) {  //返回值为-1，数据库中搜索不止一个记录，错误
            statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_wrong));
            idView.setText(getResources().getString(R.string.buscard_search_more_than_one));
            sumView.setText("");
//            rechargeText.setText("");
        } else {  //返回金额，更新UI
            idView.setText(CardId);
//            statusView.setImageDrawable(getResources().getDrawable(R.drawable.please_recharge));
            statusView.setImageDrawable(getResources().getDrawable(R.drawable.buscard_symbol_right));
            sumView.setText(searchResult);
//            idView.setText("");
        }
    }
    /**
     * 查询一条记录
     * @param key
     * @param selectionArgs
     * @return
     */
    private String searchHFCard(String key,String selectionArgs) {
        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{SUM}, key + "=?", new String[] {selectionArgs}, null, null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //double[] sumList = new double[cursor.getCount()];
        if (cursor.getCount() == 1) {
            double sum = cursor.getDouble(0);
            cursor.close();
            return Double.toString(sum);
        }else if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            for (int i = 0;i <cursor.getCount();i++)
            {
                Log.v(TAG,"Current cursor = "+Double.toString(cursor.getDouble(0)));
                cursor.moveToNext();
            }
            cursor.close();
            return "-1";
        }
    }

    /**
     * 插入一条记录
     * @param key   需要插入的列名称
     * @param data  对应列赋值
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    private long insertHFCard(String key,String data) {
        ContentValues values = new ContentValues();
        values.put(key,data);
        return mDatabase.insert(TABLE_NAME,null,values);
    }

    /**
     * 删除一条记录
     * @param key
     * @param data
     * @return 返回所删除的行数，否则返回0。
     */
    private int deleteHFCard(String key, String data) {
        return mDatabase.delete(TABLE_NAME,key + "=?", new String[] {data});
    }

    /**
     * 更新一条记录
     * @param key
     * @param data
     * @return 返回充值后的金额金额字符串，错误返回null
     */
    private String updateHFCard(String key, String data,String Column, String value) {
        ContentValues values = new ContentValues();
        String oldSum = searchHFCard(key,data);
        if (oldSum != null && !oldSum.equals("-1")) {
            double sum = Double.valueOf(oldSum) + Double.valueOf(value);
            values.put(Column, sum);
            int result =  mDatabase.update(TABLE_NAME, values, key + "=?",new String[]{data});
            if (result != 0) {
                return Double.toString(sum);
            }
        }

        return null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        /**用于接收group发送过来的广播**/
        IntentFilter filter = new IntentFilter(CardActivityGroup.recharge_action);
        registerReceiver(mBroadcastReceiver,filter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCardUI(currentId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBroadcastReceiver);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_recharge, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
