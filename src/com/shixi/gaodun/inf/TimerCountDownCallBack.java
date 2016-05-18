package com.shixi.gaodun.inf;

/**
 * 计时器对应的回调接口
 * 
 * @author ronger
 * @date:2015-10-20 下午3:01:38
 */
public interface TimerCountDownCallBack {
	// 计时中
	public void timing(long millisUntilFinished);

	// 计时结束
	public void timeFinish();
}
