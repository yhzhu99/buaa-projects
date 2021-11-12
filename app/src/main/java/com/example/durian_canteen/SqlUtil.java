package com.example.durian_canteen;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.database.Database;

public class SqlUtil {
    private final static String TAG = ".SQL";
    static Context mContext;
    Database mDatabaseHelper;
    SQLiteDatabase mDatabase;

    private final static String TABLE_NAME = "Card";
    private final static String ID = "_id";
    private final static String CARD_ID = "card_id";
    private final static String SUM = "sum";
    private static SqlUtil sqlUtil;
    private SqlUtil(Activity activity){
        mContext = activity;
        mDatabaseHelper = Database.getInstance(mContext);
        mDatabase = mDatabaseHelper.getReadableDatabase();
    }
    static SqlUtil getInstance(Activity activity){
        if (sqlUtil == null) {
            sqlUtil = new SqlUtil(activity);
        }
        setContext(activity);
        return sqlUtil;
    }
    private static void setContext(Activity activity) {
        mContext = activity;
    }
    public Double getCardSUM(String card){
        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{SUM},CARD_ID + "=?", new String[] {card}, null, null,null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 1){
            double sum = cursor.getDouble(0);
            cursor.close();
            return sum;
        } else if (cursor.getCount() == 0){
            cursor.close();
            return null;
        } else{
            System.out.println("multi cardID equals " + card);
            return null;
        }
    }

    public void updatesum(String card_id, double x){
        ContentValues value = new ContentValues();
        value.put(SUM,x);
        mDatabase.update(TABLE_NAME,value,CARD_ID+ "=?",new String[]{card_id});
    }

    public void insertCard(String card_id){
        ContentValues values = new ContentValues();
        values.put(CARD_ID,card_id);
        mDatabase.insert(TABLE_NAME,null,values);
    }
    public void deleteCard(String card_id){
        mDatabase.delete(TABLE_NAME, CARD_ID+"=?",new String[]{card_id});
    }
    //    public boolean haveBook(String card) {
//        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{CARD_ID}, CARD_ID + "=?", new String[] {card}, null, null,null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        if (cursor.getCount() > 0) {
//            cursor.close();
//            return true;
//        } else {
//            cursor.close();
//            return false;
//        }
//    }
//    public boolean isBorrowed(String card, String book){
//        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{BOOK}, CARD_ID + "=?", new String[] {card}, null, null,null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        for (int i = 0;i <cursor.getCount();i++)
//        {
//            String current = cursor.getString(0);
//            if (current.equals(book)) {
//                cursor.close();
//                return true;
//            }
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return false;
//    }
//    public boolean isBorrowed(String book) {
//        Cursor cursor = mDatabase.query(TABLE_NAME, new String[]{BOOK}, BOOK + "=?", new String[] {book}, null, null,null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//        if (cursor.getCount() > 0) {
//            cursor.close();
//            return true;
//        } else {
//            cursor.close();
//            return false;
//        }
//    }
//    public long borrowBook(String card, String book) {
//        ContentValues values = new ContentValues();
//        values.put(CARD_ID, card);
//        values.put(BOOK, book);
//        return mDatabase.insert(TABLE_NAME, null, values);
//
//    }
//    public int returnBook(String card) {
//        return mDatabase.delete(TABLE_NAME,CARD_ID + "=? ", new String[] {card});
//    }
}
