package com.shopping.fruit.client.base;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.Volley;
import com.shopping.fruit.client.R;
import com.shopping.fruit.client.entity.ResultEntity;
import com.shopping.fruit.client.network.RequestWithCookie;
import com.shopping.fruit.client.util.FormatUtils;
import com.shopping.fruit.client.util.Log;
import com.shopping.fruit.client.widget.pulltorefresh.IPullRefresh;
import com.shopping.fruit.client.widget.pulltorefresh.MoreView;
import com.shopping.fruit.client.widget.pulltorefresh.XListView;
import com.shopping.fruit.client.widget.pulltorefresh.XListViewHeader;

/**
 * 封装ListView相关方法
 * 
 * @author keshuangjie
 * 
 * @param <T>
 */
public abstract class MyListPage<T> extends CommonPage implements
		OnItemClickListener, OnScrollListener, IPullRefresh {
	protected static final String TAG = MyListPage.class.getName();
	
	// private static final long REFRESH_INTERVAL_TIME = 1000;// 刷新间隔，防止多长点击刷新

	protected static final int MESSAGE_LOAD_FIRST = 0;
	protected static final int MESSAGE_PULL_REFRESH = 1; // 下拉刷新
	protected static final int MESSAGE_LOAD_MORE = 2; // 加载更多
	protected static final int MESSAGE_LOAD_COMPLETE = 3;// 加载完成
	protected static final int MESSAGE_NO_DATA = 4; // 没有数据
	protected static final int MESSAGE_NET_ERROR = 5; // 网络错误
    protected static final int MESSAGE_NOT_LOGIN = 6; // 没有登录

	protected static final int MESSAGE_LOAD_MORE_PULL = 6;// 下拉加载更多
	protected static final int MESSAGE_REFRESH_PULL_UP = 7;// 上拉刷新。

	// protected static final int MESSAGE_LOAD_TOPIC = 8;

	protected static final String COUNT_PER_LOAD = "10"; // 默认每次加载条数
	/** 改成可重写的方法获取  modify 2014-06-24 18:36 */

	protected ListView mListView;
	protected View mRootView;
	protected MoreView mMoreView;
	protected View netErrorLayout;
	protected ViewGroup noDataLayout;
    protected ViewGroup noLoginLayout;

	protected AbsAdapter<T> mAdapter;
	private boolean isDestoryed = false;
	protected ArrayList<T> mContents;
	protected int requestIndex = 0;

	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			final int what = msg.what;
			switch (what) {
			// 下拉刷新
			case MESSAGE_PULL_REFRESH:
				ArrayList<T> list = (ArrayList<T>) msg.obj;
				if (list != null && list.size() > 0) {
					mContents = list;
					mAdapter.setContents(mContents);
				}
				if (list != null && list.size() < getCountPerPage()) {
					hasMore(false);
				} else {
					hasMore(true);
				}
				loadComplete(true, what);
				Log.i(TAG, "mHandler -> refresh list");
				break;
			// 加载更多
			case MESSAGE_LOAD_FIRST:
				mContents.clear();
			case MESSAGE_LOAD_MORE:
				ArrayList<T> list1 = (ArrayList<T>) msg.obj;
				if (list1 != null && list1.size() > 0) {
					for (T t : list1) {
						mContents.add(t);
					}
					mAdapter.setContents(mContents);
				}
				if (list1 != null && list1.size() < getCountPerPage()) {
					hasMore(false);
				} else {
					hasMore(true);
				}
				loadComplete(true, what);
				Log.i(TAG, "mHandler -> refresh list");
				break;
			// 加载完成
			case MESSAGE_LOAD_COMPLETE:
				boolean state = (Boolean) msg.obj;
				loadComplete(state, what);
				Log.i(TAG, "mHandler -> load complete");
				break;
			// 无数据提示
			case MESSAGE_NO_DATA:
				loadComplete(false, what);
				showNoDataNotice(msg.arg1);
				Log.i(TAG, "mHandler -> no data");
				break;
			// 网络错误提示
			case MESSAGE_NET_ERROR:
				loadComplete(false, what);
				showNetErrorNotice(msg.arg1);
				Log.i(TAG, "mHandler -> net error");
				break;
            case MESSAGE_NOT_LOGIN:
                loadComplete(false, what);
                showNoLoginLayout();
                break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContents = new ArrayList<T>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    protected View onCreatePageContent(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xlistview, container,
                false);
        return view;
    }

    @Override
    protected void onFindViews(View view) {
        super.onFindViews(view);
        mRootView = view;
        initView();
    }

    protected void initView() {
		mListView = (ListView) mRootView.findViewById(R.id.listView);
		addHeaderView();
		addFooterView();
		// mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mAdapter = initAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);
        if (mListView instanceof XListView) {
            XListView listView = (XListView) mListView;
            XListViewHeader headerView = (XListViewHeader) listView
                    .getHeaderView();
            listView.setPullRefreshEnable(isPullRefreshEnable());
            listView.setPullRefreshListener(this);
        }
	}

	@Override
	public void onStart() {
		super.onStart();
		if (isOnStartLoad()) {
			initData();
		}
	}

	protected boolean isOnStartLoad() {
		return true;
	}

	private boolean mIsFirstLoad = true;

	public void initData() {
		if (mIsFirstLoad) {
			mIsFirstLoad = false;
			firstLoad();
		}
	}

	protected void reInitState() {
		mIsFirstLoad = true;
	}

	protected void reInitData() {
		mIsFirstLoad = true;
		mContents.clear();
        requestIndex = 0;
		initData();
	}

	protected void firstLoad() {
		loadData(MESSAGE_LOAD_FIRST);
	}

	protected boolean mIsLoading = false;

	protected boolean isLoading() {
		return mIsLoading;
	}

	/**
	 * 是否存储json数据
	 * 
	 * @return
	 */
	protected boolean isCache() {
		return false;
	}

	protected void loadData(int type) {
		if (mIsLoading) {
			loadComplete(false, type);
//			return;
		}
		// showNoDataView(false);
		showListView();
		loadDataFromNetWork(type);
	}

    private void executeRequest(final int type) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = getFinalUrl(type);
        Log.i("kshj", "MyListPage -> executeRequest() -> buildUrl: " + url);
        RequestWithCookie request = new RequestWithCookie(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                 parseResponse(jsonObject, type);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
            }
        });
        queue.add(request);

    }

    private String getFinalUrl(final int type) {
        String url = buildUrl();
        if (!url.contains("?")) {
            url += "?";
        }
        String finalUrl = url + "&index=" + getLastItemIndex();
        return finalUrl;

    }

    protected abstract String buildUrl();

	/**
	 * @param type
	 *            下拉刷新/加载更多
	 */
	public void loadDataFromNetWork(final int type) {
		mIsLoading = true;
		if (type == MESSAGE_LOAD_MORE) {
			if(mMoreView != null){
				mMoreView.setDisplayType(MoreView.TYPE_LOADING);
			}
		} else if (type == MESSAGE_LOAD_FIRST) {
            showProgress();
        }

        executeRequest(type);
	}

	protected void parseResponse(JSONObject json, final int type) {

//		if (httpResponse == null) {
//			if (type == MESSAGE_LOAD_FIRST && isCache()) {
//				json = HttpNetWorkCache.getCacheJson(mFinalUrl);
//			} else {
//				sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
//				return;
//			}
//		} else {
//			json = httpResponse.getJSONObject();
//			if (type == MESSAGE_LOAD_FIRST && isCache()) {
//				json = getCacheJson(json);
//			}
//		}
//
//		// JSONObject json = httpResponse.getJSONObject();
		if (json != null) {
			Log.i("kshj", "onEnd()->JSONObject: " + json.toString());

			ResultEntity result = new ResultEntity(json);
			if (result.isSuccess()) {
                requestIndex++;
				result.data = parseData(json,type);
				// 有数据返回
				if (result.data != null && result.data.size() > 0) {
					if ((type == MESSAGE_LOAD_FIRST || type == MESSAGE_PULL_REFRESH)
							&& isCache()) {
						// 保存到缓存
//						HttpNetWorkCache.saveCache(mFinalUrl, json.toString());
					}

					sendHandlerMessage(type, result.data, type);
				} else {
					sendHandlerMessage(MESSAGE_NO_DATA, null, type);
//					showNoDataNotice(type);
				}
				// 未登录
			} else if (result.isNotLogin()) {
                sendHandlerMessage(MESSAGE_NOT_LOGIN, null, type);
				Log.i(TAG, "onEnd()-> no login");
			} else {
				sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
				Log.i(TAG, "onEnd()-> net error");
			}
		} else {
			sendHandlerMessage(MESSAGE_NET_ERROR, null, type);
			Log.i(TAG, "onEnd()-> net error");
		}
	}

//	private JSONObject getCacheJson(JSONObject json) {
//		if (json == null || TextUtils.isEmpty(json.toString())) {
//			json = HttpNetWorkCache.getCacheJson(mFinalUrl);
//		} else {
//			ResultEntity result = new ResultEntity(json);
//			if (!result.isSuccess()) {
//				JSONObject cacheJson = HttpNetWorkCache.getCacheJson(mFinalUrl);
//				if (cacheJson != null
//						&& TextUtils.isEmpty(cacheJson.toString())) {
//					ResultEntity cacheResult = new ResultEntity(cacheJson);
//					if (cacheResult.isSuccess()) {
//						json = cacheJson;
//					}
//				}
//			}
//		}
//		return json;
//	}
//
//	private String mFinalUrl;
//
//	private void urlParam(HttpSetting httpSetting) {
//		if (httpSetting == null || TextUtils.isEmpty(httpSetting.getUrl())) {
//			return;
//		}
//		StringBuilder sb = new StringBuilder(httpSetting.getUrl());
//		if (!TextUtils.isEmpty(httpSetting.getFunctionId())) {
//			sb.append("?");
//			sb.append("functionId=");
//			sb.append(httpSetting.getFunctionId());
//		}
//		JSONObject json = httpSetting.getJsonParams();
//		if (json != null && !TextUtils.isEmpty(json.toString())) {
//			sb.append("&");
//			sb.append("body=");
//			sb.append(json.toString());
//		}
//		mFinalUrl = sb.toString();
//	}

//	protected String getFinalUrl() {
//		return mFinalUrl;
//	}

	protected void sendHandlerMessage(int what, Object data, int type) {
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = data;
		msg.arg1 = type;
		mHandler.sendMessage(msg);
	}

	/**
	 * @param success
	 *            true：加载数据成功 false:加载数据失败
	 * @param type
	 *            :加载类型
	 */
	protected void loadComplete(boolean success, int type) {
		mIsLoading = false;

        dismissProgress();

		if (success) {
			mListView.setVisibility(View.VISIBLE);
		}
		
		if (isPullRefreshEnable() && mListView != null && mListView instanceof XListView) {
			((XListView) mListView).stopRefresh();
		}

		if (success) {
			switch (type) {
			case MESSAGE_LOAD_FIRST:
				setRefreshTime();
				break;
			case MESSAGE_PULL_REFRESH:
				// showNewStoryHint();
				setRefreshTime();
				break;
			default:
				break;
			}
		}
	};
	
	protected long lastRefreshTime = 0;
	
	protected void setRefreshTime() {
		lastRefreshTime = System.currentTimeMillis();
		if (isPullRefreshEnable() && mListView instanceof XListView) {
			((XListView) mListView).setRefreshTime(FormatUtils
					.formatDate(new Date(lastRefreshTime)));
		}
	}
	
	/** 每页默认加载列表条数， 默认10条 **/
	protected int getCountPerPage(){
		return Integer.valueOf(COUNT_PER_LOAD);
	}

	// 是否还有更多页
	protected boolean mHasMore = true;

	/**
	 * 是否还有更多数据
	 * 
	 * @param hasMore
	 */
	protected void hasMore(boolean hasMore) {
		if (hasMore) {
			mHasMore = true;
			if(mMoreView != null){
				mMoreView.setDisplayType(MoreView.TYPE_SHOW);
				mMoreView.setDisplayType(MoreView.TYPE_NORMAL);
			}
			mAdapter.notifyDataSetChanged();
		} else {
			mHasMore = false;
			if(mMoreView != null){
				mMoreView.setDisplayType(MoreView.TYPE_NORMAL);
				mMoreView.setDisplayType(MoreView.TYPE_HIDE);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	protected void addHeaderView() {
	}

	protected void addFooterView() {
		if (mMoreView == null) {
			mMoreView = new MoreView(getActivity());
			mMoreView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadData(MESSAGE_LOAD_MORE);
				}
			});
			mMoreView.setDisplayType(MoreView.TYPE_HIDE);
			mListView.addFooterView(mMoreView, null, true);
		}
	}

	/**
	 * @param type
	 *            下拉刷新/加载更多 显示网络错误提示
	 */
	protected void showNetErrorNotice(int type) {
		switch (type) {
		case MESSAGE_LOAD_FIRST:
		case MESSAGE_LOAD_MORE:
			if (mContents != null && mContents.size() > 0) {
				if(mMoreView != null){
					mMoreView.setDisplayType(MoreView.TYPE_FAILED);
				}
				mHasMore = false;
			} else {
				// 显示网络错误
				Log.i(TAG, "显示网络错误");
				showNetErrorLayout();
			}
			break;
		case MESSAGE_PULL_REFRESH:
			Log.i(TAG, "网络不给力哦");
			Toast.makeText(getActivity(), "网络不给力哦", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

	}

	/**
	 * 没有数据时网络错误提示
	 */
	protected void showNetErrorLayout() {
		if (netErrorLayout == null && mRootView != null) {
			netErrorLayout = mRootView.findViewById(R.id.net_error);
			Button refresh = (Button) netErrorLayout.findViewById(R.id.refresh);
			refresh.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mIsFirstLoad = true;
					netErrorLayout.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					initData();
				}
			});
		}
		mListView.setVisibility(View.GONE);
		netErrorLayout.setVisibility(View.VISIBLE);
	}

	protected void showNodataLayout() {
		mListView.setVisibility(View.GONE);
        if (noDataLayout == null) {
            noDataLayout = (ViewGroup) findViewById(R.id.notice);
            View nodataView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_nodata, null);
            noDataLayout.addView(nodataView);
        }
        noDataLayout.setVisibility(View.VISIBLE);
	}

    protected void showNoLoginLayout() {
        mListView.setVisibility(View.GONE);
        if (noLoginLayout == null) {
            noLoginLayout = (ViewGroup) findViewById(R.id.rl_nologin);
            View noLoginView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_nologin, null);
            noLoginView.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToLoginPage();
                }
            });
            noLoginLayout.addView(noLoginView);
        }
        noLoginLayout.setVisibility(View.VISIBLE);
    }

	/**
	 * @param type
	 *            下拉刷新/加载更多 显示没有数据提示
	 */
	protected void showNoDataNotice(final int type) {
		switch (type) {
		case MESSAGE_LOAD_FIRST:
		case MESSAGE_LOAD_MORE:
			if (mContents != null && mContents.size() > 0) {
				hasMore(false);
			} else {
				// 显示无数据
				hasMore(false);
				showNodataLayout();
				Log.i(TAG, "显示无数据");
			}
			break;
		case MESSAGE_PULL_REFRESH:
			if (mContents != null && mContents.size() > 0) {
				Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
			} else {
				// 显示无数据
				// hasMore(false);
				showNodataLayout();
				Log.i(TAG, "显示无数据");
			}
			break;
		default:
			break;
		}

	}

	protected void showListView() {
		if (netErrorLayout != null) {
			netErrorLayout.setVisibility(View.GONE);
		}
		if (noDataLayout != null) {
            noDataLayout.setVisibility(View.GONE);
        }
        if (noLoginLayout != null) {
            noLoginLayout.setVisibility(View.GONE);
        }
		if(mListView != null){
			mListView.setVisibility(View.VISIBLE);
		}
	}

	protected abstract ArrayList<T> parseData(JSONObject json,int type);

	protected abstract AbsAdapter<T> initAdapter();

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.i(TAG, "onScroll()");
		if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
			if (mContents != null && !mContents.isEmpty()
			/* && totalItemCount >= mContents.size() */) {
				if (mHasMore
						&& mMoreView != null && mMoreView.getDisplayType() != MoreView.TYPE_LOADING) {
					loadData(MESSAGE_LOAD_MORE);
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.i(TAG, "onScrollStateChanged() -> scrollState: " + scrollState);
	}
	
	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
                mContents.clear();
                requestIndex = 0;
				loadData(MESSAGE_PULL_REFRESH);
			}
		}, 400);

	}
	
	/**
	 * 滑到列表的第一项
	 */
	public void scrollToFirstItem(){
		if (mContents == null || mContents.size() <= 0 || mListView.getVisibility() != View.VISIBLE) {
			return;
		}

		if (isLoading()) {
			return;
		}

		if (!mListView.isStackFromBottom()) {
			mListView.setStackFromBottom(true);
		}
		mListView.setStackFromBottom(false);
	}

	/**
	 * 是否支持下拉刷新，默认不支持
	 * 
	 * @return
	 */
	protected boolean isPullRefreshEnable() {
		return true;
	}

    protected int getLastItemIndex() {
        if (mContents == null || mContents.size() == 0) {
            return 0;
        }
        return mContents.size() - 1;
    }

	protected T getLastItem() {
		if (mContents != null && mContents.size() > 0) {
			return mContents.get(mContents.size() - 1);
		}
		return null;
	}
	
	protected T getFirstItem() {
		if (mContents != null && mContents.size() > 0) {
			return mContents.get(0);
		}
		return null;
	}

	protected View findViewById(int id) {
        return getView().findViewById(id);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		isDestoryed = true;
		super.onDestroy();
	}

    /// ListView only support addHeaderView after adapter set till level 11.
    protected static boolean isAbleToAddLazyHeaderView() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
}
