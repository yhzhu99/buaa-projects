package com.buaa.canteen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.canteen.CanteenActivityGroup;
import com.buaa.database.DatabaseHelper;

import it.moondroid.durian.R;

public class CanteenResumeActivity extends Activity {

    private static final String TAG = ".CanteenResumeActivity";
    private static final double stepValue = 60.00;

    private ImageView statusView;
    private TextView idView;
    private TextView stepView;
    private TextView sumView;

    /**数据库相关**/
    Context mContext;
    DatabaseHelper mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private final static String TABLE_NAME = "HFCard";
    private final static String ID = "_id";
    private final static String CARD_ID = "card_id";
    private final static String SUM = "sum";
    private final static String CONSUME_TIME = "consume_time";

    /**
     * 用于同步UI,接受CanteenActivityGroup的broadcast
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int what = intent.getExtras().getInt("what");
            switch (what) {
                case 1://初始化错误
                    //TODO:
                    showMsgPage(R.drawable.buscard_consume_check_wrong,intent.getExtras().getString("Result"),"","");
                    break;
                case 2://未检测到卡
                    hideMsgPage();
                    break;
                case 3: //成功获取卡号
                    String currentId = intent.getExtras().getString("Result");
                    updateCardUI(currentId);
                    break;
                default:
                    break;
            }
        }
    };

    private void hideMsgPage(){

        statusView.setImageDrawable(null);
        idView.setText("");
        stepView.setText("");
        sumView.setText("");
    }
    private void showMsgPage(int imageId,String cardId,String stepNum,String sumNum){
        statusView.setImageDrawable(getResources().getDrawable(imageId));
        idView.setText(cardId);
        stepView.setText(stepNum);
        sumView.setText(sumNum);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);


        /**数据库相关变量初始化**/
        mContext = this;
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        mDatabase = mDatabaseHelper.getReadableDatabase();

        statusView = (ImageView)findViewById(R.id.resume_statusView);
        idView = (TextView)findViewById(R.id.resume_idView);
        stepView = (TextView)findViewById(R.id.stepView);
        sumView = (TextView)findViewById(R.id.resume_sumView);
        hideMsgPage();
    }

    /**
     *
     * @param CardId 卡号
     */
    private void updateCardUI(String CardId) {
        String searchResult = searchHFCard(CARD_ID,CardId);
        long searchTime = searchConsumeTime(CARD_ID,CardId);
        long timeStamp = System.currentTimeMillis();
        System.out.println(searchTime);
        System.out.println(timeStamp);
        System.out.println("-------------");
        if (searchResult == null || searchResult.length() <= 0) { //如果数据库中没有记录
            showMsgPage(R.drawable.buscard_consume_check_wrong,getResources().getString(R.string.buscard_please_author_first),"","");

        } else if (searchResult.equals("-1")) {  //返回值为-1，数据库中搜索不止一个记录，错误
            showMsgPage(R.drawable.buscard_consume_check_wrong,getResources().getString(R.string.buscard_search_more_than_one),"","");

        } else {  //返回金额，更新UI
            double newSum = Double.valueOf(searchResult) - stepValue;

            // System.out.println(timeStamp);
            if (newSum < 0) {
                showMsgPage(R.drawable.buscard_consume_check_wrong,getResources().getString(R.string.buscard_shortage),"",searchResult);
            }else if(timeStamp-searchTime<10000 && searchTime!=0){
                showMsgPage(R.drawable.buscard_consume_check_wrong,getResources().getString(R.string.buscard_time_limit),"",searchResult);
                System.out.println("刷卡间隔太短，请稍后再试");
            } else {
                if (Double.toString(newSum).equals(updateHFCard(CARD_ID, CardId, SUM, Double.toString(newSum)))) {
                    updateHFCard(CARD_ID, CardId, CONSUME_TIME, Long.toString(timeStamp));
                    // System.out.println(searchTime);
                    showMsgPage(R.drawable.buscard_consume_check_right,CardId,Double.toString(stepValue),Double.toString(newSum));
                }

            }

        }
    }
       /**
     * 查询一条记录
     * @param key
     * @param selectionArgs
     * @return 返回金额数值对应的字符串
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
        }else {
            for (int i = 0;i <cursor.getCount();i++)
            {
                Log.v(TAG, "Current cursor = " + Double.toString(cursor.getDouble(0)));
                cursor.moveToNext();
            }
            cursor.close();
            return "-1";
        }
    }

    /**
     * 查询一条某一张卡片的最近消费时间
     * @param key
     * @param selectionArgs
     * @return 返回时间戳（Long）
     */
    private long searchConsumeTime(String key,String selectionArgs) {
        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{CONSUME_TIME}, key + "=?", new String[] {selectionArgs}, null, null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        //double[] sumList = new double[cursor.getCount()];
        if (cursor.getCount() == 1) {
            long sum = cursor.getLong(0);
            cursor.close();
            return sum;
        }else if (cursor.getCount() == 0) {
            cursor.close();
            return 0; // 没搜到的话，对应时间是0
        }else {
            for (int i = 0;i <cursor.getCount();i++)
            {
                // Log.v(TAG, "Current cursor = " + Double.toString(cursor.getDouble(0)));
                cursor.moveToNext();
            }
            cursor.close();
            return -1;
        }
    }

    /**
     * 更新一条记录
     * @param key
     * @param data
     * @return 返回充值后的金额金额字符串，错误返回 null
     */
    private String updateHFCard(String key, String data,String Column, String value) {
        ContentValues values = new ContentValues();
        values.put(Column, value);
        int result =  mDatabase.update(TABLE_NAME, values, key + "=?",new String[]{data});
        if (result != 0) {
            return value;
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**用于接收group发送过来的广播**/
        /***用于接收group发送过来的广播***/
        IntentFilter filter = new IntentFilter(CanteenActivityGroup.resume_action);
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}
