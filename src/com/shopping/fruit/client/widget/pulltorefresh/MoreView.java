/**
 * 
 */
package com.shopping.fruit.client.widget.pulltorefresh;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.util.Log;


/**
 *更多控件，可以控制显示状态~ 正常，正在加载，加载失败~
 *添加点击态~
 * @author lichunming
 *
 */
public class MoreView extends LinearLayout {
	private static final String TAG = "MoreView";
	protected LayoutInflater mInflater;
	protected ViewHolder viewHolder;
	protected Context mContext;
	
	/**
	 * 四种状态，显示更多，正在加载，以及加载失败~，及释放刷新
	 */
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOADING = 1;
	public static final int TYPE_FAILED = 2;
	public static final int TYPE_PULL_2_REFRESH = 3;
	public static final int TYPE_NO_DATA = 4;
	public static final int TYPE_HIDE = 5;
	public static final int TYPE_SHOW = 6;
	
	private int mType = TYPE_NORMAL;
	
	private int mLastType = mType;
	
	
	protected String mStrNormal;
	protected String mStrLoading;
	protected String mStrFailed;
	protected String mStrNoData;
	
	private OnClickListener mClickListener;
	
	protected RelativeLayout mContentLayout;
	
	public MoreView(Context context) {
		this(context, null);
	}
	
	public MoreView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	protected void initView(Context mContext) {
		this.mContext=mContext;
		Log.i(TAG, "initView");
		mInflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
		initString();
		mContentLayout = (RelativeLayout) mInflater.inflate(R.layout.item_more_view, null);
		viewHolder = new ViewHolder();
		viewHolder.loadingLayout = (LinearLayout) mContentLayout.findViewById(R.id.l_layout_1);
		viewHolder.textView_loading =  (TextView) mContentLayout.findViewById(R.id.txt_1);
		viewHolder.textView = (TextView) mContentLayout.findViewById(R.id.txt_2);
		viewHolder.contentLayout = (LinearLayout) mContentLayout.findViewById(R.id.l_layout_2);
		
		mContentLayout.setClickable(true);
		mContentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MoreView.this.onClick();
			}
		});
		
		addView(mContentLayout);
		
	}
	
	public void onClick(){
		if(mType != TYPE_LOADING){
			if(mClickListener != null){
				mClickListener.onClick(this);
			}
		}
	}
	
	protected void initString(){
		mStrNormal = mContext.getString(R.string.lib_story_load_message_more);
		mStrLoading = mContext.getString(R.string.lib_story_load_data);
		mStrFailed = mContext.getString(R.string.lib_story_load_message_fail);
		mStrNoData = mContext.getString(R.string.lib_story_load_no_data);
	}
	
	/**
	 * 设置三种状态显示文案
	 * @param normal 正常时
	 * @param loading 正在加载是显示文案
	 * @param failed 加载失败时显示文案
	 */
	public void setString(String normal, String loading, String failed, String noData){
		if(!TextUtils.isEmpty(normal)){
			mStrNormal = normal;
		}
		if(!TextUtils.isEmpty(loading)){
			mStrLoading = loading;
		}
		if(!TextUtils.isEmpty(failed)){
			mStrFailed = failed;
		}
		if(!TextUtils.isEmpty(noData)){
			mStrNoData = noData;
		}
	}
	
	/**
	 *  设置三种状态显示文案
	 * @param normalId
	 * @param loadingId
	 * @param failedId
	 */
	public void setString(int normalId, int loadingId, int failedId, int noDataId){
		if(normalId != -1){
			mStrNormal = mContext.getString(normalId);
		}
		if(loadingId != -1){
			mStrLoading = mContext.getString(loadingId);
		}
		if(failedId != -1){
			mStrFailed = mContext.getString(failedId);
		}
		if(noDataId != -1){
			mStrNoData = mContext.getString(noDataId);
		}
	}
	
	public void setString(int normalId, int loadingId, int failedId){
		setString(normalId, loadingId, failedId, -1);
	}
	
	public void  setString(String normal, String loading, String failed){
		setString(normal, loading, failed, "");
	}
	
	public void setDisplayType(int type){
		mLastType = mType;
		mType = type;
		if(viewHolder != null){
			viewHolder.setDisplay(type);
		}
	}
	
	public void resetType(){
		mType = mLastType;
		if(viewHolder != null){
			viewHolder.setDisplay(mType);
		}
	}
	
	public int getDisplayType(){
		return mType;
	}
	
	public void setOnClickListener(OnClickListener listener){
		mClickListener = listener;
	}

	protected void finalize() throws Throwable {
		mInflater = null;
		viewHolder = null;
	}
	
	private void hide(){
		AbsListView.LayoutParams param = (AbsListView.LayoutParams) this.getLayoutParams();
		if(param != null){
			param.height = 1;
		}
		this.setVisibility(View.GONE);
	}
	
	private void show(){
		AbsListView.LayoutParams param = (AbsListView.LayoutParams) this.getLayoutParams();
		if(param != null){
			param.height = AbsListView.LayoutParams.MATCH_PARENT;
		}
		this.setVisibility(View.VISIBLE);
	}
	
	class ViewHolder{
		LinearLayout loadingLayout;
		LinearLayout contentLayout;
		TextView textView_loading;
		TextView textView;
		ImageView arrowImageView;
		
		public void setDisplay(int type){
			setSelected(false);
			setVisibility(View.VISIBLE);
			switch (type) {
			default:
				loadingLayout.setVisibility(View.GONE);
				contentLayout.setVisibility(View.VISIBLE);
//				arrowImageView.setVisibility(View.GONE);
				textView.setText(mStrNormal);
				break;
			case TYPE_NORMAL:
			case TYPE_LOADING:
				loadingLayout.setVisibility(View.VISIBLE);
				contentLayout.setVisibility(View.GONE);
				textView_loading.setText(mStrLoading);
				break;
			case TYPE_FAILED:
				loadingLayout.setVisibility(View.GONE);
				contentLayout.setVisibility(View.VISIBLE);
//				arrowImageView.setVisibility(View.GONE);
				textView.setText(mStrFailed);
				break;
			case TYPE_NO_DATA:
				loadingLayout.setVisibility(View.GONE);
				contentLayout.setVisibility(View.VISIBLE);
//					arrowImageView.setVisibility(View.GONE);
				textView.setText(mStrNoData);
				break;
			case TYPE_HIDE:
				hide();
				break;
			case TYPE_SHOW:
				show();
			}
		}
	}

}
