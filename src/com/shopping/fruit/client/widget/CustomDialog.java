package com.shopping.fruit.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.util.Log;
import com.sinaapp.whutec.util.common.SysAPIUtil;

/**
 * 自定义确认对话框
 *
 * @author keshuangjie
 * @date 2015-05-23 17:54
 */
public class CustomDialog extends DialogFragment {

    private static OnEventListener mOnEventListener;

    private boolean isDestroyed = false;

    public CustomDialog() {}

    public static CustomDialog newInstance(String message, OnEventListener leristener) {
        return create(message, leristener);
    }

    private static CustomDialog create(String message,  OnEventListener listener) {
        mOnEventListener = listener;
        CustomDialog frag = new CustomDialog();
        Bundle bundle = new Bundle();
        if (message != null) {
            bundle.putString("message", message);
        }

        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ICustomDialog dialog = new ICustomDialog(getActivity(), getArguments().getString("message"));
        return dialog;
    }

    public void dismiss() {
        isDestroyed = true;
        if (this.getActivity() != null) {
            try {
                dismissAllowingStateLoss();
            } catch (Exception ex) {
                Log.d(CommonProgressDialog.class.getSimpleName(), "exception", ex);
            }
        }
    }

    private class ICustomDialog extends Dialog implements View.OnClickListener{

        private TextView tv_message, tv_confirm, tv_cancle;

        String message;

        public ICustomDialog(Context context, String message) {
            super(context, R.style.custom_dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
            this.getWindow().getAttributes().gravity = Gravity.CENTER;
            view.setMinimumWidth((int) (SysAPIUtil.getInstance().getScreenWidth() * 0.8));//设置dialog的宽度
            this.setContentView(view);
            this.message = message;
            init();
        }

        private void init() {
            tv_message = (TextView) findViewById(R.id.tv_message);
            tv_confirm = (TextView) findViewById(R.id.tv_confirm);
            tv_cancle = (TextView) findViewById(R.id.tv_cancle);

            tv_message.setText(message);
            tv_confirm.setOnClickListener(this);
            tv_cancle.setOnClickListener(this);
        }

        @Override
        public void show() {
            try {
                if (isDestroyed == false) {
                    super.show();
                } else {
                    dismissAllowingStateLoss();
                }
            } catch (Exception ex) {
                Log.d(CommonProgressDialog.class.getSimpleName(), "exception", ex);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_confirm:mOnEventListener:
                    if (mOnEventListener != null) {
                        mOnEventListener.onConfirm();
                    }
                    dismiss();
                    break;
                case R.id.tv_cancle:
                    if (mOnEventListener != null) {
                        mOnEventListener.onCancle();
                    }
                    dismiss();
                    break;
            }
        }
    }

    public interface OnEventListener {
        void onConfirm();

        void onCancle();
    }

}
