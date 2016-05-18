package com.shixi.gaodun.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.ParentFragment;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.MakeCircleImg;
import com.shixi.gaodun.view.PercentageView;
import com.shixi.gaodun.view.activity.BasicInfoOneActivity;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.MineBrowsingHistoryActivity;
import com.shixi.gaodun.view.activity.MinePreviewResumeActivity;
import com.shixi.gaodun.view.activity.MineResumeDynamicActivity;
import com.shixi.gaodun.view.activity.PersonalInformationActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.ResumeEditActivity;
import com.shixi.gaodun.view.activity.SettingActivity;
import com.shixi.gaodun.view.activity.TalentBankGuideActivity;
import com.shixi.gaodun.view.activity.hunter.HunterJobsActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页我的
 *
 * @author ronger guolin
 * @date:2015-11-6 上午11:04:18
 */
@SuppressLint("InflateParams")
public class TabMineFragment extends ParentFragment {
    private View mMainLayout;
    private FrameLayout mTopLayout;
    private LayoutInflater mInflater;

    // 未登陆情况下的layout
    private RelativeLayout mNotLoginLayout;
    private Dialog mDialog;
    // 登陆情况下没有数据的layout
    private RelativeLayout mNotDataLayout;
    private TextView mNotDataAccountText;
    // 登陆情况下有数据的layout
    private RelativeLayout mExitDataLayout;
    private TextView mNameText, mSchoolNameText, mEducationText,
            mMajorNameText;
    private ImageView mTopImage;
    // 简历相关
    private TextView mResumePercentText;// 简历百分比
    private PercentageView mPercentageView;// 简历完整度图示
    private View mSupernatantLayout;// 浮层
    private TextView mResumeStatusIcon;// 小红点
    private int resumePostStatus;// 简历动态,小红点个数
    private int mSource;// 来源1代表从创建简历过来
    private RelativeLayout rl_hunter;
    private UserInfoBean userinfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainLayout = inflater.inflate(R.layout.tab_mine_layout, container,
                false);

        // CacheUtils.saveAccountId(mContext, "5580");// TODO 绕过登陆
        // CacheUtils.saveStudentId(mContext, "4654");// TODO 绕过登陆
        // CacheUtils.saveIf_is_hunter(mContext, "1");// 是猎头

        initView(mMainLayout);
        if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
            setNotLoginShow();
        } else if (NumberUtils.getFloat(CacheUtils.getComplete_rate(mContext),
                0.0f) <= 11) {
            setNotDataShow();
        } else {
            setExitDataShow();
            setRequestParamsPre(TAG);
        }

        return mMainLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isExcute = false;
        mInflater = LayoutInflater.from(mContext);
        Bundle bundle = getArguments();
        mSource = bundle.getInt("source");
        setFilterRegister();
        if (mSource == 1) {
            CustomDialog.SupernatantAlertToDialog(mContext);
        }
        // registerIntentFilter();
    }

    @Override
    public void onResume() {// TODO
        super.onResume();

        MobclickAgent.onPageStart("positionList"); // 统计页面，"positionList"为页面名称

        if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext))) {
            setRequestParamsPre(TAG, false);
            return;
        }
    }

    @Override
    public void initView(View view) {
        initTopView(view);
        mResumePercentText = (TextView) view
                .findViewById(R.id.tv_mine_resume_integrity_value);
        mPercentageView = (PercentageView) view
                .findViewById(R.id.pb_mine_progress);
        mResumeStatusIcon = (TextView) view
                .findViewById(R.id.tv_resume_status_record_icon);
        rl_hunter = (RelativeLayout) view.findViewById(R.id.rl_hunter);
        if (CacheUtils.getIf_is_hunter(mContext).equals("1")) {
            // 是猎头
            rl_hunter.setVisibility(View.VISIBLE);
        } else {// 不是猎头
            rl_hunter.setVisibility(View.GONE);
        }

        // if (CacheUtils.get_hunter_id(mContext) != null
        // && (!CacheUtils.get_hunter_id(mContext).equals(""))) {
        // String hunterIdStr = Base64.encodeBase64String(CacheUtils
        // .get_hunter_id(mContext).getBytes());
        // String content =
        // "http://shixipre.gaodun.cn/MobileAccount/login/btn/regin/hid/"
        // + hunterIdStr;
        // // 根据猎头id设置二维码图片
        // QRCodeUtil qrCodeUtil = new QRCodeUtil();// 生成二维码工具
        // Bitmap qrcodeBitmap = qrCodeUtil.createQRCode2(mContext, null,
        // R.drawable.icon_logo_black, content);
        // qrCodeUtil = null;
        // }

        setOnClick(view);

    }

    public void setCircleFrame(int point) {
        Resources res = mContext.getResources();
        GradientDrawable redCircleDrawable = (GradientDrawable) res
                .getDrawable(R.drawable.red_round);
        String pointStr = String.valueOf(point);
        if (point <= 0) {
            mResumeStatusIcon.setVisibility(View.GONE);
            return;
        }
        mResumeStatusIcon.setVisibility(View.VISIBLE);
        if (point < 10) {
            redCircleDrawable.setShape(GradientDrawable.OVAL);
            mResumeStatusIcon.setText(pointStr);
            return;
        } else if (point < 100) {
            redCircleDrawable.setShape(GradientDrawable.RECTANGLE);
            redCircleDrawable
                    .setCornerRadius(ActivityUtils.dip2px(mContext, 6));
            mResumeStatusIcon.setText(pointStr);
        } else {
            redCircleDrawable.setShape(GradientDrawable.RECTANGLE);
            redCircleDrawable
                    .setCornerRadius(ActivityUtils.dip2px(mContext, 4));
            pointStr = pointStr.substring(0, 1);
            mResumeStatusIcon.setText("99+");
        }
    }

    public void setOnClick(View view) {
        view.findViewById(R.id.rl_resume_edit).setOnClickListener(this);
        view.findViewById(R.id.rl_resume_status).setOnClickListener(this);
        view.findViewById(R.id.rl_browsing_history).setOnClickListener(this);
        view.findViewById(R.id.rl_connect).setOnClickListener(this);
        view.findViewById(R.id.rl_hr_source).setOnClickListener(this);
        view.findViewById(R.id.rl_setting).setOnClickListener(this);
        view.findViewById(R.id.tv_mine_preview_resume).setOnClickListener(this);
        view.findViewById(R.id.rl_hunter).setOnClickListener(this);// TODO
        mNotLoginLayout.setOnClickListener(this);
        mNotDataLayout.setOnClickListener(this);
        mExitDataLayout.setOnClickListener(this);
    }

    /**
     * 头部view的初始化
     *
     * @param view
     */
    public void initTopView(View view) {
        mTopLayout = (FrameLayout) view.findViewById(R.id.fl_basic_info_layout);
        mNotLoginLayout = (RelativeLayout) mInflater.inflate(
                R.layout.mine_not_login_layout, null);
        mNotDataLayout = (RelativeLayout) mInflater.inflate(
                R.layout.mine_login_nonedata_layout, null);
        mExitDataLayout = (RelativeLayout) mInflater.inflate(
                R.layout.mine_login_exitdata_layout, null);
        mNotDataAccountText = (TextView) mNotDataLayout
                .findViewById(R.id.tv_mine_account);
        mNameText = (TextView) mExitDataLayout.findViewById(R.id.tv_mine_name);
        mSchoolNameText = (TextView) mExitDataLayout
                .findViewById(R.id.tv_mine_school);
        mEducationText = (TextView) mExitDataLayout
                .findViewById(R.id.tv_mine_education);
        mMajorNameText = (TextView) mExitDataLayout
                .findViewById(R.id.tv_mine_major);
        mTopImage = (ImageView) mExitDataLayout
                .findViewById(R.id.iv_tab_mine_image);
    }

    /**
     * 设置未登陆情况下显示
     */
    public void setNotLoginShow() {
        mTopLayout.removeAllViews();
        mTopLayout.addView(mNotLoginLayout);
        mNotLoginLayout.setOnClickListener(this);
        mResumePercentText.setText("0");
        mResumeStatusIcon.setVisibility(View.GONE);
    }

    /**
     * 设置登陆情况下没有数据的显示
     */
    public void setNotDataShow() {
        mTopLayout.removeAllViews();
        mTopLayout.addView(mNotDataLayout);
        mNotDataLayout.setOnClickListener(this);
        mNotDataAccountText.setText(CacheUtils.getLoginAccount(mContext));
        mResumePercentText.setText("0");
        float completeRate = NumberUtils.getFloat(
                CacheUtils.getComplete_rate(mContext), 0.0f);
        mResumePercentText.setText(completeRate + "");
        mPercentageView.setRefresh(true);
        mPercentageView.setPreogress(completeRate);
    }

    /**
     * 设置登陆情况下有数据的显示
     */
    public void setExitDataShow() {
        mTopLayout.removeAllViews();
        mTopLayout.addView(mExitDataLayout);
        mExitDataLayout.setOnClickListener(this);
        float completeRate = NumberUtils.getFloat(
                CacheUtils.getComplete_rate(mContext), 0.0f);
        mResumePercentText.setText(completeRate + "");
        mPercentageView.setRefresh(true);
        mPercentageView.setPreogress(completeRate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mine_preview_resume:// 预览简历
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                if (NumberUtils.getFloat(CacheUtils.getComplete_rate(mContext),
                        0.0f) <= 11 || userinfo.getResume_id().length() == 0) {
                    // 应该是11
                    mDialog = CustomDialog.CancelAlertToDialog(
                            "你还没有简历，只有创建完简历后才能完成此操作", "创建简历", "取消", mContext, this);
                    break;
                }
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                mContext.setMove(1);
                MinePreviewResumeActivity.startAction(mContext);
                break;
            case R.id.rl_mine_nonedata_toplayout:// 登陆没有数据清空下头部的点击，弹出提示创建简历对话框
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }

                mDialog = CustomDialog.CancelAlertToDialog(
                        "你还没有简历，只有创建完简历后才能完成此操作", "创建简历", "取消", mContext, this);
                break;
            // 创建简历
            case R.id.tv_confrim_btn:
                mDialog.dismiss();
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>(
                        0);
                mContext.setMove(1);
                BasicInfoOneActivity.startAction(mContext);
                break;
            // 取消
            case R.id.tv_cancel_btn:
                if (mDialog.isShowing())
                    mDialog.dismiss();
                break;
            case R.id.rl_mine_notlogin_toplayout:// 去登陆或注册
                if (CacheUtils.getIsFirstLogin(mContext)) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                } else {
                    BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                    LoginActivity.startAction(mContext, 1, false);
                }
                break;
            case R.id.btn_select_two:// 去登陆
                mDialog.dismiss();
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                LoginActivity.startAction(mContext, 1, false);
                break;
            case R.id.btn_select_three:// 去注册
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                RegisterActivity.startAction(mContext, 1, top);
                mDialog.dismiss();
                break;
            case R.id.rl_mine_exitdata_toplayout:// 去编辑个人信息
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                if (NumberUtils.getFloat(CacheUtils.getComplete_rate(mContext),
                        0.0f) <= 11 || userinfo.getResume_id().length() == 0) {
                    mDialog = CustomDialog.CancelAlertToDialog(
                            "你还没有简历，只有创建完简历后才能完成此操作", "创建简历", "取消", mContext, this);
                    break;
                }
                mContext.setMove(1);
                PersonalInformationActivity.startAction(mContext, 1003);
                break;
            case R.id.rl_setting:// 设置
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                mContext.setMove(1);
                SettingActivity.startAction(mContext);
                break;
            case R.id.rl_hunter:// 校园猎头
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                mContext.setMove(1);
                HunterJobsActivity.startAction(mContext);
                break;
            case R.id.rl_connect:// 收藏
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                mContext.setMove(1);
                com.shixi.gaodun.view.activity.mine.MineCollectionActivity
                        .startAction(mContext);
                // MineCollectionActivity.startAction(mContext);
                break;
            case R.id.rl_hr_source:// 人才库
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                mContext.setMove(1);
                // 判断是否是第一次进入人才库
                if (CacheUtils.getIsFirstEnterTalentBank(mContext)) {
                    TalentBankGuideActivity.startAction(mContext, 2);
                } else {// 第一次进入
                    CacheUtils.saveIsFirstEnterTalentBank(mContext, true);
                    TalentBankGuideActivity.startAction(mContext, 1);
                }
                break;
            case R.id.rl_browsing_history:// 浏览记录
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                MineBrowsingHistoryActivity.startAction(mContext);
                break;
            case R.id.rl_resume_status:// 简历动态
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                MineResumeDynamicActivity.startAction(mContext);
                break;
            case R.id.rl_resume_edit:// 编辑简历
                if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
                    mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
                    break;
                }
                if (NumberUtils.getFloat(CacheUtils.getComplete_rate(mContext),
                        0.0f) <= 11 || userinfo.getResume_id().length() == 0) {
                    mDialog = CustomDialog.CancelAlertToDialog(
                            "你还没有简历，只有创建完简历后才能完成此操作", "创建简历", "取消", mContext, this);
                    break;
                }
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                ResumeEditActivity.startAction(mContext);
                break;
            default:
                break;
        }
    }

    @Override
    public void setRequestParams(String className) {
        super.setRequestParams(className);
        getUserBasicInfo();
    }

    @Override
    public void getResponse(JSONObject response) {
        if (myProgressDialog != null)
            myProgressDialog.dismiss();
        try {
            if (response.getInt("returnCode") != 0) {
                ToastUtils.showToastInCenter(response.getString("returnDesc"));
                return;
            }
            getUserBasicInfoSuccess(response.getString("returnData"));
        } catch (Exception e) {
            setDebugLog(e);
            // ToastUtils.showToastInCenter("数据解析错误");
        }
    }

    /**
     * 获取基本信息
     */
    public void getUserBasicInfo() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(mContext));
        map.put("student_id", CacheUtils.getStudentId(mContext));
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                StringUtils.getCommonIP()
                        + GlobalContants.GET_USERBASICINFO_URL, map,
                new RequestResponseLinstner(this), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);
    }

    /**
     * 获取基本信息成功
     *
     * @param resultData
     * @throws Exception
     */
    public void getUserBasicInfoSuccess(String resultData) throws Exception {
        userinfo = TransFormModel.getResponseResultObject(resultData,
                UserInfoBean.class);
        JSONObject jsonObject = new JSONObject(resultData);
        float complete_rate = NumberUtils.getFloat(userinfo.getComplete_rate(),
                11.0f);
        if (complete_rate >= 99) {
            complete_rate = 100;
        }
        CacheUtils.saveComplete_rate(mContext, String.valueOf(complete_rate));
        resumePostStatus = NumberUtils
                .getInt(userinfo.getResumePostStatus(), 0);
        setCircleFrame(resumePostStatus);
        // if (resumePostStatus > 0) {
        // mResumeStatusIcon.setVisibility(View.VISIBLE);
        // } else {
        // mResumeStatusIcon.setVisibility(View.GONE);
        // }
        float completeRate = NumberUtils.getFloat(userinfo.getComplete_rate(),
                0.0f);
        if (completeRate <= 11 ) {
            setNotDataShow();
            return;
        }
        setExitDataShow();//
        int selectSexIndex = NumberUtils.getInt(userinfo.getGender(), 1);
        // 1男2女
        userinfo.setSexName(selectSexIndex == 1 ? "男" : "女");
        setDataShow(userinfo);
        // 初次创建简历后显示浮层
        if (mSource == 1) {

        }

        // 是否是猎头
        if (jsonObject.has("is_hunter")) {
            CacheUtils.saveIf_is_hunter(mContext,
                    jsonObject.getString("is_hunter"));
        }
        // 如果是 猎头id
        if (jsonObject.has("hunter_id")) {
            CacheUtils.save_hunter_id(mContext,
                    jsonObject.getString("hunter_id"));
        }// TODO
        if (CacheUtils.getIf_is_hunter(mContext).equals("1")) {
            // 是猎头
            rl_hunter.setVisibility(View.VISIBLE);
        } else {// 不是猎头
            rl_hunter.setVisibility(View.GONE);
        }

        if (CacheUtils.getIf_is_hunter(mContext).equals("1")) {
            // String hunterIdStr = Base64.encodeBase64String(CacheUtils
            // .get_hunter_id(mContext).getBytes());
            // String content =
            // "http://shixipre.gaodun.cn/MobileAccount/login/btn/regin/hid/"
            // + hunterIdStr;
            // // 根据猎头id设置二维码图片
            // QRCodeUtil qrCodeUtil = new QRCodeUtil();// 生成二维码工具
            // Bitmap qrcodeBitmap = qrCodeUtil.createQRCode2(mContext, null,
            // R.drawable.icon_logo_black, content);
            // qrCodeUtil = null;
        }
    }

    /**
     * 设置数据显示
     *
     * @param userinfo
     */
    public void setDataShow(UserInfoBean userinfo) {
        mNameText.setText(NumberUtils.getString(userinfo.getName(), ""));
        Drawable right = getResources().getDrawable(
                userinfo.getGender().equals("2") ? R.drawable.female
                        : R.drawable.male);
        right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
        mNameText.setCompoundDrawables(null, null, right, null);
        mSchoolNameText.setText(NumberUtils.getString(
                userinfo.getGraduate_school(), ""));
        mMajorNameText.setText(NumberUtils.getString(
                userinfo.getDetail_major(), ""));
        String education = userinfo.getEducation();
        if (StringUtils.isNotEmpty(education)) {
            String educationName = "";
            if (education.equals("1"))
                educationName = "专科";
            if (education.equals("2"))
                educationName = "本科";
            if (education.equals("3"))
                educationName = "硕士";
            if (education.equals("4"))
                educationName = "博士";
            mEducationText.setText(educationName);
        }
        float completeRate = NumberUtils.getFloat(
                CacheUtils.getComplete_rate(mContext), 0.0f);
        mResumePercentText.setText(completeRate + "");
        mPercentageView.setRefresh(true);
        mPercentageView.setPreogress(completeRate);
        mPercentageView.invalidate();
        if (StringUtils.isNotEmpty(userinfo.getAvatar()))
            setCirImage(mTopImage, userinfo.getAvatar());
    }

    /**
     * 下载头像并显示
     *
     * @param headImg
     * @param url
     */
    private void setCirImage(final ImageView headImg, final String url) {
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap arg0) {
                        MakeCircleImg cirImg = new MakeCircleImg();
                        headImg.setImageBitmap(cirImg.creatCircle(arg0));
                    }
                }, 300, 200, Config.ARGB_8888, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        BaseApplication.mApp.addToRequestQueue(imgRequest, TAG);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setErrorShow() {

    }

    /**
     * 友盟统计
     */
    // @Override
    // public void onResume() {
    // super.onResume();
    // MobclickAgent.onPageStart("positionList"); //
    // 统计页面，"positionList"为页面名称，可自定义
    // }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("positionList"); // 职位列表
    }
}
