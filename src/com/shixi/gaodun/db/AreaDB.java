package com.shixi.gaodun.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 地区方面数据存储
 * 
 * @author ronger
 * @date:2015-10-27 下午1:49:37
 */
public class AreaDB extends SQLiteOpenHelper {
	private static AreaDB areaDB;
	private static Gson gson = new Gson();
	/**
	 * 数据库名称
	 */
	private static final String AREA_DB_NAME = "area_db.db";
	/**
	 * 数据库版本
	 */
	private static final int AREA_DB_VERSION = 1;
	/**
	 * 表名
	 */
	private static final String AREA_DB_TABLE_NAME = "area";
	private static final String AREA_DB_TABLE_NAME2 = "area2";

	// 主键
	private static final String KEY_ID = "_id";
	// 地区内容
	private static final String AREA_CONTENT = "content";
	// 地区分类
	private static final String CITY_TYPE = "city_type";
	/**
	 * 创建表语句
	 */
	private static final String CAREATE_TABLE_SQL = "create table if not exists " + AREA_DB_TABLE_NAME + "(" + KEY_ID
			+ " integer primary key autoincrement," + CITY_TYPE + " integer," + AREA_CONTENT + " text)";
	/**
	 * 创建表语句
	 */
	private static final String CAREATE_TABLE_SQL2 = "create table if not exists " + AREA_DB_TABLE_NAME2 + "(" + KEY_ID
			+ " integer primary key autoincrement," + CITY_TYPE + " integer," + AREA_CONTENT + " text)";

	public static AreaDB getInstance(Context context) {
		if (null == areaDB) {
			areaDB = new AreaDB(context);
		}
		return areaDB;
	}

	public AreaDB(Context context) {
		super(context, AREA_DB_NAME, null, AREA_DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CAREATE_TABLE_SQL);
		db.execSQL(CAREATE_TABLE_SQL2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + AREA_DB_TABLE_NAME);
			onCreate(db);
		}
	}

	/**
	 * 存储所有的地区
	 * 
	 * @param db
	 * @param addressContent
	 */
	public void insertAddress(SQLiteDatabase db, String addressContent) {
		db.beginTransaction();
		ContentValues values = new ContentValues();
		values.put(AREA_CONTENT, addressContent);
		db.insert(AREA_DB_TABLE_NAME, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// /**
	// * 获取地区信息
	// *
	// * @param db
	// * @return
	// * @throws Exception
	// */
	// public Map<String, List<CityBean>> getAddress(SQLiteDatabase db) throws Exception {
	// Map<String, List<CityBean>> addressMap = new HashMap<String, List<CityBean>>();
	// Cursor cursor = db.query(AREA_DB_TABLE_NAME, new String[] { AREA_CONTENT }, null, null, null, null, null);
	// if (cursor.moveToFirst()) {
	// String content = cursor.getString(cursor.getColumnIndex(AREA_CONTENT));
	// if (!content.equals("")) {
	// addressMap = TransFormModel.getCitysByInitial(content);
	// }
	// }
	// cursor.close();
	// db.close();
	// return addressMap;
	// }

	/**
	 * 存储城市 现居地
	 * 
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void insetrAddress(ArrayList<CityBean> cityLists, int type) throws SQLException, JSONException {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(AREA_DB_TABLE_NAME, CITY_TYPE + "=? ", new String[] { String.valueOf(type) });
		String sql = "insert into " + AREA_DB_TABLE_NAME + "(" + CITY_TYPE + "," + AREA_CONTENT + ")" + "values(?,?)";
		db.beginTransaction();
		db.execSQL(sql, new Object[] { type, getListContent(cityLists) });
		// db.insert(AREA_DB_TABLE_NAME, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	/**
	 * 存储城市 期望实习地
	 * 
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void insetrAddress2(ArrayList<CityBean> cityLists, int type) throws SQLException, JSONException {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(AREA_DB_TABLE_NAME2, CITY_TYPE + "=? ", new String[] { String.valueOf(type) });
		String sql = "insert into " + AREA_DB_TABLE_NAME2 + "(" + CITY_TYPE + "," + AREA_CONTENT + ")" + "values(?,?)";
		db.beginTransaction();
		db.execSQL(sql, new Object[] { type, getListContent(cityLists) });
		// db.insert(AREA_DB_TABLE_NAME, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 存储热门城市
	 */
	public void insertHotAddress(ArrayList<CityBean> hotCityLists) {

	}

	/**
	 * 获取所有的城市列表
	 * 
	 * @return
	 */
	public ArrayList<CityBean> getCityLists() {
		ArrayList<CityBean> citys = new ArrayList<CityBean>();
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(AREA_DB_TABLE_NAME, new String[] { AREA_CONTENT }, null, null, null, null, null);
		if (cursor.moveToNext()) {
			String content = cursor.getString(cursor.getColumnIndex("content"));
			if (!StringUtils.isEmpty(content)) {
				try {
					citys = getCityLists(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		cursor.close();
		db.close();
		return citys;
	}
	/**
	 * 获取所有的城市列表 期望实习地
	 * 
	 * @return
	 */
	public ArrayList<CityBean> getCityLists2() {
		ArrayList<CityBean> citys = new ArrayList<CityBean>();
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(AREA_DB_TABLE_NAME2, new String[] { AREA_CONTENT }, null, null, null, null, null);
		if (cursor.moveToNext()) {
			String content = cursor.getString(cursor.getColumnIndex("content"));
			if (!StringUtils.isEmpty(content)) {
				try {
					citys = getCityLists(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		cursor.close();
		db.close();
		return citys;
	}

	public ArrayList<CityBean> getCityLists(String content) throws JSONException {
		JSONArray array = new JSONArray(content);
		ArrayList<CityBean> list = new ArrayList<CityBean>();
		for (int i = 0; i < array.length(); i++) {
			list.add(gson.fromJson(array.getString(i), CityBean.class));
		}
		return list;
	}

	/**
	 * 获取热门城市列表
	 * 
	 * @return
	 */
	public ArrayList<CityBean> getHotCityLists() {
		return null;
	}

	/**
	 * 获取存储信息
	 * 
	 * @param list
	 * @return
	 * @throws JSONException
	 */
	public String getListContent(List<?> list) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (Object object : list) {
			jsonArray.put(new JSONObject(toJson(object)));
		}
		return jsonArray.toString();
	}

	/**
	 * 转换为gson对象
	 * 
	 * @param obj
	 * @return
	 */
	public String toJson(Object obj) {
		return gson.toJson(obj);
	}
}
