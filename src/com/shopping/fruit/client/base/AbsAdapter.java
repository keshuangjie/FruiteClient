package com.shopping.fruit.client.base;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 适配器基类
 * @author keshuangjie
 *
 * @param <T>
 */
public abstract class AbsAdapter<T> extends BaseAdapter {

	protected ArrayList<T> mContent = new ArrayList<T>();

	protected final Context mContext;
	
	protected final LayoutInflater mInflater;

	public AbsAdapter(Context context) {
		this(context, null);
	}

	public void setEditable(boolean editable) {
	}

	public AbsAdapter(Context context, ArrayList<T> content) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null");
		}
		mContext = context;
		mInflater = LayoutInflater.from(context);
		setContents(content);
	}

	public ArrayList<T> getContents() {
		return mContent;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mContent != null && !mContent.isEmpty()) {
			count = mContent.size();
		}
		return count;
	}

	@Override
	public T getItem(int position) {
		T result = null;
		if (mContent != null && !mContent.isEmpty()) {
			result = mContent.get(position);
		}
		return result;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);

	public void removeAll() {
		if (mContent != null && !mContent.isEmpty()) {
			mContent.clear();
			notifyDataSetChanged();
		}
	}

	public void setContents(ArrayList<T> contents) {
		if (contents == null) {
			contents = new ArrayList<T>();
		}
        mContent.clear();
        mContent.addAll(contents);
		notifyDataSetChanged();
	}
	
	public int getPosition(T t){
		int position = 0;
		for(T t1: mContent){
			if(t1 == t){
				break;
			}
			position++;
		}
		return position;
	}
	
	public void clearContents(){
		mContent.clear();
		notifyDataSetChanged();
	}
	
	public void deleteItem(T item){
		mContent.remove(item);
		notifyDataSetChanged();
	}
	
//	protected int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	
//	public void setOnScrollState(int state){
//		mScrollState = state;
//	}
//	
//	protected boolean mIsCanLoad = true;;
//	
//	public void setCanLoad(boolean canLoad){
//		mIsCanLoad = canLoad;
//	}
//	
//	/**
//	 * 列表是否处于滑动状态
//	 * @return
//	 */
//	protected boolean isNotFling(){
//		return mScrollState == OnScrollListener.SCROLL_STATE_FLING ? false : true; 
//	}

    public static class ViewHolder {
        // I added a generic return type to reduce the casting noise in client code
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}
