package com.shixi.gaodun.view.activity.enterprise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.inf.ScrollViewListener;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.FlowLayout;
import com.shixi.gaodun.view.MyProgressDialog;
import com.shixi.gaodun.view.ObservableScrollView;
import com.shixi.gaodun.view.activity.BasicInfoOneActivity;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.ResumeEditActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职位详情页
 *
 * @author ronger guolin
 * @date:2015-11-11 下午4:34:35
 */
public class PositionDetailActivity extends TitleBaseActivity implements
        ScrollViewListener, OnAlertSelectId {
    private String mPositionId;
    private ObservableScrollView mScrollView;
    private TextView mPositionNameText, mDailyWageText, mWorkdDaysText,
            mEducationText, mMajorRequestText, mGradeTimeText,
            mWorkAddressText;
    private TextView mCompanyNameText, mScaleText, mIndustryText;
    private TextView mPositionDesText;
    private ImageView mCompanyImage;
    private LinearLayout mLinearLayout;
    private Button mButton;
    private PositionBean positionBean;
    private int[] mPositionLocation = new int[2];
    // 职位名称下面的控件相对父布局的高度
    private int mBasicInfoTop;
    private Dialog mDialog;
    private boolean ifGoon;
    // 0获取详情信息，1投递简历
    private int requestType;
    private int mCollectStatus;
    private int mWhichActivity; // 哪个Activity跳转过来的 1 表示正常职位列表跳转来的 可以投递 可以收藏 2
    // 表示从猎头任务列表跳转来的 不能投递 能分享
    private UMShareListener umShareListener;
    private String positionName;
    private String postCity;
    private String enterprise_name;
    private SpannableString spannableString;
    private String education;
    private String postId;
    private UMImage image;
    private String logo;
    private String school_hunter_id;// 校园猎头id
    private String mShareURL;
    private String mWeekAvailable;
    private FlowLayout mLlTags;//职位标签
    private TextView mTvHunterTagsTitle;//猎头标签标题
    private View mViewLine2;//猎头标签下的线

    // public static void startAction(Activity context, String id) {
    // Intent intent = new Intent(context, PositionDetailActivity.class);
    // intent.putExtra("id", id);
    // context.startActivity(intent);
    // }

    /**
     * @param context
     * @param id
     * @param whichActivity 是哪个activity跳转过来的 1 表示正常职位列表跳转来的 可以投递 可以收藏 2 表示从猎头任务列表跳转来的 不能投递
     *                      能分享
     */
    public static void startAction(Activity context, String id,
                                   int whichActivity) {
        Intent intent = new Intent(context, PositionDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("whichActivity", whichActivity);
        context.startActivity(intent);
    }

    @Override
    protected void getIntentData() {
        mPositionId = getIntent().getStringExtra("id");
        mWhichActivity = getIntent().getIntExtra("whichActivity", 1);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        getIntentData();
        super.onCreate(arg0);
        isExcute = true;
        setContentView(R.layout.position_detail_layout);
        // if (StringUtils.isEmpty(mPositionId)) {
        // ToastUtils.showToastInCenter("职位失效");
        // return;
        // }
        setRequestParamsPre(TAG, true);
        setFilterRegister();
    }

    @Override
    public void setBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                requestType = 0;
                toRequestServer(intent);
            }
        };
    }

    @Override
    public void initView() {
        super.initView();
        int textWidth = ActivityUtils.getScreenWidth()
                - ActivityUtils.dip2px(this, 120);
        mTitleName.setMaxWidth(textWidth);
        mTitleName.setText("职位详情");
        mBackIcon.setImageResource(R.drawable.icon_back);
        // mRightOneLayout.setVisibility(View.VISIBLE);
        mScrollView = (ObservableScrollView) findViewById(R.id.scrollView);
        mScrollView.setScrollViewListener(this);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_basicinfo_layout);
        mPositionNameText = (TextView) findViewById(R.id.tv_positionname);
        mDailyWageText = (TextView) findViewById(R.id.tv_daily_wage);
        mWorkdDaysText = (TextView) findViewById(R.id.tv_work_days);
        mEducationText = (TextView) findViewById(R.id.tv_education);
        mMajorRequestText = (TextView) findViewById(R.id.tv_major_request_value);
        mGradeTimeText = (TextView) findViewById(R.id.tv_gradetime_value);
        mWorkAddressText = (TextView) findViewById(R.id.tv_work_address);
        mCompanyImage = (ImageView) findViewById(R.id.iv_commpany_image);
        mCompanyNameText = (TextView) findViewById(R.id.tv_commpany_name);
        mScaleText = (TextView) findViewById(R.id.tv_address_scale);
        mIndustryText = (TextView) findViewById(R.id.tv_industry);
        mTvHunterTagsTitle = (TextView) findViewById(R.id.tv_hunter_tags_title);
        mViewLine2 = (View) findViewById(R.id.view_line2);
        mButton = (Button) findViewById(R.id.btn_delivery_resume);
        mLlTags = (FlowLayout) findViewById(R.id.fl_tags);
        if (mWhichActivity == 2) {
            mButton.setVisibility(View.GONE);
        } else {
            mButton.setVisibility(View.VISIBLE);
        }
        mButton.setText("投递简历");
        mPositionDesText = (TextView) findViewById(R.id.tv_position_value);
        findViewById(R.id.rl_enterpriseinfo_layout).setOnClickListener(this);
        mButton.setOnClickListener(this);

        // // 开启可编辑 分享
        Config.OpenEditor = false;
        // 设置进度条
        MyProgressDialog dialog = new MyProgressDialog(this);
        Dialog createPD = dialog.createPD();
        Window window = createPD.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.7f;
        window.setAttributes(params);
        // dialog.setTitle("11");
        // dialog.setMessage("222");
        Config.dialog = createPD;
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_again:
                setRetryRequest();
                break;
            case R.id.fl_title_bar_back:
                finish();
                break;
            // case R.id.fl_title_bar_two:
            // if (null == positionBean)
            // break;
            // ActionSheetDialog.showShareAlert(this, this);
            // break;
            case R.id.fl_title_bar_one:
                if (null == positionBean)
                    break;
                if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
                    mDialog = CustomDialog.AlertToCustomDialog(this, this);
                    break;
                }
                requestType = 2;
                if (mWhichActivity == 1) {
                    setRequestParamsPre(TAG, true);
                } else if (mWhichActivity == 2) {// 猎头模块跳转过来的
                    ActionSheetDialog.showShareAlert2(this, this);
                }

                break;
            case R.id.fl_title_bar_two:
                if (null == positionBean)
                    break;
                if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
                    mDialog = CustomDialog.AlertToCustomDialog(this, this);
                    break;
                }
                // requestType = 2;
                if (mWhichActivity == 1) {
                    ActionSheetDialog.showShareAlert(this, this);
                } else if (mWhichActivity == 2) {// 猎头模块跳转过来的
                    // ActionSheetDialog.showShareAlert2(this, this);
                }

                break;
            case R.id.rl_enterpriseinfo_layout:
                if (null == positionBean)
                    break;
                if (mWhichActivity == 1) {
                    EnterpriseDetailActivity.startAction(this,
                            positionBean.getEnterprise_id(), 1);// 正常职位进入
                } else if (mWhichActivity == 2) {// 职位是猎头任务
                    EnterpriseDetailActivity.startAction(this,
                            positionBean.getEnterprise_id(), 2);
                }

                // CompanyDetailActivity.startAction(this,
                // positionBean.getEnterprise_id());
                break;
            case R.id.btn_delivery_resume:
                if (mWhichActivity == 2) {
                    break;
                }
                if (null == positionBean)
                    break;
                if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
                    if (CacheUtils.getIsFirstLogin(this)) {
                        mDialog = CustomDialog.AlertToCustomDialog(this, this);
                    } else {
                        BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                        LoginActivity.startAction(this, 1, false);
                    }
                    break;
                }
                if (NumberUtils.getFloat(CacheUtils.getComplete_rate(this), 0.0f) <= 11
                        || positionBean.getResume_id().length() == 0) {
                    ifGoon = false;
                    mDialog = CustomDialog.CancelAlertToDialog("你还没有简历", "创建简历",
                            "再逛逛", this, this);
                    break;
                }
                if (NumberUtils.getFloat(CacheUtils.getComplete_rate(this), 0.0f) <= 60) {
                    ifGoon = true;
                    mDialog = CustomDialog.integrityAlertToDialog("简历完整度低",
                            "你的简历完整度低于60%，很容易石沉大海或被HR拒掉哦。", "继续投递", "完善简历", this,
                            this);
                    break;
                }
                requestType = 1;
                setRequestParamsPre(TAG, true);
                break;
            case R.id.btn_select_two:// 去登陆
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                LoginActivity.startAction(this, 1, false);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                mDialog.dismiss();
                break;
            case R.id.btn_select_three:// 去注册
                BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
                RegisterActivity.startAction(this, 1, top);
                mDialog.dismiss();
                break;
            case R.id.tv_title_cancel_btn:// 继续投递
                mDialog.dismiss();
                requestType = 1;
                setRequestParamsPre(TAG, true);
                break;
            case R.id.tv_title_confrim_btn:// 完善简历
                mDialog.dismiss();
                ResumeEditActivity.startAction(this);
                break;
            // 创建简历
            case R.id.tv_confrim_btn:
                mDialog.dismiss();
                BasicInfoOneActivity.startAction(this);
                // if (ifGoon) {
                // ResumeEditActivity.startAction(this);
                // } else {
                // BasicInfoOneActivity.startAction(this);
                // }
                break;
            // 取消
            case R.id.tv_cancel_btn:
                mDialog.dismiss();
                // if (mDialog.isShowing())
                // mDialog.dismiss();
                // if (ifGoon) {
                // requestType = 1;
                // setRequestParamsPre(TAG, true);
                // }
                break;
            default:
                break;
        }
    }

    @Override
    public void setRequestParams(String className) {
        super.setRequestParams(className);
        if (requestType == 1) {
            handInResume();
            return;
        }
        if (requestType == 2) {
            collectResume();
            return;
        }
        getPositionDetail();
    }

    /**
     * 获取职位详情信息
     */
    public void getPositionDetail() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(this));
        map.put("id", mPositionId);
        if (mWhichActivity == 2) {// 如果是猎头任务页面跳转过去的 那么请求参数加入school_hunter_id
            map.put("school_hunter_id", CacheUtils.get_hunter_id(this));
        }
        if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this)))
            map.put("student_id", CacheUtils.getStudentId(this));
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                StringUtils.getCommonIP()
                        + GlobalContants.GETPOSITIONDETAIL_URL, map,
                new RequestResponseLinstner(this), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);
    }

    /**
     * 投递简历
     */
    public void handInResume() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(this));
        map.put("student_id", CacheUtils.getStudentId(this));
        map.put("post_id", mPositionId);
        map.put("enterprise_id", positionBean.getEnterprise_id());
        map.put("resume_id", positionBean.getResume_id());
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                StringUtils.getCommonIP() + GlobalContants.HANDINRESUME_URL,
                map, new RequestResponseLinstner(new ResponseCallBack() {
            @Override
            public void getResponse(JSONObject response) {
                myProgressDialog.dismiss();
                try {
                    HttpRes httpRes = TransFormModel
                            .getResponseData(response);
                    if (httpRes.getReturnCode() != 0) {
                        ToastUtils.showToastInCenter(httpRes
                                .getReturnDesc());
                        return;
                    }
                    ToastUtils.showCustomToastInCenter(
                            "投递成功，请耐心等待企业反馈",
                            PositionDetailActivity.this);
                    // 更改投递状态
                    changePostIconStatus("2");
                } catch (Exception e) {
                    myProgressDialog.dismiss();
                    ToastUtils.showToastInCenter("数据解析错误");
                }
            }
        }), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);
    }

    /**
     * 收藏取消收藏简历
     */
    public void collectResume() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(this));
        map.put("student_id", CacheUtils.getStudentId(this));
        map.put("post_id", mPositionId);
        String url;
        if (mCollectStatus == 1) {// 状态为已收藏，点击为取消收藏
            map.put("type", "1");
            url = StringUtils.getCommonIP() + GlobalContants.DELETECOLLECT_URL;
        } else {
            url = StringUtils.getCommonIP()
                    + GlobalContants.COLLECTPOSITION_URL;
        }
        JsonObjectPostRequest request = new JsonObjectPostRequest(url, map,
                new RequestResponseLinstner(new ResponseCallBack() {
                    @Override
                    public void getResponse(JSONObject response) {
                        myProgressDialog.dismiss();
                        try {
                            HttpRes httpRes = TransFormModel
                                    .getResponseData(response);
                            if (httpRes.getReturnCode() != 0) {
                                ToastUtils.showToastInCenter(httpRes
                                        .getReturnDesc());
                                return;
                            }
                            // if (mCollectStatus == 2) {
                            // ToastUtils.showToastInCenter("收藏成功");
                            // mCollectStatus = 1;
                            // }
                            if (mCollectStatus == 1) {
                                ToastUtils.showToastInCenter("取消收藏成功");
                                mCollectStatus = 2;
                            } else {
                                ToastUtils.showToastInCenter("收藏成功");
                                mCollectStatus = 1;
                            }
                            changeCollectIconStatus(mCollectStatus);
                        } catch (Exception e) {
                            myProgressDialog.dismiss();
                            ToastUtils.showToastInCenter("数据解析错误");
                        }
                    }
                }), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);
    }

    @Override
    public void getResponse(JSONObject response) {
        super.getResponse(response);
        dissMissProgress();
        try {
            HttpRes httpRes = TransFormModel.getResponseData(response);
            if (httpRes.getReturnCode() != 0) {
                ToastUtils.showToastInCenter(httpRes.getReturnDesc());
                return;
            }
            setRightOneIconShow();
            setNolmalShow();
            positionBean = TransFormModel.getResponseResultObject(
                    httpRes.getReturnData(), PositionBean.class);
            mShareURL = positionBean.getShare_url();
            setPositionInfo(positionBean);
            if (StringUtils.isNotEmpty(CacheUtils.getAccountId(this))) {
                CacheUtils.saveComplete_rate(this,
                        positionBean.getComplete_rate());
                CacheUtils.saveComplete_rate_en(this,
                        positionBean.getComplete_rate_en());
            }
            setPositionDetailShow(positionBean);
            setIconStatus();
        } catch (Exception e) {
            setDebugLog(e);
            ToastUtils.showToastInCenter("数据解析错误");
        }
    }

    private void setPositionInfo(PositionBean pb) {
        positionName = pb.getTitle();
        postCity = pb.getRegion_name();
        logo = pb.getLogo();
        // logo = "http://www.umeng.com/images/pic/social/integrated_3.png";
        enterprise_name = pb.getEnterprise_name();
        mWeekAvailable = pb.getWeek_available();
        String str = pb.getSalary_range();
        // if (str == null) {
        // str = "";
        // }
        int endIndex = str.indexOf("元");
        spannableString = new SpannableString(str);
        if (endIndex >= 0) {
            spannableString.setSpan(new ForegroundColorSpan(this.getResources()
                            .getColor(R.color.salary_range_font_color)), 0, endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        education = pb.getEducation();
        postId = pb.getPkid();
        // image;
        image = new UMImage(this, logo);// 设置企业logo

        if (pb.getTaglist() == null || pb.getTaglist().size() == 0) return;
        List<PositionBean.PositionLabelBean> taglist = pb.getTaglist();

        mViewLine2.setVisibility(View.VISIBLE);
        mTvHunterTagsTitle.setVisibility(View.VISIBLE);
        mLlTags.setVisibility(View.VISIBLE);
        mLlTags.removeAllViews();
        for (int i = 0; i < taglist.size(); i++) {
            TextView textView = new TextView(this);
            //设置宽高
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置背景图片
            textView.setBackgroundResource(R.drawable.rectangle_tags);
            // 设置字体颜色 大小
            textView.setTextColor(getResources().getColor(R.color.form_value_color));
            textView.setTextSize(13f);
            textView.setPadding(ActivityUtils.dip2px(this, 2.5f), ActivityUtils.dip2px(this, 2.5f),
                    ActivityUtils.dip2px(this, 2.5f), ActivityUtils.dip2px(this, 2.5f));

            textView.setText(taglist.get(i).getTitle());
//            mLlTags.setHorizontalSpacing(ActivityUtils.dip2px(this, 2.5f));
            mLlTags.addView(textView);
        }
    }

    /**
     * 设置详情显示
     *
     * @param positionBean
     */
    public void setPositionDetailShow(PositionBean positionBean) {
        setTextShow(mPositionNameText, positionBean.getTitle(), "");
        setTextShow(mDailyWageText, positionBean.getSalary_range(), "");
        setTextShow(mWorkdDaysText, positionBean.getWeek_available(), "");
        setTextShow(mMajorRequestText, positionBean.getMajor_wish(), "");
        setTextShow(mGradeTimeText, positionBean.getGraduate_year(), "");
        setTextShow(mWorkAddressText, positionBean.getAddress(), "");
        setTextShow(mEducationText, positionBean.getEducation(), "");
        setTextShow(mCompanyNameText, positionBean.getEnterprise_name(), "");
        setTextShow(
                mScaleText,
                NumberUtils.getString(positionBean.getEnter_city_name(), "")
                        + NumberUtils.getString(positionBean.getScale_title(),
                        ""), "");
        setTextShow(mIndustryText, positionBean.getIndustry_name(), "");
        setTextShow(mPositionDesText, positionBean.getDescription(), "");
        BaseApplication.mApp.setRoundedImageByUrl(mCompanyImage,
                positionBean.getLogo(), R.drawable.default_image_icon, 5);
        // setImageByUrl(mCompanyImage, positionBean.getLogo(),
        // R.drawable.default_image_icon, 0);
        // try {
        // BaseApplication.mApp.getImageLoader().displayImage(positionBean.getLogo(),
        // mCompanyImage);
        // } catch (Exception e) {
        // mCompanyImage.setImageDrawable(this.getResources().getDrawable(R.drawable.touxiang_listdefault));
        // }
    }

    /**
     * 设置按钮的状态
     */
    public void setIconStatus() {
        String collectStatus = positionBean.getCollect_status();
        mCollectStatus = NumberUtils.getInt(collectStatus, 2);
        changeCollectIconStatus(mCollectStatus);
        String postStatus = positionBean.getCan_post();
        if (StringUtils.isNotEmpty(postStatus)) {
            changePostIconStatus(postStatus);
        }
    }

    public void changePostIconStatus(String postStatus) {
        if (postStatus.equals("2")) {
            mButton.setTextColor(Color.parseColor("#a5a5a5"));
            mButton.setBackgroundColor(Color.parseColor("#e0e0e0"));
            mButton.setText("已投递");
            mButton.setEnabled(false);
            return;
        } else {
            mButton.setTextColor(Color.parseColor("#ffffff"));
            mButton.setBackgroundColor(getResources().getColor(
                    R.color.selected_font_color));
            mButton.setText("投递简历");
            mButton.setEnabled(true);
        }
    }

    public void changeCollectIconStatus(int status) {
        if (mWhichActivity == 2) {// 是HunterJobsActivity跳转过来的
            mRightOneIcon.setImageResource(R.drawable.hunter_share);
        } else {// 是MainActivity跳转过来的
            mRightTwoLayout.setVisibility(View.VISIBLE);
            mRightTwoIcon.setImageResource(R.drawable.hunter_share);
            mRightOneIcon.setImageResource(status == 1 ? R.drawable.favouriteh
                    : R.drawable.favourite);
        }
    }

    public void setTextShow(TextView textView, String str, String defaultStr) {
        textView.setText(StringUtils.isEmpty(str) ? defaultStr : str);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 获取职位名称在window中的坐标位置
        mPositionNameText.getLocationInWindow(mPositionLocation);
        mBasicInfoTop = mLinearLayout.getTop();
        // mPostionHeightText = mPositionNameText.getHeight();
        // mScrollViewHeight = mScrollView.getHeight();
    }

    /**
     * 对ScrollView的滑动监听，主要用来在滑动到影藏了职位名称时将职位名称显示在标题栏
     */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
                                int oldx, int oldy) {
        if (y >= mBasicInfoTop) {
            mTitleName.setText(positionBean.getTitle());
        } else {
            mTitleName.setText("职位详情");
        }
    }

    // 分享按钮的点击事件
    @SuppressLint("NewApi")
    @Override
    public void onClick(int whichButton) {
        if (mWhichActivity == 1) {// 学生职位跳转
            switch (whichButton) {
                // 微信
                case 1:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // 朋友圈
                case 2:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // qq
                case 3:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // 新浪微博
                case 4:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.SINA)
                            .setCallback(umShareListener)
                            .withText(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable + " "
                                            + spannableString + "  " + education
                                            + " " + mShareURL).withMedia(image)
                            .share();
                    break;
            }

        } else {// 猎头任务列表跳转
            switch (whichButton) {
                // 微信
                case 1:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // 朋友圈
                case 2:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // qq
                case 3:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .setCallback(umShareListener)
                            .withTitle(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable)
                            .withText(spannableString + "  " + education)
                            .withTargetUrl(mShareURL).withMedia(image).share();
                    break;
                // 新浪微博
                case 4:
                    new ShareAction(this)
                            .setPlatform(SHARE_MEDIA.SINA)
                            .setCallback(umShareListener)
                            .withText(
                                    positionName + "[" + postCity + "]" + "-"
                                            + enterprise_name + "  "
                                            + mWeekAvailable + " "
                                            + spannableString + "  " + education
                                            + " " + mShareURL).withMedia(image)
                            .share();
                    break;
                // 复制链接
                case 5:
                    // 得到系统剪切板管理类，并把文字链接设置为剪切板的内容
                    ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    // cbm.setText("www.hishixi.com");
                    ClipData clipData = new ClipData(ClipData.newHtmlText(
                            "高顿hi实习网站", mShareURL, mShareURL));
                    cbm.setPrimaryClip(clipData);
                    if (cbm.hasPrimaryClip()) {
                        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                    }
                    break;

            }

        }

        umShareListener = new UMShareListener() {

            @Override
            public void onResult(SHARE_MEDIA platform) {
                Toast.makeText(PositionDetailActivity.this,
                        platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(PositionDetailActivity.this,
                        platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(PositionDetailActivity.this,
                        platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };
    }

    // @Override
    // public void onClick(int whichButton) {
    // if (whichButton == 1) {// 微信
    //
    // }
    // if (whichButton == 2) {// 朋友圈
    //
    // }
    // if (whichButton == 3) {// QQ
    //
    // }
    // if (whichButton == 4) {// 微博
    //
    // }
    // }

    @Override
    protected void setRetryRequest() {
        setRequestParamsPre(TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (isExcute && isFirstJoin && requestType == 0) {
            setOnErrorResponse(error);
            return;
        }
        nomalOnErrorResponse(error);
    }

    /**
     * 友盟统计
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("positionDetail"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("positionDetail");
        MobclickAgent.onPause(this);
    }
}
