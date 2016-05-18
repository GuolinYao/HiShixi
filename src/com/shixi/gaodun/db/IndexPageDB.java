package com.shixi.gaodun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 引导页的数据存储
 * 
 * @author ronger
 * @date:2015-12-8 上午11:12:38
 */
public class IndexPageDB extends SQLiteOpenHelper {
	private static String name = "IndexPageDBDunShixiCache";
	// 版本号，如有变动需要在原来的基础上+1
	private static int VERSION = 1;
	private static Gson gson = new Gson();
	private static IndexPageDB cacheDB = null;

	public static synchronized IndexPageDB getInstance(Context context) {
		if (cacheDB == null) {
			cacheDB = new IndexPageDB(context);
		}
		return cacheDB;
	}

	public IndexPageDB(Context context) {
		super(context, name, null, VERSION);
	}

	public IndexPageDB(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * 创建名为myindexinfo_tab表格
	 * 
	 * content内容：这里主要保存一个个人信息的实体 stuednt_id:学生Id
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists myindexinfo_tab (_id integer primary key autoincrement,content text,stuednt_id varchar(20))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE myindexinfo_tab");
		onCreate(db);
	}

	/**
	 * 存储基本信息
	 * 
	 * @param userinfo
	 * @param stuednt_id
	 * @throws Exception
	 */
	public void insertBasicInfo(UserInfoBean userinfo, String stuednt_id) throws Exception {
		SQLiteDatabase db = getWritableDatabase();
		db.delete("myindexinfo_tab", "stuednt_id=?", new String[] { stuednt_id });
		ContentValues values = new ContentValues();
		values.put("stuednt_id", stuednt_id);
		values.put("content", beanToJson(userinfo));
		db.insert("myindexinfo_tab", null, values);
		db.close();

	}

	/**
	 * 清除指定数据
	 * 
	 * @param stuednt_id
	 * @throws Exception
	 */
	public void clearBasicInfo(String stuednt_id) throws Exception {
		SQLiteDatabase db = getWritableDatabase();
		db.delete("myindexinfo_tab", "stuednt_id=?", new String[] { stuednt_id });
		db.close();
	}

	/**
	 * 获取基本信息
	 * 
	 * @param stuednt_id
	 * @return
	 */
	public UserInfoBean getBasicInfo(String stuednt_id) {
		UserInfoBean bean = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query("myindexinfo_tab", new String[] { "content" }, "stuednt_id=?",
				new String[] { stuednt_id }, null, null, null);
		if (cursor.moveToNext()) {
			String content = cursor.getString(cursor.getColumnIndex("content"));
			if (!StringUtils.isEmpty(content)) {
				try {
					bean = gson.fromJson(content, UserInfoBean.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		cursor.close();
		db.close();
		return bean;
	}

	// 转换成gson对象
	public String beanToJson(Object obj) {
		return gson.toJson(obj);
	}
}
