package com.shixi.gaodun.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * 手机定位服务
 * 需要权限: <uses-permission
 * android:name="android.permission.ACCESS_FINE_LOCATION"/> <uses-permission
 * android:name="android.permission.ACCESS_MOCK_LOCATION"/> <uses-permission
 * android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 * @author gaodun
 *
 */
public class LocationService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
