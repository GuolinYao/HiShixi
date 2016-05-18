package com.shixi.gaodun.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.db.IndexPageDB;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.CutPhotoUtils;
import com.shixi.gaodun.model.utils.HttpUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;
import com.shixi.gaodun.view.MakeCircleImg;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本信息引导页1
 *
 * @author guolinyao
 * @date 2016年4月14日 上午9:37:24
 */
@SuppressLint("HandlerLeak")
public class BasicInfoOneActivity extends TitleBaseActivity implements
        OnAlertSelectId {
    private ImageView mHeadImage;
    private TextView mName, mSex, mBirth;
    private TextView mIdentity_card, mPolitics_status, mPresent_address,
            mGraduate_year, mSchool_name, mHighest_education;
    private int requestType, mSelectSexIndex = -1,
            mSelectPoliticsStatusIndex = -1;
    private CityBean mSelectCity;
    private File mFile;
    // true提交基本信息或false提交个人头像
    private boolean ifCommitBasicOrHeadimg = true;
    private DateWheelDialog mDateWheelDialog;
    private String mUserPic;
    private Dialog mDialog;
    private IndexPageDB mIndexPageDB;
    // 上一次进入时存储的基本信息
    private UserInfoBean mOldBasicInfo;
    private int mSelectGraduateIndex = -1;//选择的毕业年份下标
    private InternshipIntentionBean mInternshipIntention;

    public static void startAction(Activity context) {
        Intent intent = new Intent(context, BasicInfoOneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        isExcute = false;
        mIndexPageDB = IndexPageDB.getInstance(this);
        mOldBasicInfo = mIndexPageDB
                .getBasicInfo(CacheUtils.getStudentId(this));
        BaseApplication.mApp.mCenterTaskActivitys.add(this);
        setContentView(R.layout.basicinfo_one_layou);
    }

    @Override
    public void initView() {
        super.initView();
        mTitleName.setText("基本信息（1/2）");
        mBackIcon.setVisibility(View.GONE);
        mOtherName.setVisibility(View.VISIBLE);
        mOtherName.setText("跳过");
        mOtherName.setTextColor(Color.parseColor("#00acf0"));
        // mHeadImage = (ImageView) findViewById(R.id.detail_shop_image);
        mName = (TextView) findViewById(R.id.tv_basicinfo_name);
        mSex = (TextView) findViewById(R.id.tv_basicinfo_sex);
        mBirth = (TextView) findViewById(R.id.tv_basicinfo_birth);
        // mIdentity_card = (TextView) findViewById(R.id.tv_identity_card);
        mPresent_address = (TextView) findViewById(R.id.tv_present_address);
        // mPolitics_status = (TextView) findViewById(R.id.tv_politics_status);
        mGraduate_year = (TextView) findViewById(R.id.tv_graduate_year);
        mSchool_name = (TextView) findViewById(R.id.tv_school_name);
        mHighest_education = (TextView) findViewById(R.id.tv_highest_education);

        setRequestParamsPre(TAG);
    }

    /**
     * 初始化数据显示
     */
    public void initDataShow() {
        if (null == mOldBasicInfo)
            return;
        // mUserPic = mOldBasicInfo.getAvatar();
        // // 头像
        // BaseApplication.mApp.setCircleImageByUrl(mHeadImage,
        // mOldBasicInfo.getAvatar(),
        // R.drawable.default_touxiang_xiaoerhei);
        // setImageByUrl(mHeadImage, mOldBasicInfo.getAvatar(),
        // R.drawable.morenidtouxiang, 50);
        // 姓名
        setTextViewShow(mName, mOldBasicInfo.getName());
        // 性别
        int sex = NumberUtils.getInt(mOldBasicInfo.getGender(), 3);
        if(sex==1||sex==2){
            setTextViewShow(mSex, sex == 1 ? "男" : "女");
        }
        // 出生日期
        setTextViewShow(mBirth, mOldBasicInfo.getBirth_date());
        // // 身份证
        // setTextViewShow(mIdentity_card, mOldBasicInfo.getId_number());
        // // 政治面貌
        // setTextViewShow(mPolitics_status,
        // mOldBasicInfo.getPolitics_status_name());

        setTextViewShow(mGraduate_year, mOldBasicInfo.getGraduate_year());
        setTextViewShow(mSchool_name, mOldBasicInfo.getGraduate_school());
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

        // 现居地
        mSelectCity = mOldBasicInfo.getLiving_city();
        if (null == mSelectCity)
            return;
        setTextViewShow(mPresent_address, mSelectCity.getRegion_name());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_bar_otherbtn:
                mDialog = CustomDialog.CancelAlertToDialog("跳过将失去已填写的信息", "跳过",
                        "继续填写", this, this);
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
            // case R.id.rl_head_layout:
            // ActionSheetDialog.showAlert(this, this, "拍照", "手机相册");
            // break;
            case R.id.rl_name_layout:
                requestType = 0;
                BasicInfoSetActivity.startAction(this, requestType,
                        Finals.TOEDITNAME_REQUEST, mName.getText().toString());
                break;
            case R.id.rl_sex_layout:
                PoliticsStatusOrSexActivity.startAction(this,
                        Finals.TOEDITSEX_REQUEST, mSex.getText().toString(),
                        mSelectSexIndex, 0);
                break;
            case R.id.rl_birth_layout:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                mDateWheelDialog = new DateWheelDialog(this, year, month, day);
                mDateWheelDialog.show();
                DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog
                        .getmDateWheelFrameLayout();
                setDateCallBack(mDateWheelFrameLayout);
                break;
            // case R.id.rl_identity_card_layout:
            // requestType = 1;
            // BasicInfoSetActivity.startAction(this, requestType,
            // Finals.TOEDITINDENTITY_REQUEST, mIdentity_card.getText()
            // .toString());
            // break;
            // case R.id.rl_politics_status_layout:
            // PoliticsStatusOrSexActivity.startAction(this,
            // Finals.TOPOLITICSSTATUS_REUQEST, mPolitics_status.getText()
            // .toString(), mSelectPoliticsStatusIndex, 1);
            // break;
            case R.id.rl_present_address_layout:
                SelectPresentAddressActivity.startAction(this,
                        Finals.TPSELECTADDRESS_REQUEST, mSelectCity);
                break;
            // 毕业年份
            case R.id.rl_graduate_year:
                SelectGraduateYearActivity.startAction(this,
                        Finals.TOEDITGRADUATEYEAR, mGraduate_year.getText()
                                .toString(), mSelectGraduateIndex);
                break;
            // 就读学校
            case R.id.rl_school_name_layout:
                BasicInfoSetActivity.startAction(this, 2,
                        Finals.TOEDITSCHOOLNAME_REQUEST, mSchool_name.getText()
                                .toString());
                break;
            // 最高学历
            case R.id.rl_highest_education_layout:
                EducationListActivity.startAction(this,
                        Finals.TOSELECTHIGHEST_EDUCATION_REQUEST,
                        mHighest_education.getText().toString());
                break;
            case R.id.btn_next:
                ifCommitBasicOrHeadimg = true;
                commitBasicInfoPre();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mIndexPageDB.clearBasicInfo(CacheUtils.getStudentId(this));
        } catch (Exception e) {
            setDebugLog(e);
        }
    }

    public void commitBasicInfoPre() {
        // if (StringUtils.isEmpty(mUserPic)) {
        // ToastUtils.showToastInCenter("请选择头像");
        // return;
        // }
        if (StringUtils.isEmpty(mName.getText().toString())) {
            ToastUtils.showToastInCenter("请填写姓名");
            return;
        }
        if (StringUtils.isEmpty(mSex.getText().toString())) {
            ToastUtils.showToastInCenter("请选择性别");
            return;
        }
        if (StringUtils.isEmpty(mBirth.getText().toString())) {
            ToastUtils.showToastInCenter("请选择出生日期");
            return;
        }
        if (StringUtils.isEmpty(mPresent_address.getText().toString())) {
            ToastUtils.showToastInCenter("请选择现居地");
            return;
        }
        if (StringUtils.isEmpty(mGraduate_year.getText().toString())) {
            ToastUtils.showToastInCenter("请选择毕业年份");
            return;
        }
        if (StringUtils.isEmpty(mSchool_name.getText().toString())) {
            ToastUtils.showToastInCenter("请填写就读学校");
            return;
        }
        if (StringUtils.isEmpty(mHighest_education.getText().toString())) {
            ToastUtils.showToastInCenter("请选择最高学历");
            return;
        }
        saveBasicInfo();
    }

    @Override
    public void setRequestParams(String className) {
        super.setRequestParams(className);
        // uploadPhoto(mFile);
        Map<String, String> map = new HashMap<String, String>();
        map.put("ticket", CacheUtils.getToken(this));
        map.put("student_id", CacheUtils.getStudentId(this));
        JsonObjectPostRequest request = new JsonObjectPostRequest(
                StringUtils.getCommonIP() + GlobalContants.GET_GUIDE_INFO,
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
                    getBasicInfoSuccess(response.getString("returnData"));

                } catch (Exception e) {
                    dissMissProgress();
                    setDebugLog(e);
                }
            }
        }), this);
        BaseApplication.mApp.addToRequestQueue(request, TAG);

    }

    /**
     * 获取基本信息
     */
    private void getBasicInfoSuccess(String returnData) {
        try {
            mOldBasicInfo = TransFormModel.getResponseResultObject(returnData,
                    UserInfoBean.class);
            mInternshipIntention = new InternshipIntentionBean();
            // 期望实习地
            mInternshipIntention.setExpect_city(mOldBasicInfo.getExpect_city());
            // 每周实习天数
            String day = mOldBasicInfo.getWeek_available();
            if (StringUtils.isNotEmpty(day) && day.contains("天")) {
                day = day.replace("天", "");
            }
            mInternshipIntention.setWeek_available(day);
            // 期望日薪
            mInternshipIntention.setSalary_range(mOldBasicInfo.getSalary_range());
            // 期望实习职位
            if (null != mOldBasicInfo.getExpect_category()) {
//				MapFormatBean positionBean = new MapFormatBean(
//						mSelectPosition.getPkid(), mSelectPosition.getTitle());
                mInternshipIntention.setExpect_category(mOldBasicInfo.getExpect_category());
            }
            // 期望实习阶段
            if (StringUtils.isNotEmpty(mOldBasicInfo.getPeriod_finish())
                    && StringUtils.isNotEmpty(mOldBasicInfo.getPeriod_start())) {
                mInternshipIntention.setPeriod_start(mOldBasicInfo.getPeriod_start());
                mInternshipIntention.setPeriod_finish(mOldBasicInfo.getPeriod_finish());
            }
            mOldBasicInfo.setmIntershipBean(mInternshipIntention);
            dissMissProgress();
            initDataShow();
        } catch (Exception e) {
            dissMissProgress();
            e.printStackTrace();
        }
    }


    /**
     * 提交基本信息成功
     *
     * @param resultData
     */
    public void commitBasicInfoSuccess(String resultData) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayedJump();
            }
        }, Finals.delayTime);
    }

    @Override
    public void delayedJump() {
        super.delayedJump();
    }

    @Override
    public void onClick(int whichButton) {
        // 拍照
        // if (whichButton == 1) {
        // Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // // 下面这句指定调用相机拍照后的照片存储的路径
        // intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
        // .fromFile(new File(Environment
        // .getExternalStorageDirectory(), "photo.jpg")));
        // startActivityForResult(intentCamera, Finals.TOCAMERA_REQUEST);
        // return;
        // }
        // // 从手机相册选择
        // if (whichButton == 2) {
        // Intent intentPick = new Intent(Intent.ACTION_PICK, null);
        // intentPick.setDataAndType(
        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        // startActivityForResult(intentPick, Finals.TOPICK_REQUEST);
        // }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // if (requestCode == Finals.TOCAMERA_REQUEST) {
        // toCameraReturn(arg2);
        // }
        // if (requestCode == Finals.TOPICK_REQUEST) {
        // toPickReturn(arg2);
        // }
        // if (requestCode == Finals.TOCUTPHOTO_REQUEST) {
        // toCutPhotoReturn(arg2);
        // }
        if (requestCode == Finals.TOEDITNAME_REQUEST) {
            toEditNameReturn(arg2);
        }
        if (requestCode == Finals.TOEDITSEX_REQUEST) {
            toSelectSexReturn(arg2);
        }
        // if (requestCode == Finals.TOEDITINDENTITY_REQUEST) {
        // toEditIdentityCardReturn(arg2);
        // }
        // if (requestCode == Finals.TOPOLITICSSTATUS_REUQEST) {
        // toSelectPoliticsStatusReturn(arg2);
        // }
        if (requestCode == Finals.TPSELECTADDRESS_REQUEST) {
            toSelectAddressReturn(arg2);
        }
        if (requestCode == Finals.TOEDITGRADUATEYEAR) {
            toSelectGraduateYearReturn(arg2);
        }
        if (requestCode == Finals.TOEDITSCHOOLNAME_REQUEST) {
            toEditSchoolNameReturn(arg2);
        }
        if (requestCode == Finals.TOSELECTHIGHEST_EDUCATION_REQUEST) {
            toSelectHighestEducationReturn(arg2);
        }
        if (requestCode == Finals.REQUESTCODE_ONE) {
            // 跟新上一页面更新过的数据，防止本页点击下一页时将上次的数据覆盖
            mOldBasicInfo = mIndexPageDB.getBasicInfo(CacheUtils
                    .getStudentId(this));
        }
    }

    /**
     * 去相机返回
     *
     * @param data
     */
    public void toCameraReturn(Intent data) {
        File picture = new File(Environment.getExternalStorageDirectory() + "/"
                + "photo.jpg");
        startActivityForResult(
                CutPhotoUtils.startPhotoZoom(Uri.fromFile(picture)),
                Finals.TOCUTPHOTO_REQUEST);
    }

    /**
     * 去相册返回
     *
     * @param data
     */
    public void toPickReturn(Intent data) {
        if (null == data.getData())
            return;
        startActivityForResult(CutPhotoUtils.startPhotoZoom(data.getData()),
                Finals.TOCUTPHOTO_REQUEST);
    }

    /**
     * 裁剪图片返回
     *
     * @param data
     */
    public void toCutPhotoReturn(Intent data) {
        if (data != null) {
            setPicToView(data);
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
     * 选择性别返回
     *
     * @param data
     */
    public void toSelectSexReturn(Intent data) {
        String resultStr = data.getStringExtra("selectStr");
        mSelectSexIndex = data.getIntExtra("selectIndex", -1);
        mSex.setText(resultStr);
    }

    /**
     * 编辑身份证号返回
     *
     * @param data
     */
    public void toEditIdentityCardReturn(Intent data) {
        String resultStr = data.getStringExtra("resultInfo");
        mIdentity_card.setText(resultStr);
    }

    /**
     * 选择政治面貌返回
     *
     * @param data
     */
    public void toSelectPoliticsStatusReturn(Intent data) {
        String resultStr = data.getStringExtra("selectStr");
        mSelectPoliticsStatusIndex = data.getIntExtra("selectIndex", -1);
        mPolitics_status.setText(resultStr);
    }

    /**
     * 选择现居地后返回
     *
     * @param data
     */
    public void toSelectAddressReturn(Intent data) {
        mSelectCity = (CityBean) data.getSerializableExtra("selectCity");
        mPresent_address.setText(mSelectCity.getRegion_name());
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
     * 设置裁剪图片后的显示
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Bitmap cirbitmap = new MakeCircleImg().creatCircle(bitmap);
            // mHeadImage.setImageResource(getResources().getColor(R.color.transparent));
            mHeadImage.setImageBitmap(cirbitmap);
            mFile = BitMapUtils.writeToSDCard(bitmap, this);
            bitmap.recycle();
            ifCommitBasicOrHeadimg = false;
            checkExitToken();
        }
    }

    /**
     * 上传图片前检测是否存在token或者token是否有效
     */
    public void checkExitToken() {
        myProgressDialog.setTitle("上传图片中...");
        setRequestParamsPre(TAG);
    }

    /**
     * 提交基本信息
     * <p/>
     * 姓名、性别、出生日期、现居地，毕业年份，就读学校，最高学历
     */
    private void saveBasicInfo() {
        if (null == mOldBasicInfo) {
            mOldBasicInfo = new UserInfoBean();
        }
        mOldBasicInfo.setName(mName.getText().toString());
        mOldBasicInfo.setGender(mSex.getText().toString().equals("男") ? "1"
                : "2");
        // mOldBasicInfo.setAvatar(mUserPic);
        mOldBasicInfo.setBirth_date(mBirth.getText().toString());
        // mOldBasicInfo.setId_number(mIdentity_card.getText().toString());
        // String politics_status = mPolitics_status.getText().toString();
        // if (StringUtils.isNotEmpty(politics_status)) {
        // mOldBasicInfo
        // .setPolitics_status(politics_status.equals("非党员") ? "2"
        // : "1");
        // mOldBasicInfo.setPolitics_status_name(politics_status);
        // }
        if (StringUtils.isNotEmpty(mPresent_address.getText().toString())
                && null != mSelectCity) {
            mOldBasicInfo.setLiving_city(mSelectCity);
        }
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
        BasicInfoTwoActivity.startAction(this, Finals.REQUESTCODE_ONE);
    }

    /**
     * 上传用户头像
     *
     * @param file
     */
    private void uploadPhoto(final File file) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("ticket", CacheUtils
                        .getToken(BasicInfoOneActivity.this)));
                params.add(new BasicNameValuePair("student_id", CacheUtils
                        .getStudentId(BasicInfoOneActivity.this)));
                params.add(new BasicNameValuePair("type", "1"));
                try {
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = HttpUtils.postSingleFile(
                            StringUtils.getCommonIP()
                                    + GlobalContants.UPLOAD_URL, params, file,
                            "headpic");
                    uploadPhotoHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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

    /**
     * 上传图片完成
     */
    private Handler uploadPhotoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                HttpRes message = (HttpRes) msg.obj;
                myProgressDialog.dismiss();
                try {
                    if (message.getReturnCode() != 0) {
                        ToastUtils.showToastInCenter(message.getReturnDesc());
                        return;
                    }
                    // ToastUtils.showToastInCenter("上传图片成功");
                    mUserPic = message.getReturnData();
                } catch (Exception e) {
                }

            }
        }
    };

    @Override
    protected void getIntentData() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setRetryRequest() {
        // mMainNoneDataLayout.setVisibility(View.GONE);
        // setRequestParamsPre(TAG);

    }

    /**
     * 友盟统计
     */
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("oneGuid"); // 引导页1
        MobclickAgent.onResume(this); // 统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("oneGuid");
        MobclickAgent.onPause(this);
    }
}
