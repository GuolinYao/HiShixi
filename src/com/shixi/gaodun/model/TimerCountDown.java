package com.shixi.gaodun.model;

import android.os.CountDownTimer;

import com.shixi.gaodun.inf.TimerCountDownCallBack;

/**
 * 倒计时实现
 * 
 * @author ronger
 * @date:2015-10-20 下午3:00:34
 */
public class TimerCountDown extends CountDownTimer {
	private TimerCountDownCallBack mTimerCountDownCallBack;

	public TimerCountDown(long millisInFuture, long countDownInterval, TimerCountDownCallBack timerCountDownCallBack) {
		super(millisInFuture, countDownInterval);
		this.mTimerCountDownCallBack = timerCountDownCallBack;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		mTimerCountDownCallBack.timing(millisUntilFinished);
	}

	@Override
	public void onFinish() {
		mTimerCountDownCallBack.timeFinish();

	}

}
