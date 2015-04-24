package com.shopping.fruit.client.base.pagestack;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * 页面栈管理类,该类不是线程安全的，必须在主线程调用
 */
class TaskManagerImpl implements TaskManager {

    private static final boolean DEBUG = false;

    private static final String TAG = "TaskManager";

    private final ReorderStack<HistoryRecord> mHistoryList = new ReorderStack<HistoryRecord>();

    private final Map<HistoryRecord, Intent> taskIntentMap = new HashMap<HistoryRecord, Intent>();

    private HistoryRecord mRootRecord;

    private String mRootTaskName;

//    private Class<?> mContainerActivityClass;
    private SoftReference<Activity> mContainerActivity;

    /**s
     * 子级页面栈实例
     */
    private final List<SoftReference<Task>> mTaskList = new ArrayList<SoftReference<Task>>();

    private SoftReference<Task> mCurTask = new SoftReference<Task>(null);

    private static class TaskManagerHolder {
        static final TaskManager sINSTANCE = new TaskManagerImpl();
    }

    /**
     * 获取TaskManager的实例
     */
    public static TaskManager getInstance() {
        return TaskManagerHolder.sINSTANCE;
    }

    private TaskManagerImpl() {
//        EventBus.getDefault().register(this);
    }

    public void registerRootTask(String clsName) {
        mRootTaskName = clsName;
    }

    @Override
    public void attach(Activity activity) {
        mContainerActivity = new SoftReference<Activity>(activity);
//        mContainerActivityClass = activity.getClass();
    }

    @Override
    public void detach() {
        mContainerActivity = null;
    }

    public Activity getContainerActivity() {
        return mContainerActivity.get();
    }


    /**
     * 跳转基线页面
     *
     * @param ctx         所在Context
     * @param pageClsName 页面类名
     * @param pageArgs    参数
     */
    @Override
    public void navigateTo(Context ctx, String pageClsName, String pageTagString, Bundle pageArgs) {
        if (ctx == null) {
            try {
                throw new Exception("The Context is Null!!");
            } catch (Exception e) {
                return;
            }
        }

        String taskName = mRootTaskName;
        if (TextUtils.isEmpty(taskName)) {
            return;
        }

        try {
            Class<?> targetCls = Class.forName(taskName);
            if (mCurTask != null && targetCls != null) {
                Task curTask = mCurTask.get();
                if (curTask != null && targetCls.equals(curTask.getClass()) && ctx.equals(curTask)) {
                    if (DEBUG) {
                    }
                    mCurTask.get().navigateTo(pageClsName, pageTagString, pageArgs);
                    return;
                }
            }
            if (targetCls != null) {
                Intent localIntent = new Intent(ctx, targetCls);

                localIntent.setAction(ACTION_NAVIGATE_PAGE);
                localIntent.putExtra(NAVIGATE_PAGE_TAG, pageTagString);
                localIntent.putExtra(NAVIGATE_PAGE_NAME, pageClsName);

                if (pageArgs != null) {
                    localIntent.putExtra(NAVIGATE_PAGE_PARAM, pageArgs);
                }

                localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                if (!(ctx instanceof Activity)) {
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                ctx.startActivity(localIntent);
            }

        } catch (ClassNotFoundException e) {
            // 如果没有找到该类，什么也不做
        }
    }

    @Override
    public void navigateTo(Context ctx, String pageClsName) {
        navigateTo(ctx, pageClsName, null, null);
    }

    @Override
    public void navigateTo(Context ctx, String pageClsName, Bundle pageArgs) {
        navigateTo(ctx, pageClsName, null, pageArgs);
    }

    @Override
    public void navigateTo(Context ctx, String pageClsName, String pageTagString) {
        navigateTo(ctx, pageClsName, pageTagString, null);
    }

    /**
     * 跳转到指定Task,显示默认页面
     *
     * @param ctx    Context
     * @param intent Intent 参数
     */
    @Override
    public void navigateToTask(Context ctx, Intent intent) {
        if (ctx == null || intent == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "== navigateToTask == [intent]:" + intent);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        ctx.startActivity(intent);
    }

    @Override
    public void navigateToTask(Context ctx, Intent intent, int flags) {
        if (ctx == null || intent == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "== navigateToTask == [intent]:" + intent);
        }
        if (flags != 0) {
            intent.addFlags(flags);
        }
        ctx.startActivity(intent);
    }

    /**
     * 打开页面
     */
    @Override
    public void navigateTo(Context ctx, URI uri, Bundle pageArgs) {

    }

    /**
     * @hide
     */
    @Override
    public void onGoBack() {
        onGoBack(null);
    }

    /**
     * 页面回退
     * 切换Task
     *
     * @param backArgs
     *
     * @hide
     */
    @Override
    public void onGoBack(Bundle backArgs) {
        HistoryRecord target = getLatestRecord();
        if (target != null) {
            try {
                Intent backIntent = new Intent(getContext(), Class.forName(target.taskName));
                backIntent.putExtra(ACTION_NAVIGATE_BACK, true);
                backIntent.putExtra(NAVIGATE_PAGE_PARAM, backArgs);
                navigateToTask(getContext(), backIntent);
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
            }
        }
        mCurTask.clear();

        if (DEBUG) {
            Log.d(TAG, "GOBACK " + Arrays.toString(mTaskList.toArray()));
        }
    }

    // 处理跨Task back到上个task中的某个page
    protected void navigateBackFromTask(Context ctx, HistoryRecord record, Bundle pageArgs) {
        if (DEBUG) {
            Log.d(TAG, "== TaskMgr navigateBack == " + record.pageName);
        }
        if (ctx == null) {
            try {
                throw new Exception("The Context is Null!!");
            } catch (Exception e) {
                return;
            }
        }

        String taskName = record.taskName;
        if (TextUtils.isEmpty(taskName)) {
            return;
        }

        try {
            Class<?> targetCls = Class.forName(taskName);

            if (targetCls != null) {
                Intent localIntent = new Intent(ctx, targetCls);

                localIntent.setAction(ACTION_NAVIGATE_PAGE);
                localIntent.putExtra(NAVIGATE_PAGE_TAG, record.pageSignature);
                localIntent.putExtra(NAVIGATE_PAGE_NAME, record.pageName);
                localIntent.putExtra(ACTION_NAVIGATE_BACK, true);

                if (pageArgs != null) {
                    localIntent.putExtra(NAVIGATE_PAGE_PARAM, pageArgs);
                }

                localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                if (!(ctx instanceof Activity)) {
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                ctx.startActivity(localIntent);
            }

        } catch (ClassNotFoundException e) {
            // 如果没有找到该类，什么也不做
        }
    }

    /**
     * 记录页面历史
     *
     * @param record 历史记录
     */
    @Override
    public void track(HistoryRecord record) {
        if (record == null) {
            return;
        }
        mHistoryList.push(record);
        if (DEBUG) {
            Log.d(TAG, "track record:" + record + "\n" + dump());
        }
        //如果查看根页面，需要保留根页面的记录
        if (record.equals(mRootRecord) && mHistoryList.indexOf(record) != 0) {
            mHistoryList.insertElementAt(record, 0);
        }

//        if (!TextUtils.isEmpty(record.componentId)) {
//            // add comEntity RefCount
//            ComEntityFactory.addComEntityRefCount(record.componentId);
//        }
    }

    protected void track(HistoryRecord record, Intent intent) {
        taskIntentMap.put(record, intent);
    }

    protected Intent getTaskIntent(HistoryRecord record) {
        return taskIntentMap.get(record);
    }

    /**
     * 以record作为标记重置页面栈状态
     * <p>切断历史记录，将record之前的记录清掉。</p>
     *
     * @return 成功返回 true,否则返回 false
     */
    @Override
    public boolean resetStackStatus(HistoryRecord record) {
        int lastIndex = getValidRecordLastIndex(record);
        if (lastIndex == -1) {
            return false;
        }
        Stack<HistoryRecord> del = new Stack<HistoryRecord>();
        for (int i = 0; i < lastIndex; ++i) {
            //排除根节点的主页
            if (mRootRecord != null && (mHistoryList.elementAt(i).equals(mRootRecord) && i == 0)) {
                continue;
            }
            del.add(mHistoryList.elementAt(i));
        }
        for (HistoryRecord r : del) {
            removeStackRecord(r);
        }
        return true;
    }

    private int getValidRecordIndex(HistoryRecord record) {
        int retVal = -1;
        for (int i = 0, cnt = mHistoryList.size(); i < cnt; ++i) {
            if (mHistoryList.elementAt(i).equalsIgnoreSig(record)) {
                return i;
            }
        }
        return retVal;
    }

    private int getValidRecordLastIndex(HistoryRecord record) {
        int retVal = -1;
        for (int cnt = mHistoryList.size() - 1, i = cnt; i >= 0; i--) {
            if (mHistoryList.elementAt(i).equalsIgnoreSig(record)) {
                return i;
            }
        }
        return retVal;
    }

    @Override
    public synchronized void clear() {
        if (mHistoryList.isEmpty()) {
            return;
        }
        List<HistoryRecord> delRecords = new ArrayList<HistoryRecord>();
        delRecords.addAll(mHistoryList);
        for (HistoryRecord delRecord : delRecords) {
            removeStackRecord(delRecord);
        }
    }

    @Override
    public boolean removeStackRecord(HistoryRecord record) {
        if (DEBUG) {
            Log.d(TAG, "removeStackRecord\n record:" + record);
        }
        int index;
        synchronized(this) {
            index = getValidRecordLastIndex(record);
            if (index == -1) {
                return false;
            }

            mHistoryList.remove(index);
        }

        // release comentity ref
//        if (!TextUtils.isEmpty(record.componentId) && !TextUtils.equals(record.componentId, ComConstant.COM_ID_MAIN)) {
//            ComEntityFactory.releaseComEntityRefCount(record.componentId);
//        }

        Task delItem = null;
        for (SoftReference<Task> taskRef : mTaskList) {
            Task task = taskRef.get();
            if (task != null && task.getClass().getName().equals(record.taskName)) {
                if (task.getPageStack() == null || task.getPageStack().isEmpty()) {
                    if (index != 0 || !record.equals(mRootRecord)) {
                        delItem = task;
                        task.finish();
                    }
                    break;
                } else {
                    Stack<Page> pages = task.getPageStack();
                    Page topPage = pages.peek();
                    //需移除顶层页面
                    if (topPage.getClass().getName().equals(record.pageName)) {
                        pages.remove(topPage);
                        FragmentManager fm = ((BaseTask) task).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove((BasePage) topPage);
                        ft.commitAllowingStateLoss();
                        PageFactoryImpl.getInstance().removePage((BasePage) topPage);
                        topPage = null;
                        continue;
                    }
                    Page delPage = null;
                    for (Page page : pages) {
                        if (page.getClass().getName().equals(record.pageName)) {
                            delPage = page;
                            break;
                        }
                    }
                    if (delPage != null) {
                        pages.remove(delPage);
                        PageFactoryImpl.getInstance().removePage((BasePage) delPage);
                        delPage = null;
                    }
                }
            }
        }
        if (delItem != null) {
            removeTaskFromList(delItem);
            delItem = null;
        }

        Log.d(TAG, "cur records:" + dump());
        return true;
    }

    @Override
    public HistoryRecord getRootRecord() {
        return mRootRecord;
    }

    @Override
    public synchronized final HistoryRecord getLatestRecord() {
        if (mHistoryList.isEmpty()) {
            return null;
        }
        return mHistoryList.get(mHistoryList.size() - 1);
    }

    @Override
    public final ReorderStack<HistoryRecord> getHistoryRecords() {
        return mHistoryList;
    }

    @Override
    public synchronized boolean pop() {
        if (!mHistoryList.isEmpty()) {
            HistoryRecord record = mHistoryList.remove(mHistoryList.size() - 1);
            if (record != null) {
                // release comentity ref
//                if (!TextUtils.isEmpty(record.componentId)) {
//                    ComEntityFactory.releaseComEntityRefCount(record.componentId);
//                }
//                if (!IComponentManager.NEW_PLATFORM_ENABLE && !hasComponentPageInUIStack(record.componentId)) {
//                    try {
//                        final ComModel model = com.baidu.mapframework.component.comcore.manager.ComponentManager.
//                                                                                                                        getComponentManager()
//                                                       .queryComponent(record.componentId);
//                        if (model != null) {
//                            model.setComStatus(ComStatus.STOPPED);
//                        }
//                    } catch (ComException e) {
//                        Log.d(TAG, "exception", e);
//                    }
//
//                }
                return true;
            }
        }
        return false;
    }

    /**
     * 输出栈历史记录，调试用
     *
     * @return 历史记录的格式化字符串
     */
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        final ReorderStack<HistoryRecord> records = mHistoryList;
        if (records == null) {
            return "";
        }
        for (HistoryRecord r : records) {
            if (r == null) {
                continue;
            }
            sb.append("#").append(index).append(":").append(r.toString());
            index++;
        }
        return sb.toString();
    }

    @Override
    public int getStackStrategy() {
        return TaskManager.STACK_STRATEGY_REPLACE;
    }

    @Override
    public void setRootRecord(HistoryRecord record) {
        mRootRecord = record;
    }

    @Override
    public void resetRootRecord(HistoryRecord record) {
        if (DEBUG) {
            Log.d(TAG, "resetRootRecord:" + record);
        }
        mRootRecord = record;

        List<HistoryRecord> delRecords = new ArrayList<HistoryRecord>();
        delRecords.addAll(mHistoryList);
        for (HistoryRecord r : delRecords) {
            removeStackRecord(r);
        }
        track(record);
    }

    @Override
    public void resetToRootRecord() {
        if (DEBUG) {
            Log.d(TAG, "resetToRootRecord");
        }
        List<HistoryRecord> delRecords = new ArrayList<HistoryRecord>();
        delRecords.addAll(mHistoryList);
        for (HistoryRecord r : delRecords) {
            removeStackRecord(r);
        }
        track(mRootRecord);
    }

    @Override
    public void clearTop(HistoryRecord record) {
        if (DEBUG) {
            Log.d(TAG, "clearTop:" + record);
        }

        //a,b,c,d,e | ->c | a,b | -> a,b,c
        //        if(!mHistoryList.contains(record))
        //            return;
        int lastIndex = getValidRecordIndex(record);
        if (lastIndex == -1) {
            return;
        }

        //        int index = mHistoryList.indexOf(record);
        if (lastIndex >= mHistoryList.size() - 1) {
            return;
        }
        List<HistoryRecord> delRecords = new ArrayList<HistoryRecord>();
        delRecords.addAll(mHistoryList.subList(lastIndex, mHistoryList.size()));
        for (HistoryRecord delRecord : delRecords) {
            removeStackRecord(delRecord);
        }
    }

    @Override
    public void destroy() {
        if (DEBUG) {
            Log.d(TAG, "destroy");
        }
        clear();
        mTaskList.clear();
        mCurTask.clear();
        EventBus.getDefault().unregister(this);
        PageFactoryImpl.getInstance().clearCache();
    }

    @Override
    public Context getContext() {
        return (Context) mCurTask.get();
    }

    @Override
    public Parcelable saveState() {
        if (mHistoryList == null || mHistoryList.size() <= 0) {
            return null;
        }

        TaskState saveState = new TaskState();
        saveState.mRecords = new HistoryRecord[mHistoryList.size()];
        saveState.mRootRecord = mRootRecord;

        for (int i = 0, len = mHistoryList.size(); i < len; ++i) {
            saveState.mRecords[i] = mHistoryList.elementAt(i);
        }
        return saveState;
    }

    @Override
    public void restoreState(Parcelable state) {
        if (state == null) {
            return;
        }
        TaskState taskState = (TaskState) state;
        if (taskState.mRecords == null) {
            return;
        }
        mHistoryList.clear();
        Collections.addAll(mHistoryList, taskState.mRecords);
        mRootRecord = taskState.mRootRecord;
    }

    private void onEventMainThread(TaskChangeEvent event) {
        if (event.type == TaskChangeEvent.TASK_CUR_CHANGE) {
            if (DEBUG) {
                Log.d(TAG, "onTaskChangeEvent CHANGE_CUR_TASK");
            }
            mCurTask = new SoftReference<Task>(event.curTask);
            addCurTask(event.curTask);
        } else if (event.type == TaskChangeEvent.TASK_REMOVE) {
            if (DEBUG) {
                Log.d(TAG, "onTaskChangeEvent REMOVE_TASK");
            }
            removeTaskFromList(event.curTask);
            removeTaskRecord(event.curTask);
        }

        if (DEBUG) {
            Log.d(TAG, "onTaskChangeEvent curTaskList: " + Arrays.toString(mTaskList.toArray()));
        }
    }

    private void addCurTask(Task task) {
        if (task == null) {
            return;
        }

        SoftReference<Task> item = null;
        for (SoftReference<Task> ref : mTaskList) {
            if (ref.get() != null) {
                if (ref.get().equals(task)) {
                    return;
                }
            }
        }
        item = new SoftReference<Task>(task);
        mTaskList.add(item);
    }

    private void removeTaskFromList(Task task) {
        if (task == null) {
            return;
        }
        SoftReference<Task> item = null;
        for (SoftReference<Task> ref : mTaskList) {
            if (ref.get() != null) {
                if (ref.get().equals(task)) {
                    item = ref;
                    break;
                }
            }
        }
        if (item != null) {
            mTaskList.remove(item);
        }
    }

    private void removeTaskRecord(Task task) {
        HistoryRecord del = null;
        for (HistoryRecord r : mHistoryList) {
            if (r.taskName == null) {
                continue;
            }
            if (r.taskName.equals(task.getClass().getName()) && r.taskSignature != null && r.taskSignature
                                                                                                   .equals(HistoryRecord
                                                                                                                   .genSignature(task))) {
                del = r;
                break;
            }
        }
        if (del != null) {
            mHistoryList.remove(del);
        }
    }

    void updateHistoryRecord(String taskClsName, String oldSig, String newSig) {
        for (HistoryRecord r : mHistoryList) {
            if (r != null && r.taskName.equals(taskClsName) && r.taskSignature.equals(oldSig)) {
                r.taskSignature = newSig;
            }
        }
        if (mRootRecord != null && mRootRecord.taskName.equals(taskClsName) && mRootRecord.taskSignature
                                                                                       .equals(oldSig)) {
            mRootRecord.taskSignature = newSig;
        }
    }

    void clearHistoryRecords() {
        mHistoryList.clear();
    }
}

final class TaskState implements Parcelable {

    HistoryRecord[] mRecords;
    HistoryRecord mRootRecord;

    public TaskState() {
    }

    public TaskState(Parcel in) {
        mRecords = in.createTypedArray(HistoryRecord.CREATOR);
        mRootRecord = in.readParcelable(((Object) this).getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(mRecords, flags);
        dest.writeParcelable(mRootRecord, flags);
    }

    public static final Creator<TaskState> CREATOR = new Creator<TaskState>() {
        public TaskState createFromParcel(Parcel in) {
            return new TaskState(in);
        }

        public TaskState[] newArray(int size) {
            return new TaskState[size];
        }
    };
}
