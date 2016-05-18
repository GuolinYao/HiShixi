package com.shixi.gaodun.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;

/**
 * 可实习阶段
 *
 * @author ronger guolin
 * @date:2015-11-2 下午2:04:43
 */
public class CanIntershipStagActivity extends TitleBaseActivity {
    private TextView mStartTimeText, mEndTimeText;
    // true开始false结束
    private boolean startOrEndTime;
    private DateWheelDialog mDateWheelDialog;
    // private int mStartYear = -1, mStartMonth = -1, mStartDay = -1;
    // private int mEndYear = -1, mEndMonth = -1, mEndDay = -1;
    private int[] mStartArray, mEndArray;
    private String startTime;
    private String endTime;
    private String mSplitChar = ".";
//    private String startYear;
//    private String endYear;
//    private String startMonth;
//    private String endMonth;
//    private String startDay;
//    private String endDay;

    public static void startAction(Activity context, int requestCode, String startTime, String endTime) {
        Intent intent = new Intent(context, CanIntershipStagActivity.class);
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void getIntentData() {
        Intent data = getIntent();
        startTime = data.getStringExtra("startTime");
        endTime = data.getStringExtra("endTime");
        setTime(startTime, endTime);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getIntentData();
        isExcute = false;
        setContentView(R.layout.canintership_stag_layout);
    }

    public void setTime(String startTime, String endTime) {
        mStartArray = StringUtils.getTime(startTime, mSplitChar);
        mEndArray = StringUtils.getTime(endTime, mSplitChar);

//        Log.i(TAG, "start==" + mStartArray[0] + mStartArray[1] + mStartArray[2]);
//        Log.i(TAG, "end==" + mEndArray[0] + mEndArray[1] + mEndArray[2]);
    }

    public void setStartTime(String startTime) {
        mStartArray = StringUtils.getTime(startTime, mSplitChar);
    }

    public void setEndTime(String endTime) {
        mEndArray = StringUtils.getTime(endTime, mSplitChar);
    }

    @Override
    public void initView() {
        super.initView();
        mTitleName.setText("可实习阶段");
        mBackIcon.setImageResource(R.drawable.icon_back);
        mOtherName.setVisibility(View.VISIBLE);
        mOtherName.setOnClickListener(this);
        mOtherName.setEnabled(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) ? true : false);
        mOtherName.setText("保存");
        mStartTimeText = (TextView) findViewById(R.id.tv_intership_starttime_value);
        mEndTimeText = (TextView) findViewById(R.id.tv_intership_endttime_value);
        if (StringUtils.isNotEmpty(startTime) && !startTime.equals("0000.00.00")) {
            mStartTimeText.setText(startTime);
        }
        if (StringUtils.isNotEmpty(endTime) && !endTime.equals("0000.00.00")) {
            mEndTimeText.setText(endTime);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fl_title_bar_back) {
            finish();
        }
        if (v.getId() == R.id.tv_title_bar_otherbtn) {
            Intent intent = new Intent();
            if ((mStartArray[0] > mEndArray[0]) || (mStartArray[0] == mEndArray[0]
                    && mStartArray[1] > mEndArray[1]) ||
                    (mStartArray[0] == mEndArray[0] && mStartArray[1] == mEndArray[1] &&
                            mStartArray[2] > mEndArray[2])) {
                ToastUtils.showToastInCenter("开始时间不能大于结束时间");
            } else {
                intent.putExtra("startTime", mStartTimeText.getText().toString());
                intent.putExtra("endTime", mEndTimeText.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        }
        if (v.getId() == R.id.rl_intership_endttime_layout) {
            startOrEndTime = false;
            setEndTime(mEndTimeText.getText().toString());
            mDateWheelDialog = new DateWheelDialog(this, mEndArray[0], mEndArray[1], mEndArray[2]);
            mDateWheelDialog.show();
            DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog.getmDateWheelFrameLayout();
            setDateCallBack(mDateWheelFrameLayout);
        }
        if (v.getId() == R.id.rl_intership_starttime_layout) {
            startOrEndTime = true;
            setStartTime(mStartTimeText.getText().toString());
            mDateWheelDialog = new DateWheelDialog(this, mStartArray[0], mStartArray[1], mStartArray[2]);
            mDateWheelDialog.show();
            DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog.getmDateWheelFrameLayout();
            setDateCallBack(mDateWheelFrameLayout);
        }
    }

    /**
     * 设置选择日期后的回调
     *
     * @param mDateWheelFrameLayout
     */
    public void setDateCallBack(DateWheelFrameLayout mDateWheelFrameLayout) {
        mDateWheelFrameLayout.setOnDateChangedListener(new OnDateChangedListener() {

            @Override
            public void onDateChanged(String year, String month, String day) {

                if (month.length() == 1) {
                    month = "0" + month;
                }
                if (day.length() == 1) {
                    day = "0" + day;
                }

                if (startOrEndTime) {
                    mStartArray[0] = Integer.parseInt(year);
                    mStartArray[1] = Integer.parseInt(month) - 1;
                    mStartArray[2] = Integer.parseInt(day);

                    mStartTimeText.setText(year + mSplitChar + month + mSplitChar + day);
                } else {

                    mEndArray[0] = Integer.parseInt(year);
                    mEndArray[1] = Integer.parseInt(month) - 1;
                    mEndArray[2] = Integer.parseInt(day);
                    mEndTimeText.setText(year + mSplitChar + month + mSplitChar + day);
                }
                mOtherName.setEnabled(StringUtils.isNotEmpty(mStartTimeText.getText().toString())
                        && StringUtils.isNotEmpty(mEndTimeText.getText().toString()) ? true : false);
                mDateWheelDialog.cancel();
            }

            @Override
            public void onCancel() {
                mDateWheelDialog.cancel();
            }
        });
    }

    @Override
    protected void setRetryRequest() {
    }
}
