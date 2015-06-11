package com.shopping.fruit.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopping.fruit.client.R;
import com.shopping.fruit.client.util.Log;

/**
 * Created by keshuangjie on 2015/3/29.
 */
public class PlusView extends LinearLayout implements View.OnClickListener{

    private ImageView iv_reduce;
    private ImageView iv_plus;
    private TextView tv_count;

    private int mCount;

    public PlusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iv_plus = (ImageView) findViewById(R.id.iv_plus);
        iv_reduce = (ImageView) findViewById(R.id.iv_reduce);
        tv_count = (TextView) findViewById(R.id.tv_count);
        iv_reduce.setOnClickListener(this);
        iv_plus.setOnClickListener(this);
    }

    public void updateView(int count) {
        mCount = count;
        tv_count.setText(mCount + "");
        if(mCount > 0){
            iv_reduce.setVisibility(View.VISIBLE);
            tv_count.setVisibility(View.VISIBLE);
        } else {
            iv_reduce.setVisibility(View.GONE);
            tv_count.setVisibility(View.GONE);
        }
        if(mOnChangeListener != null){
            mOnChangeListener.onChange(this, mCount);
        }
    }

    private void plus(){
        mCount++;
        Log.i("kshj", "PlusView -> reduce() -> mCount: " + mCount);
        updateView(mCount);
    }

    private void reduce(){
        mCount--;
        Log.i("kshj", "PlusView -> reduce() -> mCount: " + mCount);
        updateView(mCount);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_plus:
                plus();
                break;
            case R.id.iv_reduce:
                reduce();
                break;
        }
    }

    private OnChangeListener mOnChangeListener;

    public void setOnChangeListener(OnChangeListener listener){
        this.mOnChangeListener = listener;
    }

    public interface OnChangeListener {
        public void onChange(View view, int count);
    }
}
