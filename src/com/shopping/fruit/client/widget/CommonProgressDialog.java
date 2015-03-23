package com.shopping.fruit.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.util.Log;

/**
 * loading框封装
 * @author keshuangjie
 * @date 2015-3-18 11:52
 */
public class CommonProgressDialog extends DialogFragment {
    private static final String TITLE = "title";
    private static final String MESSAGE = "message";
    private static final String LAYOUT_RESID = "layout_resid";

    private static DialogInterface.OnCancelListener mCancelListener;

    private boolean isDestroyed = false;

    public static CommonProgressDialog newInstance(String title, String message) {
        return create(title, message, 0, null);
    }

    public static CommonProgressDialog newInstance(String title, String message,
            DialogInterface.OnCancelListener cancelListener) {
        return create(title, message, 0, cancelListener);
    }

    public static CommonProgressDialog newInstance(int layoutResId, DialogInterface.OnCancelListener cancelListener) {
        return create(null, null, layoutResId, cancelListener);
    }

    private static CommonProgressDialog create(String title, String message, int layoutResId,
            DialogInterface.OnCancelListener cancelListener) {
        mCancelListener = cancelListener;
        CommonProgressDialog frag = new CommonProgressDialog();
        Bundle bundle = new Bundle();
        if (title != null) {
            bundle.putString(TITLE, title);
        }
        if (message != null) {
            bundle.putString(MESSAGE, message);
        }
        bundle.putInt(LAYOUT_RESID, layoutResId);

        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog progressDlg = new INProgressDialog(getActivity(), getArguments().getInt(LAYOUT_RESID));
        return progressDlg;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mCancelListener != null) {
            mCancelListener.onCancel(dialog);
        }
        mCancelListener = null;
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

    public class INProgressDialog extends Dialog {
        private static final int COMMON_RESID = R.layout.progress_dialog;
        private int mLayoutResId = COMMON_RESID;

        public INProgressDialog(Context context, int layoutResId) {
            super(context, R.style.theme_comm_progressdlg);

            if (layoutResId != 0) {
                mLayoutResId = layoutResId;
            }
            this.setContentView(mLayoutResId);
            this.getWindow().getAttributes().gravity = Gravity.CENTER;
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
    }
}