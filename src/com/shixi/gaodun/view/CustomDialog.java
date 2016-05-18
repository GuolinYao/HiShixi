package com.shixi.gaodun.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.view.activity.LoginActivity;

import java.util.ArrayList;

/**
 * 确定对话框
 *
 * @author ronger
 * @date:2015-10-19 下午5:02:55
 */
@SuppressLint("InflateParams")
public class CustomDialog {

    /**
     * 不带标题的提示对话框
     *
     * @param str
     * @param confirmStr
     * @param cancelStr
     * @param context
     * @param listener
     * @return
     */
    @SuppressLint("InflateParams")
    public static Dialog CancelAlertToDialog(final String str,
                                             final String confirmStr, final String cancelStr, Context context,
                                             android.view.View.OnClickListener listener) {
        final Dialog dlg = new Dialog(context, R.style.CancelTheme_DataSheet);// 对话框主题
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.dialog_notitle_base, null);
        LayoutParams params = new LayoutParams(ActivityUtils.getScreenWidth()
                - ActivityUtils.dip2px(context, 90), LayoutParams.WRAP_CONTENT);
        TextView strDes = (TextView) layout.findViewById(R.id.tv_confirm_des);
        strDes.setText(str);
        final TextView confirmBtn = (TextView) layout
                .findViewById(R.id.tv_confrim_btn);
        confirmBtn.setText(confirmStr);
        final TextView cancelBtn = (TextView) layout
                .findViewById(R.id.tv_cancel_btn);
        cancelBtn.setText(cancelStr);
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER_VERTICAL;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout, params);
        dlg.show();
        return dlg;
    }

    /**
     * 未登陆提示对话框
     *
     * @param btnOne
     * @param btnTwo
     * @param btnThree
     * @param context
     * @param listener
     * @return
     */
    @SuppressLint("InflateParams")
    public static Dialog AlertToCustomDialog(Context context,
                                             android.view.View.OnClickListener listener) {

        if (!CacheUtils.getIsFirstLogin(context)) {
            BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
            LoginActivity.startAction((BaseMainActivity) context, 1, false);
            return null;
        }


        final Dialog dlg = new Dialog(context, R.style.CancelTheme_DataSheet);// 对话框主题
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.login_dialog_layout, null);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ActivityUtils.dip2px(context, 230), LayoutParams.WRAP_CONTENT);

        Button dialogTitle = (Button) layout
                .findViewById(R.id.btn_select_title);
        Button twoBtn = (Button) layout.findViewById(R.id.btn_select_two);
        Button threeBtn = (Button) layout.findViewById(R.id.btn_select_three);
        Button dialCancel = (Button) layout
                .findViewById(R.id.btn_select_cancel);
        dialCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();

            }
        });
        dialogTitle.setText("是否已在Hi实习网站注册过？");
        twoBtn.setText("是，直接登录");
        threeBtn.setText("我是新用户，去注册");
        dialCancel.setText("再逛逛");
        twoBtn.setOnClickListener(listener);
        threeBtn.setOnClickListener(listener);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER_VERTICAL;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout, params);
        dlg.show();
        return dlg;
    }

    /**
     * 设置隐藏动画
     *
     * @param view
     * @param Visibility
     */
    public static void hideListAnimation(final View view, final int visibility) {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1,
                1f);
        ta.setDuration(200);
        view.startAnimation(ta);
        ta.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
            }
        });
    }

    /**
     * 显示动画
     */
    public static void showListAnimation(View view) {
        TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1,
                0f);
        ta.setDuration(200);
        view.startAnimation(ta);
    }

    /**
     * 带标题的提示框
     *
     * @param warnTitle
     * @param warnContent
     * @param leftBtn
     * @param rightBtn
     * @param listener
     * @param context
     * @return
     */
    public static Dialog warnDialogHasTitle(final String warnTitle,
                                            final String warnContent, final String leftBtn,
                                            final String rightBtn, android.view.View.OnClickListener listener,
                                            final Context context) {
        final Dialog dlg = new Dialog(context, R.style.CancelTheme_DataSheet);// 对话框主题
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.dialog_base, null);
        TextView tvTitle = (TextView) layout.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(StringUtils.isNotEmpty(warnTitle) ? warnTitle : "温馨提示");
        TextView strDes = (TextView) layout.findViewById(R.id.tv_confirm_des);
        strDes.setText(warnContent);
        final TextView confirmBtn = (TextView) layout
                .findViewById(R.id.tv_title_confrim_btn);
        confirmBtn.setText(rightBtn);
        final TextView cancelBtn = (TextView) layout
                .findViewById(R.id.tv_title_cancel_btn);
        cancelBtn.setText(leftBtn);
        cancelBtn.setOnClickListener(listener);
        confirmBtn.setOnClickListener(listener);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER_VERTICAL;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(false);
        LayoutParams params = new LayoutParams(ActivityUtils.getScreenWidth()
                - ActivityUtils.dip2px(context, 90), LayoutParams.WRAP_CONTENT);
        dlg.setContentView(layout, params);
        dlg.show();
        return dlg;
    }

    /**
     * 投递简历时建立完整度提示对话框
     *
     * @param str
     * @param confirmStr
     * @param cancelStr
     * @param context
     * @param listener
     * @return
     */
    public static Dialog integrityAlertToDialog(final String title,
                                                final String des, final String leftStr, final String rightStr,
                                                Context context, android.view.View.OnClickListener listener) {
        final Dialog dlg = new Dialog(context, R.style.CancelTheme_DataSheet);// 对话框主题
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout layout = (FrameLayout) inflater.inflate(
                R.layout.integrity_dialog_layout, null);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                ActivityUtils.getScreenWidth() - 20, LayoutParams.WRAP_CONTENT);
        // android.widget.FrameLayout.LayoutParams params = new
        // android.widget.FrameLayout.LayoutParams(
        // ActivityUtils.getScreenWidth() - ActivityUtils.dip2px(context, 32),
        // LayoutParams.WRAP_CONTENT);
        ImageButton cancelBtn = (ImageButton) layout
                .findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        TextView titleText = (TextView) layout
                .findViewById(R.id.tv_dialog_title);
        titleText.setText(title);
        TextView descText = (TextView) layout.findViewById(R.id.tv_confirm_des);
        descText.setText(des);
        TextView leftText = (TextView) layout
                .findViewById(R.id.tv_title_cancel_btn);
        leftText.setText(leftStr);
        TextView rightText = (TextView) layout
                .findViewById(R.id.tv_title_confrim_btn);
        rightText.setText(rightStr);
        leftText.setOnClickListener(listener);
        rightText.setOnClickListener(listener);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER_VERTICAL;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout, params);
        dlg.show();
        return dlg;
    }

    /**
     * 我的页面的浮层
     *
     * @return
     */
    public static Dialog SupernatantAlertToDialog(Context context) {
        final Dialog dlg = new Dialog(context, R.style.Supernatant_Theme);// 对话框主题
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.tab_mine_supernatant_layout, null);
        android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                ActivityUtils.getScreenWidth(), ActivityUtils.getScreenHeight());
        // 设置点击对话框本身，让其消失
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout, params);
        dlg.show();
        return dlg;
    }
}
