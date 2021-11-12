package com.example.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wbr on 2021/11/5.数据库单例类
 */
public class Database extends SQLiteOpenHelper {
    private static Database mInstance = null;

    /**高频卡数据库名称**/
    public static final String DATABASE_NAME = "cards.db";

    /**数据库版本号**/
    private static final int DATABASE_VERSION = 1;

    /**创建卡信息语句**/
    private static final String CREATE_Card_TABLE = "CREATE TABLE Card("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "card_id TEXT,"
            + "sum REAL DEFAULT 0.00);";


    /**创建高频表的SQL语句**/
    private static final String CREATE_HF_TABLE = "CREATE TABLE Book("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + "card_id TEXT,"
            + "book TEXT);";


    /**构造函数**/
    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**单例模式 初始化函数**/
    public static synchronized Database getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Database(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Card_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**可以拿到当前数据库的版本信息 与之前数据库的版本信息   用来更新数据库**/
    }

    /**
     * 删除数据库
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
