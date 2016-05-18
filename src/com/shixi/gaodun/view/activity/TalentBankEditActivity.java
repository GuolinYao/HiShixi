package com.shixi.gaodun.view.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.db.IndexPageDB;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 人才库填写页
 *
 * @author guolinyao
 * @date 2016年4月27日 上午9:54:50
 */
@SuppressLint("HandlerLeak")
public class TalentBankEditActivity extends TitleBaseActivity implements
        OnAlertSelectId {
    private TextView mName, mBirth;
    private TextView mGraduate_year, mSchool_name, mHighest_education;
    private int requestType;
    private CityBean mSelectCity;
    // true提交基本信息或false提交个人头像
    private boolean ifCommitBasicOrHeadimg = true;
    private DateWheelDialog mDateWheelDialog;
    private Dialog mDialog;
    private IndexPageDB mIndexPageDB;
    // 上一次进入时存储的基本信息
    private UserInfoBean mOldBasicInfo;
    private int mSelectGraduateIndex = -1;// 选择的毕业年份下标
    private String mobile;// 手机号
    private TextView mMajorType;
    private TextView mContactEmail;
    private TextView mContactNumber;
    private TextView mMajorName;
    private TextView mExpectInternArea;
    private TextView mCanInternshipStage;
    private TextView mDearHR;
    // 已结选择过了的期望实习地
    private ArrayList<CityBean> mSelectCitys;
    private InternshipIntentionBean mInternshipIntention;
    // 实习阶段
    private String mStartTime, mEndTime;
    private MajorClassifyBean mSelectMajorBean;

    public static void startAction(Activity context, String mobile) {
        Intent intent = new Intent(context, TalentBankEditActivity.class);
        intent.putExtra("mobile", mobile);
        context.startActivity(intent);
    }

    @Override
    protected void getIntentData() {
        mobile = getIntent().getStringExtra("mobile");
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getIntentData();
        isExcute = false;
        mIndexPageDB = IndexPageDB.getInstance(this);
        mOldBasicInfo = mIndexPageDB
                .getBasicInfo(CacheUtils.getStudentId(this));
        BaseApplication.mApp.mCenterTaskActivitys.add(this);
        setContentView(R.layout.activity_talent_edit_layout);
    }

    @Override
    public void initView() {
        super.initView();
        mTitleName.setText("人才库");
        mBackIcon.setVisibility(View.VISIBLE);
        mBackIcon.setImageResource(R.drawable.icon_back);
        mName = (TextView) findViewById(R.id.tv_basicinfo_name);
        mSchool_name = (TextView) findViewById(R.id.tv_school_name);
        mMajorType = (TextView) findViewById(R.id.tv_major_type);
        mContactEmail = (TextView) findViewById(R.id.tv_contact_email);
        mContactNumber = (TextView) findViewById(R.id.tv_contact_number);
        mMajorName = (TextView) findViewById(R.id.tv_major_name);
        mHighest_education = (TextView) findViewById(R.id.tv_highest_education);
        mGraduate_year = (TextView) findViewById(R.id.tv_graduate_year);
        mExpectInternArea = (TextView) findViewById(R.id.tv_expect_intern_area);
        mCanInternshipStage = (TextView) findViewById(R.id.tv_can_internship_stage);
        mDearHR = (TextView) findViewById(R.id.tv_dear_hr);

        initDataShow();
    }

    /**
     * 初始化数据显示
     */
    public void initDataShow() {
        setTextViewShow(mContactNumber, mobile);
        if (null == mOldBasicInfo)
            return;

        // 姓名
        setTextViewShow(mName, mOldBasicInfo.getName());
        setTextViewShow(mSchool_name, mOldBasicInfo.getGraduate_school());
        // 专业分类
        MapFormatBean majorType = mOldBasicInfo.getMajor_type();
        if (null != majorType) {
            MajorClassifyBean mSelectMajorBean = new MajorClassifyBean(
                    majorType.getKey(), majorType.getValue());
            setTextViewShow(mMajorType, mSelectMajorBean.getTitle());
        }
        // 联系邮箱
        setTextViewShow(mContactEmail, mOldBasicInfo.getContact_email());
        // 联系电话
        setTextViewShow(mContactNumber, mOldBasicInfo.getContact_mobile());
        setTextViewShow(mContactNumber, mobile);
        // 专业名称
        setTextViewShow(mMajorName, mOldBasicInfo.getDetail_major());
        // 最高学历
        String highest_education = mOldBasicInfo.getEducation();
        if (StringUtils.isNotEmpty(highest_education)) {
            if (highest_education.equals("1"))
                setTextViewShow(mHighest_education, "专科");
            if (highest_education.equals("2"))
                setTextViewShow(mHighest_education, "本科");
            if (highest_education.equals("3"))
                setTextViewShow(mHighest_education, "硕士");
            if (highest_education.equals("4"))
                setTextViewShow(mHighest_education, "博士");
        }
        // 毕业年份
        setTextViewShow(mGraduate_year, mOldBasicInfo.getGraduate_year());
        // 期望实习地
        mInternshipIntention = mOldBasicInfo.getmIntershipBean();
        if (null == mInternshipIntention)
            return;
        mSelectCitys = mInternshipIntention.getExpect_city();
        setAreaShow();
        // 可实习阶段
        if (StringUtils.isNotEmpty(mInternshipIntention.getPeriod_start())
                && StringUtils.isNotEmpty(mInternshipIntention
                .getPeriod_finish())) {
            mStartTime = mInternshipIntention.getPeriod_start();
            mEndTime = mInternshipIntention.getPeriod_finish();
            setCanIntershipStageText(mStartTime, mEndTime);
        }
        // 对HR说
        setTextViewShow(mDearHR, mOldBasicInfo.getDear_hr());
    }

    @Override
    public void onBackPressed() {
        saveBasicInfo();
        saveCurrentPageData();
        setResult(RESULT_OK);
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_bar_otherbtn:
                mDialog = CustomDialog.CancelAlertToDialog("跳过将失去已填写的信息", "跳过",
                        "继续填写", this, this);
                break;
            case R.id.fl_title_bar_back:
                saveBasicInfo();
                saveCurrentPageData();
                finish();
                break;
            case R.id.tv_confrim_btn:
                mDialog.cancel();
                try {
                    mIndexPageDB.clearBasicInfo(CacheUtils.getStudentId(this));
                } catch (Exception e) {
                    setDebugLog(e);
                }
                finishPartProcess();
                break;
            case R.id.tv_cancel_btn:
                mDialog.cancel();
                break;
            case R.id.rl_name_layout:
                requestType = 0;
                BasicInfoSetActivity.startAction(this, requestType,
                        Finals.TOEDITNAME_REQUEST, mName.getText().toString());
                break;
            // 就读学校
            case R.id.rl_school_name:
                BasicInfoSetActivity.startAction(this, 2,
                        Finals.TOEDITSCHOOLNAME_REQUEST, mSchool_name.getText()
                                .toString());
                break;
            // 专业分类
            case R.id.rl_major_type:
                MajorClassifyActivity.startAction(this,
                        Finals.TOSELECTMAJORTYPE_REQUEST, mSelectMajorBean, "专业分类");
                break;
            // 联系邮箱
            case R.id.rl_contact_email:
                BasicInfoSetActivity.startAction(this, 5,
                        Finals.TOSEDITEMAIL_REQUEST, mContactEmail.getText()
                                .toString());
                break;
            // 联系电话
            case R.id.rl_contact_number:
                requestType = 4;
                BasicInfoSetActivity.startAction(this, requestType,
                        Finals.TOEDITNAME_REQUEST, mContactNumber.getText()
                                .toString());
                break;
            case R.id.rl_major_name:
                BasicInfoSetActivity.startAction(this, 3,
                        Finals.TOEDITMAJORNAME_REQUEST, mMajorName.getText()
                                .toString());
                break;
            // 最高学历
            case R.id.rl_highest_education_layout:
                EducationListActivity.startAction(this,
                        Finals.TOSELECTHIGHEST_EDUCATION_REQUEST,
                        mHighest_education.getText().toString());
                break;
            // 毕业年份
            case R.id.rl_graduate_year:
                SelectGraduateYearActivity.startAction(this,
                        Finals.TOEDITGRADUATEYEAR, mGraduate_year.getText()
                                .toString(), mSelectGraduateIndex);
                break;
            case R.id.rl_expect_intern_area:// 期望实习地
                SelectInternshipAreaActivity.startAction(this,
                        Finals.EXPECT_INTERN_AREA, mSelectCitys, 1);
                break;
            case R.id.rl_can_internship_stage:// 可实习阶段
                CanIntershipStagActivity.startAction(this,
                        Finals.CAN_INTERNSHIP_STAGE, mStartTime, mEndTime);
                break;
            case R.id.rl_dear_hr:// 对HR说
                AddDearHRActivity.startAction(this, Finals.HR_REQUEST, mDearHR
                        .getText().toString());
                break;
            // 加入人才库
            case R.id.btn_add:
                ifCommitBasicOrHeadimg = true;
                commitBasicInfoPre();

                break;
        }
    }

    public void commitBasicInfoPre() {
        if (StringUtils.isEmpty(mName.getText().toString())) {
            ToastUtils.showToastInCenter("请填写姓名");
            return;
        }
        if (StringUtils.isEmpty(mSchool_name.getText().toString())) {
            ToastUtils.showToastInCenter("请填写就读学校");
            return;
        }
        if (StringUtils.isEmpty(mMajorType.getText().toString())) {
            ToastUtils.showToastInCenter("请选择专业分类");
            return;
        }
        if (StringUtils.isEmpty(mContactEmail.getText().toString())) {
            ToastUtils.showToastInCenter("请填写联系邮箱");
            return;
        }
        if (StringUtils.isEmpty(mContactNumber.getText().toString())) {
            ToastUtils.showToastInCenter("请填写联系电话");
            return;
        }
        if (StringUtils.isEmpty(mMajorName.getText().toString())) {
            ToastUtils.showToastInCenter("请填写专业名称");
            return;
        }
        if (StringUtils.isEmpty(mHighest_education.getText().toString())) {
            ToastUtils.showToastInCenter("请选择最高学历");
            return;
        }
        if (StringUtils.isEmpty(mGraduate_year.getText().toString())) {
            ToastUtils.showToastInCenter("请选择毕业年份");
            return;
        }
        if (null == mSelectCitys || mSelectCitys.isEmpty()) {
            ToastUtils.showToastInCenter("请选择期望实习地");
            return;
        }
        if (StringUtils.isEmpty(mCanInternshipStage.getText().toString())) {
            ToastUtils.showToastInCenter("请选择可实习阶段");
            return;
        }

        saveBasicInfo();
        saveCurrentPageData();
        setRequestParamsPre(TAG);
    }

    @Override
    public void setRequestParams(String className) {
        super.setRequestParams(className);
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(this));
        map.put("student_id", CacheUtils.getStudentId(this));
        map.put("name", mOldBasicInfo.getName());
        if (StringUtils.isNotEmpty(mOldBasicInfo.getGraduate_year()))
            map.put("graduate_year", mOldBasicInfo.getGraduate_year()
                    .substring(0, 4));
        if (StringUtils.isNotEmpty(mOldBasicInfo.getGraduate_school())) {
            map.put("graduate_school", mOldBasicInfo.getGraduate_school());
        }
        MapFormatBean mSelectMajorBean = mOldBasicInfo.getMajor_type();
        if (null != mSelectMajorBean
                && StringUtils.isNotEmpty(mSelectMajorBean.getKey())) {
            map.put("major_type", mSelectMajorBean.getKey());
        }
        if (StringUtils.isNotEmpty(mOldBasicInfo.getDetail_major())) {
            map.put("detail_major", mOldBasicInfo.getDetail_major());
        }
        if (StringUtils.isNotEmpty(mOldBasicInfo.getEducation())) {
            map.put("education", mOldBasicInfo.getEducation());
        }
        if (StringUtils.isNotEmpty(mOldBasicInfo.getContact_email()))
            map.put("contact_email", mOldBasicInfo.getContact_email());
        if (StringUtils.isNotEmpty(mOldBasicInfo.getContact_mobile())) {
            map.put("contact_mobile", mOldBasicInfo.getContact_mobile());
        }
        if (StringUtils.isNotEmpty(mInternshipIntention.getCityListStr(","))) {
            map.put("expect_city", mInternshipIntention.getCityListStr(","));
        }
        if (StringUtils.isNotEmpty(mInternshipIntention.getToServerStartTime())) {
            map.put("period_start", mInternshipIntention.getToServerStartTime());
        }
        if (StringUtils.isNotEmpty(mInternshipIntention.getToServerEndTime())) {
            map.put("period_finish", mInternshipIntention.getToServerEndTime());
        }
        if (StringUtils.isNotEmpty(mOldBasicInfo.getDear_hr())) {
            map.put("dear_hr", mOldBasicInfo.getDear_hr());
        }
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                StringUtils.getCommonIP() + GlobalContants.UPDATE_TALENT_BANK,
                map, new RequestResponseLinstner(new ResponseCallBack() {
            @Override
            public void getResponse(JSONObject response) {
                try {
                    if (response.getInt("returnCode") != 0) {
                        dissMissProgress();
                        ToastUtils.showToastInCenter(response
                                .getString("returnDesc"));
                        return;
                    }
                    dissMissProgress();

                    delayedJump();
                } catch (Exception e) {

                    dissMissProgress();
                    setDebugLog(e);
                }
            }
        }), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);
    }

    @Override
    public void delayedJump() {
        super.delayedJump();
        sendBroadcast();
        try {
            mIndexPageDB.clearBasicInfo(CacheUtils.getStudentId(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyTalentBankPreviewActivity.startAction(this, 1);// 跳转到预览人才库
        finish();
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void finishTranstition() {
        overridePendingTransition(0, R.anim.slide_right);
    }

    // /**
    // * 提交基本信息成功
    // *
    // * @param resultData
    // */
    // public void commitBasicInfoSuccess(String resultData) {
    // mHandler.postDelayed(new Runnable() {
    // @Override
    // public void run() {
    // delayedJump();
    // }
    // }, Finals.delayTime);
    // }

    @Override
    public void onClick(int whichButton) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case Finals.TOEDITNAME_REQUEST:
                toEditNameReturn(arg2);
                break;
            case Finals.TOEDITSCHOOLNAME_REQUEST:
                toEditSchoolNameReturn(arg2);
                break;
            case Finals.TOSELECTMAJORTYPE_REQUEST:
                toSelectMajorTypeReturn(arg2);
                break;
            case Finals.TOSEDITEMAIL_REQUEST:
                toEditEmailReturn(arg2);
                break;
            case Finals.TOEDITPHONE_REQUEST:
                toEditMobileReturn(arg2);
                break;
            case Finals.TOEDITMAJORNAME_REQUEST:
                toEditMajorNameReturn(arg2);
                break;
            case Finals.TOSELECTHIGHEST_EDUCATION_REQUEST:
                toSelectHighestEducationReturn(arg2);
                break;
            case Finals.TOEDITGRADUATEYEAR:
                toSelectGraduateYearReturn(arg2);
                break;
            case Finals.EXPECT_INTERN_AREA:// 期望实习地
                toSelectIntershipAreaReturn(arg2);
                break;
            case Finals.CAN_INTERNSHIP_STAGE:// 实习阶段
                toSelectCanIntershipStageReturn(arg2);
                break;
            case Finals.HR_REQUEST:// 实习阶段
                toEditDearHRReturn(arg2);
                break;
            case Finals.REQUESTCODE_ONE:
                // 跟新上一页面更新过的数据，防止本页点击下一页时将上次的数据覆盖
                mOldBasicInfo = mIndexPageDB.getBasicInfo(CacheUtils
                        .getStudentId(this));
                break;

        }
    }

    /**
     * 选择可实习阶段后返回
     *
     * @param data
     */
    public void toSelectCanIntershipStageReturn(Intent data) {
        mStartTime = data.getStringExtra("startTime");
        mEndTime = data.getStringExtra("endTime");
        setCanIntershipStageText(mStartTime, mEndTime);
    }

    /**
     * 去选择期望实习地后返回
     *
     * @param data
     */
    @SuppressWarnings("unchecked")
    public void toSelectIntershipAreaReturn(Intent data) {
        mSelectCitys = (ArrayList<CityBean>) data
                .getSerializableExtra("selectCity");
        setAreaShow();
    }

    /**
     * 联系邮箱返回
     *
     * @param data
     */
    public void toEditEmailReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mContactEmail.setText(resultStr);
    }

    /**
     * 联系电话返回
     *
     * @param data
     */
    public void toEditMobileReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mContactNumber.setText(resultStr);
    }

    /**
     * 专业名称返回
     *
     * @param data
     */
    public void toEditMajorNameReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mMajorName.setText(resultStr);
    }

    /**
     * 专业名称返回
     *
     * @param data
     */
    public void toEditDearHRReturn(Intent data) {
        String resultStr = data.getStringExtra("dear_hr");
        mDearHR.setText(resultStr);
    }

    /**
     * 编辑专业名称返回
     *
     * @param data
     */
    public void toSelectMajorTypeReturn(Intent data) {
        mSelectMajorBean = (MajorClassifyBean) data
                .getSerializableExtra("bean");
        if (null != mSelectMajorBean) {
            mMajorType
                    .setText(StringUtils.isEmpty(mSelectMajorBean.getTitle()) ? ""
                            : mSelectMajorBean.getTitle());
        }
    }

    /**
     * 编辑姓名返回
     *
     * @param data
     */
    public void toEditNameReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mName.setText(resultStr);
    }

    /**
     * 选择学校名返回
     *
     * @param data
     */
    public void toSelectGraduateYearReturn(Intent data) {
        String resultStr = data.getStringExtra("graduateyear");
        mSelectGraduateIndex = data.getIntExtra("selectIndex", 0);
        mGraduate_year.setText(resultStr);
    }

    /**
     * 选择学校名返回
     *
     * @param data
     */
    public void toEditSchoolNameReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mSchool_name.setText(resultStr);
    }

    /**
     * 选择最高学历返回
     *
     * @param data
     */
    public void toSelectHighestEducationReturn(Intent data) {
        String selectStr = data.getStringExtra("selectStr");
        if (StringUtils.isNotEmpty(selectStr)) {
            mHighest_education.setText(selectStr);
        }
    }

    /**
     * 提交基本信息
     * <p/>
     * 姓名、毕业年份，就读学校，最高学历
     */
    private void saveBasicInfo() {
        if (null == mOldBasicInfo) {
            mOldBasicInfo = new UserInfoBean();
        }
        mOldBasicInfo.setName(mName.getText().toString());
        mOldBasicInfo.setGraduate_year(mGraduate_year.getText().toString());
        mOldBasicInfo.setGraduate_school(mSchool_name.getText().toString());
        // 最高学历
        String highest_education = mHighest_education.getText().toString();
        String education = null;
        if (StringUtils.isNotEmpty(highest_education)) {
            if (highest_education.equals("专科"))
                education = "1";
            if (highest_education.equals("本科"))
                education = "2";
            if (highest_education.equals("硕士"))
                education = "3";
            if (highest_education.equals("博士"))
                education = "4";
        }
        mOldBasicInfo.setEducation(education);

        try {
            mIndexPageDB.insertBasicInfo(mOldBasicInfo,
                    CacheUtils.getStudentId(this));
        } catch (Exception e) {
            setDebugLog(e);
        }

    }

    /**
     * 设置选择日期后的回调
     *
     * @param mDateWheelFrameLayout
     */
    public void setDateCallBack(DateWheelFrameLayout mDateWheelFrameLayout) {
        mDateWheelFrameLayout
                .setOnDateChangedListener(new OnDateChangedListener() {
                    @Override
                    public void onDateChanged(String year, String month,
                                              String day) {
                        mBirth.setText(year + "-" + month + "-" + day);
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
        // mMainNoneDataLayout.setVisibility(View.GONE);
        // setRequestParamsPre(TAG);

    }

    /**
     * 设置实习地区的显示
     */
    public void setAreaShow() {
        if (null == mSelectCitys || mSelectCitys.isEmpty())
            return;
        StringBuilder selectCitySB = new StringBuilder();
        for (int i = 0; i < mSelectCitys.size(); i++) {
            CityBean cityBean = mSelectCitys.get(i);
            selectCitySB.append(cityBean.getRegion_name());
            if (i != mSelectCitys.size())
                selectCitySB.append("	");
        }
        mExpectInternArea.setText(selectCitySB.toString());
    }

    /**
     * 设置可实习阶段
     *
     * @param mStartTime
     * @param mEndTime
     */
    public void setCanIntershipStageText(String mStartTime, String mEndTime) {
        if (StringUtils.isNotEmpty(mStartTime)
                && StringUtils.isNotEmpty(mEndTime)) {
            mCanInternshipStage.setText(mStartTime + "-" + mEndTime);
        }
    }

    /**
     * 保存当页的数据
     */
    public void saveCurrentPageData() {
        if (null == mOldBasicInfo)
            mOldBasicInfo = new UserInfoBean();
        // 专业分类
        if (null != mSelectMajorBean) {
            mOldBasicInfo.setMajor_type(new MapFormatBean(mSelectMajorBean
                    .getPkid(), mSelectMajorBean.getTitle()));
        }
        // 专业名称
        mOldBasicInfo.setDetail_major(mMajorName.getText().toString());
        // 联系邮箱
        mOldBasicInfo.setContact_email(mContactEmail.getText().toString());
        // 期望实习地
        mInternshipIntention = new InternshipIntentionBean();
        mInternshipIntention.setExpect_city(mSelectCitys);
        // 期望实习阶段
        String time = mCanInternshipStage.getText().toString();
        if (StringUtils.isNotEmpty(time)) {
            if (StringUtils.isNotEmpty(mEndTime)
                    && StringUtils.isNotEmpty(mStartTime)) {
                mInternshipIntention.setPeriod_start(mStartTime);
                mInternshipIntention.setPeriod_finish(mEndTime);
            }
        }
        mOldBasicInfo.setmIntershipBean(mInternshipIntention);
        // 联系电话
        mOldBasicInfo.setContact_mobile(mContactNumber.getText().toString());
        // 对HR说
        mOldBasicInfo.setDear_hr(mDearHR.getText().toString());

        try {
            mIndexPageDB.insertBasicInfo(mOldBasicInfo,
                    CacheUtils.getStudentId(this));
        } catch (Exception e) {
            setDebugLog(e);
        }
    }

    /**
     * 友盟统计
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TalentBankEditActivity"); // 引导页1
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TalentBankEditActivity");
        MobclickAgent.onPause(this);
    }
}
