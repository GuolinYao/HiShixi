package com.shixi.gaodun.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.PositionMissionBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.MyProgressDialog;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.List;

/**
 * 转发职位列表适配器
 *
 * @author guolin
 * @date:2016-2-24
 */
public class SharePositionAdapter extends CommonAdapter<PositionMissionBean>
        implements ActionSheetDialog.OnAlertSelectId {
    //
    // public SeachPositionAdapter(Activity context, ArrayList<PositionBean>
    // lists) {
    // super(context, lists);
    // // TODO Auto-generated constructor stub
    // }
    private Context context;
    private Activity activity;
    private UMImage image;
    private UMShareListener umShareListener;
    private String postId;
    private String logo;
    private String education;
    private String enterprise_name;
    private String positionName;
    private String salaryRange;
    private String postCity;
    private SpannableString spannableString;
    private String shareURL;// 职位分享链接
    private String mWeekAvailable;

    public SharePositionAdapter(Activity context,
                                List<PositionMissionBean> mDatas, int itemLayoutId) {

        super(context, mDatas, itemLayoutId);
        this.context = context;
        activity = context;

        // 开启可编辑 分享
        Config.OpenEditor = true;
        // 设置进度条
        MyProgressDialog dialog = new MyProgressDialog(context);
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

    public void setData(final ViewHolder helper, PositionMissionBean item,
                        int position) {
        postId = item.getPost_id();// 职位id
        logo = item.getLogo();// 企业logo
        education = item.getEducation();// 学历
        enterprise_name = item.getEnterprise_name();// 企业名称
        positionName = item.getTitle();// 职位名称
        salaryRange = item.getSalary_range();// 日薪
        postCity = item.getRegion_name();// 职位城市
        mWeekAvailable = item.getWeek_available();// 每周实习天数
        //猎头标签
        List<PositionMissionBean.PositionLabelBean> taglist = item.getTaglist();
        if (taglist.size() == 0 || taglist == null) {
            helper.setLinearLayoutVisibility(R.id.ll_tags, View.GONE);
        } else {
            helper.setLinearLayoutVisibility(R.id.ll_tags, View.VISIBLE);
            if (taglist.size() >= 1) {
                helper.setText(R.id.tv_tag_1, taglist.get(0).getTitle());
            }
            if (taglist.size() >= 2) {
                helper.setTextViewVisibility(R.id.tv_tag_2, View.VISIBLE);
                helper.setText(R.id.tv_tag_2, taglist.get(1).getTitle());
            }else{
                helper.setTextViewVisibility(R.id.tv_tag_2, View.GONE);
            }
            if (taglist.size() >= 3) {
                helper.setTextViewVisibility(R.id.tv_tag_3, View.VISIBLE);
                helper.setText(R.id.tv_tag_3, taglist.get(2).getTitle());
            }else{
                helper.setTextViewVisibility(R.id.tv_tag_3, View.GONE);
            }

        }
        shareURL = item.getShare_url();
        helper.setImageByUrl(R.id.iv_commpany_image, item.getLogo(),
                R.drawable.default_image_icon, 5);
        helper.setText(R.id.tv_position_name, item.getTitle());
        helper.setText(R.id.tv_publish_time, item.getCreate_time());
        helper.setText(R.id.tv_position_address, "[" + item.getRegion_name()
                + "]");
        TextView salaryRange = helper.getView(R.id.tv_salary_range);
        String str = item.getSalary_range();
        if (StringUtils.isNotEmpty(str)) {
            int endIndex = str.indexOf("元");
            if (endIndex < 0) {
                salaryRange.setText(str);
                return;
            }
            spannableString = new SpannableString(str);
            spannableString.setSpan(new ForegroundColorSpan(mContext
                            .getResources().getColor(R.color.salary_range_font_color)),
                    0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            salaryRange.setText(spannableString);
        } else {
            salaryRange.setText("");
        }
        helper.setText(R.id.tv_education, item.getEducation());
        helper.setText(R.id.tv_weekday, item.getWeek_available());
        helper.setText(R.id.tv_commpany_name, item.getEnterprise_name());
        helper.setText(R.id.tv_industry_name, item.getIndustry_name());
        helper.setText(R.id.tv_commpany_number, item.getScale_name());
        final TextView bt_share = helper.getView(R.id.tv_share);
        bt_share.setTag(position);
        // 点击转发后弹出分享页面
        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionTag = (Integer) bt_share.getTag();
                convert(helper, getItem(positionTag), positionTag);
                ActionSheetDialog.showShareAlert2(context,
                        SharePositionAdapter.this);
            }
        });
        // 设置进度条
        helper.setText(R.id.tv_finish_resume_2, " " + item.getGet_resume()
                + "/" + item.getAim_resume() + " ");
        // ProgressBar resumeProgress = helper.getView(R.id.pb_finish_resume);
        // resumeProgress.setProgress(Integer.valueOf(item.getGet_resume()));
        // resumeProgress.setMax(Integer.valueOf(item.getAim_resume()));
        helper.setProgressMax(R.id.pb_finish_resume, item.getAim_resume());
        helper.setProgress(R.id.pb_finish_resume, item.getGet_resume());

        image = new UMImage(context, logo);// 设置企业logo
    }


    @Override
    public void convert(ViewHolder helper, PositionMissionBean item,
                        int position) {
        setData(helper, item, position);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(int whichButton) {
        switch (whichButton) {
            // 微信
            case 1:
                new ShareAction(activity)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .setCallback(umShareListener)
                        .withTitle(
                                positionName + "[" + postCity + "]" + "-"
                                        + enterprise_name)
                        .withText(
                                spannableString + "  " + education + "  "
                                        + mWeekAvailable)
                        .withTargetUrl(
                                shareURL + CacheUtils.get_hunter_id(mContext) + "/"
                                        + postId).withMedia(image).share();
                break;
            // 朋友圈
            case 2:
                new ShareAction(activity)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .withTitle(
                                positionName + "[" + postCity + "]" + "-"
                                        + enterprise_name)
                        .withText(
                                spannableString + "  " + education + "  "
                                        + mWeekAvailable)
                        .withTargetUrl(
                                shareURL + CacheUtils.get_hunter_id(mContext) + "/"
                                        + postId).withMedia(image).share();
                break;
            // qq
            case 3:
                new ShareAction(activity)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .setCallback(umShareListener)
                        .withTitle(
                                positionName + "[" + postCity + "]" + "-"
                                        + enterprise_name)
                        .withText(
                                spannableString + "  " + education + "  "
                                        + mWeekAvailable)
                        .withTargetUrl(
                                shareURL + CacheUtils.get_hunter_id(mContext) + "/"
                                        + postId).withMedia(image).share();
                break;
            // 新浪微博
            case 4:
                new ShareAction(activity)
                        .setPlatform(SHARE_MEDIA.SINA)
                        .setCallback(umShareListener)
                        .withText(
                                positionName + "[" + postCity + "]" + "-"
                                        + enterprise_name + " " + spannableString
                                        + "  " + education + "  " + mWeekAvailable
                                        + " " + shareURL).withMedia(image).share();
                break;
            // 复制链接
            case 5:
                // 得到系统剪切板管理类，并把文字链接设置为剪切板的内容
                ClipboardManager cbm = (ClipboardManager) mContext
                        .getSystemService(mContext.CLIPBOARD_SERVICE);
                // cbm.setText("www.hishixi.com");
                ClipData clipData = new ClipData(ClipData.newHtmlText("高顿hi实习网站",
                        shareURL, shareURL));
                cbm.setPrimaryClip(clipData);
                if (cbm.hasPrimaryClip()) {
                    Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        umShareListener = new UMShareListener() {

            @Override
            public void onResult(SHARE_MEDIA platform) {
                Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(context, platform + " 分享失败啦", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(context, platform + " 分享取消了", Toast.LENGTH_SHORT)
                        .show();
            }
        };
    }
}
