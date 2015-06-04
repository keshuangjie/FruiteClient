package com.shopping.fruit.client.base.pagestack;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.sinaapp.whutec.util.common.Log;

import java.lang.reflect.Field;
import java.util.Observable;

/**
 * 页面基类实现
 * 页面基类使用时注意View的生命周期和整个Fragment的生命周期
 */
public class BasePage extends Fragment implements Page {

    private static final String TAG = BasePage.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String STATE_BACK_KEY = "BasePage.is_back";
    private static final String STATE_BACK_ARGS = "BasePage.back_args";
    private static final String STATE_PAGE_TAG = "BasePage.page_tag";

    /**
     * 页面回退标志
     */
    protected boolean mIsBack = false;
    protected Bundle mBackArgs = null;

    /**
     * 延迟加载的 Handler
     */
    private Handler handler = new Handler(Looper.getMainLooper());

    protected static interface DelayedTask extends Runnable {
        /**
         * 如果返回值等于或者小于 0 则没有延迟间隔。
         * @return
         */
        long getDelayDelta();
    }

    protected abstract class SafeDelayedTask implements DelayedTask {
        @Override
        public final void run() {
            if (!isDetached() && getActivity() != null) {
                doSomething();
            }
        }

        public abstract void doSomething();
    }

    protected abstract class NoDeltaSafeDelayedTask extends SafeDelayedTask {
        @Override
        public final long getDelayDelta() {
            return 0;
        }
    }

    /**
     * 如果有需要延迟加载的模块代码，请重写此方法。
     * @return
     */
    protected DelayedTask onPostDelayTask() {
        return null;
    }

    /**
     * 优化 View 加载所需代码
     */
    private View pageContent;

    /**
     * 注意：该方法不是 {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * 的替代方法。但是如果该方法不返回 null 则会影响 onCreateView 方法的执行逻辑。
     * 主要是方便子类编写惰性加载逻辑。<br/>
     * 注意：一旦重写本方法，则不需要重写 onCreateView 方法。
     * 另外，如果同时重写了 onCreateView 和 {@link #onDestroyView()} 则必须返回或者调用 super 的同名方法。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    /**
     * 将子 View 的 findViewById 过程集中优化。
     * 当然为了兼容性考虑不做强制要求。
     *
     * @param view
     */
    protected void onFindViews(View view) {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isWidgetPage()) {
            final PageInfo pageInfo = new PageInfo();
            pageInfo.classname = getClass().getCanonicalName();
            pageInfo.tag = getPageLogTag();
            pageInfo.tag = pageInfo.tag == null ? "" :
                    (isNavigateBack() ? pageInfo.tag + "-" : pageInfo.tag);
            view.setTag(pageInfo);
            if (isNavigateBack()) {
                onBackFromOtherPage(mBackArgs);
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isWidgetPage()) {

            if (DEBUG) {
                Log.e(TAG, ((Object) this).getClass().getSimpleName()
                        + " onConfigurationChanged " + newConfig.orientation);
            }

            View newContent = buildOrientationContentView(newConfig);

            ViewGroup rootView = (ViewGroup) getView();
            if (newContent != null) {
                // Remove all the existing views from the root view.
                rootView.removeAllViews();
                rootView.addView(newContent);
                updateOrientationUI(newConfig);
            }
        }
    }

    private String pageTag = PageFactory.DEFAULT_PAGE_TAG;

    private Task mTask;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isWidgetPage()) {
            saveState(outState);
        }
    }

    private void saveState(Bundle outState) {
        outState.putBoolean(STATE_BACK_KEY, mIsBack);
        outState.putString(STATE_PAGE_TAG, pageTag);
        if (mBackArgs != null)
            outState.putBundle(STATE_BACK_ARGS, mBackArgs);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;

        if (!isWidgetPage()) {
            mIsBack = savedInstanceState.getBoolean(STATE_BACK_KEY);
            pageTag = savedInstanceState.getString(STATE_PAGE_TAG);
            mBackArgs = savedInstanceState.getBundle(STATE_BACK_ARGS);
            mTask = (BaseTask) getActivity();
        }
    }

    protected void setTask(Task task) {
        this.mTask = task;
    }

    @Override
    public void setPageTag(String pageTag) {
        this.pageTag = pageTag;

    }

    @Override
    final public String getPageTag() {
        return this.pageTag;
    }

    @Override
    public Task getTask() {
        return mTask;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Bundle getPageArguments() {
        return getArguments();
    }

    /**
     * @param args
     */
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPageArguments(Bundle args) {
        setArguments(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackwardArguments(Bundle args) {
        mBackArgs = args;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle getBackwardArguments() {
        return mBackArgs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNavigateBack() {
        return mIsBack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackFromOtherPage(Bundle args) {

    }

    /**
     * 触发页面回退操作
     */
    public void goBack() {
        goBack(null);
    }

    /**
     * 触发页面回退操作
     */
    public void goBack(Bundle args) {
        hideSoftInput();
        getTask().goBack(args);

    }

    protected void hideSoftInput() {
        Activity activity = getActivity();
        ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG) {
            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onCreate");

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (DEBUG) {
            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onCreateView");
        }

        // 优化 View 的 inflate 过程，保证只执行一次。
        if (pageContent == null) {
            View view = onCreatePageContent(inflater, container, savedInstanceState);
            if (view != null) {
                pageContent = view;
                onFindViews(pageContent);
                return pageContent;
            }
        } else {
            return pageContent;
        }

        // 如果 pageContent == null 并且 onCreatePageContent 方法也返回 null
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) {
            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onPause");
        }

        if (mTask != null) {
            mTask.updatePageName("");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) {
            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onResume");
        }

//        boolean shouldOverrideOrientation = this.shouldOverrideRequestedOrientation();
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            if (shouldOverrideOrientation) {
//                int orientation = getDefaultRequestedOrientation();
//                getActivity().setRequestedOrientation(orientation);
//            } else {
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//            }
//        }

        if (mTask != null) {
            mTask.updatePageName(((Object) this).getClass()
                    .getCanonicalName());
        }

        if (!isWidgetPage()) {

            // 优化延迟加载的代码
            DelayedTask delayedTask = onPostDelayTask();
            if (!isDetached() && delayedTask != null) {
                long delayDelta = delayedTask.getDelayDelta();
                if (delayDelta <= 0) {
                    handler.post(delayedTask);
                } else {
                    handler.postDelayed(delayedTask, delayDelta);
                }
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onStop");
    }

    /**
     * 增加将mChildFragmentManager置为null，子类在执行super.onDetach后不要再执行getChildFragmentManager
     * 因为使用了反射，所以今后替换support包时请验证此方法
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (!isWidgetPage()) {

            if (DEBUG) {
                Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onDetach");
            }
            mBackArgs = null;
            mIsBack = false;

            try {
                Field childFMField = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFMField.setAccessible(true);
                childFMField.set(this, null);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onDestroy");
    }

    @Override
    public void onDestroyView() {
        // 添加 View inflate 优化代码
        if (pageContent != null) {
            final ViewGroup parent = (ViewGroup) pageContent.getParent();
            if (parent != null) {
                parent.removeView(pageContent); // 防止 pageContent 被重复加载到不同的 ViewGroup 中
            }
        }
        super.onDestroyView();
        if (DEBUG) {
            Log.e(TAG, ((Object) this).getClass().getSimpleName() + " onDestroyView");
        }
    }

    /**
     * 根据横竖屏状态构建View，横竖屏切换不同布局时需要实现
     *
     * @return 所需的竖屏布局
     */
    public View buildOrientationContentView(Configuration newConfig) {
        return null;
    }

    /**
     * 更新UI界面，有横竖屏切换布局时需要实现
     * 在横竖屏切换后，更新相关的布局
     */
    public void updateOrientationUI(Configuration newConfig) {
    }

    @Override
    public void update(Observable observable, Object data) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getCustomAnimations() {
        return new int[] { 0, 0, 0, 0 };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldOverrideCustomAnimations() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPageLogTag() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getDefaultRequestedOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public boolean shouldOverrideRequestedOrientation() {
        return false;
    }

    @Override
    public boolean isWidgetPage() {
        return false;
    }
}

