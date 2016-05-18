package com.shixi.gaodun.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 搜索的缓存
 * 
 * @author ronger
 * @date:2015-11-10 下午3:33:29
 */
public class SearchDB extends SQLiteOpenHelper {
	private static String name = "SearchGaoDunShixiCache";
	private static int VERSION = 1;
	// 保存的个数
	private int insertNumber = 3;

	public SearchDB(Context context) {
		super(context, name, null, VERSION);
	}

	public SearchDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists historysearch (" + "_id integer primary key autoincrement,"
				+ "content varchar(20))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE searchcache");
		onCreate(db);
	}

	/** 缓存搜索历史数据 */
	public void insertHistory(String content, String data) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query("historysearch", new String[] { "_id", "content" }, null, null, null, null, null);
		ContentValues values = new ContentValues();
		// values.put("content", content);
		if (cursor.getCount() <= insertNumber) {
			db.delete("historysearch", "content=?", new String[] { content });// 删除相同项
			values.put("content", content);
			db.insert("historysearch", null, values);
		} else {
			cursor.moveToNext();
			db.delete("historysearch", "_id=?", new String[] { cursor.getString(0) });// 删除第一项
			values.put("content", content);
			db.insert("historysearch", null, values);
		}
		cursor.close();
		db.close();
	}

	public void dropTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE historysearch");
		db.execSQL("create table if not exists historysearch (" + "_id integer primary key autoincrement,"
				+ "content varchar(20))");
		db.close();
	}

	/**
	 * 查询搜索的输入历史记录
	 * 
	 * @param content
	 * @return
	 */
	public ArrayList<String> selectHistoryData() {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> historyList = new ArrayList<String>();
		Cursor cursor = db.query("historysearch", new String[] { "content" }, null, null, null, null, "_id desc");
		if (cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return historyList;
		} else {
			while (cursor.moveToNext()) {
				int len = historyList.size();
				if (len <= insertNumber) {
					HashMap<String, String> map = new HashMap<String, String>();
					historyList.add(cursor.getString(cursor.getColumnIndex("content")));
				}
			}
			cursor.close();
			db.close();
			return historyList;

		}
	}
}
