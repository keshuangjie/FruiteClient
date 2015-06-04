package com.shopping.fruit.client.widget;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;

import java.util.List;

/**
 * ProgressDialog封装
 * @author keshuangjie
 * @date 2015-3-18 11:52
 */
public class DialogUtil {
    static CommonProgressDialog mBMProgressDialog;
    static CustomDialog mCustomDialog;
    static final int RUNNING_TASK_SIZE = 1;

    public static void showCustomDialog(FragmentActivity fragActivity, String message,
                                   CustomDialog.OnEventListener listener) {
        dismiss();
        if (isActivityRunning(fragActivity) && fragActivity.getSupportFragmentManager() != null) {
            try {
                mCustomDialog = CustomDialog.newInstance(message, listener);
                mCustomDialog.show(fragActivity.getSupportFragmentManager(), "customDialog");
                /*
                 * 立即执行，因为调用show()后 可能会立即调用dismiss()
                 * 使用executePendingTransactions(),在Page的OnViewCreated()中调用MProgressDialog.show()会引起
                 * illegalstateexception recursive entry to execute pending transactions
                 */
                //          fragActivity.getSupportFragmentManager().executePendingTransactions();
            } catch (Exception ex) {
            }
        }
    }

    private static void showDialog(FragmentActivity fragActivity, int layoutResId, String title, String message,
                                   DialogInterface.OnCancelListener cancelListener) {
        dismiss();
        if (isActivityRunning(fragActivity) && fragActivity.getSupportFragmentManager() != null) {
            try {
                mBMProgressDialog = CommonProgressDialog.newInstance(layoutResId, cancelListener);
                mBMProgressDialog.show(fragActivity.getSupportFragmentManager(), "BMProgressDialog");
                /*
                 * 立即执行，因为调用show()后 可能会立即调用dismiss()
                 * 使用executePendingTransactions(),在Page的OnViewCreated()中调用MProgressDialog.show()会引起
                 * illegalstateexception recursive entry to execute pending transactions
                 */
                //          fragActivity.getSupportFragmentManager().executePendingTransactions(); 
            } catch (Exception ex) {
            }
        }
    }

    public static void show(FragmentActivity fragActivity, String title, String message,
                            DialogInterface.OnCancelListener cancelListener) {
        showDialog(fragActivity, 0, title, message, cancelListener);
    }

    public static void show(FragmentActivity fragActivity, String title, String message) {
        showDialog(fragActivity, 0, title, message, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    public static void show(FragmentActivity fragActivity, int layoutResId,
                            DialogInterface.OnCancelListener cancelListener) {
        showDialog(fragActivity, layoutResId, null, null, cancelListener);
    }

    public static void show(FragmentActivity fragActivity, DialogInterface.OnCancelListener cancelListener) {
        showDialog(fragActivity, 0, null, null, cancelListener);
    }

    public static void dismiss() {
        if (mBMProgressDialog != null && mBMProgressDialog.getFragmentManager() != null) {
            mBMProgressDialog.dismiss();
        }
        mBMProgressDialog = null;
    }

    public static void dismiss2() {
        if (mCustomDialog != null && mCustomDialog.getFragmentManager() != null) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = null;
    }

    private static boolean isActivityRunning(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return false;
        } else {
            ActivityManager activityManager = null;
            try {
                activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                if (activityManager != null) {
                    List<ActivityManager.RunningTaskInfo> runningTask = activityManager.getRunningTasks(RUNNING_TASK_SIZE);
                    if (runningTask != null) {
                        for (ActivityManager.RunningTaskInfo info : runningTask) {
                            if (info.topActivity.equals(activity.getComponentName())) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
