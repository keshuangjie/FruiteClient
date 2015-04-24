package com.shopping.fruit.client.base.pagestack;

import java.util.ArrayList;
import java.util.Stack;

import de.greenrobot.event.EventBus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;

import com.shopping.fruit.client.R;

/**
 * Task的基类实现，以FragmentActivity作为载体，子页面用Fragment实现 </p>
 */
public abstract class BaseTask extends ActionBarActivity implements Task {

	// protected static final boolean UI_DEBUG = true;
	private static final boolean DEBUG = false;
	private static final String TAG = "BaseTask";
	private String taskTag;
	protected ReorderStack<Page> pageStack = new ReorderStack<Page>();

	private static final String TASKMGR_STATE_KEY = "key_task_state";
	private static final String TASK_SIG_KEY = "key_task_sig";

	static final String FRAGMENTS_TAG = "android:support:fragments";

	// 是否调用了onDestroy API17 新增了 isDestroyed()
	boolean mDestroyed = false;

	@Override
	public TaskManager getTaskManager() {
		return TaskManagerFactory.getTaskManager();
	}

	@Override
	public void setTaskTag(String taskTag) {
		this.taskTag = taskTag;
	}

	@Override
	public final String getTaskTag() {
		return this.taskTag;
	}

	@Override
	public Stack<Page> getPageStack() {
		return pageStack;
	}

	@Override
	public void navigateTo(String pageClsName, String pageTagString,
			Bundle pageArgs) {
		if (DEBUG) {
			Log.d(TAG, "=== navigateTo === " + pageClsName);
		}
		if (mDestroyed) {
			return;
		}

		BasePage page = PageFactoryImpl.getInstance().getBasePageInstance(
				pageClsName, pageTagString);
		if (page == null) {
			return;
		}

		page.setTask(this);
		if (pageTagString != null) {
			page.setPageTag(pageTagString);
		}

		BasePage top = null;
		if (!pageStack.isEmpty()) {
			top = (BasePage) pageStack.peek();
			// 如果顶层页面和要跳转到的页面相同
			if (top.equals(page)) {
				boolean ret = false;
				try {
					ret = getSupportFragmentManager()
							.executePendingTransactions();
				} catch (IllegalStateException e) {
					if (DEBUG) {
						Log.e(TAG, "", e);
					}
				}
				Fragment f = getSupportFragmentManager().findFragmentByTag(
						((Object) page).getClass().getName()
								+ page.getPageTag());
				if (f != null) {
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					ft.remove(page);
					ft.commitAllowingStateLoss();
					try {
						getSupportFragmentManager().popBackStackImmediate();
					} catch (Exception e) {
						if (DEBUG) {
							Log.e(TAG, "", e);
						}
					}

					page.mIsBack = false;
					page.mBackArgs = null;
					try {
						page.setArguments(pageArgs);
						FragmentTransaction ftr = getSupportFragmentManager()
								.beginTransaction();
						ftr.add(R.id.fragment_container, page, ((Object) page)
								.getClass().getName() + page.getPageTag());
						ftr.commitAllowingStateLoss();
						// add the page to page-stack
						// if the record has been in stack,bring it to front
						recordPageNavigation(page);
					} catch (Exception e) {
						if (DEBUG) {
							Log.e(TAG, "", e);
						}
					}

				} else if (!ret && !page.isAdded()) {
					page.mIsBack = false;
					page.mBackArgs = null;
					try {
						getSupportFragmentManager().popBackStackImmediate();
						page.setArguments(pageArgs);
						FragmentTransaction ftr = getSupportFragmentManager()
								.beginTransaction();
						ftr.add(R.id.fragment_container, page, ((Object) page)
								.getClass().getName() + page.getPageTag());
						ftr.commitAllowingStateLoss();
						// add the page to page-stack
						// if the record has been in stack,bring it to front
						recordPageNavigation(page);
					} catch (Exception e) {
						if (DEBUG) {
							Log.e(TAG, "", e);
						}
					}

				}
				return;
			}
		}
		try {
			page.setArguments(pageArgs);
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "", e);
			}
		}
		// add the page to page-stack
		// if the record has been in stack,bring it to front
		recordPageNavigation(page);
		if (DEBUG) {
			Log.i(TAG, getTaskManager().dump());
		}
		navigateAction(top, page, getTaskManager().getStackStrategy());
	}

	@Override
	public void onShowDefaultContent(Intent intent) {
		if (DEBUG) {
			Log.d(TAG, "onShowDefaultContent");
		}
	}

	/**
	 * 返回
	 * 
	 * @param args
	 *            附加参数
	 * 
	 * @return 需要外层TaskManager处理返回 false，不需要返回 true
	 */
	@Override
	public boolean goBack(Bundle args) {
		if (mDestroyed) {
			return false;
		}
		HistoryRecord record = getTaskManager().getLatestRecord();
		if (record == null) {
			return false;
		}
		// 当前task
		if (record.taskName.equals(((Object) this).getClass().getName())) {
			getTaskManager().pop();
			if (DEBUG) {
				Log.i(TAG, getTaskManager().dump());
			}

			// 当前task中无page时back
			if (record.pageName == null && pageStack.isEmpty()) {

				HistoryRecord nextRecord = getTaskManager().getLatestRecord();

				if (nextRecord != null) {
					// 纯task跳转
					if (!TextUtils.isEmpty(nextRecord.taskName)
							&& nextRecord.pageName == null) {
						Intent backIntent = ((TaskManagerImpl) getTaskManager())
								.getTaskIntent(nextRecord);
						if (backIntent != null) {
							backIntent.putExtra(
									TaskManager.ACTION_NAVIGATE_BACK, true);
							backIntent.putExtra(
									TaskManager.NAVIGATE_PAGE_PARAM, args);
							getTaskManager().navigateToTask(this, backIntent);
						}

					} else if (!TextUtils.isEmpty(nextRecord.pageName)
							&& !nextRecord.taskName.equals(((Object) this)
									.getClass().getName())) {
						// back到上个task中的page
						((TaskManagerImpl) getTaskManager())
								.navigateBackFromTask(this, nextRecord, args);
					}
				}

				// 结束该Task
				finish();
				return true;
			} else {
				HistoryRecord nextRecord = getTaskManager().getLatestRecord();
				if (pageStack.isEmpty()) {
					if (nextRecord != null) {
						navigateTo(nextRecord.pageName,
								nextRecord.pageSignature, args);
						return true;
					} else {
						finish();
						return false;
					}

				} else if (nextRecord != null
						&& !nextRecord.taskName.equals(((Object) this)
								.getClass().getName())) {
					Page topPage = pageStack.peek();
					pageStack.pop();
					if (topPage != null) {
						FragmentTransaction ft = getSupportFragmentManager()
								.beginTransaction();
						ft.remove((BasePage) topPage);
						ft.commitAllowingStateLoss();
						PageFactoryImpl.getInstance().removePage(
								(BasePage) topPage);
					}
					try {
						Intent backIntent = new Intent(this,
								Class.forName(nextRecord.taskName));
						backIntent.putExtra(TaskManager.ACTION_NAVIGATE_BACK,
								true);
						backIntent.putExtra(TaskManager.NAVIGATE_PAGE_PARAM,
								args);
						getTaskManager().navigateToTask(this, backIntent);
					} catch (Exception e) {
						if (DEBUG) {
							Log.e(TAG, "", e);
						}
					}
					return true;
				} else {
					Page topPage = pageStack.peek();
					pageStack.pop();
					if (pageStack.isEmpty()) {
						FragmentTransaction ft = getSupportFragmentManager()
								.beginTransaction();

						if (pageStack.isEmpty()) {
							ft.remove((BasePage) topPage);
							ft.commitAllowingStateLoss();
							PageFactoryImpl.getInstance().removePage(
									(BasePage) topPage);

							// 如果还有历史，则到上个历史页面，否则finish该task
							// HistoryRecord nextRecord =
							// getTaskManager().getLatestRecord();
							if (nextRecord != null) {
								navigateTo(nextRecord.pageName,
										nextRecord.pageSignature, args);
								return true;
							} else {
								// AppLauncher.getInstance().onQuitApp(this);
								// test 退出app
								finish();
							}

							return false;
						}
					} else {
						return navigateBackAction((BasePage) topPage,
								getTaskManager().getStackStrategy(), args);
					}
				}

			}
		} else {
			// throw new IllegalStateException("Illegal Page Stack State!");
			// 如果栈状态错误，结束该Task
			finish();
		}
		return false;
	}

	@Override
	public boolean goBack() {
		return goBack(null);
	}

	private Bundle getArguments() {
		Intent localIntent = getIntent();
		if (localIntent == null) {
			return null;
		} else {
			return localIntent.getBundleExtra(TaskManager.NAVIGATE_PAGE_PARAM);
		}
	}

	@Override
	public boolean handleBack(Bundle args) {
		return false;
	}

	@Override
	/**
	 * @see {@link android.support.v4.app.FragmentActivity#startActivityFromFragment }
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		boolean isCalled = false;
		int index = (requestCode >> 16) - 1;
		if (index >= 0) {
			Bundle bundle = new Bundle();
			String key = "index";
			bundle.putInt(key, index);
			Fragment frag = null;
			try {
				frag = this.getSupportFragmentManager()
						.getFragment(bundle, key);
			} catch (IllegalStateException e) {
				if (DEBUG) {
					Log.e(TAG, "", e);
				}
			}
			if (frag == null) {
				// 没有找到发起调用的fragment，说明不是在fragment内部调用的startActivityForResult
				// 顶层的fragment没有收到onActivityResult，这里主动再调一次
				isCalled = false;
			} else {
				// super里面已经调用过，不用再调
				isCalled = true;
			}
		} else {
			// index为0，也说明不是在fragment内部调用的startActivityForResult，需要主动再调
			isCalled = false;
		}

		if (!isCalled) {
			if (!pageStack.isEmpty()) {
				Page topPage = pageStack.peek();
				if (topPage != null) {
					try {
						// 主动调用
						((Fragment) topPage).onActivityResult(requestCode,
								resultCode, data);
					} catch (ClassCastException e) {
						// Page都是从Fragment继承的，理论上不会到这里，如果以后又重构，可能要修改这里
						if (DEBUG) {
							Log.e(TAG, "", e);
						}
					}
				}
			}
		}

	}

	@Override
	public void finish() {
		if (DEBUG) {
			Log.d(TAG, "Finish:" + this);
		}
		// 清理page
		Stack<Page> pages = getPageStack();
		if (pages != null && !pages.isEmpty()) {
			pages.clear();
		}
		removeHistoryRecords();
		if (DEBUG) {
			Log.d(TAG, "removeHistoryRecords end\n" + getTaskManager().dump());
		}
		super.finish();
	}

	/**
	 * 清理该Task中的记录
	 */
	private boolean removeHistoryRecords() {
		if (DEBUG) {
			Log.d(TAG, "removeHistoryRecords");
		}
		ReorderStack<HistoryRecord> historyRecords = TaskManagerFactory
				.getTaskManager().getHistoryRecords();
		ArrayList<HistoryRecord> delItems = new ArrayList<HistoryRecord>();
		if (historyRecords != null) {
			for (HistoryRecord record : historyRecords) {
				if (record == null
						|| record.taskName.equals(((Object) this).getClass()
								.getName())
						&& record.taskSignature.equals(HistoryRecord
								.genSignature(this))) {
					delItems.add(record);
				}
			}
			return historyRecords.removeAll(delItems);
		}
		return false;
	}

	protected void create(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			if (savedInstanceState.getParcelable(TASKMGR_STATE_KEY) != null) {
				getTaskManager().restoreState(
						savedInstanceState.getParcelable(TASKMGR_STATE_KEY));

				String newSig = HistoryRecord.genSignature(this);
				String oldSig = savedInstanceState.getString(TASK_SIG_KEY);
				if (!TextUtils.isEmpty(oldSig)) {
					((TaskManagerImpl) getTaskManager()).updateHistoryRecord(
							((Object) this).getClass().getName(), oldSig,
							newSig);
				}
				// 其实恢复历史记录没有用处
				((TaskManagerImpl) getTaskManager()).clearHistoryRecords();
				notifyTaskChange();
			}
			return;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (savedInstanceState != null) {
			if (savedInstanceState.getParcelable(TASKMGR_STATE_KEY) != null) {
				getTaskManager().restoreState(
						savedInstanceState.getParcelable(TASKMGR_STATE_KEY));

				String newSig = HistoryRecord.genSignature(this);
				String oldSig = savedInstanceState.getString(TASK_SIG_KEY);
				if (!TextUtils.isEmpty(oldSig)) {
					((TaskManagerImpl) getTaskManager()).updateHistoryRecord(
							((Object) this).getClass().getName(), oldSig,
							newSig);
				}
				// 其实恢复历史记录没有用处,还要清空
				((TaskManagerImpl) getTaskManager()).clearHistoryRecords();
				if (DEBUG) {
					Log.e(TAG,
							"Activity restore:" + savedInstanceState.toString());
				}
			}

			notifyTaskChange();

			if (getTaskManager().getRootRecord() != null
					&& !((Object) this).getClass().getName()
							.equals(getTaskManager().getRootRecord().taskName)) {
				finish();
			}
			return;
		}

		notifyTaskChange();

//		// 处理需要跳转到子页面
//		Intent localIntent = getIntent();
//		if (localIntent != null) {
//			// args
//			Bundle pageArgs = getArguments();
//			// navigate page
//			// if(TaskManager.ACTION_NAVIGATE_PAGE.equals(localIntent.getAction()))
//			{
//				String pageName = localIntent
//						.getStringExtra(TaskManager.NAVIGATE_PAGE_NAME);
//				String pageTag = localIntent
//						.getStringExtra(TaskManager.NAVIGATE_PAGE_TAG);
//				// no page,show default content ,the param is intent
//				if (TextUtils.isEmpty(pageName)) {
//					onShowDefaultContent(localIntent);
//					boolean isReorderTask = localIntent.getBooleanExtra(
//							TaskManager.NAVIGATE_REORDER_TASK, false);
//					if (!isReorderTask && !isTaskNaviBack(localIntent)) {
//						recordTaskNavigation(localIntent);
//					}
//					// activityLifecycleCallbacks.onActivityCreated(this,savedInstanceState);
//				} else {
//					// navigate to page
//					this.navigateTo(pageName, pageTag, pageArgs);
//				}
//			}
//		}
	}

	private boolean isTaskNaviBack(Intent intent) {
		return intent != null
				&& intent.getBooleanExtra(TaskManager.ACTION_NAVIGATE_BACK,
						false);
	}

	private void notifyTaskChange() {
		TaskChangeEvent event = new TaskChangeEvent();
		event.type = TaskChangeEvent.TASK_CUR_CHANGE;
		event.curTask = this;
		EventBus.getDefault().post(event);
	}

	protected void newIntent(Intent intent) {
		if (DEBUG) {
			Log.e(TAG, "onNewIntent");
		}
		super.onNewIntent(intent);
		this.setIntent(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (DEBUG) {
			Log.e(TAG, this + "onNewIntent");
		}
		super.onNewIntent(intent);
		this.setIntent(intent);

		// 处理需要跳转到子页面
		if (intent != null) {
			String pageName = intent
					.getStringExtra(TaskManager.NAVIGATE_PAGE_NAME);
			String pageTag = intent
					.getStringExtra(TaskManager.NAVIGATE_PAGE_TAG);
			// 回退处理
			if (intent.getBooleanExtra(TaskManager.ACTION_NAVIGATE_BACK, false)) {
				handleBack(getArguments());
				if (!TextUtils.isEmpty(pageName)) { // 跳页面
					Bundle pageArgs = getArguments();
					// todo
					this.navigateBack(pageName, pageTag, pageArgs);
				} else {
					this.onShowDefaultContent(intent);
				}
			} else {
				if (!TextUtils.isEmpty(pageName)) { // 跳页面
					Bundle pageArgs = getArguments();
					this.navigateTo(pageName, pageTag, pageArgs);
				} else {
					this.onShowDefaultContent(intent);
					boolean isReorderTask = intent.getBooleanExtra(
							TaskManager.NAVIGATE_REORDER_TASK, false);
					if (!isReorderTask) {
						recordTaskNavigation(intent);
					}
				}
			}
		}
	}

	// 回退到该页面page
	protected void navigateBack(String pageName, String pageTag, Bundle pageArgs) {
		// 如果pageStack为空，返回
		if (TextUtils.isEmpty(pageName) || pageStack.isEmpty()) {
			return;
		}

		BasePage topPage = (BasePage) pageStack.peek();

		boolean ret = false;
		try {
			ret = getSupportFragmentManager().executePendingTransactions();
		} catch (IllegalStateException e) {
			if (DEBUG) {
				Log.e(TAG, "", e);
			}
		}

		Fragment f = getSupportFragmentManager().findFragmentByTag(
				pageName + pageTag);
		if (f != null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.remove(topPage);
			ft.commitAllowingStateLoss();
			try {
				getSupportFragmentManager().popBackStackImmediate();
			} catch (Exception e) {
				if (DEBUG) {
					Log.e(TAG, "", e);
				}
			}

			topPage.mIsBack = true;
			topPage.mBackArgs = pageArgs;
			if (topPage.getTask() == null) {
				topPage.setTask(this);
			}
			try {
				FragmentTransaction ftr = getSupportFragmentManager()
						.beginTransaction();
				ftr.add(R.id.fragment_container, topPage, ((Object) topPage)
						.getClass().getName() + topPage.getPageTag());
				ftr.commitAllowingStateLoss();
			} catch (Exception e) {
				if (DEBUG) {
					Log.e(TAG, "", e);
				}
			}
		} else if (!ret && !topPage.isAdded()) {
			topPage.mIsBack = true;
			topPage.mBackArgs = pageArgs;
			if (topPage.getTask() == null) {
				topPage.setTask(this);
			}
			try {
				getSupportFragmentManager().popBackStackImmediate();
				FragmentTransaction ftr = getSupportFragmentManager()
						.beginTransaction();
				ftr.add(R.id.fragment_container, topPage, ((Object) topPage)
						.getClass().getName() + topPage.getPageTag());
				ftr.commitAllowingStateLoss();
			} catch (Exception e) {
				if (DEBUG) {
					Log.e(TAG, "", e);
				}
			}
		}
	}

	private void recordTaskNavigation(Intent intent) {
		HistoryRecord record = new HistoryRecord(((Object) this).getClass()
				.getName(), null);
		record.taskSignature = HistoryRecord.genSignature(this);
		getTaskManager().track(record);
		((TaskManagerImpl) getTaskManager()).track(record, intent);
		if (DEBUG) {
			Log.d(TAG, "recordTaskNavigation");
			Log.d(TAG, getTaskManager().dump());
		}
	}

	@Override
	protected void onDestroy() {
		if (DEBUG) {
			Log.d(TAG, "onDestroy");
		}
		super.onDestroy();
		mDestroyed = true;
		TaskChangeEvent event = new TaskChangeEvent();
		event.type = TaskChangeEvent.TASK_REMOVE;
		event.curTask = this;
		EventBus.getDefault().post(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		notifyTaskChange();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		checkPageState();
	}

	private void checkPageState() {
		HistoryRecord lastRecord = getTaskManager().getLatestRecord();
		if (lastRecord != null
				&& lastRecord.pageName != null
				&& ((Object) this).equals(TaskManagerFactory.getTaskManager()
						.getContainerActivity())) {
			final Stack<Page> pages = getPageStack();
			if (pages.isEmpty()) {
				navigateTo(lastRecord.pageName, lastRecord.pageSignature, null);
				return;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (DEBUG) {
			Log.d(TAG, "onPause");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (DEBUG) {
			Log.d(TAG, "onStart");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (DEBUG) {
			Log.d(TAG, "onStop");
		}
	}

	/**
	 * 系统默认会恢复Fragment状态，在此忽略系统的恢复
	 * 
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(TASKMGR_STATE_KEY, getTaskManager().saveState());
		outState.putString(TASK_SIG_KEY, HistoryRecord.genSignature(this));
		if (DEBUG) {
			Log.e(TAG, "onSaveInstanceState:" + outState.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (DEBUG) {
			Log.d(TAG, "onAttachedToWindow");
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (DEBUG) {
			Log.d(TAG, "onDetachedFromWindow");
		}
	}

	@Override
	public void onBackPressed() {
		if (DEBUG) {
			Log.d(TAG, "onBackPressed");
		}

		backAction();
	}

	@SuppressLint("NewApi")
	private void backAction() {
		HistoryRecord record = getTaskManager().getLatestRecord();
		if (record == null) {
			finish();
			return;
		}
		Bundle backArgs = null;
		Page topPage = null;
		if (record.taskName.equals(((Object) this).getClass().getName())) {
			if (!pageStack.isEmpty()) {
				topPage = pageStack.peek();
				if (topPage.onBackPressed()) {
					return;
				}
			}
		}

		if (topPage != null) {
			backArgs = topPage.getBackwardArguments();
		}

		if (!goBack(backArgs)) {
			getTaskManager().onGoBack();
		}
	}

	/**
	 * 切换fragment
	 * 
	 * @param page
	 * @param strategy
	 */
	private void navigateAction(BasePage topPage, BasePage page, int strategy) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		page.mIsBack = false;
		switch (strategy) {
		case TaskManager.STACK_STRATEGY_REPLACE:
			// if (GlobalConfig.getInstance().isAnimationEnabled()) {
			// 添加到栈顶的界面的进入动画
			int[] customAnims = page.getCustomAnimations();
			int inAnim = customAnims[0];
			// 替换掉的界面的退出动画
			int outAnim = customAnims[1];
			if (topPage != null && !topPage.shouldOverrideCustomAnimations()) {
				outAnim = topPage.getCustomAnimations()[3];
			}

			ft.setCustomAnimations(inAnim, outAnim);
			// }
			ft.replace(R.id.fragment_container, page, ((Object) page)
					.getClass().getName() + page.getPageTag());
			break;
		default: {
			break;
		}

		}

		ft.commitAllowingStateLoss();
	}

	/**
	 * 返回处理
	 * 
	 * @param page
	 * @param strategy
	 */
	private boolean navigateBackAction(BasePage page, int strategy, Bundle args) {
		if (DEBUG) {
			Log.d(TAG, "navigateBackAction "
					+ ((Object) page).getClass().getSimpleName());
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (pageStack.isEmpty()) {
			ft.remove(page);
			ft.commitAllowingStateLoss();
			PageFactoryImpl.getInstance().removePage(page);
			finish();
			if (DEBUG) {
				Log.d(TAG, "The Task's page-stack is empty.Finish!");
			}
			return false;
		}

		switch (strategy) {
		case TaskManager.STACK_STRATEGY_REPLACE:
			HistoryRecord record = getTaskManager().getLatestRecord();
			if (record == null) {
				return false;
			}
			String pageName = record.pageName;
			String pageTag = record.pageSignature;
			BasePage targetPage = PageFactoryImpl.getInstance()
					.getBasePageInstance(pageName, pageTag);
			if (targetPage == null) {
				return false;
			}
			targetPage.mIsBack = true;
			targetPage.mBackArgs = args;
			if (targetPage.getTask() == null) {
				targetPage.setTask(this);
			}
			// targetPage.setArguments(args);

			// if (GlobalConfig.getInstance().isAnimationEnabled()) {
			// 当前界面的退出动画
			int[] customAnims = page.getCustomAnimations();
			int outAnim = customAnims[3];
			// 回退到的界面的重新进入动画
			int inAnim = targetPage.shouldOverrideCustomAnimations() ? customAnims[2]
					: targetPage.getCustomAnimations()[0];

			ft.setCustomAnimations(inAnim, outAnim);
			// }
			ft.replace(
					R.id.fragment_container,
					targetPage,
					((Object) targetPage).getClass().getName()
							+ targetPage.getPageTag());
			PageFactoryImpl.getInstance().removePage(page);
			break;
		default:
			break;
		}

		ft.commitAllowingStateLoss();

		return true;
	}

	private void recordPageNavigation(Page page) {

		HistoryRecord record = new HistoryRecord(((Object) this).getClass()
				.getName(), page.getClass().getName());
		record.taskSignature = HistoryRecord.genSignature(this);
		record.pageSignature = page.getPageTag() != null ? page.getPageTag()
				: PageFactory.DEFAULT_PAGE_TAG;
		pageStack.push(page);
		// record
		getTaskManager().track(record);
	}
	
	@Override
	public void updatePageName(CharSequence message) {
		Log.e(TAG, "updatePageName -> message: " + message);
	}

}
